package com.microstrategy.tools.integritymanager.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.microstrategy.tools.integritymanager.model.entity.mstr.ObjectInfo;
import com.microstrategy.tools.integritymanager.util.UrlHelper;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectInfoExecutor {

    @Setter
    @Accessors(chain = true)
    private RestParams restParams;

    public static ObjectInfoExecutor build() {
        return new ObjectInfoExecutor();
    }

    private ObjectInfoExecutor() {}

    public ResponseEntity<JsonNode> execute(String objectId, Map<String, Object> urlPrams) {
        return execute(objectId, urlPrams, JsonNode.class);
    }

    public <T> ResponseEntity<T> execute(String objectId, Map<String, Object> urlPrams, Class<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();
        String authToken = restParams.getAuthToken();
        List<String> cookies = restParams.getCookies();
        String libraryUrl = restParams.getLibraryUrl();
        String projectId = restParams.getProjectId();

        if (authToken.isEmpty() && cookies.isEmpty()) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-MSTR-AuthToken", authToken);
        headers.add("X-MSTR-ProjectID", projectId);
        headers.addAll("Cookie", cookies);
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
        HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<>(headers);

        String urlPramsString = urlPrams != null ? Joiner.on("&").withKeyValueSeparator("=").join(urlPrams) : "";
        String url = UrlHelper.joinUrl(libraryUrl, "api", "objects", objectId + "?" + urlPramsString);

        try {
            ResponseEntity<T> objectInfoResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);
            return objectInfoResponse;
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            return null;
        }
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

        final List<String> objectIds = Arrays.asList("13CFD83A458A68655A13CBA8D7C62CD5");

        for (String objId: objectIds
        ) {
            try {
                ObjectInfoExecutor objectInfoExecutor = ObjectInfoExecutor.build().setRestParams(restParams);
                Map<String, Object> urlPrams = Map.of(
                        "type", Integer.valueOf(3)
                );
                ResponseEntity<ObjectInfo> response2 = objectInfoExecutor.execute(objId, urlPrams, ObjectInfo.class);
                System.out.println("response = " + response2);
            } catch (HttpServerErrorException e) {
                System.out.println("e = " + e);
            }
        }
    }
}
