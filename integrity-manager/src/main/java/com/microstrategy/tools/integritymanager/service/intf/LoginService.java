package com.microstrategy.tools.integritymanager.service.intf;

import com.microstrategy.tools.integritymanager.model.entity.mstr.MSTRAuthToken;

import java.util.List;

public interface LoginService {
    MSTRAuthToken login(String libraryUrl, String username, String password);

    List<MSTRAuthToken> login(String libraryUrl, String username, String password, int count);
}
