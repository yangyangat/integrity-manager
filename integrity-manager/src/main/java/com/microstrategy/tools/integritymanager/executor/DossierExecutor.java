package com.microstrategy.tools.integritymanager.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.microstrategy.tools.integritymanager.exception.ReportExecutionException;
import com.microstrategy.tools.integritymanager.exception.ReportExecutorInternalException;
import com.microstrategy.tools.integritymanager.model.bo.ExecutionResult;
import com.microstrategy.tools.integritymanager.model.bo.ReportExecutionResult;
import com.microstrategy.tools.integritymanager.model.bo.intf.Query;
import com.microstrategy.tools.integritymanager.model.entity.mstr.dossier.DossierDefinition;
import com.microstrategy.tools.integritymanager.model.entity.mstr.dossier.DossierQueryDetails;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ExecutionResultFormat;
import com.microstrategy.tools.integritymanager.util.UrlHelper;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Accessors(chain = true)
public class DossierExecutor {
    @Setter
    @Accessors(chain = true)
    private RestParams restParams;

    private final RestTemplate restTemplate = new RestTemplate();

    public static DossierExecutor build() {
        return new DossierExecutor();
    }

    private DossierExecutor() {}

    public ExecutionResult execute(String dossierId, EnumSet<ExecutionResultFormat> resultFormats)
            throws ReportExecutorInternalException, ReportExecutionException  {
        return this.execute(dossierId, resultFormats, 60 * 30);
    }

    public ExecutionResult execute(String dossierId, EnumSet<ExecutionResultFormat> resultFormats, int maxWaitSecond)
            throws ReportExecutorInternalException, ReportExecutionException  {
        String instanceId = createDossierInstanceAndAnwserPrompts(dossierId, maxWaitSecond);

        ExecutionResult result = new ExecutionResult();

        // Get dossier hierarchy definition
        DossierDefinition dossierDefinition = getDossierDefinition(dossierId, instanceId);
        result.setHierarchyDefinition(dossierDefinition);

        // Get the all viz definition and data in the dossier
        Map<String, String> mapOfViz = fetchDossierInstanceData(dossierId, instanceId, dossierDefinition);
        Map<String, ReportExecutionResult> mapOfVizResult = new HashMap<>();
        result.setMapOfVizResult(mapOfVizResult);
        mapOfViz.forEach((key, vizInString) -> {
            mapOfVizResult.put(key, new ReportExecutionResult().setReport(vizInString));
        });

        if (resultFormats.contains(ExecutionResultFormat.SQL)) {
            Map<String, Query> mapOfQuery = this.fetchDossierQueryDetails(dossierId, instanceId);
            mapOfQuery.forEach((key, query) -> {
                if (!mapOfVizResult.containsKey(key)) {
                    mapOfVizResult.put(key, new ReportExecutionResult());
                }
                ReportExecutionResult vizResult = mapOfVizResult.get(key);
                vizResult.setSql(query.getSql());
                vizResult.setQueryDetails(query.getQueryDetails());
            });
        }

        return result;
    }

    private Map<String, Query> fetchDossierQueryDetails(String dossierId, String instanceId)
            throws ReportExecutionException {
        String libraryUrl = restParams.getLibraryUrl();
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/dossiers/%s/instances/%s/queryDetails",
                libraryUrl, dossierId, instanceId);

        try {
            ResponseEntity<DossierQueryDetails> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, DossierQueryDetails.class);
            DossierQueryDetails queryDetails = response.getBody();
            return queryDetails.getMapOfQuery();
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException(String.format("Fail to get the query details of the dossier viz:\ndossier id: %s\ninstance id: %s\n",
                    dossierId, instanceId));
        }
    }

    private Map<String, String> fetchDossierInstanceData(String dossierId, String instanceId, DossierDefinition dossierDefinition) {
        // Get the chapter viz map, and iterate the viz list and get the grid data for each viz
        Map<String, String> vizKeyGridMap = new HashMap<>();
        dossierDefinition.getChapterVizMap().forEach((chapterKey, vizKeyList) -> {
            vizKeyList.forEach(vizKey -> {
                try {
                    String vizDefinitionAndData = fetchVizDefinitionAndData(dossierId, instanceId, chapterKey, vizKey);
                    vizKeyGridMap.put(vizKey, vizDefinitionAndData);
                } catch (ReportExecutionException e) {
                    e.printStackTrace();
                }
            });
        });

        return vizKeyGridMap;
    }

    private String fetchVizDefinitionAndData(String dossierId, String instanceId, String chapterKey, String vizKey)
            throws ReportExecutionException {
        String libraryUrl = restParams.getLibraryUrl();
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/v2/dossiers/%s/instances/%s/chapters/%s/visualizations/%s",
                libraryUrl, dossierId, instanceId, chapterKey, vizKey);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            return response.getBody();
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException(String.format("Fail to get the definition and data of the dossier viz:\ndossier id: %s\ninstance id: %s\nchapter key:%s\nvisualization key:%s\n",
                    dossierId, instanceId, chapterKey, vizKey));
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

    private String createDossierInstanceAndAnwserPrompts(String dossierId, int maxWaitSecond) throws ReportExecutorInternalException, ReportExecutionException {
        if (!restParams.isValid() || !StringUtils.hasLength(dossierId)) {
            throw new ReportExecutorInternalException("At least one of the following is not set: " +
                    "authToken, cookie, projectId, reportId, libraryUrl");
        }

        // Create the instance
        String instanceId = createDossierInstance(dossierId);

        /*
        // Check instance status
        int dossierStatus;

        ReportPromptAnswerer reportPromptAnswerer = new ReportPromptAnswerer(this);
        while ((dossierStatus = fetchDossierInstanceStatus(dossierId, instanceId))
                != ReportInstanceStatus.REPORT_INSTANCE_STATUS_FINISH && maxWaitSecond-- >= 0) {
            if (dossierStatus == ReportInstanceStatus.REPORT_INSTANCE_STATUS_PROMPTED) {
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
                    String.format("Max wait time (%d)s exceeds when executing the report: %s",
                            maxWaitSecond, dossierId));
        }
         */
        return instanceId;
    }

    private int fetchDossierInstanceStatus(String dossierId, String instanceId) throws ReportExecutionException{
        String libraryUrl = restParams.getLibraryUrl();
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/documents/%s/instances/status", libraryUrl, dossierId, instanceId);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JsonNode.class);
            return response.getBody().get("status").asInt();
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException("Fail to get the dossier instance status due to the following reason:\n" + exception.getLocalizedMessage(), exception);
        }
    }

    private String createDossierInstance(String dossierId)
            throws ReportExecutionException{
        String libraryUrl = restParams.getLibraryUrl();
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/dossiers/%s/instances", libraryUrl, dossierId);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, JsonNode.class);
            return response.getBody().get("mid").asText();
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException(String.format("Fail to create the dossier instance for the dossier: %s, due to the following reason:\n%s", dossierId, exception.getLocalizedMessage())
                    , exception);
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

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        final String libraryUrl = "http://10.23.34.25:8080/MicroStrategyLibrary";
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

        final List<String> objectIds = Arrays.asList("80FDE73E4A791F63F91F9384708FA258");

        for (String objId: objectIds) {
            try {
                DossierExecutor dossierExecutor = DossierExecutor.build().setRestParams(restParams);
                ExecutionResult executionResult = dossierExecutor.execute(objId, EnumSet.allOf(ExecutionResultFormat.class));
                System.out.println("response = " + executionResult);
            } catch (Exception e) {
                System.out.println("e = " + e);
            }
        }
    }
}
