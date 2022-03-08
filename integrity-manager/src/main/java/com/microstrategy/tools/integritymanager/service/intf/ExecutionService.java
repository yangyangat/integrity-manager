package com.microstrategy.tools.integritymanager.service.intf;

import com.microstrategy.tools.integritymanager.exception.ReportExecutionException;
import com.microstrategy.tools.integritymanager.exception.ReportExecutorInternalException;
import com.microstrategy.tools.integritymanager.model.bizobject.MSTRAuthToken;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface ExecutionService {
    String execute(String libraryUrl, MSTRAuthToken token, String projectId, String objectId, int objectType, Object options)
            throws ReportExecutorInternalException, ReportExecutionException;

    CompletableFuture<String> executeAsync(String libraryUrl, MSTRAuthToken token, String projectId, String objectId, int objectType, Object options, Executor executor);
}
