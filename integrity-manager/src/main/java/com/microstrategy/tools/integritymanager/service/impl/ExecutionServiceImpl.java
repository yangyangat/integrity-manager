package com.microstrategy.tools.integritymanager.service.impl;

import com.microstrategy.tools.integritymanager.exception.ReportExecutionException;
import com.microstrategy.tools.integritymanager.exception.ReportExecutorInternalException;
import com.microstrategy.tools.integritymanager.executor.ObjectInfoExecutor;
import com.microstrategy.tools.integritymanager.executor.RestParams;
import com.microstrategy.tools.integritymanager.model.entity.mstr.MSTRAuthToken;
import com.microstrategy.tools.integritymanager.executor.ReportExecutor;
import com.microstrategy.tools.integritymanager.model.entity.mstr.ObjectInfo;
import com.microstrategy.tools.integritymanager.service.intf.ExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

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

    @Override
    public CompletableFuture<ObjectInfo> executeObjectInfoAsync(String libraryUrl, MSTRAuthToken token, String projectId, String objectId, int objectType, Object options, ExecutorService executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.executeObjectInfo(libraryUrl, token, projectId, objectId, objectType);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }, executor).whenComplete((u, v)->{

        });
    }

    private ObjectInfo executeObjectInfo(String libraryUrl, MSTRAuthToken token, String projectId, String objectId, int objectType) {
        RestParams restParams = new RestParams()
                .setAuthToken(token.getToken())
                .setCookies(token.getCookies())
                .setLibraryUrl(libraryUrl)
                .setProjectId(projectId);

        ObjectInfoExecutor objectInfoExecutor = ObjectInfoExecutor.build().setRestParams(restParams);
        Map<String, Object> urlPrams = Map.of(
                "type", Integer.valueOf(objectType)
        );
        ResponseEntity<ObjectInfo> response = objectInfoExecutor.execute(objectId, urlPrams, ObjectInfo.class);
        return response.getBody();
    }
}
