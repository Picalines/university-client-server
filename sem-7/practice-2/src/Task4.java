import java.nio.file.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class Task4 {
    private record FileRecord(List<String> lines, String hash) {
    }

    private static final Map<Path, FileRecord> fileRecords = new HashMap<>();

    public static void main(String[] args) throws Exception {
        var directory = Paths.get("task4_dir/");

        gatherInitialData(directory);

        var watchService = FileSystems.getDefault().newWatchService();
        directory.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

        while (true) {
            var key = watchService.take();

            for (var event : key.pollEvents()) {
                var kind = event.kind();

                var filePath = directory.resolve((Path) event.context());

                if (kind == ENTRY_CREATE) {
                    System.out.println("created: " + filePath);

                    var lines = Files.lines(filePath).toList();
                    var hash = calculateFileHash(filePath);
                    fileRecords.put(filePath, new FileRecord(lines, hash));
                } else if (kind == ENTRY_MODIFY) {
                    System.out.println("edited: " + filePath);
                    printFileChanges(filePath);
                } else if (kind == ENTRY_DELETE) {
                    System.out.println("deleted: " + filePath);
                    System.out.println(filePath + " hash was: " + fileRecords.get(filePath).hash);
                    fileRecords.remove(filePath);
                }
            }

            key.reset();
        }
    }

    private static void gatherInitialData(Path directory) throws Exception {
        try (var directoryStream = Files.newDirectoryStream(directory)) {
            for (var dirItemPath : directoryStream) {
                if (!Files.isRegularFile(dirItemPath)) {
                    continue;
                }

                var lines = Files.lines(dirItemPath).toList();
                var hash = calculateFileHash(dirItemPath);
                fileRecords.put(dirItemPath, new FileRecord(lines, hash));
            }
        }
    }

    private static void printFileChanges(Path filePath) throws Exception {
        var oldRecord = fileRecords.get(filePath);

        var newLines = Files.lines(filePath).toList();
        String hash;

        if (oldRecord != null) {
            hash = oldRecord.hash;

            var oldLines = oldRecord.lines();

            oldLines.stream()
                    .filter(line -> !newLines.contains(line))
                    .forEach(line -> System.out.println("- " + line));
            newLines.stream()
                    .filter(line -> !oldLines.contains(line))
                    .forEach(line -> System.out.println("+ " + line));
        } else {
            hash = calculateFileHash(filePath);
        }

        fileRecords.put(filePath, new FileRecord(newLines, hash));
    }

    private static String calculateFileHash(Path filePath) throws Exception {
        var md5 = MessageDigest.getInstance("MD5");

        try (var is = Files.newInputStream(filePath); var dis = new DigestInputStream(is, md5)) {
            while (dis.read() != -1) ;
            return bytesToHex(md5.digest());
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}