// Класс, отвечающий за хранение и отображение результатов анализа содержимого файла
public class FileAnalysisResult {

    private final String fileName; // Название файла
    private final int totalLines; // Число строк
    private final int maxLength; // Макс длина строки
    private final int minLength; // Мин длина строки

    public FileAnalysisResult(String fileName, int totalLines, int maxLength, int minLength) {
        this.fileName = fileName;
        this.totalLines = totalLines;
        this.maxLength = maxLength;
        this.minLength = minLength;
    }

    // Вывод результатов
    public void printResults() {
        System.out.println("\nРезультаты анализа файла " + fileName + ":");
        System.out.println("1. Общее количество строк: " + totalLines);
        System.out.println("2. Длина самой длинной строки: " + maxLength);
        System.out.println("3. Длина самой короткой строки: " + minLength);
    }
}
