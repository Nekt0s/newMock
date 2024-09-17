import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;

public class HtmlTableToJson {
    public static void main(String[] args) {
        String url = "https://deadlocktracker.gg/heroes"; // Вставьте сюда ссылку на вашу страницу
        String outputFile = "dedlockheroes.json"; // Имя файла для сохранения

        try {
            // Парсим HTML-страницу
            Document doc = Jsoup.connect(url).get();

            // Находим таблицу (например, первую таблицу на странице)
            Element table = doc.select("table").first();
            Elements rows = table.select("tr");

            JsonArray tableJson = new JsonArray();

            // Извлекаем заголовки таблицы
            Elements headers = rows.get(0).select("th");
            JsonArray headersJson = new JsonArray();
            for (Element header : headers) {
                headersJson.add(header.text());
            }

            // Проходим по строкам таблицы и собираем данные
            for (int i = 1; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements columns = row.select("td");

                JsonObject rowJson = new JsonObject();
                for (int j = 0; j < columns.size(); j++) {
                    rowJson.addProperty(headersJson.get(j).getAsString(), columns.get(j).text());
                }
                tableJson.add(rowJson);
            }

            // Сохраняем JSON в файл
            saveJsonToFile(tableJson, outputFile);
            System.out.println("Данные успешно сохранены в файл: " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для записи JSON в файл
    private static void saveJsonToFile(JsonArray jsonData, String fileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(gson.toJson(jsonData));
        } catch (IOException e) {
            System.err.println("Ошибка записи файла: " + e.getMessage());
        }
    }
}
