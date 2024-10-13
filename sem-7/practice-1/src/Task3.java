import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

record File(String fileType, int fileSize) {
}

class FileGenerator implements Runnable {
    private final BlockingQueue<File> queue;

    public FileGenerator(BlockingQueue<File> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        var random = ThreadLocalRandom.current();

        while (true) {
            try {
                Thread.sleep(random.nextInt(901) + 100);
                queue.put(generateFile());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static final String[] fileTypes = {"XML", "JSON", "XLS"};

    private static File generateFile() {
        var random = ThreadLocalRandom.current();

        var fileType = fileTypes[random.nextInt(fileTypes.length)];
        var fileSize = 10 + random.nextInt(91);

        return new File(fileType, fileSize);
    }
}

class FileProcessor implements Runnable {
    private final BlockingQueue<File> queue;
    private final String allowedFileType;

    public FileProcessor(BlockingQueue<File> queue, String allowedFileType) {
        this.queue = queue;
        this.allowedFileType = allowedFileType;
    }

    @Override
    public void run() {
        while (true) {
            try {
                var file = queue.take();
                if (!file.fileType().equals(allowedFileType)) {
                    continue;
                }

                processFile(file);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static void processFile(File file) throws InterruptedException {
        var processingTime = file.fileSize() * 7L;
        Thread.sleep(processingTime);

        System.out.println(file.fileType() + " file of size " + file.fileSize() + " was processed in " + processingTime + "ms");
    }
}

public class Task3 {
    public static void main(String[] args) {
        BlockingQueue<File> queue = new LinkedBlockingQueue<>(5);

        var generatorThread = new Thread(new FileGenerator(queue));

        var jsonProcessorThread = new Thread(new FileProcessor(queue, "JSON"));
        var xmlProcessorThread = new Thread(new FileProcessor(queue, "XML"));
        var xlsProcessorThread = new Thread(new FileProcessor(queue, "XLS"));

        generatorThread.start();
        jsonProcessorThread.start();
        xmlProcessorThread.start();
        xlsProcessorThread.start();
    }
}
