import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

// Основной класс приложения
public class MainApplication {

    private final InputHandler inputHandler; // Обработчик пользовательского ввода путей к файлам
    private final FileValidator fileValidator; // Валидатор путей к файлам
    private final FileContentReader fileContentReader; // Чтение файлов, проверка на максимально допустимую длину строки
    private final LineAnalyzer lineAnalyzer; // Анализатор содержимого файлов
    private int processedFilesCount; // Счетчик обработанных файлов

    public MainApplication(InputHandler inputHandler, FileValidator fileValidator,
                           FileContentReader fileContentReader, LineAnalyzer lineAnalyzer)  {
        this.inputHandler = inputHandler;
        this.fileValidator = fileValidator;
        this.fileContentReader = fileContentReader;
        this.lineAnalyzer = lineAnalyzer;
        this.processedFilesCount = 0;
    }

    // Метод запуска основного цикла программы
    public void start() {
        try {
            while (true) {
                // 1. Получаем путь к файлу через inputHandler
                String path = inputHandler.getFilePath();
                // 2. Проверяем, не хочет ли пользователь выйти (команда "exit")
                if (inputHandler.shouldExit(path)) break;
                // 3. Создаем объект File
                File file = new File(path);
                // 4. Валидируем путь к файлу
                if (!fileValidator.isValid(file)) continue;
                // 5. Обрабатываем файл, если путь к нему валиден
                processFile(file);
            }
        } finally {
            // 6. Выводим статистику по числу обработанных файлов при завершении программы
            System.out.printf("%nПрограмма завершена. Всего обработано файлов: %d%n", processedFilesCount);
        }
    }

    // Метод обработки одного файла
    private void processFile(File file) {
        // 1. Увеличиваем и выводим Счетчик обработанных файлов
        processedFilesCount++;
        System.out.printf("%nПуть указан верно%nЭто файл номер %d%n", processedFilesCount);
        // 2. Анализируем содержимое файла и выбрасываем исключения, в случае ошибок
        try {
            // Читаем строки из файла через класс fileContentReader
            List<String> lines = fileContentReader.readLines(file);
            // Анализируем строки через класс lineAnalyzer
            FileAnalysisResult result = lineAnalyzer.analyze(file.getName(), lines);
            // Выводим результаты через класс FileAnalysisResult
            result.printResults();
            // Обрабатываем возможные исключения
        } catch (LongLineException e) { // Пользовательский класс исключения для случая превышения макс допустимой длины строки
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка при обработке файла: " + e.getMessage());
        }
    }

    // Точка входа в программу
    public static void main(String[] args) {
        // Создаем экземпляр Scanner для ввода путей к файлам
        try (Scanner scanner = new Scanner(System.in)) {
            // Инициализируем все компоненты
            MainApplication app = new MainApplication(
                    new InputHandler(scanner),
                    new FileValidator(),
                    new FileContentReader(),
                    new LineAnalyzer()
            );
            // Запускаем программу
            app.start();
        }
    }
}
