package org.example.sber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class YamlFileComparatorV2 {

    // Точка входа для тестирования функциональности класса
    public static void main(String[] args) {
        String file1Path = "deployment-fssp-ms-adapter-ucp-data-search-deploymentNT.yaml";
        String file2Path = "deployment-fssp-ms-adapter-ucp-data-search-deploymentPROM.yaml";

        try {
            // Извлечение строк с ключевыми словами из обоих файлов
            List<String> file1Lines = extractLinesWithKeywords(file1Path, "agent-limits-mem", "agent-requests-mem", "agent-limits-cpu", "agent-requests-cpu", "limits", "cpu", "memory", "request");
            List<String> file2Lines = extractLinesWithKeywords(file2Path, "agent-limits-mem", "agent-requests-mem", "agent-limits-cpu", "agent-requests-cpu", "limits", "cpu", "memory", "request");

            // Вывод извлеченных строк с номерами сравнения
            System.out.println("Строки с 'limits', 'request', 'cpu' и 'memory' в файле " + file1Path + ":");
            printLinesWithNumbers(file1Lines);

            System.out.println("\nСтроки с 'limits', 'request', 'cpu' и 'memory' в файле " + file2Path + ":");
            printLinesWithNumbers(file2Lines);

            // Здесь можно добавить код для сравнения строк или других действий
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Извлечение строк с ключевыми словами из переданного содержимого файла
    public static List<String> extractLinesWithKeywords(String fileContent, String... keywords) throws IOException {
        List<String> allLines = List.of(fileContent.split("\n"));
        List<String> keywordLines = new ArrayList<>();
        Set<String> printedNames = new HashSet<>();
        Set<String> printedLines = new HashSet<>();

        boolean isBlock = false;
        String currentBlockName = null;

        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);

            // Проверка наличия любого из ключевых слов
            boolean containsKeyword = false;
            for (String keyword : keywords) {
                if (line.contains(keyword)) {
                    containsKeyword = true;
                    break;
                }
            }

            // Проверка строки на пустые объекты
            boolean isEmptyObject = line.trim().matches(".*: \\{\\}");

            // Добавлено условие для исключения строк слишком большой длины
            if (containsKeyword && !isEmptyObject && line.length() <= 100) {
                String nameBeforeKeyword = findNameBeforeKeyword(allLines, i);
                if (nameBeforeKeyword != null && printedNames.add(nameBeforeKeyword)) {
                    keywordLines.add(nameBeforeKeyword);
                }

                if (currentBlockName != null && printedLines.add(currentBlockName)) {
                    keywordLines.add(currentBlockName);
                    currentBlockName = null;
                }

                // Добавлено условие для обработки запятых в строках
                if (line.contains(",")) {
                    int keywordIndex = findKeywordIndex(line, keywords);
                    int commaIndex = line.indexOf(",");

                    if (keywordIndex != -1 && keywordIndex < commaIndex) {
                        keywordLines.add(line.substring(keywordIndex, commaIndex));
                    } else {
                        // Если не найдено ключевое слово или оно после запятой, добавляем всю строку
                        keywordLines.add(line);
                    }
                } else {
                    keywordLines.add(line);
                }

                isBlock = true;

                // Запоминаем имя блока, если это не "cpu" или "memory"
                if (!line.contains("cpu") && !line.contains("memory")) {
                    currentBlockName = line;
                }
            } else if (isBlock && line.isEmpty()) {
                // Завершение блока при пустой строке
                isBlock = false;
            }
        }

        return keywordLines;
    }

    // Поиск строки с "name" перед ключевым словом
    private static String findNameBeforeKeyword(List<String> lines, int currentIndex) {
        // Ищем "name" перед ключевым словом в обычном порядке относительно текущей позиции
        for (int i = currentIndex + 1; i < lines.size(); i++) {
            String line = lines.get(i);

            // Изменим условие для более точной проверки наличия ключевого слова "name"
            if (line.contains("name:") && !line.trim().endsWith(":")) {
                // Возвращаем строку с "name"
                return line;
            }
        }
        return null; // Возвращаем null, если не найдено подходящее "name"
    }

    // Поиск индекса ключевого слова в строке
    private static int findKeywordIndex(String line, String[] keywords) {
        for (String keyword : keywords) {
            int index = line.indexOf(keyword);
            if (index != -1) {
                return index;
            }
        }
        return -1;
    }

    // Вывод строк с номерами
    public static void printLinesWithNumbers(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + lines.get(i));
        }
    }
}
