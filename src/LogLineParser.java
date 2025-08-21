// Класс, отвечающий за парсинг строк лога
public class LogLineParser {

    // Метод для извлечения программы (бота) из User-Agent
    public String extractUserAgentProgram(String logLine) {
        try {
            // 1. Извлекаем User-Agent из кавычек
            String userAgent = extractUserAgentString(logLine);
            if (userAgent == null) return null;

            // 2. Извлекаем содержимое первых скобок
            String firstBrackets = extractFirstBracketsContent(userAgent);
            if (firstBrackets == null) return null;

            // 3. Извлекаем название программы
            return extractProgramFromBrackets(firstBrackets);

        } catch (Exception e) {
            return null; // При любой ошибке возвращаем null
        }
    }

    // 1: Извлечение User-Agent строки
    private String extractUserAgentString(String logLine) {
        int lastQuoteIndex = logLine.lastIndexOf("\""); // Находим последнюю кавычку
        if (lastQuoteIndex == -1) return null;

        int secondLastQuoteIndex = logLine.lastIndexOf("\"", lastQuoteIndex - 1); // Находим предпоследнюю кавычку
        if (secondLastQuoteIndex == -1) return null;

        return logLine.substring(secondLastQuoteIndex + 1, lastQuoteIndex).trim(); // Извлекаем между ними
    }

    // 2: Извлечение содержимого первых скобок
    private String extractFirstBracketsContent(String userAgent) {
        int openBracketIndex = userAgent.indexOf('('); // Первая открывающая скобка
        int closeBracketIndex = userAgent.indexOf(')', openBracketIndex + 1); // Соответствующая закрывающая

        if (openBracketIndex == -1 || closeBracketIndex == -1) return null;

        return userAgent.substring(openBracketIndex + 1, closeBracketIndex); // Содержимое между скобок
    }

    // 3: Разделение по точке с запятой и извлечение программы (бота)
    private String extractProgramFromBrackets(String firstBrackets) {
        String[] parts = firstBrackets.split(";"); // Разделяем по ;
        if (parts.length >= 2) {
            String fragment = parts[1].trim(); // Берем второй элемент

            // Отделяем часть до слэша
            int slashIndex = fragment.indexOf('/'); // Ищем слэш
            if (slashIndex != -1) {
                return fragment.substring(0, slashIndex).trim(); // Берем часть до слэша
            }
            return fragment;
        }
        return null;
    }
}
