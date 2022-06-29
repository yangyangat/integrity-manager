package com.microstrategy.tools.integritymanager.model.bo;

import com.fasterxml.jackson.databind.JsonNode;
import com.microstrategy.tools.integritymanager.constant.enums.EnumExecutionStatus;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutionResult;
import com.microstrategy.tools.integritymanager.model.entity.mstr.ObjectInfo;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ExecutionResultFormat;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ReportInstance;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.EnumSet;

@NoArgsConstructor
@Accessors(chain = true)
public class ReportExecutionResult extends ExecutableInfo implements ExecutionResult {
    @Setter
    @Getter
    private String report = "";

    @Setter
    @Getter
    private ReportInstance reportInstance;

    @Setter
    @Getter
    private EnumSet<ExecutionResultFormat> resultFormats;

    @Setter
    @Getter
    private String sqlStatement;

    @Setter
    @Getter
    private String detailedExecStatus = "\n";

    public static ReportExecutionResult build() {
        return new ReportExecutionResult();
    }

    @Override
    public EnumExecutionStatus getExecutionStatus() {
        return EnumExecutionStatus.ERROR;
    }

//    @Override
//    public String getDetailedExecStatus() {
//        return null;
//    }

    @Override
    public String getPrevDetailedStatus() {
        return null;
    }

    @Override
    public String getPromptAnsDetail() {
        return null;
    }

    @Override
    public int getDataRowsGenerated() {
        return 0;
    }

    @Override
    public int getDataColsGenerated() {
        return 0;
    }

    @Override
    public int getReportRowPageSize() {
        return 0;
    }

    @Override
    public long[][] getExecutionTime() {
        return new long[0][];
    }
}
