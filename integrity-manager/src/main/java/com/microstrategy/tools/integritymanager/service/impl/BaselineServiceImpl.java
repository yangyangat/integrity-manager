package com.microstrategy.tools.integritymanager.service.impl;

import com.microstrategy.tools.integritymanager.mapper.BaselineFileMapper;
import com.microstrategy.tools.integritymanager.model.bo.ComparisonResult;
import com.microstrategy.tools.integritymanager.model.bo.ExecutionResult;
import com.microstrategy.tools.integritymanager.model.bo.ValidataionInfo;
import com.microstrategy.tools.integritymanager.model.bo.ValidationResult;
import com.microstrategy.tools.integritymanager.model.entity.convertor.DataConvertor;
import com.microstrategy.tools.integritymanager.model.entity.filesystem.upgradeimpacts.UpgradeImpactsHolderJson;
import com.microstrategy.tools.integritymanager.service.intf.BaselineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class BaselineServiceImpl implements BaselineService {
    @Autowired
    BaselineFileMapper baselineFileMapper;

    @Override
    public void initBaseline(String jobId, String sourceLibraryUrl, List<String> sourceObjectIds, String targetLibraryUrl, List<String> targetObjectIds) {
    }

    @Override
    public void initBaseline(ValidataionInfo validationInfo) throws IOException {
        baselineFileMapper.initBaseline(validationInfo);
    }

    @Override
    public void updateSourceBaseline(String jobId, String objectId, ExecutionResult result) throws IOException {
        baselineFileMapper.updateSourceBaseline(jobId, objectId, result);
    }

    @Override
    public void updateTargetBaseline(String jobId, String objectId, ExecutionResult result) throws IOException {
        baselineFileMapper.updateTargetBaseline(jobId, objectId, result);
    }

    @Override
    public void updateValidationSummary(String jobId, List<ValidationResult> validationResultSet) throws IOException {
        baselineFileMapper.updateValidationSummary(jobId, validationResultSet);
    }

    @Override
    public void updateComparison(String jobId, String projectId, String objectId, Object comparisonResult, ExecutionResult source, ExecutionResult target) throws IOException {
        ComparisonResult result = (ComparisonResult) comparisonResult;
        //update data comparison
        List<List<Object>> sourceData = DataConvertor.restToFileSystem(source.getReport());
        List<List<Object>> targetData = DataConvertor.restToFileSystem(target.getReport());
        boolean[][] diff = result.getDiff();
        baselineFileMapper.updateDataDiff(jobId, objectId, sourceData, targetData, diff);

        //update sql comparison

        //TODO, get the sql diff for both source and target
        int sourceSqlDiff[] = result.getSourceSqlDiff();
        int targetSqlDiff[] = result.getTargetSqlDiff();
        baselineFileMapper.updateSqlDiff(jobId, objectId, sourceSqlDiff, targetSqlDiff);
    }

    @Override
    public void updateUpgradeImpacts(String jobId, UpgradeImpactsHolderJson upgradeImpacts) throws IOException {
        baselineFileMapper.updateUpgradeImpacts(jobId, upgradeImpacts);
    }

    @Override
    public void updateComparison(String jobId, String projectId, String objectId, Object comparisonResult) {

    }
}
