// Представление HTTP методов в виде enum
public enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH, CONNECT, TRACE, UNKNOWN;

    public static HttpMethod fromString(String method) {
        if (method == null || method.isEmpty()) {
            return UNKNOWN;
        }

        try {
            return valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}