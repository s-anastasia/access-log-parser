import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Класс, отвечающий за накопление и расчет статистических данных
public class Statistics {

    private long totalTraffic; // Общий объем трафика в байтах
    private LocalDateTime minTime; // Самое раннее время запроса
    private LocalDateTime maxTime; // Самое позднее время запроса
    private int totalEntries; // Общее количество обработанных запросов
    private int googlebotCount; // Количество запросов от Googlebot
    private int yandexbotCount; // Количество запросов от YandexBot

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