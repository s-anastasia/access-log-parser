import java.util.Map;
import java.util.Set;

public class FileAnalysisResult {
    private final String fileName;
    private final int totalLines;
    private final int googlebotCount;
    private final int yandexbotCount;
    private final double googlebotPercentage;
    private final double yandexbotPercentage;
    private final double averageTrafficPerHour;
    private final long totalTraffic;
    private final Statistics statistics;

    public FileAnalysisResult(String fileName, Statistics stats) {
        this.fileName = fileName;
        this.totalLines = stats.getTotalEntries();
        this.googlebotCount = stats.getGooglebotCount();
        this.yandexbotCount = stats.getYandexbotCount();
        this.googlebotPercentage = stats.getGooglebotPercentage();
        this.yandexbotPercentage = stats.getYandexbotPercentage();
        this.averageTrafficPerHour = stats.getTrafficRate();
        this.totalTraffic = stats.getTotalTraffic();
        this.statistics = stats;
    }

    public void printResults() {
        System.out.println("\n📊 Результаты анализа файла '" + fileName + "':");
        System.out.println("1. Общее количество строк: " + totalLines);
        System.out.printf("2. Запросов от Googlebot: %d (%.2f%%)%n",
                googlebotCount, googlebotPercentage);
        System.out.printf("3. Запросов от YandexBot: %d (%.2f%%)%n",
                yandexbotCount, yandexbotPercentage);
        System.out.println("4. Общий объем трафика: " + formatBytes(totalTraffic));
        System.out.printf("5. Средний объём трафика за час: %s/час%n",
                formatBytes(averageTrafficPerHour));

        // Статистика существующих страниц (только количество)
        Set<String> existingPages = statistics.getExistingPages();
        System.out.println("6. Количество существующих страниц (код 200): " + existingPages.size());

        // Статистика операционных систем
        Map<String, Double> osStats = statistics.getOsStatistics();
        System.out.println("7. Статистика операционных систем:");
        if (!osStats.isEmpty()) {
            osStats.forEach((os, percentage) ->
                    System.out.printf("   - %s: %.2f%%%n", os, percentage * 100));
        } else {
            System.out.println("   Нет данных об операционных системах");
        }

        if (totalLines == 0) {
            System.out.println("⚠️  Файл не содержит валидных лог-записей");
        }
    }

    private String formatBytes(double bytes) {
        if (bytes < 1024) {
            return String.format("%.0f байт", bytes);
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f КБ", bytes / 1024);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f МБ", bytes / (1024 * 1024));
        } else {
            return String.format("%.2f ГБ", bytes / (1024 * 1024 * 1024));
        }
    }

    public String getFileName() { return fileName; }
    public int getTotalLines() { return totalLines; }
    public int getGooglebotCount() { return googlebotCount; }
    public int getYandexbotCount() { return yandexbotCount; }
    public double getGooglebotPercentage() { return googlebotPercentage; }
    public double getYandexbotPercentage() { return yandexbotPercentage; }
    public double getAverageTrafficPerHour() { return averageTrafficPerHour; }
    public long getTotalTraffic() { return totalTraffic; } // изменено с int на long
}