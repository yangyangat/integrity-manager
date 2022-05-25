package com.microstrategy.tools.integritymanager.model.entity.filesystem.summary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;

import java.util.ArrayList;
import java.util.List;

class SummaryTestConfigJson {

    @JsonProperty("tested_objects")
    List<SummaryTestedObjectJson> testedObjects;

    public static SummaryTestConfigJson build(List<? extends ExecutableSet> executableSets) {
        if (executableSets == null) {
            return null;
        }
        SummaryTestConfigJson summaryTestConfigJson = new SummaryTestConfigJson();
        summaryTestConfigJson.testedObjects = new ArrayList<>(executableSets.size());
        for (ExecutableSet executableSet : executableSets) {
            summaryTestConfigJson.testedObjects.add(SummaryTestedObjectJson.build(executableSet.getExecutable()));
        }

        return summaryTestConfigJson;
    }
}
