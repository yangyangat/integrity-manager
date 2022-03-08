package com.microstrategy.tools.integritymanager.model.appobject;

import lombok.Data;

import java.util.concurrent.CompletableFuture;

@Data
public class ValidationTask {
    public ValidationTask(String sourceObjectId,
                          String targetObjectId,
                          CompletableFuture<? > sourceObjectExecution,
                          CompletableFuture<? > targetObjectExecution,
                          CompletableFuture<? > comparison) {
        this.sourceObjectId = sourceObjectId;
        this.targetObjectId = targetObjectId;
        this.sourceObjectExecution = sourceObjectExecution;
        this.targetObjectExecution = targetObjectExecution;
        this.comparison = comparison;
    }
    public ValidationTask() {}
    private String sourceObjectId;
    private String targetObjectId;
    private CompletableFuture<? > sourceObjectExecution;
    private CompletableFuture<? > targetObjectExecution;
    private CompletableFuture<? > comparison;
}
