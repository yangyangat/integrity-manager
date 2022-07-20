package com.microstrategy.tools.integritymanager.service.intf;

import com.microstrategy.tools.integritymanager.model.bo.ExecutionResult;
import com.microstrategy.tools.integritymanager.model.bo.ValidataionInfo;
import com.microstrategy.tools.integritymanager.model.bo.ValidationResult;
import com.microstrategy.tools.integritymanager.model.entity.filesystem.upgradeimpacts.UpgradeImpactsHolderJson;

import java.io.IOException;
import java.util.List;

public interface BaselineService {

    void initBaseline(String jobId, String sourceLibraryUrl, List<String> sourceObjectIds, String targetLibraryUrl, List<String> targetObjectIds);

    void updateComparison(String jobId, String projectId, String objectId, Object comparisonResult);

    void initBaseline(ValidataionInfo validationInfo) throws IOException;

    void updateSourceBaseline(String jobId, String objectId, ExecutionResult result) throws IOException;

    void updateTargetBaseline(String jobId, String objectId, ExecutionResult result) throws IOException;

    void updateValidationSummary(String jobId, List<ValidationResult> validationResultSet) throws IOException ;

    void updateComparison(String jobId, String projectId, String objectId, Object comparisonResult, ExecutionResult source, ExecutionResult target) throws IOException;

    void updateUpgradeImpacts(String jobId, UpgradeImpactsHolderJson upgradeImpacts) throws IOException;
}
