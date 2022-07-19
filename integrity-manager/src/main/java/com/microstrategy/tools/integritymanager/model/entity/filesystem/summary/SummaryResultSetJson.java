package com.microstrategy.tools.integritymanager.model.entity.filesystem.summary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;

class SummaryResultSetJson {

    @JsonProperty("exec_id")
    String execId;

    @JsonProperty("converted_exec_id")
    String convertedExecId;

    @JsonProperty("id")
    String id;

    @JsonProperty("comparison_status")
    SummaryComparisonStatusJson comparisonStatus;

    @JsonProperty("base_result")
    SummaryResultJson baseResult;

    @JsonProperty("target_result")
    SummaryResultJson targetResult;

    public static SummaryResultSetJson build(ExecutableSet executableSet) {
        if (executableSet == null) {
            return null;
        }

        SummaryResultSetJson summaryResultSetJson = new SummaryResultSetJson();
        summaryResultSetJson.execId = String.valueOf(executableSet.getExecutable().getExecID());
        summaryResultSetJson.convertedExecId = String.valueOf(executableSet.getExecutable().getConvertedExecId());
        summaryResultSetJson.id = executableSet.getExecutable().getID();
        summaryResultSetJson.comparisonStatus = SummaryComparisonStatusJson.build(executableSet);
        summaryResultSetJson.baseResult = SummaryResultJson.build(executableSet, executableSet.getExecutable(),
                executableSet.getBaseExecutedInfo());
        if (executableSet.getExecutables().size() == 2) {
            summaryResultSetJson.targetResult = SummaryResultJson.build(executableSet,
                    executableSet.getExecutables().get(1),
                    executableSet.getTargetExecutedInfo());
        }

        return summaryResultSetJson;

    }
}
