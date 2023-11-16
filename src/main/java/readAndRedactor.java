import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class readAndRedactor {
    public static void main(String[] args) {

        String inputFilePath = "src/log-2014-08-04__short.txt";
        String outputFilePath = "src/newFile.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {

                String newLine = line.replaceAll(" ", "и").replaceAll("-",";");

                writer.write(newLine);
                writer.newLine();
            }

            System.out.println("Пробелы успешно заменены на дефисы.");

        } catch (IOException e) {
            System.out.println("Ошибка при обработке файла: " + e.getMessage());
        }
    }
}
