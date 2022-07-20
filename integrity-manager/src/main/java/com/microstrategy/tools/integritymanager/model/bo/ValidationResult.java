package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executable;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executed;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ValidationResult implements ExecutableSet {

    private ExecutionResult sourceExecutionResult;

    private ExecutionResult targetExecutionResult;

    private ComparisonResult comparisonResult;

    private ComparedInfo comparedInfo;

    {
        comparedInfo = new ComparedInfo();
    }

    @Override
    public EnumComparisonStatus getSQLComparisonStatus() {
        return comparedInfo.getSQLComparisonStatus();
    }

    @Override
    public EnumComparisonStatus getDataComparisonStatusForNewSummary() {
        return comparedInfo.getDataComparisonStatusForNewSummary();
    }

    @Override
    public EnumComparisonStatus getDataComparisonStatus() {
        return comparedInfo.getDataComparisonStatus();
    }

    @Override
    public int getRwdDataDifferenceCount() {
        return comparedInfo.getRwdDataDifferenceCount();
    }

    @Override
    public int getRwdSqlDifferenceCount() {
        return comparedInfo.getRwdSqlDifferenceCount();
    }

    @Override
    public int getRwdDataNodeCount() {
        return comparedInfo.getRwdDataNodeCount();
    }

    @Override
    public int getRwdSqlNodeCount() {
        return comparedInfo.getRwdSqlNodeCount();
    }

    @Override
    public Executable getExecutable() {
        return sourceExecutionResult;
    }

    @Override
    public List<Executable> getExecutables() {
        return Arrays.asList(sourceExecutionResult, targetExecutionResult);
    }

    @Override
    public Executed getBaseExecutedInfo() {
        return sourceExecutionResult.getExecutedInfo();
    }

    @Override
    public Executed getTargetExecutedInfo() {
        return targetExecutionResult.getExecutedInfo();
    }
}
