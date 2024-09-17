package org.example.sber;

import io.fabric8.kubernetes.client.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//import static mail.OutlookEmailSender.sendEmail;


@RestController
class YamlFileComparatorController {

    private final YamlFileComparatorV2 fileComparator;

    public YamlFileComparatorController() {
        this.fileComparator = new YamlFileComparatorV2();
    }
    private String generateHtmlResponseForEmail(List<List<String>> allFileLines, List<String> fileNames) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><body style=\"background-color: #bbbbbb; color: #343a40; padding: 50px;\">");
        // Добавление блоков для каждой пары файлов в HTML с названиями файлов
        for (int i = 0; i < allFileLines.size(); i += 2) {
            List<String> file1Lines = allFileLines.get(i);
            List<String> file2Lines = allFileLines.get(i + 1);
            String fileName1 = fileNames.get(i); // Получаем название первого файла
            String fileName2 = fileNames.get(i + 1); // Получаем название второго файла
            htmlBuilder.append("<div style=\"background-color: #ffffff; color: #000000; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); margin-bottom: 20px;\">");
            htmlBuilder.append("<h1 style=\"color: #007bff; font-size: larger;\">").append("Сравнение: ").append(fileName1).append(" и ").append(fileName2).append("</h1>");
            htmlBuilder.append("<table border=\"1\" style=\"border-collapse: collapse; font-size: larger;\">");
            htmlBuilder.append("<tr>");
            htmlBuilder.append("<th>Переменная</th>");
            htmlBuilder.append("<th>").append("").append(fileName1).append("</th>"); // Изменено: добавлено название первого файла
            htmlBuilder.append("<th>").append("").append(fileName2).append("</th>"); // Изменено: добавлено название второго файла
            htmlBuilder.append("</tr>");
            for (int j = 0; j < Math.max(file1Lines.size(), file2Lines.size()); j++) {
                String line1 = j < file1Lines.size() ? file1Lines.get(j) : "";
                String line2 = j < file2Lines.size() ? file2Lines.get(j) : "";
                String variable1 = extractVariable(line1);
                String value1 = extractValue(line1);
                String variable2 = extractVariable(line2);
                String value2 = extractValue(line2);
                // Добавляем новую переменную для хранения отформатированного значения
                String formattedVariable1 = variable1.equals("name") ? "name" : formatVariable(variable1);
                String formattedVariable2 = variable2.equals("name") ? "name" : formatVariable(variable2);
                htmlBuilder.append("<tr>");
                htmlBuilder.append("<td>").append(formattedVariable1).append("</td>");
                htmlBuilder.append("<td>").append(value1).append("</td>");
                htmlBuilder.append("<td>").append(value2).append("</td>");
                htmlBuilder.append("</tr>");
            }
            htmlBuilder.append("</table>");
            htmlBuilder.append("</div>");
        }
        htmlBuilder.append("</body></html>");
        return htmlBuilder.toString();
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
            htmlBuilder.append("<table border=\"1\" style=\"border-collapse: collapse; font-size: larger;\">");
            htmlBuilder.append("<tr>");
            htmlBuilder.append("<th>Переменная</th>");
            htmlBuilder.append("<th>Файл NT</th>");
            htmlBuilder.append("<th>Файл PROM</th>");
            htmlBuilder.append("</tr>");

            for (int j = 0; j < Math.max(file1Lines.size(), file2Lines.size()); j++) {
                String line1 = j < file1Lines.size() ? file1Lines.get(j) : "";
                String line2 = j < file2Lines.size() ? file2Lines.get(j) : "";

                String variable1 = extractVariable(line1);
                String value1 = extractValue(line1);

                String variable2 = extractVariable(line2);
                String value2 = extractValue(line2);

                // Добавляем новую переменную для хранения отформатированного значения
                String formattedVariable1 = variable1.equals("name") ? "name" : formatVariable(variable1);
                String formattedVariable2 = variable2.equals("name") ? "name" : formatVariable(variable2);

                htmlBuilder.append("<tr>");
                htmlBuilder.append("<td>").append(formattedVariable1).append("</td>");
                htmlBuilder.append("<td>").append(value1).append("</td>");
                htmlBuilder.append("<td>").append(value2).append("</td>");
                htmlBuilder.append("</tr>");
            }

            htmlBuilder.append("</table>");
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

    // Добавим новый метод для форматирования переменной
    private String formatVariable(String variable) {
        return variable.replace("-", "");
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
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            fileNames.add(file.getOriginalFilename());

        }
        String htmlResponse = generateHtmlResponseForEmail(allFileLines, fileNames);
        // Отправка результатов сравнения на почту
//        try {
//            String [] recipients = {"NAlTokarev@sberbank.ru",  }; //,  "VYuStakhov@sberbank.ru", "OOPrudnikov@sberbank.ru", "Krasheninnikov.I.A@sberbank.ru", "ANiPoddubny@omega.sbrf.ru", "Markin.Se.Ol@omega.sbrf.ru" ,"AMRakhmatullin@omega.sbrf.ru", "Kravchenko.A.Vladimirovi@sberbank.ru", "Sadovskiy.A.Va@sberbank.ru","
//            String subject = "Результат сравнения конфигураций - "  ;
//            sendEmail("smtpas.sigma.sbrf.ru", "465", subject, htmlResponse, recipients);
//            System.out.println("Email успешно отправлен");
//            // Добавляем отправку на Confluence
//            String pageTitle = "Результат сравнения конфигураций";
//            ConfluenceSender.updateConfluencePage(htmlResponse, pageTitle);
//        } catch (Exception e) {
//            System.out.println("Ошибка отправки email: " + e.getMessage());
//        }
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
                "<form action=\"/downloadpodsyaml\" method=\"post\">" +
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
                "<form action=\"/\" method=\"post\">" +
                "    <button type=\"submit\" class=\"btn btn-secondary mt-3\">Вернуться на главную страницу</button>" +
                "</form>" +
                "</body>" +
                "</html>";
    }
    @RestController
    public class DownloadController {

        @PostMapping("/downloadpodsyaml")

        public ResponseEntity<byte[]> downloadPodsYaml(

                @RequestParam String openshiftUrl,
                @RequestParam String sshToken,
                @RequestParam String namespace

        ) {
            try {
                Config config = new ConfigBuilder()
                        .withMasterUrl(openshiftUrl)
                        .withOauthToken(sshToken)
                        .withNamespace(namespace)
                        .withRequestTimeout(10000)
                        .withConnectionTimeout(10000)
                        .withTrustCerts(true)
                        .build();

                OpenShiftClient kubernetesClient = new DefaultOpenShiftClient(config);

                ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
                try (ZipOutputStream zipOutputStream = new ZipOutputStream(zipStream)) {

// Получение списка Pod в указанном namespace
                    PodList podList = kubernetesClient.pods().inNamespace(namespace).list();

// Загрузка YAML-файлов для каждого Pod
                    for (Pod pod : podList.getItems()) {
                        String podName = pod.getMetadata().getName();
                        String yamlContent = kubernetesClient.pods().inNamespace(namespace).withName(podName).getLog();

// Добавление YAML-файла в zip-архив
                        zipOutputStream.putNextEntry(new ZipEntry(podName + ".yaml"));
                        zipOutputStream.write(yamlContent.getBytes(StandardCharsets.UTF_8));
                        zipOutputStream.closeEntry();
                    }
                }

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=openshift-yaml.zip");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(zipStream.toByteArray());

            } catch (Exception e) {
// Обработка ошибок
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

    }
}