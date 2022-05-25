package com.microstrategy.tools.integritymanager.model.bo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ValidationTask {
    private String sourceObjectId;
    private String targetObjectId;
    private CompletableFuture<? > sourceObjectExecution;
    private CompletableFuture<? > targetObjectExecution;
    private CompletableFuture<? > comparison;
    private ValidationResult validationResult;
}
