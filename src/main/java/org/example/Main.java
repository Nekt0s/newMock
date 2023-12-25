package org.example;

import lombok.SneakyThrows;
import org.example.sber.YamlFileComparatorV2;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    private static YamlFileComparatorV2 fileComparator;

    @SneakyThrows
    public static void main(String[] args) {

        String qwe = "\uFEFFkind: Deployment\n" +
                "apiVersion: apps/v1\n" +
                "metadata:\n" +
                "  annotations:\n" +
                "    deployment.kubernetes.io/revision: '1'\n" +
                "    kubectl.kubernetes.io/last-applied-configuration: >\n" +
                "      {\"apiVersion\":\"apps/v1\",\"kind\":\"Deployment\",\"metadata\":{\"annotations\":{},\"labels\":{\"app\":\"fssp-ms-adapter-ucp-data-search\",\"name\":\"fssp-ms-adapter-ucp-data-search\"},\"name\":\"fssp-ms-adapter-ucp-data-search-deployment\",\"namespace\":\"ci02705348-fssp-fnd-process\"},\"spec\":{\"replicas\":0,\"selector\":{\"matchLabels\":{\"app\":\"fssp-ms-adapter-ucp-data-search\"}},\"template\":{\"metadata\":{\"annotations\":{\"sidecar.istio.io/inject\":\"true\",\"sidecar.istio.io/proxyCPU\":\"100m\",\"sidecar.istio.io/proxyCPULimit\":\"100m\",\"sidecar.istio.io/proxyMemory\":\"300Mi\",\"sidecar.istio.io/proxyMemoryLimit\":\"400Mi\",\"vault.hashicorp.com/agent-init-first\":\"false\",\"vault.hashicorp.com/agent-inject\":\"true\",\"vault.hashicorp.com/agent-inject-file-gps_kafka_store\":\"gps.jks\",\"vault.hashicorp.com/agent-inject-file-secman_properties\":\"secman.properties\",\"vault.hashicorp.com/agent-inject-secret-gps_kafka_store\":\"true\",\"vault.hashicorp.com/agent-inject-secret-secman_properties\":\"true\",\"vault.hashicorp.com/agent-inject-template-gps_kafka_store\":\"{{-\n" +
                "      with secret\n" +
                "      \\\"/CI02705348_CI02720459/A/PROM/OSH/CI02705348-FSSP-FND-PROCESS/KV/ci02705348-fssp-fnd-process/gps-kafka-secret\\\"\n" +
                "      -}}\\n{{ base64Decode (index .Data \\\"jks\\\") }}{{- end\n" +
                "      -}}\\n\",\"vault.hashicorp.com/agent-inject-template-secman_properties\":\"{{-\n" +
                "      with secret\n" +
                "      \\\"/CI02705348_CI02720459/A/PROM/OSH/CI02705348-FSSP-FND-PROCESS/KV/ci02705348-fssp-fnd-process/gps-kafka-secret\\\"\n" +
                "      -}}\\nsberbank.adapter.kafka.properties.ssl.keystore.password={{\n" +
                "      .Data.password}}\\nsberbank.adapter.kafka.properties.ssl.truststore.password={{\n" +
                "      .Data.password}}\\n{{- end\n" +
                "      -}}\\n\",\"vault.hashicorp.com/agent-limits-cpu\":\"180m\",\"vault.hashicorp.com/agent-limits-mem\":\"256Mi\",\"vault.hashicorp.com/agent-pre-populate\":\"false\",\"vault.hashicorp.com/agent-requests-cpu\":\"156m\",\"vault.hashicorp.com/agent-requests-mem\":\"180Mi\",\"vault.hashicorp.com/log-level\":\"debug\",\"vault.hashicorp.com/namespace\":\"CI02705348_CI02720459\",\"vault.hashicorp.com/role\":\"ci02705348-fssp-fnd-process\",\"vault.hashicorp.com/secret-volume-path-gps_kafka_store\":\"/certs/kafka\",\"vault.hashicorp.com/secret-volume-path-secman_properties\":\"/properties/secman\"},\"labels\":{\"app\":\"fssp-ms-adapter-ucp-data-search\",\"name\":\"fssp-ms-adapter-ucp-data-search\",\"secman-injector\":\"enabled\"}},\"spec\":{\"affinity\":{\"podAntiAffinity\":{\"requiredDuringSchedulingIgnoredDuringExecution\":[{\"labelSelector\":{\"matchExpressions\":[{\"key\":\"app\",\"operator\":\"In\",\"values\":[\"fssp\"]}]},\"topologyKey\":\"kubernetes.io/hostname\"}]}},\"containers\":[{\"env\":[{\"name\":\"WAIT_FOR_ISTIO\",\"value\":\"true\"},{\"name\":\"REQUIRED_FILES\",\"value\":\"/properties/secman/secman.properties,\n" +
                "      /certs/kafka/gps.jks\"},{\"name\":\"TZ\",\"value\":\"Europe/Moscow\"},{\"name\":\"MANAGEMENT_SERVER_PORT\",\"value\":\"8080\"},{\"name\":\"logging.config\",\"value\":\"/logback/logback.xml\"},{\"name\":\"JAVA_TOOL_OPTIONS\",\"value\":\"-Duser.timezone=Europe/Moscow\n" +
                "      -XX:+UseG1GC -Dcom.sun.management.jmxremote=true\n" +
                "      -Dcom.sun.management.jmxremote.port=3000\n" +
                "      -Dcom.sun.management.jmxremote.rmi.port=3001\n" +
                "      -Djava.rmi.server.hostname=127.0.0.1\n" +
                "      -Dcom.sun.management.jmxremote.authenticate=false\n" +
                "      -Dcom.sun.management.jmxremote.ssl=false -Djava.io.tmpdir=/tmp-app\n" +
                "      -Dspring.profiles.active=main\n" +
                "      -Dspring.config.additional-location=file:/properties/app/\n" +
                "      -Dspring.config.import=file:/properties/secman/secman.properties\"},{\"name\":\"NODE_NAME\",\"valueFrom\":{\"fieldRef\":{\"apiVersion\":\"v1\",\"fieldPath\":\"spec.nodeName\"}}},{\"name\":\"POD_NAMESPACE\",\"valueFrom\":{\"fieldRef\":{\"apiVersion\":\"v1\",\"fieldPath\":\"metadata.namespace\"}}},{\"name\":\"POD_NAME\",\"valueFrom\":{\"fieldRef\":{\"apiVersion\":\"v1\",\"fieldPath\":\"metadata.name\"}}}],\"envFrom\":[{\"configMapRef\":{\"name\":\"fssp-ms-adapter-ucp-data-search-application-properties-configmap\"}}],\"image\":\"registry.ca.sbrf.ru/pprb/ci02705348/ci03607103_fsspfinder/fssp-ms-adapter-ucp-data-search@sha256:922f6e09d62adc91c418b5de34d2baf82ac08dfcbe57a89ec7affe3148226a17\",\"imagePullPolicy\":\"Always\",\"livenessProbe\":{\"failureThreshold\":3,\"httpGet\":{\"path\":\"/actuator/health\",\"port\":8080,\"scheme\":\"HTTP\"},\"initialDelaySeconds\":10,\"periodSeconds\":10,\"successThreshold\":1,\"timeoutSeconds\":5},\"name\":\"fssp-ms-adapter-ucp-data-search\",\"ports\":[{\"containerPort\":8080,\"name\":\"tcp-8080\",\"protocol\":\"TCP\"}],\"readinessProbe\":{\"failureThreshold\":2,\"httpGet\":{\"path\":\"/actuator/health\",\"port\":8080,\"scheme\":\"HTTP\"},\"initialDelaySeconds\":20,\"periodSeconds\":10,\"successThreshold\":2,\"timeoutSeconds\":5},\"resources\":{\"limits\":{\"cpu\":\"600m\",\"memory\":\"900Mi\"},\"requests\":{\"cpu\":\"400m\",\"memory\":\"640Mi\"}},\"securityContext\":{\"readOnlyRootFilesystem\":true},\"startupProbe\":{\"failureThreshold\":20,\"httpGet\":{\"path\":\"/actuator/health\",\"port\":8080,\"scheme\":\"HTTP\"},\"initialDelaySeconds\":30,\"periodSeconds\":10},\"terminationMessagePath\":\"/dev/termination-log\",\"terminationMessagePolicy\":\"File\",\"volumeMounts\":[{\"mountPath\":\"/properties/app\",\"name\":\"application-properties\",\"readOnly\":true},{\"mountPath\":\"/logback\",\"name\":\"logger-logback-xml\"},{\"mountPath\":\"/logs\",\"name\":\"logs\"},{\"mountPath\":\"/tmp-app\",\"name\":\"tmp-app\"}]},{\"env\":[{\"name\":\"standId\",\"value\":\"prom\"},{\"name\":\"cluster\",\"value\":\"ca\"},{\"name\":\"moduleId\",\"value\":\"fssp-ms-adapter-ucp-data-search-prom\"},{\"name\":\"moduleVersion\",\"value\":\"1.0.2.111\"},{\"name\":\"namespace\",\"valueFrom\":{\"fieldRef\":{\"apiVersion\":\"v1\",\"fieldPath\":\"metadata.namespace\"}}},{\"name\":\"nodeId\",\"valueFrom\":{\"fieldRef\":{\"apiVersion\":\"v1\",\"fieldPath\":\"spec.nodeName\"}}},{\"name\":\"pod\",\"valueFrom\":{\"fieldRef\":{\"apiVersion\":\"v1\",\"fieldPath\":\"metadata.name\"}}},{\"name\":\"hostAddress\",\"valueFrom\":{\"fieldRef\":{\"apiVersion\":\"v1\",\"fieldPath\":\"status.hostIP\"}}},{\"name\":\"serviceHostname\",\"value\":\"logger-cloud-promrb-ott.prom-19-20-apps.ocp-geo.ocp.ca.sbrf.ru\"},{\"name\":\"servicePort\",\"value\":\"80\"},{\"name\":\"service\",\"value\":\"fssp-ms-adapter-ucp-data-search\"},{\"name\":\"zoneId\",\"value\":\"default\"}],\"image\":\"registry.ca.sbrf.ru/ci00734898/ci00685811_synapse/fluent-bit@sha256:286475684f3b403ec84203b0b6965eb38a41218a966f6371aed7300cd05ac92b\",\"imagePullPolicy\":\"Always\",\"name\":\"logger-module\",\"resources\":{\"limits\":{\"cpu\":\"350m\",\"memory\":\"800Mi\"},\"requests\":{\"cpu\":\"300m\",\"memory\":\"400Mi\"}},\"securityContext\":{\"readOnlyRootFilesystem\":true},\"terminationMessagePath\":\"/dev/termination-log\",\"terminationMessagePolicy\":\"File\",\"volumeMounts\":[{\"mountPath\":\"/logs\",\"name\":\"logs\"},{\"mountPath\":\"/fluent-bit/etc\",\"name\":\"logger-fluent-bit-conf\"}]}],\"restartPolicy\":\"Always\",\"volumes\":[{\"configMap\":{\"defaultMode\":256,\"name\":\"fssp-ms-adapter-ucp-data-search-application-properties-configmap\"},\"name\":\"application-properties\"},{\"emptyDir\":{},\"name\":\"logs\"},{\"emptyDir\":{},\"name\":\"tmp-app\"},{\"configMap\":{\"defaultMode\":256,\"name\":\"fssp-ms-adapter-ucp-data-search-logback-xml-configmap\"},\"name\":\"logger-logback-xml\"},{\"configMap\":{\"defaultMode\":256,\"name\":\"fssp-ms-adapter-ucp-data-search-fluent-bit-conf-configmap\"},\"name\":\"logger-fluent-bit-conf\"}]}}}}\n" +
                "  resourceVersion: '1550061766'\n" +
                "  name: fssp-ms-adapter-ucp-data-search-deployment\n" +
                "  uid: c3aa59db-e03c-48bb-a1fb-6626f7eeea82\n" +
                "  creationTimestamp: '2023-11-22T20:52:55Z'\n" +
                "  generation: 1\n" +
                "  managedFields:\n";


  //      List<String> file1Lines = fileComparator.extractLinesWithKeywords(qwe, "agent-limits-mem", "agent-requests-mem", "agent-limits-cpu", "agent-requests-cpu", "limits", "cpu", "memory", "request");
           List<String> allLines = List.of(qwe.split("\n"));
        System.out.println(allLines);
/*        try (BufferedReader reader = new BufferedReader(new StringReader(qwe))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // Весь остальной код остается неизменным
                // ...
            }
        }*/

    }
}