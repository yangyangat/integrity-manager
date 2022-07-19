package com.microstrategy.tools.integritymanager.model.entity.filesystem.summary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executable;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executed;

class SummaryResultJson {

    @JsonProperty("exec_result")
    SummaryExecutionResult executionResult;

    public static SummaryResultJson build(ExecutableSet executableSet,
                                          Executable executable, Executed resultInfo) {
        if (executableSet == null) {
            return null;
        }

        SummaryResultJson resultJson = new SummaryResultJson();
        resultJson.executionResult = SummaryExecutionResult.build(executableSet, executable, resultInfo);

        return resultJson;
    }

}
