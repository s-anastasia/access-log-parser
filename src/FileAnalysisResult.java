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
        // Статистика посещаемости
        System.out.printf("6. Среднее количество посещений в час: %.2f/час%n",
                statistics.getAverageVisitsPerHour());
        System.out.printf("7. Среднее количество ошибочных запросов в час: %.2f/час%n",
                statistics.getAverageErrorRequestsPerHour());
        System.out.printf("8. Средняя посещаемость одним пользователем: %.2f посещений/пользователь%n",
                statistics.getAverageVisitsPerUser());
        System.out.println("9. Количество реальных пользователей: " + statistics.getUniqueHumanUsers());
        System.out.println("10. Количество ошибочных запросов: " + statistics.getErrorRequests());
        System.out.println("11. Количество существующих страниц: " + statistics.getExistingPages().size());
        System.out.println("12. Количество несуществующих страниц: " + statistics.getNotFoundPages().size());

        // Статистика операционных систем
        Map<String, Double> osStats = statistics.getOsStatistics();
        System.out.println("13. Статистика операционных систем:");
        if (!osStats.isEmpty()) {
            osStats.forEach((os, percentage) ->
                    System.out.printf("   - %s: %.2f%%%n", os, percentage * 100));
        } else {
            System.out.println("   Нет данных об операционных системах");
        }

        // Статистика браузеров
        Map<String, Double> browserStats = statistics.getBrowserStatistics();
        System.out.println("14. Статистика браузеров:");
        if (!browserStats.isEmpty()) {
            browserStats.forEach((browser, percentage) ->
                    System.out.printf("   - %s: %.2f%%%n", browser, percentage * 100));
        } else {
            System.out.println("   Нет данных о браузерах");
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

    // Геттеры
    public String getFileName() { return fileName; }
    public int getTotalLines() { return totalLines; }
    public int getGooglebotCount() { return googlebotCount; }
    public int getYandexbotCount() { return yandexbotCount; }
    public double getGooglebotPercentage() { return googlebotPercentage; }
    public double getYandexbotPercentage() { return yandexbotPercentage; }
    public double getAverageTrafficPerHour() { return averageTrafficPerHour; }
    public long getTotalTraffic() { return totalTraffic; }
    public Statistics getStatistics() { return statistics; }
    public Set<String> getExistingPages() { return statistics.getExistingPages(); }
    public Set<String> getNotFoundPages() { return statistics.getNotFoundPages(); }
    public Map<String, Double> getBrowserStatistics() { return statistics.getBrowserStatistics(); }
}