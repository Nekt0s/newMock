import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SecretSantaGame {

    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("Ильнар");
        names.add("Леша");
        names.add("Даша");
        names.add("Настя");
        names.add("Дима");
        names.add("Ирина");
        names.add("Рамиль");
        names.add("Никита");

        Map<String, String> secretSantaMap = generateSecretSantaMap(names);

        for (String giver : secretSantaMap.keySet()) {
            String receiver = secretSantaMap.get(giver);
            System.out.println(giver + " --> " + receiver);
        }
    }

    private static Map<String, String> generateSecretSantaMap(List<String> names) {
        List<String> shuffledNames = new ArrayList<>(names);
        Collections.shuffle(shuffledNames);

        Map<String, String> secretSantaMap = new HashMap<>();
        for (int i = 0; i < shuffledNames.size(); i++) {
            String giver = shuffledNames.get(i);
            String receiver = shuffledNames.get((i + 1) % shuffledNames.size());

            if ((giver.equals("Леша") && receiver.equals("Даша")) ||
                    (giver.equals("Настя") && receiver.equals("Ильнар"))) {

                Collections.shuffle(shuffledNames);
                i = -1; //
                continue;
            }

            if (giver.equals(receiver)) {
                Collections.shuffle(shuffledNames);
                i = -1;
                continue;
            }

            secretSantaMap.put(giver, receiver);
        }

        return secretSantaMap;
    }
}
