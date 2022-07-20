package com.microstrategy.tools.integritymanager.model.bo;

import com.fasterxml.jackson.databind.JsonNode;
import com.microstrategy.tools.integritymanager.model.bo.intf.Query;
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
public class ExecutionResult extends ExecutableInfo{

    // common information
    private EnumSet<ExecutionResultFormat> resultFormats;

    private ExecutedInfo executedInfo;

    // for report
    private String report;

    private ReportInstance reportInstance;

    private String sqlStatement;

    // for dossier and document
    private Map<String, JsonNode> mapOfViz;

    private Map<String, Query> mapOfQuery;

    private DossierDefinition hierarchyDefinition; // TODO, need to unify with Document hierarchy definition

    {
        report = "";
        executedInfo = new ExecutedInfo();
    }
}
