import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Task2 {
    public static void main(String[] args) throws Exception {
        var sourcePath = ProfiledValue.print("file generation", () -> SampleGenerator.file("task2.txt", 700_000_00));

        System.out.println("file of size " + Files.size(sourcePath) / 1_000_000 + " mb was generated");

        ProfiledValue.printVoid("FileInputStream/FileOutputStream",
                () -> copyWithFileStreams(sourcePath, Paths.get("task2.copy1.txt")));

        ProfiledValue.printVoid("FileChannel",
                () -> copyWithFileChannel(sourcePath, Paths.get("task2.copy2.txt")));

        ProfiledValue.printVoid("ApacheCommons",
                () -> copyWithApacheCommons(sourcePath, Paths.get("task2.copy3.txt")));

        ProfiledValue.printVoid("FilesClass",
                () -> copyWithFilesClass(sourcePath, Paths.get("task2.copy4.txt")));
    }

    private static void copyWithFileStreams(Path sourcePath, Path destPath) throws IOException {
        var fis = new FileInputStream(sourcePath.toString());
        var fos = new FileOutputStream(destPath.toString());

        var buffer = new byte[1024];

        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }

        fis.close();
        fos.close();
    }

    private static void copyWithFileChannel(Path sourcePath, Path destPath) throws IOException {
        var fis = new FileInputStream(sourcePath.toString());
        var fos = new FileOutputStream(destPath.toString());

        var sourceChannel = fis.getChannel();
        var destinationChannel = fos.getChannel();

        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
        sourceChannel.close();
        destinationChannel.close();

        fis.close();
        fos.close();
    }

    private static void copyWithApacheCommons(Path sourcePath, Path destPath) throws IOException {
        var sourceFile = sourcePath.toFile();
        var destFile = destPath.toFile();
        FileUtils.copyFile(sourceFile, destFile);
    }

    private static void copyWithFilesClass(Path sourcePath, Path destPath) throws IOException {
        Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
    }
}
