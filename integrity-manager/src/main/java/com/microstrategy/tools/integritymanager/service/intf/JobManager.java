package com.microstrategy.tools.integritymanager.service.intf;

import com.microstrategy.tools.integritymanager.model.bo.ValidationResult;
import com.microstrategy.tools.integritymanager.model.bo.ValidationTask;

import java.util.List;
import java.util.Map;

public interface JobManager {
    List<Map<String, String>> queryJobStatus(String jobId);

    String newJob();
    void addTask(String jobId, ValidationTask task);

    List<ValidationResult> getValidationResultSet(String jobId);
}
