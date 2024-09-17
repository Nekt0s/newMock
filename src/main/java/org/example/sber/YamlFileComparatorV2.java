package org.example.sber;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;

public class YamlFileComparatorV2 {

    public List<String> extractLinesWithKeywords(String fileContent, String... keywords) {
        Yaml yaml = new Yaml(new Constructor(Map.class));
        Map<String, Object> yamlObject = yaml.load(fileContent);

        List<String> linesWithKeywords = new ArrayList<>();
        Set<String> uniqueContainerNames = new HashSet<>();
        traverseYamlObject(yamlObject, yamlObject, "", linesWithKeywords, keywords, null, uniqueContainerNames);

        return linesWithKeywords;
    }

    private void traverseYamlObject(Map<String, Object> yamlObject1, Map<String, Object> yamlObject2, String currentPath, List<String> linesWithKeywords, String[] keywords, String containerName, Set<String> uniqueContainerNames) {
        if (currentPath.equals("metadata.annotations") || currentPath.contains(".env") ) {
            return;
        }

        for (Map.Entry<String, Object> entry : yamlObject1.entrySet()) {
            String key = entry.getKey();
            Object value1 = entry.getValue();
            Object value2 = yamlObject2.get(key);
            String currentFullPath = currentPath.isEmpty() ? key : currentPath + "." + key;

            if (value1 instanceof Map) {
                if (key.equals("containers")) {
                    traverseYamlObject((Map<String, Object>) value1, (Map<String, Object>) value2, currentFullPath, linesWithKeywords, keywords, null, uniqueContainerNames);
                } else {
                    traverseYamlObject((Map<String, Object>) value1, (Map<String, Object>) value2, currentFullPath, linesWithKeywords, keywords, containerName, uniqueContainerNames);
                }
            } else if (value1 instanceof List) {
                traverseYamlList((List<Object>) value1, (List<Object>) value2, currentFullPath, linesWithKeywords, keywords, containerName, uniqueContainerNames);
            } else if (value1 != null) {
                if (containsKeyword(currentFullPath, keywords)) {
                    if (containerName != null && !uniqueContainerNames.contains(containerName)) {
                        linesWithKeywords.add("name" + ": " + containerName);
                        uniqueContainerNames.add(containerName);
                    }
                }
                String[] parts = currentFullPath.split("\\.");
                String variable = parts.length >= 2 ? parts[parts.length - 2] + "." + parts[parts.length - 1] : currentFullPath; // возвращает последние 2 слова (limits.. requests..)
                //  String variable = currentFullPath;  // если нужно вывести полный путь в ямл файле
                String line1 = variable + ": " + value1.toString();
                String line2 = variable + ": " + value2.toString();
                if (containsKeyword(line1, keywords)) {
                    if (!line1.equals(line2)) {
                        linesWithKeywords.add("<span style=\"color:red\">" + line1 + "</span>");
                    } else {
                        linesWithKeywords.add(line1);
                    }
                }
            }
        }
    }

    private void traverseYamlList(List<Object> yamlList1, List<Object> yamlList2, String currentPath, List<String> linesWithKeywords, String[] keywords, String containerName, Set<String> uniqueContainerNames) {
        for (int i = 0; i < yamlList1.size(); i++) {
            Object item1 = yamlList1.get(i);
            Object item2 = yamlList2.get(i);
            String listItemPath = currentPath + "[" + i + "]";

            if (item1 instanceof Map) {
                if (currentPath.endsWith("containers")) {
                    String name = (String) ((Map<String, Object>) item1).get("name");
                    traverseYamlObject((Map<String, Object>) item1, (Map<String, Object>) item2, listItemPath, linesWithKeywords, keywords, name, uniqueContainerNames);
                } else {
                    traverseYamlObject((Map<String, Object>) item1, (Map<String, Object>) item2, listItemPath, linesWithKeywords, keywords, containerName, uniqueContainerNames);
                }
            } else if (item1 instanceof List) {
                traverseYamlList((List<Object>) item1, (List<Object>) item2, listItemPath, linesWithKeywords, keywords, containerName, uniqueContainerNames);
            } else if (item1 != null) {
                if (containsKeyword(listItemPath, keywords)) {
                    if (containerName != null && !uniqueContainerNames.contains(containerName)) {
                        linesWithKeywords.add("name" + ": " + containerName);
                        uniqueContainerNames.add(containerName);
                    }
                }
                String[] parts = listItemPath.split("\\.");
                String variable = parts.length >= 2 ? parts[parts.length - 2] + "." + parts[parts.length - 1] : listItemPath;
                String line1 = variable + ": " +
                        item1.toString();
                String line2 = variable + ": " + item2.toString();
                if (containsKeyword(line1, keywords)) {
                    if (!line1.equals(line2)) {
                        linesWithKeywords.add("<span style=\"color:red\">" + line1 + "</span>");
                    } else {
                        linesWithKeywords.add(line1);
                    }
                }
            }
        }
    }

    private boolean containsKeyword(String line, String[] keywords) {
        for (String keyword : keywords) {
            if (line.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public List<String> extractLinesWithKeywordsFromFile(String filePath, String... keywords) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            return extractLinesWithKeywords(new String(data), keywords);
        }
    }
}