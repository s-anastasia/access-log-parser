import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int correctFilesCount = 0; // Счетчик правильно указанных файлов

        while (true) {
            System.out.print("Введите путь к файлу (или 'exit' для выхода): ");
            String path = scanner.nextLine().trim();

            // Условие на команду выхода
            if (path.equalsIgnoreCase("exit")) {
                break;
            }

            // Создаем объект File для проверки
            File file = new File(path);
            // Переменная, проверяющая существование файла
            boolean fileExists = file.exists();
            // Переменная, проверяющая, что это файл, а не директория
            boolean isFile = file.isFile();

            // Проверка, что файл существует
            if (!fileExists) {
                System.out.println("Ошибка: файл не существует!");
                continue;
            }
            // Проверка, что введен путь к файлу, а не к директории
            if (!isFile) {
                System.out.println("Ошибка: указанный путь ведет к папке, а не к файлу!");
                continue;
            }

            // Увеличиваем счетчик правильно указанных файлов
            correctFilesCount++;
            System.out.println("Путь указан верно");
            System.out.println("Это файл номер " + correctFilesCount);
        }

        scanner.close();
        System.out.println("Программа завершена. Всего верно указанных файлов: " + correctFilesCount);
    }
}