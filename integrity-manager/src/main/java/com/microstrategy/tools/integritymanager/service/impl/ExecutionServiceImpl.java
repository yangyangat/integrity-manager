package com.microstrategy.tools.integritymanager.service.impl;

import com.microstrategy.tools.integritymanager.exception.ReportExecutionException;
import com.microstrategy.tools.integritymanager.exception.ReportExecutorInternalException;
import com.microstrategy.tools.integritymanager.model.bizobject.MSTRAuthToken;
import com.microstrategy.tools.integritymanager.executor.ReportExecutor;
import com.microstrategy.tools.integritymanager.service.intf.ExecutionService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class ExecutionServiceImpl implements ExecutionService {
    @Override
    public String execute(String libraryUrl, MSTRAuthToken token, String projectId, String objectId, int objectType, Object options)
            throws ReportExecutorInternalException, ReportExecutionException {
        ReportExecutor reportExecutor = ReportExecutor.build()
                .setLibraryUrl(libraryUrl).setCookie(token.getCookies().get(0))
                .setAuthToken(token.getToken()).setProjectId(projectId).setReportId(objectId);

        return reportExecutor.execute();
    }

    @Override
    public CompletableFuture<String> executeAsync(String libraryUrl, MSTRAuthToken token, String projectId, String objectId, int objectType, Object options, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            // Code to download and return the web page's content
            try {
                return this.execute(libraryUrl, token, projectId, objectId, objectType, options);
            } catch (Exception e) {
                //e.printStackTrace();
                throw new IllegalStateException(e);
            }
        }, executor).whenComplete((u, v)->{
            if (v == null) {
                System.out.println("Report : " + objectId + " result returned!");
            }
            else {
                System.out.println("Error when executing report: " + objectId);
                System.out.println("Error message: "  + v.getMessage());
            }
        });
    }
}
