import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Task4 {
    private enum FileType {
        XML,
        JSON,
        XLS,
    }

    private record File(FileType type, int size) {
        public static File createRandom() {
            var random = ThreadLocalRandom.current();
            var fileTypes = FileType.values();
            return new File(fileTypes[random.nextInt(fileTypes.length)], random.nextInt(10, 101));
        }
    }

    private static final int QUEUE_CAPACITY = 5;

    private static Completable processFiles(Observable<File> files, FileType fileType) {
        return files
                .filter(file -> file.type().equals(fileType))
                .flatMapCompletable(file -> Completable
                        .fromAction(() -> {
                            Thread.sleep(file.size * 7L);
                            System.out.println("Processed " + file);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                );
    }

    public static void main(String[] args) throws InterruptedException {
        var random = new Random();

        var files = Observable
                .fromCallable(() -> {
                    Thread.sleep(random.nextInt(100, 1001));
                    return File.createRandom();
                })
                .repeat()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());

        var fileQueue = files.replay(QUEUE_CAPACITY).autoConnect();

        fileQueue.subscribe(file -> System.out.println("Created " + file));

        for (var fileType : FileType.values()) {
            processFiles(fileQueue, fileType).subscribe();
        }

        Thread.sleep(10_000);
    }
}
