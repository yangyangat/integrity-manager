package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executable;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executed;
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
    private ReportExecutionResult sourceExecutionResult;

    @Setter
    @Getter
    private ReportExecutionResult targetExecutionResult;

    @Setter
    @Getter
    private ComparisonResult comparisonResult;

    @Setter
    @Getter
    private EnumComparisonStatus sQLComparisonStatus = EnumComparisonStatus.NOT_COMPARED;

    @Setter
    @Getter
    private EnumComparisonStatus dataComparisonStatusForNewSummary = EnumComparisonStatus.NOT_COMPARED;

    @Setter
    @Getter
    private EnumComparisonStatus dataComparisonStatus = EnumComparisonStatus.NOT_COMPARED;

    @Setter
    @Getter
    private int rwdDataDifferenceCount = 0;

    @Setter
    @Getter
    private int rwdSqlDifferenceCount = 0;

    @Override
    public Executable getExecutable() {
        return sourceExecutionResult;
    }

//    @Override
//    public EnumComparisonStatus getSQLComparisonStatus() {
//        return EnumComparisonStatus.MATCHED;
//    }
//
//    @Override
//    public EnumComparisonStatus getDataComparisonStatusForNewSummary() {
//        return EnumComparisonStatus.MATCHED;
//    }

//    @Override
//    public int getRwdDataDifferenceCount() {
//        return 0;
//    }
//
//    @Override
//    public int getRwdSqlDifferenceCount() {
//        return 0;
//    }

    @Override
    public int getRwdDataNodeCount() {
        return 1;
    }

    @Override
    public int getRwdSqlNodeCount() {
        return 1;
    }

    @Override
    public Executed getBaseExecutedInfo() {
        return sourceExecutionResult.getExecutedInfo();
    }

    @Override
    public List<Executable> getExecutables() {
        return Arrays.asList(sourceExecutionResult, targetExecutionResult);
    }

    @Override
    public Executed getTargetExecutedInfo() {
        return targetExecutionResult.getExecutedInfo();
    }

//    @Override
//    public EnumComparisonStatus getDataComparisonStatus() {
//        return EnumComparisonStatus.MATCHED;
//    }
}
