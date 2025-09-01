import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Класс, отвечающий за разбор строки лога на составляющие
public class LogEntry {

    private final String ipAddr; // IP-адрес клиента
    private final LocalDateTime time; // Временная метка запроса
    private final HttpMethod method; // HTTP метод
    private final String path; // Путь запроса
    private final int responseCode; // Код ответа
    private final int responseSize; // Размер ответа в байтах
    private final String referer; // URL источника запроса
    private final UserAgent agent; // Информация о браузере/устройстве

    // Конструктор для парсинга временных меток
    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("dd/MMM/yyyy:HH:mm:ss Z")
            .toFormatter(Locale.ENGLISH);

    // Регулярное выражение для парсинга логов
    private static final String LOG_REGEX =
            "^([\\d.]+) \\S+ \\S+ \\[(.*?)\\] \"(\\w+) (.*?) HTTP/.*?\" (\\d+) (-|\\d+) \"([^\"]*)\" \"([^\"]*)\"$";

    // Конструктор для принятия и разбиения строки на составляющие
    public LogEntry(String logLine) {
        Pattern pattern = Pattern.compile(LOG_REGEX);
        Matcher matcher = pattern.matcher(logLine);

        if (matcher.find() && matcher.groupCount() >= 8) {
            this.ipAddr = matcher.group(1);
            this.time = parseDateTime(matcher.group(2));
            this.method = HttpMethod.fromString(matcher.group(3));
            this.path = matcher.group(4);
            this.responseCode = Integer.parseInt(matcher.group(5));
            this.responseSize = parseResponseSize(matcher.group(6));
            this.referer = "-".equals(matcher.group(7)) ? null : matcher.group(7);
            this.agent = new UserAgent(matcher.group(8));
        } else {
            throw new IllegalArgumentException("Неверный формат лог-строки: " + logLine);
        }
    }

    // Парсинг временной метки
    private LocalDateTime parseDateTime(String dateTimeString) {
        try {
            return LocalDateTime.parse(dateTimeString, DATE_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Неверный формат даты: " + dateTimeString);
        }
    }

    // Парсинг размера ответа
    private int parseResponseSize(String responseSizeStr) { // изменено с int на long
        // Если размер не указан или указан как "-"
        if (responseSizeStr == null || responseSizeStr.isEmpty() || "-".equals(responseSizeStr)) {
            return 0;
        }

        try {
            long size = Long.parseLong(responseSizeStr); // изменено на Long.parseLong
            return Math.max((int) size, 0); // Гарантируем неотрицательное значение
        } catch (NumberFormatException e) {
            System.out.println("⚠️  Неверный формат размера данных: " + responseSizeStr + ", заменяем на 0");
            return 0;
        }
    }

    // Геттеры для свойств (полей) класса LogEntry
    public String getIpAddr() { return ipAddr; }
    public LocalDateTime getTime() { return time; }
    public HttpMethod getMethod() { return method; }
    public String getPath() { return path; }
    public int getResponseCode() { return responseCode; }
    public long getResponseSize() { return responseSize; } // изменено с int на long
    public String getReferer() { return referer; }
    public UserAgent getAgent() { return agent; }
}