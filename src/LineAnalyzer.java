import java.util.List;

// Класс, отвечающий за анализ списка строк
public class LineAnalyzer {
    public FileAnalysisResult analyze(String fileName, List<String> lines) {
        int totalLines = lines.size();
        int maxLength = 0;
        int minLength = Integer.MAX_VALUE;

        for (String line : lines) {
            int length = line.length();
            maxLength = Math.max(maxLength, length);
            minLength = Math.min(minLength, length);
        }

        // Обработка случая пустого файла
        if (totalLines == 0) {
            minLength = 0;
        }

        return new FileAnalysisResult(fileName, totalLines, maxLength, minLength);
    }
}
