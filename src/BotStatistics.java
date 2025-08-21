// Класс для сбора статистики по поисковым ботам
public class BotStatistics {

    private int googlebotCount = 0; // Счетчик Googlebot
    private int yandexbotCount = 0; // Счетчик YandexBot
    private int totalLines = 0; // Общее количество строк

    // Обработка одной строки
    public void processLine(String line, LogLineParser parser) {
        totalLines++; // Увеличиваем общий счетчик строк
        // Извлекаем программу из User-Agent
        String userAgentProgram = parser.extractUserAgentProgram(line);
        // Сравниваем с целевыми программами
        if ("Googlebot".equals(userAgentProgram)) {
            googlebotCount++;
        } else if ("YandexBot".equals(userAgentProgram)) {
            yandexbotCount++;
        }
        // Остальные программы игнорируем
    }

    // Геттеры для получения статистики
    public int getGooglebotCount() {
        return googlebotCount;
    }
    public int getYandexbotCount() {
        return yandexbotCount;
    }
    public int getTotalLines() {
        return totalLines;
    }

    // Методы расчета процентов
    public double getGooglebotPercentage() {
        return totalLines > 0 ? (double) googlebotCount / totalLines * 100 : 0;
    }
    public double getYandexbotPercentage() {
        return totalLines > 0 ? (double) yandexbotCount / totalLines * 100 : 0;
    }
}
