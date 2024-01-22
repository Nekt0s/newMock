package org.example.sber;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
class YamlFileComparatorController {

    private final YamlFileComparatorV2 fileComparator;

    public YamlFileComparatorController() {
        this.fileComparator = new YamlFileComparatorV2();
    }

    private String generateHtmlResponse(List<List<String>> allFileLines, List<MultipartFile> files) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><body style=\"background-color: #bbbbbb; color: #343a40; padding: 50px;\">");

        // Добавление блоков для каждой пары файлов в HTML
        for (int i = 0; i < allFileLines.size(); i += 2) {
            List<String> file1Lines = allFileLines.get(i);
            List<String> file2Lines = allFileLines.get(i + 1);

            MultipartFile file1 = files.get(i);
            MultipartFile file2 = files.get(i + 1);

            String fileName1 = file1.getOriginalFilename();
            String fileName2 = file2.getOriginalFilename();

            htmlBuilder.append("<div style=\"background-color: #ffffff; color: #000000; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); margin-bottom: 20px;\">");
            htmlBuilder.append("<h1 style=\"color: #007bff; font-size: larger;\">").append("Сравнение: ").append(fileName1).append(" и ").append(fileName2).append("</h1>");
            htmlBuilder.append("<pre>").append(formatAsTable(file1Lines, file2Lines)).append("</pre>");
            htmlBuilder.append("<form action=\"/\" method=\"get\">");
            htmlBuilder.append("<button type=\"submit\" style=\"background-color: black; color: white;\">Вернуться на главную страницу</button>");
            htmlBuilder.append("</form>");
            htmlBuilder.append("<button onclick=\"copyToClipboard('table").append(i / 2).append("')\" style=\"background-color: #007bff; color: white; margin-top: 10px;\">Копировать таблицу</button>");
            htmlBuilder.append("<textarea id=\"table").append(i / 2).append("\" style=\"position: absolute; left: -9999px;\">").append(formatAsTable(file1Lines, file2Lines)).append("</textarea>");
            htmlBuilder.append("</div>");
        }

        // Добавление скрипта для копирования в буфер обмена
        htmlBuilder.append("<script>");
        htmlBuilder.append("function copyToClipboard(elementId) {");
        htmlBuilder.append("  var textarea = document.getElementById(elementId);");
        htmlBuilder.append("  textarea.select();");
        htmlBuilder.append("  document.execCommand('copy');");
        htmlBuilder.append("}");
        htmlBuilder.append("</script>");

        htmlBuilder.append("</body></html>");

        return htmlBuilder.toString();
    }





    @PostMapping("/compareconfig")
    public String handleFileUpload(@RequestParam("files") List<MultipartFile> files) throws IOException {
        if (files == null || files.size() < 2) {
            return "Должно быть загружено как минимум два файла для сравнения.";
        }

        // Создание списка для хранения строк из всех файлов
        List<List<String>> allFileLines = new ArrayList<>();

        // Цикл по всем загруженным файлам
        for (int i = 0; i < files.size(); i++) {
            MultipartFile currentFile = files.get(i);
            String fileContent = new String(currentFile.getBytes());
            List<String> currentFileLines = fileComparator.extractLinesWithKeywords(fileContent, "agent-limits-mem", "agent-requests-mem", "agent-limits-cpu", "agent-requests-cpu", "limits", "cpu", "memory", "request");
            allFileLines.add(currentFileLines);


        }

        // Ваш код для обработки всех файлов и создания HTML-ответа
        return generateHtmlResponse(allFileLines, files);
    }





    // Форматирование строк в виде HTML-таблицы
    private String formatAsTable(List<String> file1Lines, List<String> file2Lines) {
        Set<String> uniqueNames = new HashSet<>();
        Set<String> uniqueWords = new HashSet<>();

        // Строитель таблицы
        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append("<table border=\"1\" style=\"border-collapse: collapse; font-size: larger;\">");  // Увеличьте размер шрифта здесь

        // Добавление заголовка таблицы
        tableBuilder.append("<tr>");
        tableBuilder.append("<th>Переменная</th>");
        tableBuilder.append("<th>").append("Значение ").append(file1Lines.get(0)).append("</th>");
        tableBuilder.append("<th>").append("Значение ").append(file2Lines.get(0)).append("</th>");
        tableBuilder.append("</tr>");

        // Итерация по строкам из обоих файлов
        for (int i = 1; i < Math.max(file1Lines.size(), file2Lines.size()); i++) {
            String line1 = i < file1Lines.size() ? file1Lines.get(i) : "";
            String line2 = i < file2Lines.size() ? file2Lines.get(i) : "";

            // Если строки отличаются или содержат "name", добавляем в таблицу
            if (!Objects.equals(line1, line2) || line1.contains("name") || line1.contains("limits") || line1.contains("requests") || line2.contains("limits") || line2.contains("requests")) {

            }
            {
                // Извлечение переменной и значений из строк
                String variable1 = extractVariable(line1);
                String value1 = extractValue(line1);

                String variable2 = extractVariable(line2);
                String value2 = extractValue(line2);

                if (line1.contains("name")) {
                    variable1 = formatUniqueWords(variable1, uniqueNames);
                }

                if (line2.contains("name")) {
                    variable2 = formatUniqueWords(variable2, uniqueNames);
                }

                // Добавление строки в таблицу
                tableBuilder.append("<tr>");
                tableBuilder.append("<td>").append(variable1).append("</td>");
                tableBuilder.append("<td>").append(value1).append("</td>");
                tableBuilder.append("<td>").append(value2).append("</td>");
                tableBuilder.append("</tr>");
            }
        }

        tableBuilder.append("</table>");
        return tableBuilder.toString();
    }



    // Извлечение названия переменной из строки
    private String extractVariable(String line) {
        int colonIndex = line.indexOf(":");
        if (colonIndex != -1) {
            return line.substring(0, colonIndex).trim();
        }
        return "";
    }

    // Извлечение значения переменной из строки
    private String extractValue(String line) {
        int colonIndex = line.indexOf(":");
        if (colonIndex != -1) {
            return line.substring(colonIndex + 1).trim();
        }
        return "";
    }

    // Форматирование уникальных слов в строке
    private String formatUniqueWords(String line, Set<String> uniqueWords) {
        StringBuilder formattedLine = new StringBuilder();

        // Замена символов пробела на неразрывной пробел, удаление пробелов в начале и конце строки
        String[] words = line.trim().split("\\s+");
        for (String word : words) {
            if (uniqueWords.add(word)) {
                formattedLine.append(word).append(" ");
            } else {
                // Значение уже присутствует в множестве, добавим его вместе с пробелом
                formattedLine.append(word).append(" ");
            }
        }

        return formattedLine.toString().trim().replaceAll(" ", "&nbsp;");
    }



    @GetMapping("/downloadopenshiftyaml")
    public String showDownloadForm() {
        return "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Загрузить конфиги из OpenShift</title>" +
                "    <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\">" +
                "    <style>" +
                "        body {" +
                "            background-color: #332f2c;" +
                "            color: white;" +
                "            padding: 300px;" +
                "        }" +
                "" +
                "        h1 {" +
                "            color: #ffc107;" +
                "        }" +
                "" +
                "        form {" +
                "            max-width: 600px;" +
                "            margin: auto;" +
                "        }" +
                "" +
                "        .form-group {" +
                "            margin-bottom: 10px;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "<h1 class=\"text-center\">Загрузить конфиги из OpenShift</h1>" +
                "<form action=\"/downloadopenshiftyaml\" method=\"get\">" +
                "    <div class=\"form-group\">" +
                "        <label for=\"openshiftUrl\">OpenShift URL:</label>" +
                "        <input type=\"text\" id=\"openshiftUrl\" name=\"openshiftUrl\" class=\"form-control\" required>" +
                "    </div>" +
                "    <div class=\"form-group\">" +
                "        <label for=\"sshToken\">SSH Token:</label>" +
                "        <input type=\"text\" id=\"sshToken\" name=\"sshToken\" class=\"form-control\" required>" +
                "    </div>" +
                "    <div class=\"form-group\">" +
                "        <label for=\"namespace\">Namespace:</label>" +
                "        <input type=\"text\" id=\"namespace\" name=\"namespace\" class=\"form-control\" required>" +
                "    </div>" +
                "    <button type=\"submit\" class=\"btn btn-primary\">Скачать</button>" +
                "</form>" +
                "<form action=\"/\" method=\"get\">" +
                "    <button type=\"submit\" class=\"btn btn-secondary mt-3\">Вернуться на главную страницу</button>" +
                "</form>" +
                "</body>" +
                "</html>";
    }




    @PostMapping("/downloadopenshiftyaml")
    public ResponseEntity<ByteArrayResource> downloadOpenShiftYaml(
            @RequestParam String openshiftUrl,
            @RequestParam String sshToken,
            @RequestParam String namespace
    ) {
        // Оставляем остальную часть метода без изменений
        String apiUrl = openshiftUrl + "/api/v1/namespaces/" + namespace + "/pods?limit=500";

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);

            String yamlData = responseEntity.getBody();
            byte[] yamlBytes = yamlData.getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "output.yaml");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(new ByteArrayResource(yamlBytes));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new ByteArrayResource(("Произошла ошибка: " + e.getMessage()).getBytes()));
        }
    }

}