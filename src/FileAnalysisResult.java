// Класс, отвечающий за хранение и отображение результатов анализа содержимого файла
public class FileAnalysisResult {

    private final String fileName;
    private final int totalLines;
    private final int googlebotCount;
    private final int yandexbotCount;
    private final double googlebotPercentage;
    private final double yandexbotPercentage;

    public FileAnalysisResult(String fileName, int totalLines,
                              int googlebotCount, int yandexbotCount,
                              double googlebotPercentage, double yandexbotPercentage) {
        this.fileName = fileName;
        this.totalLines = totalLines;
        this.googlebotCount = googlebotCount;
        this.yandexbotCount = yandexbotCount;
        this.googlebotPercentage = googlebotPercentage;
        this.yandexbotPercentage = yandexbotPercentage;
    }

    public void printResults() {
        System.out.println("\nРезультаты анализа файла " + fileName + ":");
        System.out.println("1. Общее количество строк: " + totalLines);
        System.out.printf("2. Запросов от Googlebot: %d (%.2f%%)%n",
                googlebotCount, googlebotPercentage);
        System.out.printf("3. Запросов от YandexBot: %d (%.2f%%)%n",
                yandexbotCount, yandexbotPercentage);
    }
}
