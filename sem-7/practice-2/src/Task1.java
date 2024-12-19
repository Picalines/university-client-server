import java.nio.file.Files;

public class Task1 {
    public static void main(String[] args) throws Exception {
        var filePath = ProfiledValue.print(
                "file generation",
                () -> SampleGenerator.file("task1.txt", 5));

        ProfiledValue.printVoid("reading " + filePath, () ->
                Files.lines(filePath).forEach(System.out::println));
    }
}