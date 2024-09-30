package SecretSanta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecretSantaGame {

    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("Ильнар");
        names.add("Леша");
        names.add("Даша");
        names.add("Настя");
        names.add("Дима");
        names.add("Ирина");
        names.add("Вася");
        names.add("Никита");
        names.add("Катя");

        Map<String, String> secretSantaMap = generateSecretSantaMap(names);

        // Выводим пары
        for (String giver : secretSantaMap.keySet()) {
            String receiver = secretSantaMap.get(giver);
            System.out.println(giver + " --> " + receiver);
        }
    }

    private static Map<String, String> generateSecretSantaMap(List<String> names) {
        List<String> shuffledNames = new ArrayList<>(names);
        Map<String, String> secretSantaMap;

        // Цикл для перетасовки до тех пор, пока не будут исключены нежелательные пары
        while (true) {
            Collections.shuffle(shuffledNames);
            secretSantaMap = new HashMap<>();

            boolean isValid = true;

            for (int i = 0; i < shuffledNames.size(); i++) {
                String giver = shuffledNames.get(i);
                String receiver = shuffledNames.get((i + 1) % shuffledNames.size());

                // Проверяем нежелательные пары в обоих направлениях
                if ((giver.equals("Леша") && receiver.equals("Даша")) ||
                        (giver.equals("Даша") && receiver.equals("Леша")) ||
                        (giver.equals("Настя") && receiver.equals("Ильнар")) ||
                        (giver.equals("Ильнар") && receiver.equals("Настя")) ||
                        (giver.equals("Никита") && receiver.equals("Катя")) ||
                        (giver.equals("Катя") && receiver.equals("Никита"))) {
                    isValid = false; // Если найдена нежелательная пара, повторяем цикл
                    break;
                }

                // Проверяем, что участник не дарит сам себе
                if (giver.equals(receiver)) {
                    isValid = false;
                    break;
                }

                secretSantaMap.put(giver, receiver);
            }

            // Если все проверки прошли успешно, выходим из цикла
            if (isValid) {
                break;
            }
        }

        return secretSantaMap;
    }
}
