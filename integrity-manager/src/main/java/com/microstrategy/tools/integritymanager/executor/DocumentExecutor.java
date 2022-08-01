package com.microstrategy.tools.integritymanager.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.microstrategy.tools.integritymanager.exception.ReportExecutionException;
import com.microstrategy.tools.integritymanager.exception.ReportExecutorInternalException;
import com.microstrategy.tools.integritymanager.model.bo.ExecutionResult;
import com.microstrategy.tools.integritymanager.model.bo.ReportExecutionResult;
import com.microstrategy.tools.integritymanager.model.bo.intf.Query;
import com.microstrategy.tools.integritymanager.model.entity.mstr.document.DocumentQueryDetails;
import com.microstrategy.tools.integritymanager.model.entity.mstr.document.DocumentQueryGridGraph;
import com.microstrategy.tools.integritymanager.model.entity.mstr.dossier.DossierDefinition;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ExecutionResultFormat;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ReportInstanceStatus;
import com.microstrategy.tools.integritymanager.util.UrlHelper;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Accessors(chain = true)
public class DocumentExecutor {
    @Setter
    @Accessors(chain = true)
    private RestParams restParams;

    private final RestTemplate restTemplate = new RestTemplate();

    public static DocumentExecutor build() {
        return new DocumentExecutor();
    }

    private DocumentExecutor() {}

    public ExecutionResult execute(String documentId, EnumSet<ExecutionResultFormat> resultFormats)
            throws ReportExecutorInternalException, ReportExecutionException {
        return this.execute(documentId, resultFormats, 60 * 30);
    }

    public ExecutionResult execute(String documentId, EnumSet<ExecutionResultFormat> resultFormats, int maxWaitSecond)
            throws ReportExecutorInternalException, ReportExecutionException  {
        String instanceId = createDocumentInstanceAndAnwserPrompts(documentId, maxWaitSecond);

        ExecutionResult result = new ExecutionResult();
        EnumSet<ExecutionResultFormat> actualResultFormats = EnumSet.noneOf(ExecutionResultFormat.class);
        result.setResultFormats(actualResultFormats);

        // Get document hierarchy definition
        // Currently REST API doesn't provide the endpoint for document hierarchy definition.
        // However, fortunately the query details result is a simplified hierarchy definition.
        DocumentQueryDetails documentQueryDetails = this.fetchDocumentQueryDetails(documentId, instanceId);
        result.setDocumentDefinition(documentQueryDetails);

        // Get the all viz definition and data in the dossier
        Map<String, byte[]> mapOfViz = fetchDocumentInstanceData(documentId, instanceId, documentQueryDetails);
        Map<String, ReportExecutionResult> mapOfVizResult = new HashMap<>();
        result.setMapOfVizResult(mapOfVizResult);
        actualResultFormats.add(ExecutionResultFormat.DATA);
        mapOfViz.forEach((key, vizData) -> {
            mapOfVizResult.put(key, new ReportExecutionResult().setGridDataInCSV(vizData));
        });

        if (resultFormats.contains(ExecutionResultFormat.SQL)) {
            Map<String, Query> mapOfQuery = documentQueryDetails.getMapOfQuery();
            mapOfQuery.forEach((key, query) -> {
                if (!mapOfVizResult.containsKey(key)) {
                    mapOfVizResult.put(key, new ReportExecutionResult());
                }
                ReportExecutionResult vizResult = mapOfVizResult.get(key);
                vizResult.setSql(query.getSql());
                vizResult.setQueryDetails(query.getQueryDetails());
            });
            actualResultFormats.add(ExecutionResultFormat.SQL);
        }

        if (resultFormats.contains(ExecutionResultFormat.PDF)) {
            String pdfInString = this.exportDocumentToPdf(documentId, instanceId);
            result.setPdfInString(pdfInString);
            actualResultFormats.add(ExecutionResultFormat.PDF);
        }

        if (resultFormats.contains(ExecutionResultFormat.EXCEL)) {
            byte[] excelInByte = this.exportDocumentToExcel(documentId, instanceId);
            result.setExcelInByte(excelInByte);
            actualResultFormats.add(ExecutionResultFormat.EXCEL);
        }

        return result;
    }

    private DocumentQueryDetails fetchDocumentQueryDetails(String documentId, String instanceId)
            throws ReportExecutionException {
        String libraryUrl = restParams.getLibraryUrl();
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/documents/%s/instances/%s/queryDetails",
                libraryUrl, documentId, instanceId);

        try {
            ResponseEntity<DocumentQueryDetails> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, DocumentQueryDetails.class);
            return response.getBody();
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException(String.format("Fail to get the query details of the dossier viz:\ndossier id: %s\ninstance id: %s\n",
                    documentId, instanceId));
        }
    }

    private Map<String, byte[]> fetchDocumentInstanceData(String documentId, String instanceId, DocumentQueryDetails documentDefinition) {
        // Iterate layouts, get the viz key and get the grid data in csv binary for each viz
        return documentDefinition.getLayouts().stream()
                .flatMap(layout -> layout.getGridGraphs().stream())
                .collect(
                        Collectors.toMap(
                                DocumentQueryGridGraph::getKey,
                                gridGraph -> {
                                    try {
                                        return fetchDocumentGridGraphData(documentId, instanceId, gridGraph.getKey());
                                    } catch (ReportExecutionException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                        )
                );
    }

    private byte[] fetchDocumentGridGraphData(String documentId, String instanceId, String vizKey)
            throws ReportExecutionException {
        String libraryUrl = restParams.getLibraryUrl();
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/documents/%s/instances/%s/visualizations/%s/csv",
                libraryUrl, documentId, instanceId, vizKey);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, byte[].class);
            return response.getBody();
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException(String.format("Fail to get the definition and data of the document viz:\ndossier id: %s\ninstance id: %s\nvisualization key:%s\n",
                    documentId, instanceId, vizKey));
        }
    }

    private DossierDefinition getDossierDefinition(String dossierId, String instanceId)
            throws ReportExecutionException {
        String libraryUrl = restParams.getLibraryUrl();
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/dossiers/%s/definition", libraryUrl, dossierId);

        try {
            ResponseEntity<DossierDefinition> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, DossierDefinition.class);
            return response.getBody();
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException("Fail to get the dossier definition due to the following reason:\n" + exception.getLocalizedMessage(), exception);
        }
    }

    private String createDocumentInstanceAndAnwserPrompts(String documentId, int maxWaitSecond) throws ReportExecutorInternalException, ReportExecutionException {
        if (!restParams.isValid() || !StringUtils.hasLength(documentId)) {
            throw new ReportExecutorInternalException("At least one of the following is not set: " +
                    "authToken, cookie, projectId, reportId, libraryUrl");
        }

        // Create the instance
        String instanceId = createDocumentInstance(documentId);

        // Check instance status
        int instanceStatus = 0;

        ReportPromptAnswerer reportPromptAnswerer = new ReportPromptAnswerer(this.restParams, documentId, EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition);
        while ((instanceStatus = fetchDocumentInstanceStatus(documentId, instanceId))
                != ReportInstanceStatus.REPORT_INSTANCE_STATUS_FINISH && maxWaitSecond-- >= 0) {
            if (instanceStatus == ReportInstanceStatus.REPORT_INSTANCE_STATUS_PROMPTED) {
                reportPromptAnswerer.answer(instanceId);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new ReportExecutorInternalException("Interrupted");
            }
        }

        if (maxWaitSecond < 0) {
            throw new ReportExecutionException(
                    String.format("Max wait time (%d)s exceeds when executing the document: %s",
                            maxWaitSecond, documentId));
        }

        return instanceId;
    }

    private int fetchDocumentInstanceStatus(String documentId, String instanceId) throws ReportExecutionException{
        String libraryUrl = restParams.getLibraryUrl();
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/documents/%s/instances/%s/status", libraryUrl, documentId, instanceId);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JsonNode.class);
            return response.getBody().get("status").asInt();
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException("Fail to get the document instance status due to the following reason:\n" + exception.getLocalizedMessage(), exception);
        }
    }

    private String createDocumentInstance(String documentId)
            throws ReportExecutionException{
        String libraryUrl = restParams.getLibraryUrl();
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/documents/%s/instances", libraryUrl, documentId);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, JsonNode.class);
            return response.getBody().get("mid").asText();
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException(String.format("Fail to create the document instance for the document: %s, due to the following reason:\n%s", documentId, exception.getLocalizedMessage())
                    , exception);
        }
    }

    private String exportDocumentToPdf(String documentId, String instanceId)
            throws ReportExecutionException {
        String libraryUrl = restParams.getLibraryUrl();
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/documents/%s/instances/%s/pdf",
                libraryUrl, documentId, instanceId);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, JsonNode.class);
            JsonNode responseBody = response.getBody();
            return responseBody.get("data").asText();
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException(String.format("Fail to export the dossier to PDF:\ndossier id: %s\ninstance id: %s\n",
                    documentId, instanceId));
        }
    }

    private byte[] exportDocumentToExcel(String documentId, String instanceId)
            throws ReportExecutionException {
        String libraryUrl = restParams.getLibraryUrl();
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/documents/%s/instances/%s/excel",
                libraryUrl, documentId, instanceId);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, byte[].class);
            return response.getBody();
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException(String.format("Fail to export the dossier to Excel:\ndossier id: %s\ninstance id: %s\n",
                    documentId, instanceId));
        }
    }

    private HttpEntity<Map<String, Object>> newMapHttpEntity() {
        String authToken = restParams.getAuthToken();
        List<String> cookies = restParams.getCookies();
        String projectId = restParams.getProjectId();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-MSTR-AuthToken", authToken);
        headers.add("X-MSTR-ProjectID", projectId);
        headers.addAll("Cookie", cookies);
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
        return new HttpEntity<>(headers);
    }

    private static void downloadPdf(ExecutionResult executionResult) throws IOException {
        String pdfInString = executionResult.getPdfInString();
        byte[] decodedBytes = Base64.getDecoder().decode(pdfInString);
        String home = System.getProperty("user.home");
        Path filePath = Paths.get(home, "Downloads", executionResult.getHierarchyDefinition().getName() + ".pdf");
        Files.write(filePath, decodedBytes);
        System.out.print("Download successfully in your Download folder");
    }

    private static void downloadExcel(ExecutionResult executionResult) throws IOException {
        byte[] excelInByte = executionResult.getExcelInByte();
        String home = System.getProperty("user.home");
        Path filePath = Paths.get(home, "Downloads", executionResult.getHierarchyDefinition().getName() + ".xlsx");
        Files.write(filePath, excelInByte);
        System.out.print("Download successfully in your Download folder");
    }

    private static void testReadingCSV() {
        String home = System.getProperty("user.home");
        Path filePath = Paths.get(home, "Downloads", "k30.csv");
        List<List<String>> records = new ArrayList<List<String>>();
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath.toAbsolutePath().toString(), StandardCharsets.UTF_16LE));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        } catch (CsvValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(records);
    }

    public static void main(String[] args) {
//        testReadingCSV();

        RestTemplate restTemplate = new RestTemplate();
        final String libraryUrl = "http://10.27.69.70:8080/MicroStrategyLibrary";
        final String projectId = "B19DEDCC11D4E0EFC000EB9495D0F44F";

        Map<String, Object> postBody = new HashMap<>();
        postBody.put("username", "administrator");
        postBody.put("password", "");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<>(postBody, headers);

        String url = UrlHelper.joinUrl(libraryUrl, "api", "auth/login");
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        String authToken = response.getHeaders().getFirst("X-MSTR-AuthToken");

        RestParams restParams = new RestParams()
                .setAuthToken(authToken)
                .setCookies(cookies)
                .setLibraryUrl(libraryUrl)
                .setProjectId(projectId);

        final List<String> objectIds = Arrays.asList(//"B1C880C64E9CD42CDBB370B5B72A1F98",
                "6F2C96DE4AB8BC420D077FA41F54C1FB");

        for (String objId: objectIds) {
            try {
                DocumentExecutor dossierExecutor = DocumentExecutor.build().setRestParams(restParams);
                ExecutionResult executionResult = dossierExecutor.execute(objId, EnumSet.allOf(ExecutionResultFormat.class));
                executionResult.getMapOfVizResult().forEach((key, vizResult) -> {
                    System.out.println(vizResult.getGridData());
                });
                downloadPdf(executionResult);
                downloadExcel(executionResult);
                System.out.println("response = " + executionResult);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("e = " + e);
            }
        }
    }
}
