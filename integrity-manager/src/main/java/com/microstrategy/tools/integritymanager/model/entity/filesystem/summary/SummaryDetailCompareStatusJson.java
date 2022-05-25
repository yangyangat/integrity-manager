package com.microstrategy.tools.integritymanager.model.entity.filesystem.summary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;


import java.util.List;
import java.util.function.Function;


class SummaryDetailCompareStatusJson {

    @JsonProperty("not_compared")
    int notCompared;

    @JsonProperty("matched")
    int matched;

    @JsonProperty("not_matched")
    int notMatched;

    @JsonProperty("error")
    int error;

    public static SummaryDetailCompareStatusJson build(List<? extends ExecutableSet> executableSets,
                                                 Function<ExecutableSet, EnumComparisonStatus> comparisonStatusSupplier) {
        if (executableSets == null) {
            return null;
        }

        SummaryDetailCompareStatusJson summaryDetailCompareStatusJson = new SummaryDetailCompareStatusJson();

        for (ExecutableSet executableSet : executableSets) {
            switch (comparisonStatusSupplier.apply(executableSet).getType()) {
                case EnumComparisonStatus.NOT_COMPARED_VAL: {
                    ++summaryDetailCompareStatusJson.notCompared;
                    break;
                }
                case EnumComparisonStatus.MATCHED_VAL: {
                    ++summaryDetailCompareStatusJson.matched;
                    break;
                }
                case EnumComparisonStatus.NOT_MATCHED_VAL: {
                    ++summaryDetailCompareStatusJson.notMatched;
                    break;
                }
                case EnumComparisonStatus.ERROR_VAL: {
                    ++summaryDetailCompareStatusJson.error;
                    break;
                }
                default: {
                    // This would never happen. If it does, let the program crash!
                    throw new IllegalStateException("Invalid comparison status enum");
                }
            }
        }

        return summaryDetailCompareStatusJson;
    }
}
