import java.io.File;
import java.util.Scanner;

public class FileInputService {

    private final Scanner scanner;

    public FileInputService(Scanner scanner) {
        this.scanner = scanner;
    }

    public File getValidFileOrExit() {
        while (true) {
            System.out.print("\nВведите путь к файлу (или 'exit' для выхода): ");
            String input = scanner.nextLine().trim();

            // Проверка на выход из программы
            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Завершение работы программы...");
                return null;
            }

                // Проверка на пустой ввод
                if (input.isEmpty()) {
                    System.out.println("❌ Ошибка: путь не может быть пустым!");
                    continue;
                }

                File file = new File(input);

                // Валидация пути к файлу
                if (!file.exists()) {
                    System.out.println("❌ Ошибка: файл не существует!");
                    continue;
                }
                if (!file.isFile()) {
                    System.out.println("❌ Ошибка: указанный путь ведет к папке, а не к файлу!");
                    continue;
                }

            return file;
        }
        }
    }
