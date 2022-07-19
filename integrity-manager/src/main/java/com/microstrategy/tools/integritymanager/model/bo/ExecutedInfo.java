package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.constant.enums.EnumExecutionStatus;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executed;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors (chain = true)
public class ExecutedInfo implements Executed {
    private EnumExecutionStatus executionStatus = EnumExecutionStatus.ERROR;
    private String detailedExecStatus = "\n";
    private String prevDetailedStatus = null;
    private String promptAnsDetail = null;
    private int dataRowsGenerated = 0;
    private int dataColsGenerated = 0;
    private int reportRowPageSize = 0;
    private long[][] executionTime = new long[0][];
}
