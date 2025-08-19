import java.io.File;

// Класс, отвечающий за валидацию введенных путей к файлам
public class FileValidator {

    // Метод валидации введенного пути к файлу
    public boolean isValid(File file) {
        // Проверяем существует ли файл и если нет, то выводим ошибку
        if (!file.exists()) {
            System.out.println("Ошибка: файл не существует!");
            return false;
        }
        // Проверяем ведет ли путь к файлу, а не к директории
        if (!file.isFile()) {
            System.out.println("Ошибка: указанный путь ведет к папке, а не к файлу!");
            return false;
        }
        return true;
    }
}
