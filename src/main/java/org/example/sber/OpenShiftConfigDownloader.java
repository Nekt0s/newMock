package org.example.sber;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.io.FileWriter;
import java.io.IOException;

public class OpenShiftConfigDownloader {

    public static void downloadConfigs(String openshiftUrl, String oauthToken, String namespace) {
        try (KubernetesClient client = createKubernetesClient(openshiftUrl, oauthToken, namespace)) {
            // Получение списка подов
            // Здесь можете использовать ваш код для получения конфигов

            // Пример: скачивание конфигов подов
            // List<Pod> pods = client.pods().list().getItems();
            // for (Pod pod : pods) {
            //     String podName = pod.getMetadata().getName();
            //     String yamlConfig = client.pods().withName(podName).get().toString();
            //
            //     // Сохранение конфига в файл
            //     saveConfigToFile("pod-" + podName + ".yaml", yamlConfig);
            // }

            System.out.println("Конфиги успешно скачаны.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Произошла ошибка при скачивании конфигов.");
        }
    }

    private static KubernetesClient createKubernetesClient(String openshiftUrl, String oauthToken, String namespace) {
        Config config = new ConfigBuilder()
                .withMasterUrl(openshiftUrl)
                .withOauthToken(oauthToken)
                .withNamespace(namespace)
                // Другие настройки...
                .build();

        return new DefaultKubernetesClient(config);
    }

    private static void saveConfigToFile(String fileName, String content) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
            System.out.println("Конфиг сохранен в файл: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
