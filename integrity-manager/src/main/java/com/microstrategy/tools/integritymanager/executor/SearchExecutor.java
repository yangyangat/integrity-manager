package com.microstrategy.tools.integritymanager.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.microstrategy.tools.integritymanager.util.UrlHelper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SearchExecutor {
    private String authToken;
    private List<String> cookies;
    private String projectId;
    private String reportId;
    private String libraryUrl;

    public static SearchExecutor build() {
        return new SearchExecutor();
    }

    private SearchExecutor() {}

    public String getAuthToken() {
        return authToken;
    }

    public SearchExecutor setAuthToken(String authToken) {
        this.authToken = authToken;
        return this;
    }

    public List<String> getCookies() {
        return cookies;
    }

    public SearchExecutor setCookies(List<String> cookies) {
        this.cookies = cookies;
        return this;
    }

    public String getProjectId() {
        return projectId;
    }

    public SearchExecutor setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getReportId() {
        return reportId;
    }

    public SearchExecutor setReportId(String reportId) {
        this.reportId = reportId;
        return this;
    }

    public String getLibraryUrl() {
        return libraryUrl;
    }

    public SearchExecutor setLibraryUrl(String libraryUrl) {
        this.libraryUrl = libraryUrl;
        return this;
    }

    public List<String> queryTopNReports(int limit) {
        Map<String, Object> urlPrams = Map.of(
                "limit", Integer.valueOf(limit),
                "type", Integer.valueOf(3)
        );
        ResponseEntity<JsonNode> searchResponse = this.execute(urlPrams);

        JsonNode searchResult = searchResponse.getBody();

        return StreamSupport.stream(searchResult.get("result").spliterator(), true)
                //.filter(obj -> obj.get("subtype").asInt(0) == 768)  //Only get the normal reports
                .map(obj -> obj.get("id").asText())
                .collect(Collectors.toList());
    }

    public ResponseEntity<JsonNode> execute(Map<String, Object> urlPrams) {
        RestTemplate restTemplate = new RestTemplate();
        if (authToken.isEmpty() && cookies.isEmpty()) {
            Map<String, Object> postBody = new HashMap<>();
            postBody.put("username", "administrator");
            postBody.put("password", "");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<>(postBody, headers);

            String url = UrlHelper.joinUrl(libraryUrl, "api", "auth/login");
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            authToken = response.getHeaders().getFirst("X-MSTR-AuthToken");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-MSTR-AuthToken", this.authToken);
        headers.add("X-MSTR-ProjectID", this.projectId);
        headers.addAll("Cookie", this.cookies);
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
        HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<>(headers);

        String urlPramsString = Joiner.on("&").withKeyValueSeparator("=").join(urlPrams);
        String url = UrlHelper.joinUrl(libraryUrl, "api", "searches/results?" + urlPramsString);
        ResponseEntity<JsonNode> searchResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JsonNode.class);

        return searchResponse;
    }

    public List<String> queryTopNDossiers(int topCount) {
        return null;
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        final String libraryUrl = "http://10.23.32.173:8080/MicroStrategyLibrary";
        final String projectId = "B7CA92F04B9FAE8D941C3E9B7E0CD754";

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

        headers.set("X-MSTR-AuthToken", authToken);
        headers.set("X-MSTR-ProjectID", projectId);
        headers.set("Cookie", cookies.get(0));
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "*/*");
        requestEntity = new HttpEntity<>(headers);

        final List<String> objectIds = Arrays.asList("028F2A1446B9ACA28C7ED79D75232B21",
                "05B202B9999F4C1AB960DA6208CADF3D",
                "064C5F9E4292E03172A2B390F3C9201A",
                "06EE9DB44B6E3070AC8486AC517045E0",
                "07020FC149B600D9034D068E83341B52");

        for (String objId: objectIds
             ) {
            try {
                URL url2 = new URL(String.format("%s/api/v2/reports/%s/instances?offset=0",
                        libraryUrl, objId));
                ResponseEntity<String> response2 = restTemplate.exchange(
                        url2.toString(),
                        HttpMethod.POST,
                        requestEntity,
                        String.class);
                System.out.println("response = " + response2);
            } catch (MalformedURLException e) {
                System.out.println("e = " + e);
                //e.printStackTrace();
            } catch (HttpServerErrorException e) {
                System.out.println("e = " + e);
            }
        }


    }
}
