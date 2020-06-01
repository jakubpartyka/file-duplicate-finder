package file_management.optimizers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class Test {
    public static void main(String[] args) throws IOException {
        File original = new File("/Users/admin/Desktop/a.jpg");
        File duplicate = new File("/Users/admin/Desktop/test/a.jpg");
        Path target = Paths.get(original.getAbsolutePath());
        Path link = Paths.get(duplicate.getAbsolutePath());
        if (Files.exists(link)) {
            Files.delete(link);
        }
        Files.createLink(link, target);
    }

    private static Path createTextFile() throws IOException {
        byte[] content = IntStream.range(0, 10000)
                .mapToObj(i -> i + System.lineSeparator())
                .reduce("", String::concat)
                .getBytes(StandardCharsets.UTF_8);
        Path filePath = Paths.get("", "target_link.txt");
        Files.write(filePath, content, CREATE, TRUNCATE_EXISTING);
        return filePath;
    }
}
