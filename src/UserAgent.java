// Класс, отвечающий за определение ОС и браузера из User-Agent строки
public class UserAgent {

    private final String osType; // Тип операционной системы
    private final String browserType; // Тип браузера
    private final String originalString;// Исходная строка User-Agent

    // Конструктор
    public UserAgent(String userAgentString) {
        this.originalString = userAgentString != null ? userAgentString : "";
        this.osType = extractOsType(this.originalString);
        this.browserType = extractBrowserType(this.originalString);
    }

    // Метод, определяющий ОС из User-Agent строки
    private String extractOsType(String userAgent) {
        if (userAgent.isEmpty()) return "Unknown";

        String ua = userAgent.toLowerCase();
        if (ua.contains("windows")) return "Windows";
        else if (ua.contains("mac")) return "macOS";
        else if (ua.contains("linux")) return "Linux";
        else if (ua.contains("android")) return "Android";
        else if (ua.contains("ios")) return "iOS";
        else return "Unknown";
    }

    // Метод, определяющий браузер из User-Agent строки
    private String extractBrowserType(String userAgent) {
        if (userAgent.isEmpty()) return "Other";

        String ua = userAgent.toLowerCase();
        if (ua.contains("edg/") || ua.contains("edge/")) return "Edge";
        else if (ua.contains("firefox")) return "Firefox";
        else if (ua.contains("chrome") && !ua.contains("chromium")) return "Chrome";
        else if (ua.contains("safari") && !ua.contains("chrome")) return "Safari";
        else if (ua.contains("opera") || ua.contains("opr/")) return "Opera";
        else return "Other";
    }

    // Метод, определяющий является ли User-Agent ботом
    public boolean isBot() {
        if (originalString.isEmpty()) return false;

        String ua = originalString.toLowerCase();
        return ua.contains("bot") ||
                ua.contains("googlebot") ||
                ua.contains("yandexbot") ||
                ua.contains("crawler") ||
                ua.contains("spider") ||
                ua.contains("crawler") ||
                ua.contains("indexer") ||
                ua.contains("scraper");
    }

    // Возвращение оригинальной строки User-Agent
    @Override
    public String toString() {
        return originalString;
    }

    // Геттеры
    public String getOsType() { return osType; }
    public String getBrowserType() { return browserType; }
}
