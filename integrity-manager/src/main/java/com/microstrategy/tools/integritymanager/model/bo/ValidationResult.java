package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executable;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutionResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Accessors(chain = true)
public class ValidationResult implements ExecutableSet {

    @Setter
    @Getter
    ReportExecutionResult sourceExecutionResult;

    @Setter
    @Getter
    ReportExecutionResult targetExecutionResult;

    @Setter
    @Getter
    ComparisonResult comparisonResult;

    @Override
    public Executable getExecutable() {
        return sourceExecutionResult;
    }

    @Override
    public EnumComparisonStatus getSQLComparisonStatus() {
        return EnumComparisonStatus.MATCHED;
    }

    @Override
    public EnumComparisonStatus getDataComparisonStatusForNewSummary() {
        return EnumComparisonStatus.MATCHED;
    }

    @Override
    public int getRwdDataDifferenceCount() {
        return 0;
    }

    @Override
    public int getRwdSqlDifferenceCount() {
        return 0;
    }

    @Override
    public int getRwdDataNodeCount() {
        return 1;
    }

    @Override
    public int getRwdSqlNodeCount() {
        return 1;
    }

    @Override
    public ExecutionResult getBaseExecutionResult() {
        return sourceExecutionResult;
    }

    @Override
    public List<Executable> getExecutables() {
        return Arrays.asList(sourceExecutionResult, targetExecutionResult);
    }

    @Override
    public ExecutionResult getTargetExecutionResult() {
        return targetExecutionResult;
    }

    @Override
    public EnumComparisonStatus getDataComparisonStatus() {
        return EnumComparisonStatus.MATCHED;
    }
}
