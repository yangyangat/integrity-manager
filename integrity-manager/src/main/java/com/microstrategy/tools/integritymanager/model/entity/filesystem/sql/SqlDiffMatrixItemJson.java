package com.microstrategy.tools.integritymanager.model.entity.filesystem.sql;

import com.fasterxml.jackson.annotation.JsonProperty;

class SqlDiffMatrixItemJson {

    @JsonProperty("begin_pos")
    int beginPos;

    @JsonProperty("end_pos")
    int endPos;

    public SqlDiffMatrixItemJson() {

    }

    public SqlDiffMatrixItemJson(int beginPos, int endPos) {
        this.beginPos = beginPos;
        this.endPos = endPos;
    }
}
