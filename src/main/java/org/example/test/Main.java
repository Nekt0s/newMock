package org.example.test;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class MessageId {
    String id;
    String spName;
    String externalSystem;
}

class Status {
    String statusCode;
    String statusDesc;
    String statusType;
}

class AvailabilityAccData {
    String ucpId;
    String actDate;
    String currencyCode;
    String debtorName;
    String acc;
    String productCode;
    String startDate;
    String endDate;
    String tb;
    String gosb;
    String vsp;
    String bicBank;
    double summa;
    String dateExchange;
    double summaRub;
}

class Answer {
    String externalKey;
    String answerDate;
    Status status;
    List<AvailabilityAccData> availabilityAccData;
}

class MyJson {
    MessageId messageId;
    Answer answer;
}

public class Main {
    public static void main(String[] args) {
        MyJson myJson = new MyJson();
        // Fill your JSON with necessary data

        // Check if 10% probability is met
        if (new Random().nextInt(10) < 1) {
            // Generate 4000 elements
            myJson.answer.availabilityAccData = generate4000Elements();
        }

        // Convert myJson to JSON string
        Gson gson = new Gson();
        String jsonString = gson.toJson(myJson);
        System.out.println(jsonString);
    }

    private static List<AvailabilityAccData> generate4000Elements() {
        List<AvailabilityAccData> elements = new ArrayList<>();
        for (int i = 0; i < 4000; i++) {
            AvailabilityAccData data = new AvailabilityAccData();
            // Fill the data with necessary values, for example:
            data.ucpId = "ucpId_" + i;
            data.actDate = "2024-05-02";
            data.currencyCode = "810";
            data.acc = "acc_" + i;
            data.bicBank = "bicBank_" + i;
            // Add the generated element to the list
            elements.add(data);
        }
        return elements;
    }
}
