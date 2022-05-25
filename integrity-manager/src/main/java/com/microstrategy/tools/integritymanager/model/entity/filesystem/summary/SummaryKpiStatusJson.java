package com.microstrategy.tools.integritymanager.model.entity.filesystem.summary;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.constant.enums.EnumExecutionStatus;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;

import java.util.List;

class SummaryKpiStatusJson {

    @JsonProperty("total_executions")
    int totalExecutions;

    @JsonProperty("total_errors")
    int totalErrors;

    @JsonProperty("total_mismatches")
    int totalMismatches;

    public static SummaryKpiStatusJson build(List<? extends ExecutableSet> executableSets) {
        if (executableSets == null) {
            return null;
        }

        SummaryKpiStatusJson summaryKpiStatusJson = new SummaryKpiStatusJson();
        summaryKpiStatusJson.totalExecutions = executableSets.size();
        for (ExecutableSet executableSet : executableSets) {
            if (isErrorStatus(SummaryJsonUtils.calculateExecutionStatus(executableSet))) {
                ++summaryKpiStatusJson.totalErrors;
            }

            if (SummaryJsonUtils.calculateExecutionStatus(executableSet) != EnumExecutionStatus.ERROR
                    && (executableSet.getSQLComparisonStatus().equals(EnumComparisonStatus.NOT_MATCHED)
                        || executableSet.getDataComparisonStatusForNewSummary().equals(EnumComparisonStatus.NOT_MATCHED))) {
                ++summaryKpiStatusJson.totalMismatches;
            }

        }

        return summaryKpiStatusJson;
    }

    static boolean isErrorStatus(EnumExecutionStatus executionStatus) {
        return executionStatus == EnumExecutionStatus.ERROR
                || executionStatus == EnumExecutionStatus.NOT_SUPPORTED
                || executionStatus == EnumExecutionStatus.NOT_RUNNABLE
                || executionStatus == EnumExecutionStatus.TIMED_OUT
                || executionStatus == EnumExecutionStatus.PAUSED
                || executionStatus == EnumExecutionStatus.PROMPT_PENDING
                || executionStatus == EnumExecutionStatus.RUNNING
                || executionStatus == EnumExecutionStatus.ANALYZING;
    }
}
