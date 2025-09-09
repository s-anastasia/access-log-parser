import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

// Класс, отвечающий за накопление и расчет статистических данных
public class Statistics {

    private long totalTraffic; // Общий объем трафика в байтах
    private LocalDateTime minTime; // Самое раннее время запроса
    private LocalDateTime maxTime; // Самое позднее время запроса
    private int totalEntries; // Общее количество обработанных запросов
    private int googlebotCount; // Количество запросов от Googlebot
    private int yandexbotCount; // Количество запросов от YandexBot
    private Set<String> existingPages; // Множество существующих страниц (код 200)
    private Map<String, Integer> osCounts; // Количество операционных систем
    // Новые поля для дополнительной статистики
    private Set<String> notFoundPages; // Множество несуществующих страниц (код 404)
    private Map<String, Integer> browserCounts; // Количество браузеров

    // Вызов метода reset для инициализации поле
    public Statistics() {
        reset();
    }

 // Основной метод анализа
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

    //Метод, добавляющий одну запись в статистику (один объект класса LogEntry)
    public void addEntry(LogEntry entry) {
        // Валидируем размер данных перед добавлением
        long dataSize = entry.getResponseSize(); // изменено с int на long
        if (dataSize < 0) {
            System.out.println("⚠️  Пропускаем запись с отрицательным размером данных: " + dataSize);
            return;
        }
     // Обновление счетчиков
        totalEntries++;
        totalTraffic += dataSize;
     // Обновление временного диапазона
        LocalDateTime entryTime = entry.getTime();
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

        // Анализ User-Agent для обнаружения ботов (регистронезависимый поиск)
        String userAgent = entry.getAgent().toString().toLowerCase();
        if (userAgent.contains("googlebot")) {
            googlebotCount++;
        } else if (userAgent.contains("yandexbot")) {
            yandexbotCount++;
        }

        // Добавляем существующую страницу (код ответа 200)
        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }

        // Добавляем несуществующую страницу (код ответа 404)
        if (entry.getResponseCode() == 404) {
            notFoundPages.add(entry.getPath());
        }

        // Обновляем статистику операционных систем
        String osType = entry.getAgent().getOsType();
        osCounts.put(osType, osCounts.getOrDefault(osType, 0) + 1);

        // Обновляем статистику браузеров
        String browserType = entry.getAgent().getBrowserType();
        browserCounts.put(browserType, browserCounts.getOrDefault(browserType, 0) + 1);
    }

    // Метод расчета средней скорости трафика в байтах/час
    public double getTrafficRate() {
        if (minTime == null || maxTime == null || totalEntries == 0) {
            return 0.0;
        }

        // Убеждаемся, что minTime раньше maxTime
        LocalDateTime startTime = minTime.isBefore(maxTime) ? minTime : maxTime;
        LocalDateTime endTime = minTime.isBefore(maxTime) ? maxTime : minTime;

        long hoursBetween = ChronoUnit.HOURS.between(startTime, endTime);

        if (hoursBetween <= 0) {
            return totalTraffic; // Если все записи в пределах одного часа
        }

        return (double) totalTraffic / hoursBetween;
    }

    // Сброс всей статистики к начальным значениям
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
    }

    // Метод, возвращающий список всех существующих страниц сайта (код 200)
    public Set<String> getExistingPages() {
        return new HashSet<>(existingPages);
    }

    // Новый метод: возвращает список всех несуществующих страниц сайта (код 404)
    public Set<String> getNotFoundPages() {
        return new HashSet<>(notFoundPages); // Возвращаем копию для безопасности
    }

    // Метод, возвращающий статистику операционных систем (доли от 0 до 1)
    public Map<String, Double> getOsStatistics() {
        Map<String, Double> osStatistics = new HashMap<>();

        if (totalEntries == 0) {
            return osStatistics; // Пустая карта, если нет записей
        }

        // Рассчитываем долю для каждой операционной системы
        for (Map.Entry<String, Integer> entry : osCounts.entrySet()) {
            double percentage = (double) entry.getValue() / totalEntries;
            osStatistics.put(entry.getKey(), percentage);
        }

        return osStatistics;
    }

    // Новый метод: возвращает статистику браузеров (доли от 0 до 1)
    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> browserStatistics = new HashMap<>();

        if (totalEntries == 0) {
            return browserStatistics; // Пустая карта, если нет записей
        }

        // Рассчитываем долю для каждого браузера
        for (Map.Entry<String, Integer> entry : browserCounts.entrySet()) {
            double percentage = (double) entry.getValue() / totalEntries;
            browserStatistics.put(entry.getKey(), percentage);
        }

        return browserStatistics;
    }


    // Дополнительные методы для получения сырых данных
    public Map<String, Integer> getOsCounts() {
        return new HashMap<>(osCounts);
    }

    public Map<String, Integer> getBrowserCounts() {
        return new HashMap<>(browserCounts);
    }

    public int getNotFoundPagesCount() {
        return notFoundPages.size();
    }

    // Геттеры
    public int getTotalEntries() { return totalEntries; }
    public int getGooglebotCount() { return googlebotCount; }
    public int getYandexbotCount() { return yandexbotCount; }
    public double getGooglebotPercentage() {
        return totalEntries > 0 ? (double) googlebotCount / totalEntries * 100 : 0;
    }
    public double getYandexbotPercentage() {
        return totalEntries > 0 ? (double) yandexbotCount / totalEntries * 100 : 0;
    }
    public long getTotalTraffic() { return totalTraffic; }
}
