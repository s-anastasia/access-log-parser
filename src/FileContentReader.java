import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Класс, отвечающий за чтение файл построчно + за выброс исключения при превышении макс доп длины строки
public class FileContentReader {

    private static final int MAX_LINE_LENGTH = 1024;

    // Метод, читающий файл построчно через BufferedReader
    public List<String> readLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                validateLineLength(file.getName(), line.length());
                lines.add(line);
            }
        }
        return lines;
    }

    private void validateLineLength(String fileName, int length) {
        if (length > MAX_LINE_LENGTH) {
            throw new LongLineException(fileName, length, MAX_LINE_LENGTH);
        }
    }
}
