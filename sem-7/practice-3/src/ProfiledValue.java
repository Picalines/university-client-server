import java.util.concurrent.Callable;

public record ProfiledValue<T>(T value, long milliseconds, long usedMemory) {
    public static <T> ProfiledValue<T> call(Callable<T> callable) throws Exception {
        Runtime.getRuntime().gc();

        var startMs = System.currentTimeMillis();
        var startMemory = currentUsedMemory();
        var returned = callable.call();
        var endMs = System.currentTimeMillis();
        var endMemory = currentUsedMemory();

        return new ProfiledValue<>(returned, endMs - startMs, endMemory - startMemory);
    }

    public static <T> T print(String taskName, Callable<T> callable) throws Exception {
        var profiled = call(callable);
        System.out.println(taskName + " took " + profiled.milliseconds + "ms and " + profiled.usedMemory + " bytes");
        return profiled.value;
    }

    public static void printVoid(String taskName, CheckedRunnable runnable) throws Exception {
        print(taskName, () -> {
            runnable.run();
            return null;
        });
    }

    private static long currentUsedMemory() {
        var runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
