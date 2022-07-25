package com.microstrategy.tools.integritymanager.service.impl;

import com.microstrategy.tools.integritymanager.model.entity.mstr.MSTRAuthToken;
import com.microstrategy.tools.integritymanager.executor.SearchExecutor;
import com.microstrategy.tools.integritymanager.service.intf.SearchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    @Override
    public List<String> getTopNReportIds(String libraryUrl, MSTRAuthToken token, String projectId, int topCount) {
        SearchExecutor searchExecutor = SearchExecutor.build()
                .setLibraryUrl(libraryUrl).setCookies(token.getCookies())
                .setAuthToken(token.getToken()).setProjectId(projectId);
        return searchExecutor.queryTopNReports(topCount);
    }

    @Override
    public List<String> getTopNDossierIds(String libraryUrl, MSTRAuthToken token, String projectId, int topCount) {
        SearchExecutor searchExecutor = SearchExecutor.build()
                .setLibraryUrl(libraryUrl).setCookies(token.getCookies())
                .setAuthToken(token.getToken()).setProjectId(projectId);
        return searchExecutor.queryTopNDossiers(topCount);
    }
}
