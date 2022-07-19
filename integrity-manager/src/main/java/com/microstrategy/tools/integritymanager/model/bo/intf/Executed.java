package com.microstrategy.tools.integritymanager.model.bo.intf;

import com.microstrategy.tools.integritymanager.constant.enums.EnumExecutionStatus;

public interface Executed {
    EnumExecutionStatus getExecutionStatus();

    String getDetailedExecStatus();

    String getPrevDetailedStatus();

    String getPromptAnsDetail();

    int getDataRowsGenerated();

    int getDataColsGenerated();

    int getReportRowPageSize();

    long[][] getExecutionTime();
}
