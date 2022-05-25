package com.microstrategy.tools.integritymanager.model.entity.filesystem.summary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executable;

class SummaryTestedObjectJson {
    @JsonProperty("type")
    int type;

    @JsonProperty("guid")
    String id;

    @JsonProperty("name")
    String name;

    @JsonProperty("path")
    String path;

    @JsonProperty("report_type")
    int reportType;

    @JsonProperty("rounds")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer rounds = null;

    public static SummaryTestedObjectJson build(Executable executable) {
        SummaryTestedObjectJson summaryTestedObjectJson = new SummaryTestedObjectJson();
        summaryTestedObjectJson.type = executable.getType();
        summaryTestedObjectJson.id = executable.getID();
        summaryTestedObjectJson.name = executable.getName();
        summaryTestedObjectJson.path = executable.getPath();
        summaryTestedObjectJson.reportType = executable.getSubType();

        return summaryTestedObjectJson;
    }
}
