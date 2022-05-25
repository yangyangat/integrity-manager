package com.microstrategy.tools.integritymanager.model.entity.mstr;

import lombok.Data;

import java.util.List;

@Data
public class MSTRAuthToken {
    public MSTRAuthToken(String token, List<String> cookies) {
        this.token = token;
        this.cookies = cookies;
    }

    String token;
    List<String> cookies;
}
