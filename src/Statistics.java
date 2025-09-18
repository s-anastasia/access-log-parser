import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

// Класс, отвечающий за накопление и расчет статистических данных
public class Statistics {

    // ========== ПОЛЯ ДЛЯ СТАТИСТИКИ ==========

    // Основная статистика
    private long totalTraffic; // Общий объем трафика в байтах
    private LocalDateTime minTime; // Самое раннее время запроса
    private LocalDateTime maxTime; // Самое позднее время запроса
    private int totalEntries; // Общее количество обработанных запросов

    // Статистика ботов
    private int googlebotCount; // Количество запросов от Googlebot
    private int yandexbotCount; // Количество запросов от YandexBot

    // Статистика страниц
    private Set<String> existingPages; // Множество существующих страниц (код 200)
    private Set<String> notFoundPages; // Множество несуществующих страниц (код 404)

    // Статистика пользовательских агентов
    private Map<String, Integer> osCounts; // Количество операционных систем
    private Map<String, Integer> browserCounts; // Количество браузеров

    // Дополнительная статистика
    private int humanVisits; // Количество посещений реальными пользователями (не ботами)
    private int errorRequests; // Количество ошибочных запросов (4xx и 5xx)
    private Set<String> uniqueHumanIPs; // Уникальные IP-адреса реальных пользователей
    private Map<Long, Integer> visitsPerSecond; // Количество посещений в каждую секунду
    private Set<String> refererDomains; // Домены рефереров
    private Map<String, Integer> visitsPerUser; // Количество посещений на пользователя

    // ========== КОНСТРУКТОР ==========

    /**
     * Конструктор класса Statistics, инициализирует все поля
     */
    public Statistics() {
        reset();
    }

    // ========== ОСНОВНЫЕ МЕТОДЫ АНАЛИЗА ==========

    /**
     * Анализирует файл логов и обрабатывает каждую строку
     * @param fileName имя файла для анализа
     * @param lines список строк лога
     * @return результат анализа файла
     */
    public FileAnalysisResult analyzeFile(String fileName, List<String> lines) {
        System.out.println("🔍 Анализируем файл...");

        int processedLines = 0;
        int errorLines = 0;

        for (String line : lines) {
            try {
                LogEntry entry = new LogEntry(line);
                addEntry(entry);
                processedLines++;

            } catch (IllegalArgumentException e) {
                System.out.println("⚠️  Неверный формат строки: " + e.getMessage());
                errorLines++;
            } catch (Exception e) {
                System.out.println("⚠️  Ошибка обработки строки: " + e.getMessage());
                errorLines++;
            }
        }

        System.out.printf("✓ Обработано строк: %d, ошибок: %d%n", processedLines, errorLines);
        return new FileAnalysisResult(fileName, this);
    }

    /**
     * Добавляет одну запись лога в статистику
     * @param entry объект LogEntry для добавления
     */
    public void addEntry(LogEntry entry) {
        // Валидируем размер данных перед добавлением
        long dataSize = entry.getResponseSize();
        if (dataSize < 0) {
            System.out.println("⚠️  Пропускаем запись с отрицательным размером данных: " + dataSize);
            return;
        }

        // Обновление счетчиков
        totalEntries++;
        totalTraffic += dataSize;

        // Обновление временного диапазона
        updateTimeRange(entry.getTime());

        // Анализ User-Agent
        analyzeUserAgent(entry);

        // Анализ страниц
        analyzePages(entry);

        // Анализ ошибок
        analyzeErrors(entry);

        // Анализ пользователей
        analyzeUsers(entry);

        // Анализ рефереров
        analyzeReferers(entry);
    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ АНАЛИЗА ==========

    /**
     * Обновляет временной диапазон статистики
     * @param entryTime время текущей записи
     */
    private void updateTimeRange(LocalDateTime entryTime) {
        if (minTime == null) {
            minTime = entryTime;
            maxTime = entryTime;
        } else {
            if (entryTime.isBefore(minTime)) {
                minTime = entryTime;
            }
            if (entryTime.isAfter(maxTime)) {
                maxTime = entryTime;
            }
        }
    }

    /**
     * Анализирует User-Agent запроса
     * @param entry запись лога для анализа
     */
    private void analyzeUserAgent(LogEntry entry) {
        // Проверяем, является ли запрос от бота
        boolean isBot = entry.getAgent().isBot();
        String userAgent = entry.getAgent().toString().toLowerCase();

        // Подсчет ботов
        if (userAgent.contains("googlebot")) {
            googlebotCount++;
        } else if (userAgent.contains("yandexbot")) {
            yandexbotCount++;
        }

        // Статистика ОС
        String osType = entry.getAgent().getOsType();
        osCounts.put(osType, osCounts.getOrDefault(osType, 0) + 1);

        // Статистика браузеров
        String browserType = entry.getAgent().getBrowserType();
        browserCounts.put(browserType, browserCounts.getOrDefault(browserType, 0) + 1);
    }

    /**
     * Анализирует страницы и коды ответов
     * @param entry запись лога для анализа
     */
    private void analyzePages(LogEntry entry) {
        // Добавляем существующую страницу (код ответа 200)
        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }

        // Добавляем несуществующую страницу (код ответа 404)
        if (entry.getResponseCode() == 404) {
            notFoundPages.add(entry.getPath());
        }
    }

    /**
     * Анализирует ошибочные запросы
     * @param entry запись лога для анализа
     */
    private void analyzeErrors(LogEntry entry) {
        // Подсчет ошибочных запросов (4xx и 5xx)
        if (entry.getResponseCode() >= 400 && entry.getResponseCode() < 600) {
            errorRequests++;
        }
    }

    /**
     * Анализирует пользовательские посещения
     * @param entry запись лога для анализа
     */
    private void analyzeUsers(LogEntry entry) {
        boolean isBot = entry.getAgent().isBot();

        // Подсчет посещений реальными пользователями и уникальных IP
        if (!isBot) {
            humanVisits++;
            uniqueHumanIPs.add(entry.getIpAddr());

            // Пиковая посещаемость в секунду
            long secondTimestamp = entry.getTime().toEpochSecond(java.time.ZoneOffset.UTC);
            visitsPerSecond.put(secondTimestamp, visitsPerSecond.getOrDefault(secondTimestamp, 0) + 1);

            // Статистика по пользователям
            String ip = entry.getIpAddr();
            visitsPerUser.put(ip, visitsPerUser.getOrDefault(ip, 0) + 1);
        }
    }

    /**
     * Анализирует рефереры
     * @param entry запись лога для анализа
     */
    private void analyzeReferers(LogEntry entry) {
        // Сбор доменов рефереров
        if (entry.getReferer() != null && !entry.getReferer().isEmpty() && !"-".equals(entry.getReferer())) {
            try {
                URI uri = new URI(entry.getReferer());
                String domain = uri.getHost();
                if (domain != null && !domain.isEmpty()) {
                    refererDomains.add(domain);
                }
            } catch (Exception e) {
                System.out.println("⚠️  Неверный формат referer: " + entry.getReferer());
            }
        }
    }

    // ========== МЕТОДЫ РАСЧЕТА СТАТИСТИКИ ==========

    /**
     * Рассчитывает среднюю скорость трафика в байтах/час
     * @return средняя скорость трафика
     */
    public double getTrafficRate() {
        if (minTime == null || maxTime == null || totalEntries == 0) {
            return 0.0;
        }

        LocalDateTime startTime = minTime.isBefore(maxTime) ? minTime : maxTime;
        LocalDateTime endTime = minTime.isBefore(maxTime) ? maxTime : minTime;

        long hoursBetween = ChronoUnit.HOURS.between(startTime, endTime);
        return hoursBetween <= 0 ? totalTraffic : (double) totalTraffic / hoursBetween;
    }

    /**
     * Рассчитывает среднее количество посещений сайта за час
     * @return среднее количество посещений в час
     */
    public double getAverageVisitsPerHour() {
        if (minTime == null || maxTime == null || humanVisits == 0) {
            return 0.0;
        }

        LocalDateTime startTime = minTime.isBefore(maxTime) ? minTime : maxTime;
        LocalDateTime endTime = minTime.isBefore(maxTime) ? maxTime : minTime;

        long hoursBetween = ChronoUnit.HOURS.between(startTime, endTime);
        return hoursBetween <= 0 ? humanVisits : (double) humanVisits / hoursBetween;
    }

    /**
     * Рассчитывает среднее количество ошибочных запросов в час
     * @return среднее количество ошибок в час
     */
    public double getAverageErrorRequestsPerHour() {
        if (minTime == null || maxTime == null || errorRequests == 0) {
            return 0.0;
        }

        LocalDateTime startTime = minTime.isBefore(maxTime) ? minTime : maxTime;
        LocalDateTime endTime = minTime.isBefore(maxTime) ? maxTime : minTime;

        long hoursBetween = ChronoUnit.HOURS.between(startTime, endTime);
        return hoursBetween <= 0 ? errorRequests : (double) errorRequests / hoursBetween;
    }

    /**
     * Рассчитывает среднее количество посещений на пользователя
     * @return среднее количество посещений на пользователя
     */
    public double getAverageVisitsPerUser() {
        return (humanVisits == 0 || uniqueHumanIPs.isEmpty()) ?
                0.0 : (double) humanVisits / uniqueHumanIPs.size();
    }

    /**
     * Рассчитывает пиковую посещаемость сайта в секунду
     * @return максимальное количество посещений в секунду
     */
    private int calculatePeakVisitsPerSecond() {
        if (visitsPerSecond.isEmpty()) {
            return 0;
        }
        return Collections.max(visitsPerSecond.values());
    }

    /**
     * Рассчитывает максимальную посещаемость одним пользователем
     * @return максимальное количество посещений одним пользователем
     */
    private int calculateMaxVisitsPerUser() {
        if (visitsPerUser.isEmpty()) {
            return 0;
        }
        return Collections.max(visitsPerUser.values());
    }

    /**
     * Возвращает статистику операционных систем в процентах
     * @return карта с долями операционных систем (0-1)
     */
    public Map<String, Double> getOsStatistics() {
        Map<String, Double> osStatistics = new HashMap<>();
        if (totalEntries == 0) return osStatistics;

        for (Map.Entry<String, Integer> entry : osCounts.entrySet()) {
            double percentage = (double) entry.getValue() / totalEntries;
            osStatistics.put(entry.getKey(), percentage);
        }
        return osStatistics;
    }

    /**
     * Возвращает статистику браузеров в процентах
     * @return карта с долями браузеров (0-1)
     */
    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> browserStatistics = new HashMap<>();
        if (totalEntries == 0) return browserStatistics;

        for (Map.Entry<String, Integer> entry : browserCounts.entrySet()) {
            double percentage = (double) entry.getValue() / totalEntries;
            browserStatistics.put(entry.getKey(), percentage);
        }
        return browserStatistics;
    }

    // ========== МЕТОД СБРОСА ==========

    /**
     * Сбрасывает всю статистику к начальным значениям
     */
    public void reset() {
        totalTraffic = 0;
        totalEntries = 0;
        googlebotCount = 0;
        yandexbotCount = 0;
        minTime = null;
        maxTime = null;
        existingPages = new HashSet<>();
        osCounts = new HashMap<>();
        notFoundPages = new HashSet<>();
        browserCounts = new HashMap<>();
        humanVisits = 0;
        errorRequests = 0;
        uniqueHumanIPs = new HashSet<>();
        visitsPerSecond = new HashMap<>();
        refererDomains = new HashSet<>();
        visitsPerUser = new HashMap<>();
    }

    // ========== ГЕТТЕРЫ ==========

    /**
     * @return общее количество обработанных запросов
     */
    public int getTotalEntries() { return totalEntries; }

    /**
     * @return общий объем трафика в байтах
     */
    public long getTotalTraffic() { return totalTraffic; }

    /**
     * @return самое раннее время запроса
     */
    public LocalDateTime getMinTime() { return minTime; }

    /**
     * @return самое позднее время запроса
     */
    public LocalDateTime getMaxTime() { return maxTime; }

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
    public double getGooglebotPercentage() {
        return totalEntries > 0 ? (double) googlebotCount / totalEntries * 100 : 0;
    }

    /**
     * @return процент запросов от YandexBot
     */
    public double getYandexbotPercentage() {
        return totalEntries > 0 ? (double) yandexbotCount / totalEntries * 100 : 0;
    }

    /**
     * @return множество существующих страниц (код 200)
     */
    public Set<String> getExistingPages() { return new HashSet<>(existingPages); }

    /**
     * @return множество несуществующих страниц (код 404)
     */
    public Set<String> getNotFoundPages() { return new HashSet<>(notFoundPages); }

    /**
     * @return количество несуществующих страниц
     */
    public int getNotFoundPagesCount() { return notFoundPages.size(); }

    /**
     * @return количество существующих страниц
     */
    public int getExistingPagesCount() { return existingPages.size(); }

    /**
     * @return статистика операционных систем (количество)
     */
    public Map<String, Integer> getOsCounts() { return new HashMap<>(osCounts); }

    /**
     * @return статистика браузеров (количество)
     */
    public Map<String, Integer> getBrowserCounts() { return new HashMap<>(browserCounts); }

    /**
     * @return количество посещений реальными пользователями
     */
    public int getHumanVisits() { return humanVisits; }

    /**
     * @return количество ошибочных запросов
     */
    public int getErrorRequests() { return errorRequests; }

    /**
     * @return количество уникальных пользователей
     */
    public int getUniqueHumanUsers() { return uniqueHumanIPs.size(); }

    /**
     * @return пиковая посещаемость в секунду
     */
    public int getPeakVisitsPerSecond() { return calculatePeakVisitsPerSecond(); }

    /**
     * @return максимальная посещаемость одним пользователем
     */
    public int getMaxVisitsPerUser() { return calculateMaxVisitsPerUser(); }

    /**
     * @return множество доменов-рефереров
     */
    public Set<String> getRefererDomains() { return new HashSet<>(refererDomains); }

    /**
     * @return количество доменов-рефереров
     */
    public int getRefererDomainsCount() { return refererDomains.size(); }

    /**
     * @return процент ошибочных запросов
     */
    public double getErrorRate() {
        return totalEntries > 0 ? (double) errorRequests / totalEntries * 100 : 0;
    }

    /**
     * @return процент посещений реальными пользователями
     */
    public double getHumanVisitPercentage() {
        return totalEntries > 0 ? (double) humanVisits / totalEntries * 100 : 0;
    }

    /**
     * @return длительность периода анализа в часах
     */
    public long getTimeRangeInHours() {
        if (minTime == null || maxTime == null) return 0;
        LocalDateTime startTime = minTime.isBefore(maxTime) ? minTime : maxTime;
        LocalDateTime endTime = minTime.isBefore(maxTime) ? maxTime : minTime;
        return ChronoUnit.HOURS.between(startTime, endTime);
    }
}