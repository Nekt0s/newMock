//package org.example.sber;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static org.example.sber.YamlFileComparatorV2.extractLinesWithKeywords;
//import static org.example.sber.YamlFileComparatorV2.printLinesWithNumbers;
//
//public class YamlFileComparator {
//
//    public static void main(String[] args) {
//        String file1Path = "deployment-fssp-ms-adapter-ucp-data-search-deploymentNT.yaml";
//        String file2Path = "deployment-fssp-ms-adapter-ucp-data-search-deploymentPROM.yaml";
//
//        try {
//            // Извлечение строк с ключевыми словами из обоих файлов
//            List<String> file1Lines = extractLinesWithKeywords(file1Path, "agent-limits-mem", "agent-requests-mem", "agent-limits-cpu", "agent-requests-cpu", "limits", "cpu", "memory", "request");
//            List<String> file2Lines = extractLinesWithKeywords(file2Path, "agent-limits-mem", "agent-requests-mem", "agent-limits-cpu", "agent-requests-cpu", "limits", "cpu", "memory", "request");
//
//            // Вывод извлеченных строк с номерами сравнения
//            System.out.println("Lines with 'limits', 'request', 'cpu', and 'memory' in " + file1Path + ":");
//            printLinesWithNumbers(file1Lines);
//
//            System.out.println("\nLines with 'limits', 'request', 'cpu', and 'memory' in " + file2Path + ":");
//            printLinesWithNumbers(file2Lines);
//
//            // Сравнение строк и вывод различий
//            System.out.println("\nDifferences between " + file1Path + " and " + file2Path + ":");
//            List<String> differences = findDifferences(file1Lines, file2Lines);
//            printLinesWithNumbers(differences);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Остальные методы остаются без изменений
//
//    private static List<String> findDifferences(List<String> list1, List<String> list2) {
//        Set<String> set1 = new HashSet<>(list1);
//        Set<String> set2 = new HashSet<>(list2);
//
//        return Stream.concat(
//                        set1.stream().filter(line -> !set2.contains(line)),
//                        set2.stream().filter(line -> !set1.contains(line))
//                )
//                .collect(Collectors.toList());
//    }
//
//    public List<String> extractLinesWithKeywords(String file1Path, String s, String s1, String s2, String s3, String limits, String cpu, String memory, String request) {
//    }
//}
