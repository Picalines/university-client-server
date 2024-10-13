import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SampleGenerator {
    public static Path file(String name, int noLines) throws IOException {
        var filePath = Paths.get(name);

        var random = ThreadLocalRandom.current();
        var lines = IntStream.range(0, noLines)
                .map(x -> random.nextInt(100))
                .boxed()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));

        Files.writeString(filePath, lines);

        return filePath;
    }
}
