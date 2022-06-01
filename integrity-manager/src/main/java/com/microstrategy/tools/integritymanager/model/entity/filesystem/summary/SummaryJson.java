package com.microstrategy.tools.integritymanager.model.entity.filesystem.summary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;

import java.util.ArrayList;
import java.util.List;

public class SummaryJson {

    @JsonProperty("test_config")
    SummaryTestConfigJson testConfig;

    @JsonProperty("execution_status")
    SummaryExecutionStatusJson executionStatus;
    @JsonProperty("result_sets")
    List<SummaryResultSetJson> resultSets;

    /**
     * Build the summary json object.
     * @param executableSets The test model.
     * @return The object.
     */
    public static SummaryJson build(List<? extends ExecutableSet> executableSets) {
        if (executableSets == null) {
            return null;
        }

        SummaryJson summaryJson = new SummaryJson();
        summaryJson.testConfig = SummaryTestConfigJson.build(executableSets);
        summaryJson.executionStatus = SummaryExecutionStatusJson.build(executableSets);
        if (executableSets != null) {
            summaryJson.resultSets = new ArrayList<>(executableSets.size());
            for (ExecutableSet executableSet : executableSets) {
                summaryJson.resultSets.add(SummaryResultSetJson.build(executableSet));
            }
        }

        return summaryJson;
    }
}
