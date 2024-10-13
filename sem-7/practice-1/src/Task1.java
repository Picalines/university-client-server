import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Task1 {
    private static class FileGenerator {
        public static void main(String[] args) throws Exception {
            var path = Paths.get("numbers.txt").toAbsolutePath();
            var random = new Random();
            var sb = new StringBuilder();

            for (int i = 0; i < 10_000; i++) {
                if (i > 0) {
                    sb.append('\n');
                }
                sb.append(random.nextInt(100));
            }

            Files.writeString(path, sb.toString());
            System.out.println("Numbers were written to " + path);
        }
    }

    public static void main(String[] args) throws Exception {
        var numbers = Files.lines(Paths.get("numbers.txt"))
                .map(Integer::parseInt).toList();

        Callable<?>[] tasks = new Callable[]{
                () -> printProfiled("sequential sum", () -> numbers.stream()
                        .reduce(Task1::slowSum)
                        .orElse(0)),
                () -> printProfiled("multithreaded sum", () ->
                        multithreadedSum(numbers, Executors::newFixedThreadPool)),
                () -> printProfiled("work-stealing sum", () ->
                        multithreadedSum(numbers, x -> Executors.newWorkStealingPool())),
        };

        for (var task : tasks) {
            task.call();
        }
    }

    private static Integer multithreadedSum(List<Integer> numbers, Function<Integer, ExecutorService> executorServiceFactory) {
        final var noThreads = numbers.size() / 1_000;
        final var chunkSize = numbers.size() / noThreads;

        var chunkTasks = IntStream.range(0, noThreads).boxed().map(chunkIdx -> {
            var fromIndex = chunkIdx * chunkSize;
            var toIndex = chunkIdx == noThreads - 1 ? numbers.size() : (chunkIdx + 1) * chunkSize;

            Callable<Integer> callable = () -> numbers.subList(fromIndex, toIndex)
                    .stream()
                    .reduce(Task1::slowSum)
                    .orElse(0);

            return new FutureTask<>(callable);
        }).toList();

        try (var executorService = executorServiceFactory.apply(noThreads)) {
            chunkTasks.forEach(executorService::submit);

            var chunkSums = chunkTasks.stream().map(task -> {
                try {
                    return task.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });

            return chunkSums.reduce(Task1::slowSum).orElse(0);
        }
    }

    private static Integer slowSum(Integer a, Integer b) {
        safeSleep(1);
        return a + b;
    }

    private static void safeSleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private record ProfiledValue<T>(T value, long milliseconds, long usedMemory) {
    }

    private static <T> ProfiledValue<T> runProfiled(Callable<T> runnable) throws Exception {
        Runtime.getRuntime().gc();

        var startMs = System.currentTimeMillis();
        var startMemory = currentUsedMemory();
        var returned = runnable.call();
        var endMs = System.currentTimeMillis();
        var endMemory = currentUsedMemory();

        return new ProfiledValue<>(returned, endMs - startMs, endMemory - startMemory);
    }

    private static <T> T printProfiled(String taskName, Callable<T> runnable) throws Exception {
        var profiledValueValue = runProfiled(runnable);
        System.out.println(taskName + " took " + profiledValueValue.milliseconds + "ms and " + profiledValueValue.usedMemory + " bytes");
        return profiledValueValue.value;
    }

    private static long currentUsedMemory() {
        var runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}