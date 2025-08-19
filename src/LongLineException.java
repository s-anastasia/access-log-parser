// Пользовательское исключение для ошибки превышения максимально допустимой длины строки
public class LongLineException extends RuntimeException {
    public LongLineException(String fileName, int lineLength, int maxLength) {
        super(String.format(
                "Ошибка: файл %s содержит строку длиной %d символов (превышено максимальное значение %d)",
                fileName, lineLength, maxLength
        ));
    }
}