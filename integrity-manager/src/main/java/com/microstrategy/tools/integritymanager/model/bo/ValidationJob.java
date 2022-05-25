package com.microstrategy.tools.integritymanager.model.bo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ValidationJob {
    //@Getter
    //@Setter
    //private String libraryUrl;

    private final ArrayList<ValidationTask> tasks;

    public ValidationJob() {
        tasks = new ArrayList<>();
    }

    public void addTask(ValidationTask task) {
        tasks.add(task);
    }

    public List<Map<String, String>> queryStatus() {
        return tasks.stream().map(task -> this.queryTaskStatus(task)).collect(Collectors.toList());
    }

    private String queryOneExecutionStatus(CompletableFuture<? > execution) {
        String status;
        if (!execution.isDone()) {
            status = "Running";
        }
        else if (execution.isCompletedExceptionally()) {
            status = "Done with execption";
        }
        else {
            status = "Done";
        }

        return status;
    }

    private Map<String, String> queryTaskStatus(ValidationTask task) {
        String sourceExecutionStatus = this.queryOneExecutionStatus(task.getSourceObjectExecution());
        String targetExecutionStatus = this.queryOneExecutionStatus(task.getTargetObjectExecution());
        String comparisonStatus = this.queryOneExecutionStatus(task.getComparison());

        return Map.of(
                "source", task.getSourceObjectId() + "'s status is: " + sourceExecutionStatus,
                "target", task.getTargetObjectId() + "'s status is: " + targetExecutionStatus,
                "comparison", "The comparison execution is " + comparisonStatus
        );
    }

    public List<ValidationResult> getValidationResultSet() {
        return tasks.stream().map(task -> task.getValidationResult()).collect(Collectors.toList());
    }
}
