package com.microstrategy.tools.integritymanager.model.bo;

import com.fasterxml.jackson.databind.JsonNode;
import com.microstrategy.tools.integritymanager.constant.enums.EnumExecutableType;
import com.microstrategy.tools.integritymanager.model.bo.intf.Query;
import com.microstrategy.tools.integritymanager.model.entity.mstr.document.DocumentQueryDetails;
import com.microstrategy.tools.integritymanager.model.entity.mstr.dossier.DossierDefinition;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ExecutionResultFormat;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ReportInstance;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.EnumSet;
import java.util.Map;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ExecutionResult extends ExecutableInfo {

    // common information
    private EnumSet<ExecutionResultFormat> resultFormats;

    private ExecutedInfo executedInfo;

    private ReportExecutionResult reportExecutionResult;

    private Map<String, ReportExecutionResult> mapOfVizResult;

    private DossierDefinition hierarchyDefinition; // TODO, need to unify with Document hierarchy definition, "documentDefinition"

    private DocumentQueryDetails documentDefinition;

    private String pdfInString;

    private byte[] excelInByte;

    {
        executedInfo = new ExecutedInfo();
    }

    public void copyResult(ExecutionResult result) {
        if (result == null)
            return;
        this.executedInfo = result.getExecutedInfo();
        this.reportExecutionResult = result.getReportExecutionResult();
        this.mapOfVizResult = result.getMapOfVizResult();
        this.hierarchyDefinition = result.getHierarchyDefinition();
        this.documentDefinition = result.getDocumentDefinition();
        this.pdfInString = result.getPdfInString();
        this.excelInByte = result.getExcelInByte();
    }

    public EnumExecutableType getExecutableType() {
        return EnumExecutableType.fromObjectTypeAndViewMedial(this.getType(), this.getViewMedia());
    }
}
