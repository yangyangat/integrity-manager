package com.microstrategy.tools.integritymanager.service.intf;

import com.microstrategy.tools.integritymanager.model.entity.mstr.MSTRAuthToken;

import java.util.List;

public interface SearchService {
    List<String> getTopNReportIds(String libraryUrl, MSTRAuthToken token, String projectId, int topCount);

    List<String> getTopNDossierIds(String libraryUrl, MSTRAuthToken token, String projectId, int topCount);
}
