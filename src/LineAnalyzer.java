import java.util.List;

// Класс, отвечающий за анализ списка строк
public class LineAnalyzer {
    private final LogLineParser logLineParser;
    private final BotStatistics botStatistics;

    public LineAnalyzer() {
        this.logLineParser = new LogLineParser();
        this.botStatistics = new BotStatistics();
    }

    // Получает список строк
    public FileAnalysisResult analyze(String fileName, List<String> lines) {
        // Для каждой строки использует BotStatistics + LogLineParser
        for (String line : lines) {
            botStatistics.processLine(line, logLineParser);
        }
        // Создает объект FileAnalysisResult
        return new FileAnalysisResult(
                fileName,
                botStatistics.getTotalLines(),
                botStatistics.getGooglebotCount(),
                botStatistics.getYandexbotCount(),
                botStatistics.getGooglebotPercentage(),
                botStatistics.getYandexbotPercentage()
        );
    }
}
