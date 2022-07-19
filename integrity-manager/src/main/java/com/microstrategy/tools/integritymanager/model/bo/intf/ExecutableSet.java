package com.microstrategy.tools.integritymanager.model.bo.intf;

import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;

import java.util.List;

public interface ExecutableSet {
    Executable getExecutable();

    EnumComparisonStatus getSQLComparisonStatus();

    EnumComparisonStatus getDataComparisonStatusForNewSummary();

    int getRwdDataDifferenceCount();

    int getRwdSqlDifferenceCount();

    int getRwdDataNodeCount();

    int getRwdSqlNodeCount();

    Executed getBaseExecutedInfo();

    List<Executable> getExecutables();

    Executed getTargetExecutedInfo();

    EnumComparisonStatus getDataComparisonStatus();
}
