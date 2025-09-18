import java.util.Map;
import java.util.Set;

/**
 * Класс для хранения и отображения результатов анализа файла логов
 */
public class FileAnalysisResult {

    // ========== ПОЛЯ КЛАССА ==========

    private final String fileName; // Имя анализируемого файла
    private final int totalLines; // Общее количество обработанных строк
    private final int googlebotCount; // Количество запросов от Googlebot
    private final int yandexbotCount; // Количество запросов от YandexBot
    private final double googlebotPercentage; // Процент запросов от Googlebot
    private final double yandexbotPercentage; // Процент запросов от YandexBot
    private final double averageTrafficPerHour; // Средний трафик в час
    private final long totalTraffic; // Общий объем трафика
    private final Statistics statistics; // Объект статистики

    // ========== КОНСТРУКТОР ==========

    /**
     * Создает объект результатов анализа на основе статистики
     * @param fileName имя анализируемого файла
     * @param stats объект статистики с данными анализа
     */
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

    // ========== МЕТОДЫ ВЫВОДА РЕЗУЛЬТАТОВ ==========

    /**
     * Выводит подробные результаты анализа в консоль
     */
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
        System.out.println("13. Пиковая посещаемость в секунду: " + statistics.getPeakVisitsPerSecond());
        System.out.println("14. Максимальная посещаемость одним пользователем: " + statistics.getMaxVisitsPerUser());

        // Сайты-рефереры
        Set<String> referers = statistics.getRefererDomains();
        System.out.println("15. Количество сайтов-рефереров: " + referers.size());

        // Статистика операционных систем
        Map<String, Double> osStats = statistics.getOsStatistics();
        System.out.println("16. Статистика операционных систем:");
        if (!osStats.isEmpty()) {
            osStats.forEach((os, percentage) ->
                    System.out.printf("   - %s: %.2f%%%n", os, percentage * 100));
        } else {
            System.out.println("   Нет данных об операционных системах");
        }

        // Статистика браузеров
        Map<String, Double> browserStats = statistics.getBrowserStatistics();
        System.out.println("17. Статистика браузеров:");
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

    /**
     * Форматирует размер в байтах в читаемый вид (КБ, МБ, ГБ)
     * @param bytes размер в байтах
     * @return отформатированная строка с размером
     */
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

    // ========== ГЕТТЕРЫ ОСНОВНЫХ ПОЛЕЙ ==========

    /**
     * @return имя анализируемого файла
     */
    public String getFileName() { return fileName; }

    /**
     * @return общее количество обработанных строк
     */
    public int getTotalLines() { return totalLines; }

    /**
     * @return количество запросов от Googlebot
     */
    public int getGooglebotCount() { return googlebotCount; }

    /**
     * @return количество запросов от YandexBot
     */
    public int getYandexbotCount() { return yandexbotCount; }

    /**
     * @return процент запросов от Googlebot
     */
    public double getGooglebotPercentage() { return googlebotPercentage; }

    /**
     * @return процент запросов от YandexBot
     */
    public double getYandexbotPercentage() { return yandexbotPercentage; }

    /**
     * @return средний трафик в час
     */
    public double getAverageTrafficPerHour() { return averageTrafficPerHour; }

    /**
     * @return общий объем трафика
     */
    public long getTotalTraffic() { return totalTraffic; }

    /**
     * @return объект статистики
     */
    public Statistics getStatistics() { return statistics; }

    // ========== ГЕТТЕРЫ ДЛЯ СТАТИСТИКИ ПОСЕЩАЕМОСТИ ==========

    /**
     * @return среднее количество посещений в час
     */
    public double getAverageVisitsPerHour() { return statistics.getAverageVisitsPerHour(); }

    /**
     * @return среднее количество ошибочных запросов в час
     */
    public double getAverageErrorRequestsPerHour() { return statistics.getAverageErrorRequestsPerHour(); }

    /**
     * @return средняя посещаемость одним пользователем
     */
    public double getAverageVisitsPerUser() { return statistics.getAverageVisitsPerUser(); }

    /**
     * @return количество уникальных пользователей
     */
    public int getUniqueHumanUsers() { return statistics.getUniqueHumanUsers(); }

    /**
     * @return количество ошибочных запросов
     */
    public int getErrorRequests() { return statistics.getErrorRequests(); }

    /**
     * @return пиковая посещаемость в секунду
     */
    public int getPeakVisitsPerSecond() { return statistics.getPeakVisitsPerSecond(); }

    /**
     * @return максимальная посещаемость одним пользователем
     */
    public int getMaxVisitsPerUser() { return statistics.getMaxVisitsPerUser(); }

    // ========== ГЕТТЕРЫ ДЛЯ СТАТИСТИКИ СТРАНИЦ ==========

    /**
     * @return количество существующих страниц
     */
    public int getExistingPagesCount() { return statistics.getExistingPages().size(); }

    /**
     * @return количество несуществующих страниц
     */
    public int getNotFoundPagesCount() { return statistics.getNotFoundPages().size(); }

    /**
     * @return количество сайтов-рефереров
     */
    public int getRefererDomainsCount() { return statistics.getRefererDomains().size(); }

    // ========== ГЕТТЕРЫ ДЛЯ КОЛЛЕКЦИЙ ==========

    /**
     * @return множество существующих страниц
     */
    public Set<String> getExistingPages() { return statistics.getExistingPages(); }

    /**
     * @return множество несуществующих страниц
     */
    public Set<String> getNotFoundPages() { return statistics.getNotFoundPages(); }

    /**
     * @return множество доменов-рефереров
     */
    public Set<String> getRefererDomains() { return statistics.getRefererDomains(); }

    /**
     * @return статистика операционных систем в процентах
     */
    public Map<String, Double> getOsStatistics() { return statistics.getOsStatistics(); }

    /**
     * @return статистика браузеров в процентах
     */
    public Map<String, Double> getBrowserStatistics() { return statistics.getBrowserStatistics(); }

    /**
     * @return статистика операционных систем (количество)
     */
    public Map<String, Integer> getOsCounts() { return statistics.getOsCounts(); }

    /**
     * @return статистика браузеров (количество)
     */
    public Map<String, Integer> getBrowserCounts() { return statistics.getBrowserCounts(); }
}