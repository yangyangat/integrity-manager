package com.microstrategy.tools.integritymanager.service.intf;

import com.microstrategy.tools.integritymanager.model.bizobject.MSTRAuthToken;

public interface LoginService {
    MSTRAuthToken login(String libraryUrl, String username, String password);
}
