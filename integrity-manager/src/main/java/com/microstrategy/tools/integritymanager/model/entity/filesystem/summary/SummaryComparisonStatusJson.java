package com.microstrategy.tools.integritymanager.model.entity.filesystem.summary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;

class SummaryComparisonStatusJson {

    @JsonProperty("execution_status")
    String executionStatus;

    @JsonProperty("execution_status_val")
    int executionStatusVal;

    @JsonProperty("sql_status")
    String sqlStatus;

    @JsonProperty("sql_status_val")
    int sqlStatusVal;

    @JsonProperty("data_status")
    String dataStatus;

    @JsonProperty("data_status_val")
    int dataStatusVal;

    @JsonProperty("data_mismatch_count")
    int dataMismatchCount;

    @JsonProperty("sql_mismatch_count")
    int sqlMismatchCount;

    @JsonProperty("data_node_count")
    int dataNodeCount;

    @JsonProperty("sql_node_count")
    int sqlNodeCount;

    public static SummaryComparisonStatusJson build(ExecutableSet executableSet) {
        if (executableSet == null) {
            return null;
        }

        SummaryComparisonStatusJson summaryComparisonStatusJson = new SummaryComparisonStatusJson();
        summaryComparisonStatusJson.executionStatus =
                SummaryJsonUtils.calculateExecutionStatus(executableSet).toString();
        summaryComparisonStatusJson.executionStatusVal =
                SummaryJsonUtils.calculateExecutionStatus(executableSet).getType();
        summaryComparisonStatusJson.sqlStatus = executableSet.getSQLComparisonStatus().toString();
        summaryComparisonStatusJson.sqlStatusVal = executableSet.getSQLComparisonStatus().getType();
        summaryComparisonStatusJson.dataStatus = executableSet.getDataComparisonStatusForNewSummary().toString();
        summaryComparisonStatusJson.dataStatusVal = executableSet.getDataComparisonStatusForNewSummary().getType();
        summaryComparisonStatusJson.dataMismatchCount = executableSet.getRwdDataDifferenceCount();
        summaryComparisonStatusJson.sqlMismatchCount = executableSet.getRwdSqlDifferenceCount();
        summaryComparisonStatusJson.dataNodeCount = executableSet.getRwdDataNodeCount();
        summaryComparisonStatusJson.sqlNodeCount = executableSet.getRwdSqlNodeCount();

        return summaryComparisonStatusJson;
    }

}
