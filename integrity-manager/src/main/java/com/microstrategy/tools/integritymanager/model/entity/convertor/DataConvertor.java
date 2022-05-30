package com.microstrategy.tools.integritymanager.model.entity.convertor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.microstrategy.tools.integritymanager.exception.ReportExecutionException;
import com.microstrategy.tools.integritymanager.exception.ReportExecutorInternalException;
import com.microstrategy.tools.integritymanager.executor.ReportExecutor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DataConvertor {

    public static List<List<Object>> restToFileSystem(String report) {
        ObjectMapper mapper = new ObjectMapper();
        List<List<Object>> tableData = new ArrayList<>();
        try {
            JsonNode reportMap = mapper.readTree(report);
            List<String> columnHeaders = getColumnHeaders(reportMap);

            List<List<String>> rowHeaders = getRowHeaders(reportMap);

            List<List<String>> metricValues = getMetricValues(reportMap);

            tableData.add((List)columnHeaders);
            for (int i = 0; i < rowHeaders.size(); i++) {
                tableData.add((List)Stream.concat(rowHeaders.get(i).stream(), metricValues.get(i).stream())
                        .collect(Collectors.toList()));
            }
            return tableData;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static List<String> getColumnHeaders(JsonNode reportMap) {
        //only handle the attributes on row, metrics on column case
        //TODO, to handle cross tab table
        ArrayNode rows = (ArrayNode) reportMap.get("definition").get("grid").get("rows");
        List<String> attributeColumnHeaders = StreamSupport.stream(rows.spliterator(), true)
                .filter(node -> node.get("type").asText().equals("attribute"))
                .flatMap(attrNode -> {
                    String name = attrNode.get("name").asText();
                    ArrayNode forms = (ArrayNode) attrNode.get("forms");
                    List<String> attrForms = StreamSupport.stream(forms.spliterator(), true)
                            .map(form -> String.format("%s %s", name, form.get("name").asText()))
                            .collect(Collectors.toList());
                    return attrForms.stream();
                })
                .collect(Collectors.toList());

        List<String> metricsColumnHeaders = Collections.emptyList();
        ArrayNode columns = (ArrayNode) reportMap.get("definition").get("grid").get("columns");
        if (columns.size() > 0) {
            JsonNode metrics = columns.get(0);
            if (metrics.get("type").asText().equals("templateMetrics")) {
                ArrayNode metricList = (ArrayNode) metrics.get("elements");
                metricsColumnHeaders = StreamSupport.stream(metricList.spliterator(), true)
                        .map(metricNode -> metricNode.get("name").asText())
                        .collect(Collectors.toList());
            }
        }

        return Stream.concat(attributeColumnHeaders.stream(), metricsColumnHeaders.stream())
                .collect(Collectors.toList());
    }

    private static List<List<String>> getRowHeaders(JsonNode reportMap) {
        List<List<String>> rowHeadersInString = new ArrayList<>();
        ArrayNode rowHeaders = (ArrayNode) reportMap.get("data").get("headers").get("rows");

        //Elements map
        ArrayNode rowUnits = (ArrayNode) reportMap.get("definition").get("grid").get("rows");

        //reverse iterate
        for (int i = rowHeaders.size() - 1; i >= 0; i--) {
            List<String> oneRowHeader = new ArrayList<>();
            ArrayNode row = (ArrayNode)rowHeaders.get(i);
            ArrayNode previousRow = (ArrayNode)rowHeaders.get(i - 1);
            for (int j = 0; j < row.size(); j++) {
                if (previousRow != null && row.get(j).asInt() == previousRow.get(j).asInt()) {
                    oneRowHeader.add("");
                }
                else {
                    List<String> elementStrings = getElementStringsbyIndex(rowUnits, j, row.get(j).asInt());
                    oneRowHeader.addAll(elementStrings);
                }
            }
            rowHeadersInString.add(oneRowHeader);
        }

        Collections.reverse(rowHeadersInString);

        return rowHeadersInString;
    }

    private static List<String> getElementStringsbyIndex(ArrayNode tempateUnits, int unitIndex, int elementIndex) {
        if (unitIndex >= 0 && unitIndex < tempateUnits.size()) {
            JsonNode unit = tempateUnits.get(unitIndex);
            if (unit.get("type").asText().equals("attribute")) {
                ArrayNode elements = (ArrayNode) unit.get("elements");
                if (elementIndex >= 0 && elementIndex < elements.size()) {
                    JsonNode element = elements.get(elementIndex);
                    ArrayNode formValues = (ArrayNode) element.get("formValues");
                    List<String> formValuesInString = StreamSupport.stream(formValues.spliterator(), true)
                            .map(value -> value.asText())
                            .collect(Collectors.toList());
                    return formValuesInString;
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<List<String>> getMetricValues(JsonNode reportMap) {
        ArrayNode metricValues = (ArrayNode) reportMap.get("data").get("metricValues").get("formatted");
        List<List<String>> metricValuesInString = StreamSupport.stream(metricValues.spliterator(), true)
                .map(values -> StreamSupport.stream(values.spliterator(), true)
                        .map(value -> value.asText())
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        return metricValuesInString;
    }
    public static void main(String[] args) throws ReportExecutionException, ReportExecutorInternalException {
        String libraryPath = "http://10.23.34.25:8080/MicroStrategyLibrary";

        String cookie;
        String authToken;
        String projectId = "B19DEDCC11D4E0EFC000EB9495D0F44F";
        String reportId = "13CFD83A458A68655A13CBA8D7C62CD5";//"125F9FB34CEB75E36192E7A7C784EE52";

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(String.join("/", libraryPath, "api", "auth/login"));
            post.setEntity(new ByteArrayEntity("{\"username\":\"administrator\"}".getBytes(StandardCharsets.UTF_8)));
            post.addHeader("Content-Type", "application/json");
            post.addHeader("accept", "application/json");
            CloseableHttpResponse response;
            response = client.execute(post);
            authToken = response.getHeaders("X-MSTR-AuthToken")[0].getElements()[0].getName();
            cookie = response.getHeaders("Set-Cookie")[0].getElements()[0].getName() + "="
                    + response.getHeaders("Set-Cookie")[0].getElements()[0].getValue();
        } catch (IOException e) {
            System.err.println("Use default auth token");
            return;
        }

        ReportExecutor reportExecutor = ReportExecutor.build()
                .setLibraryUrl(libraryPath).setCookie(cookie)
                .setAuthToken(authToken).setProjectId(projectId).setReportId(reportId);

        try {
            String res = reportExecutor.execute();
            List<List<Object>> IMReport = restToFileSystem(res);
            System.out.println("res = \n" + IMReport);
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }
}
