package org.example.sber;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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

        // Блок для обоих файлов
        htmlBuilder.append("<div style=\"flex: 1; padding-right: 10px;\">");
        htmlBuilder.append("<h1>").append("Сравнение конфигов: NT и PROM</h1>");
        htmlBuilder.append("<pre>").append(formatAsTable(file1Lines, file2Lines)).append("</pre>");
        htmlBuilder.append("<form action=\"/\" method=\"get\">");
        htmlBuilder.append("<button type=\"submit\" style=\"background-color: black; color: white;\">Вернуться на главную страницу</button>");
        htmlBuilder.append("</form>");
        htmlBuilder.append("</div>");

        htmlBuilder.append("</body></html>");

        return htmlBuilder.toString();
    }

    private String formatAsTable(List<String> file1Lines, List<String> file2Lines) {
        Set<String> uniqueNames = new HashSet<>();
        Set<String> uniqueWords = new HashSet<>();

        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append("<table border=\"1\" style=\"border-collapse: collapse;\">");

        // Добавляем заголовок таблицы
        tableBuilder.append("<tr>");
        tableBuilder.append("<th>Variable</th>");
        tableBuilder.append("<th>Value 1</th>");
        tableBuilder.append("<th>Value 2</th>");
        tableBuilder.append("</tr>");

        // Итерируем по строкам из обоих файлов
        for (int i = 0; i < Math.max(file1Lines.size(), file2Lines.size()); i++) {
            String line1 = i < file1Lines.size() ? file1Lines.get(i) : "";
            String line2 = i < file2Lines.size() ? file2Lines.get(i) : "";

            // Если строки отличаются или содержат "name", добавляем в таблицу
            if (!Objects.equals(line1, line2) || line1.contains("name")) {
                // Извлекаем переменную и значения из строк
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

    private String extractVariable(String line) {
        // Извлекаем название переменной
        int colonIndex = line.indexOf(":");
        if (colonIndex != -1) {
            return line.substring(0, colonIndex).trim();
        }
        return "";
    }

    private String extractValue(String line) {
        // Извлекаем значение переменной
        int colonIndex = line.indexOf(":");
        if (colonIndex != -1) {
            return line.substring(colonIndex + 1).trim();
        }
        return "";
    }


    private String formatUniqueWords(String line, Set<String> uniqueWords) {
        StringBuilder formattedLine = new StringBuilder();

        // Заменяем символы пробела на неразрывной пробел, удаляем пробелы в начале и конце строки
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





    private boolean isNameLine(String line) {
        return line != null && line.trim().startsWith("name:");
    }


    private Set<String> extractUniqueWords(List<String> lines) {
        Set<String> uniqueWords = new HashSet<>();
        for (String line : lines) {
            String[] words = line.split("\\s+");
            uniqueWords.addAll(Arrays.asList(words));
        }
        return uniqueWords;
    }

    private String findLineByWord(List<String> lines, String word) {
        for (String line : lines) {
            if (line.contains(word)) {
                return line;
            }
        }
        return "";
    }


    private String formatTableRow(String line1, String line2) {
        StringBuilder rowBuilder = new StringBuilder("<tr>");

        // Добавляем ячейки для строк обоих файлов
        rowBuilder.append("<td>").append(line1).append("</td>");
        rowBuilder.append("<td>").append(line2).append("</td>");

        rowBuilder.append("</tr>");
        return rowBuilder.toString();
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
