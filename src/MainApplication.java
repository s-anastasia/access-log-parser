import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

// Основной класс приложения
public class MainApplication {

    private final FileInputService fileInputService; // Ввод и валидация пути к файлу
    private final FileContentReader fileContentReader; // Построчное чтение файла
    private final Statistics statistics; // Анализ и подсчет статистики содержимого файла
    private int processedFilesCount; // Счетчик файлов

    public MainApplication(FileInputService fileInputService,
                           FileContentReader fileContentReader,
                           Statistics statistics) {
        this.fileInputService = fileInputService;
        this.fileContentReader = fileContentReader;
        this.statistics = statistics;
        this.processedFilesCount = 0;
    }

    // Метод запуска основного цикла программы
    public void start() {
        try {
            while (true) {
                // Получаем валидный файл или команду выхода через класс FileInputService
                File file = fileInputService.getValidFileOrExit();
                if (file == null) break;
                // Обрабатываем один валидный файл
                processFile(file);
                // Сбрасываем статистику для следующего файла
                statistics.reset();
            }
        } finally {
            // При выходе выводим сообщение
            System.out.printf("%nПрограмма завершена. Всего обработано файлов: %d%n", processedFilesCount);
        }
    }

    // Метод обработки одного файла
    private void processFile(File file) {
        // Увеличиваем и выводим Счетчик файлов
        processedFilesCount++;
        System.out.printf("%nПуть указан верно%nЭто файл номер %d%n", processedFilesCount);
        try {
            // Читаем построчно файл через класс fileContentReader
            List<String> lines = fileContentReader.readLines(file);
            // Анализируем строки через класс LogAnalyzerService
            FileAnalysisResult result = statistics.analyzeFile(file.getName(), lines);
            // Выводим результаты через класс FileAnalysisResult
            result.printResults();
            // Обрабатываем возможные исключения
        } catch (LongLineException e) { // Пользовательский класс исключения для случая превышения макс допустимой длины строки
            System.out.println("❌ " + e.getMessage());
        } catch (IOException e) {
            System.out.println("❌ Ошибка при обработке файла: " + e.getMessage());
        }
    }

    // Точка входа в программу
    public static void main(String[] args) {
        // Создаем экземпляр Scanner для ввода путей к файлам
        try (Scanner scanner = new Scanner(System.in)) {
            // Инициализируем все компоненты
            MainApplication app = new MainApplication(
                    new FileInputService(scanner),
                    new FileContentReader(),
                    new Statistics()
            );
            // Запускаем программу
            app.start();
        }
    }
}
