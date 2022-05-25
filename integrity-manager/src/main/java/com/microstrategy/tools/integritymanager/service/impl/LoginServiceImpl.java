package com.microstrategy.tools.integritymanager.service.impl;

import com.microstrategy.tools.integritymanager.model.entity.mstr.MSTRAuthToken;
import com.microstrategy.tools.integritymanager.service.intf.LoginService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Override
    public MSTRAuthToken login(String libraryUrl, String username, String password) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> postBody = Map.of(
                "username", username,
                "password", password
        );
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<>(postBody, headers);

        String url = String.join("/", libraryUrl, "api", "auth/login");
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            String authToken = response.getHeaders().getFirst("X-MSTR-AuthToken");
            return new MSTRAuthToken(authToken, cookies);
        }

        return null;
    }

    @Override
    public List<MSTRAuthToken> login(String libraryUrl, String username, String password, int count) {
        List<MSTRAuthToken> tokenList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            MSTRAuthToken token = this.login(libraryUrl, username, password);
            if (token != null) {
                tokenList.add(token);
            }
        }
        return tokenList;
    }
}
