import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomSanta {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("Ильнар");
        names.add("Леша");
        names.add("Ирина");
        names.add("Дима");
        names.add("Настя");
        names.add("Рамиль");
        names.add("Никита");
        names.add("Даша");

        List<String> names2 = new ArrayList<>(names);
        List<String> mergedList = new ArrayList<>();

        for (int i = 0; i < names.size(); i++) {
            String name1 = names.get(i);
            String name2 = names2.get(i);

            while (mergedList.contains(name1 + " " + name2) ||
                    name1.equals(name2) ||
                    (name1.equals("Леша") && name2.equals("Даша")) ||
                    (name1.equals("Даша") && name2.equals("Леша")) ||
                    (name1.equals("Настя") && name2.equals("Ильнар")) ||
                    (name1.equals("Ильнар") && name2.equals("Настя"))) {

                Collections.shuffle(names2);
                name2 = names2.get(i);
            }

            mergedList.add(name1 + " " + name2);
        }

        System.out.println("Объединенный список:");
        for (String item : mergedList) {
            System.out.println(item);
        }
    }
}
