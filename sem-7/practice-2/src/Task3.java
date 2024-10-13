import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Task3 {
    public static void main(String[] args) throws Exception {
        var filePath = ProfiledValue.print("sample generation", () -> SampleGenerator.file("task3.txt", 100));

        ProfiledValue.printVoid("checksum calculation", () -> {
            var checksum = calculateChecksum(filePath.toString());
            System.out.printf("checksum of %s: 0x%04X%n", filePath, checksum);
        });
    }

    public static short calculateChecksum(String filePath) throws IOException {
        short checksum = 0;
        var buffer = ByteBuffer.allocate(2);

        try (var fis = new FileInputStream(filePath); var fileChannel = fis.getChannel()) {
            while (fileChannel.read(buffer) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    checksum ^= buffer.get();
                }
                buffer.clear();
            }
        }

        return checksum;
    }
}