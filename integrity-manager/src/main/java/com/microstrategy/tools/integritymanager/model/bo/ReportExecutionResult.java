package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.constant.enums.EnumExecutionStatus;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executed;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ExecutionResultFormat;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ReportInstance;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.EnumSet;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ReportExecutionResult extends ExecutableInfo{

    private String report = "";

    private ReportInstance reportInstance;

    private EnumSet<ExecutionResultFormat> resultFormats;

    private String sqlStatement;

    private ExecutedInfo executedInfo;

    {
        executedInfo = new ExecutedInfo();
    }
}
