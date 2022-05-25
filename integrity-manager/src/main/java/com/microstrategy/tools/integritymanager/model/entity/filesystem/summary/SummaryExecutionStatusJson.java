package com.microstrategy.tools.integritymanager.model.entity.filesystem.summary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;

import java.util.List;

class SummaryExecutionStatusJson {

    @JsonProperty("kpi_status")
    SummaryKpiStatusJson kpiStatus;

    @JsonProperty("sql_mode_status")
    SummaryDetailCompareStatusJson sqlModeStatus;

    @JsonProperty("data_mode_status")
    SummaryDetailCompareStatusJson dataModeStatus;

    public static SummaryExecutionStatusJson build(List<? extends ExecutableSet> executableSets) {
        if (executableSets == null) {
            return null;
        }

        SummaryExecutionStatusJson summaryExecutionStatusJson = new SummaryExecutionStatusJson();

        summaryExecutionStatusJson.kpiStatus = SummaryKpiStatusJson.build(executableSets);
        summaryExecutionStatusJson.sqlModeStatus = SummaryDetailCompareStatusJson.build(executableSets,
                ExecutableSet::getSQLComparisonStatus);
        summaryExecutionStatusJson.dataModeStatus = SummaryDetailCompareStatusJson.build(executableSets,
                ExecutableSet::getDataComparisonStatusForNewSummary);

        return summaryExecutionStatusJson;

    }
}
