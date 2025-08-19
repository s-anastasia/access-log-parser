import java.util.Scanner;

// Класс, отвечающий за обработку пользовательского ввода путей к файлам
public class InputHandler {

    private final Scanner scanner; // Чтение пути к файлу, введенного в консоли
    public InputHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    // Запрашивает и возвращает путь к файлу
    public String getFilePath() {
        System.out.print("\nВведите путь к файлу (или 'exit' для выхода): ");
        return scanner.nextLine().trim();
    }

    // Проверяет, хочет ли пользователь выйти через ввод команды exit
    public boolean shouldExit(String input) {
        return "exit".equalsIgnoreCase(input);
    }
}
