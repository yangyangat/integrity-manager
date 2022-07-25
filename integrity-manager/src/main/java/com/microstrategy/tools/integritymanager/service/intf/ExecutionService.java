package com.microstrategy.tools.integritymanager.service.intf;

import com.microstrategy.tools.integritymanager.constant.enums.EnumViewMedia;
import com.microstrategy.tools.integritymanager.exception.ReportExecutionException;
import com.microstrategy.tools.integritymanager.exception.ReportExecutorInternalException;
import com.microstrategy.tools.integritymanager.model.entity.mstr.MSTRAuthToken;
import com.microstrategy.tools.integritymanager.model.entity.mstr.ObjectInfo;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public interface ExecutionService {
    Object execute(String libraryUrl, MSTRAuthToken token, String projectId, String objectId, int objectType, EnumViewMedia viewMedia, Object options)
            throws ReportExecutorInternalException, ReportExecutionException;

    <T> ResponseEntity<T> execute(String libraryUrl, MSTRAuthToken token, String projectId, String objectId, int objectType, EnumViewMedia viewMedia, Object options, Class<T> responseType)
            throws ReportExecutorInternalException, ReportExecutionException;

    CompletableFuture<Object> executeAsync(String libraryUrl, MSTRAuthToken token, String projectId, String objectId, int objectType, EnumViewMedia viewMedia, Object options, Executor executor);

    <T> CompletableFuture<T> executeAsync(String libraryUrl, MSTRAuthToken token, String projectId, String objectId, int objectType, EnumViewMedia viewMedia, Object options, Executor executor, Class<T> responseType);

    CompletableFuture<ObjectInfo> executeObjectInfoAsync(String libraryUrl, MSTRAuthToken token, String projectId, String objectId, int objectType, Object options, ExecutorService executor);
}
