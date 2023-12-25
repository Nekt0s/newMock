package org.example.sber;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
class YamlFileComparatorController {

    public final YamlFileComparatorV2 fileComparator;

    public YamlFileComparatorController() {
        this.fileComparator = new YamlFileComparatorV2();
    }

    @PostMapping("/compareconfig")
    public String handleFileUpload(@RequestParam("file1") MultipartFile file1,
                                   @RequestParam("file2") MultipartFile file2) throws IOException {
        String file1Content = new String(file1.getBytes());
        String file2Content = new String(file2.getBytes());

        List<String> file1Lines = fileComparator.extractLinesWithKeywords(file1Content, "agent-limits-mem", "agent-requests-mem", "agent-limits-cpu", "agent-requests-cpu", "limits", "cpu", "memory", "request");
        List<String> file2Lines = fileComparator.extractLinesWithKeywords(file2Content, "agent-limits-mem", "agent-requests-mem", "agent-limits-cpu", "agent-requests-cpu", "limits", "cpu", "memory", "request");

        return generateHtmlResponse(file1Lines, file2Lines);
    }

    private String generateHtmlResponse(List<String> file1Lines, List<String> file2Lines) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><body style=\"background-color: #332f2c; color: white; display: flex;\">");

        // Блок для первого файла
        htmlBuilder.append("<div style=\"flex: 1; padding-right: 10px;\">");
        htmlBuilder.append("<h1>").append("Сравнение конфигов: NT</h1>");
        htmlBuilder.append("<pre>").append(formatAsTable(file1Lines)).append("</pre>");
        htmlBuilder.append("<form action=\"/\" method=\"get\">");
        htmlBuilder.append("<button type=\"submit\" style=\"background-color: black; color: white;\">Вернуться на главную страницу</button>");
        htmlBuilder.append("</form>");
        htmlBuilder.append("</div>");

        // Блок для второго файла с явным margin-left
        htmlBuilder.append("<div style=\"flex: 1; margin-left: -800px;\">");
        htmlBuilder.append("<h1>").append("Сравнение конфигов: PROM</h1>");
        htmlBuilder.append("<pre>").append(formatAsTable(file2Lines)).append("</pre>");
        htmlBuilder.append("<form action=\"/\" method=\"get\">");
        //htmlBuilder.append("<button type=\"submit\" style=\"background-color: black; color: white;\">Вернуться на главную страницу</button>");
        htmlBuilder.append("</form>");
        htmlBuilder.append("</div>");

        htmlBuilder.append("</body></html>");

        return htmlBuilder.toString();
    }

    private String formatAsTable(List<String> lines) {
        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append("<table border=\"1\" style=\"border-collapse: collapse;\">");

        for (String line : lines) {
            String colorStyle = "";

            // Если строка содержит "cpu:", проверяем, одинаковы ли значения в обоих файлах
            if (line.contains("cpu:")) {
                String cpuValue = extractCpuValue(line);
                String correspondingLine = findCorrespondingLine(lines, line);
                String correspondingCpuValue = extractCpuValue(correspondingLine);

                // Если значения различны, выделяем строку красным цветом
                if (!cpuValue.equals(correspondingCpuValue)) {
                    colorStyle = "background-color: red; color: white;";
                }
            }

            // Добавляем строку в таблицу с применением стилей цвета
            tableBuilder.append("<tr><td style=\"").append(colorStyle).append("\">").append(line).append("</td></tr>");
        }

        tableBuilder.append("</table>");
        return tableBuilder.toString();
    }


    private String extractCpuValue(String line) {
        // Извлекаем значение CPU из строки, учитывая возможные единицы измерения
        String[] parts = line.split(":");
        if (parts.length >= 2) {
            return parts[1].trim().replaceAll("[^0-9]+", "");
        } else {
            return "";
        }
    }



    private String findCorrespondingLine(List<String> lines, String currentLine) {
        // Находим соответствующую строку в другом файле
        for (String line : lines) {
            if (line.startsWith(currentLine.substring(0, currentLine.indexOf(":")))) {
                return line;
            }
        }
        return "";
    }


}
