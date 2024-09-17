
package org.example.sber;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.api.model.DeploymentConfig;
import io.fabric8.openshift.api.model.DeploymentConfigList;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.kubernetes.client.Config;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import io.fabric8.openshift.client.OpenShiftClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayOutputStream;
public class main {

    public static void main(String[] args) {
        String openshiftUrl = "";     // https://...
        String sshToken = "";        //sha256~...
        String namespace = "t";      //ci02...
        try {

            Config config = new ConfigBuilder()
                    .withMasterUrl(openshiftUrl)
                    .withOauthToken(sshToken)
                    .withNamespace(namespace)
                    .withRequestTimeout(10000)
                    .withConnectionTimeout(10000)
                    .withTrustCerts(true)
                    .build();
            DefaultOpenShiftClient kubernetesClient = new DefaultOpenShiftClient(config);
            OpenShiftClient osClient = new KubernetesClientBuilder().withConfig(config).build().adapt(OpenShiftClient.class);
            List<Pod> podList = osClient.pods().list().getItems();
            try (FileOutputStream zipStream = new FileOutputStream("file.zip");
                 ZipOutputStream zipOutputStream = new ZipOutputStream(zipStream)) {

                for (Pod pod : podList) {
                    String podName = pod.getMetadata().getName();
                    ObjectMapper jsonMapper = new ObjectMapper();
                    // Подставьте правильный тип ресурса и спецификацию в зависимости от вашего случая
                    Object resourceSpec = pod.getSpec();
                    String yamlContent = convertObjectToYAML(jsonMapper, resourceSpec);
                    zipOutputStream.putNextEntry(new ZipEntry(podName + ".yaml"));
                    zipOutputStream.write(yamlContent.getBytes(StandardCharsets.UTF_8));
                    zipOutputStream.closeEntry();
                }
                System.out.println("Download successful!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Download failed!");
        }
    }



    private static String convertObjectToYAML(ObjectMapper jsonMapper, Object resourceSpec) throws JsonProcessingException {

        YAMLMapper yamlMapper = new YAMLMapper();
        String jsonContent = jsonMapper.writeValueAsString(resourceSpec);
        JsonNode jsonNode = jsonMapper.readTree(jsonContent);
        return yamlMapper.writeValueAsString(jsonNode).replace("\"", "");
    }
}