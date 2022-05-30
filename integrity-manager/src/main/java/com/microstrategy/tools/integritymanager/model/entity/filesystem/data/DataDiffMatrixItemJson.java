package com.microstrategy.tools.integritymanager.model.entity.filesystem.data;

import com.fasterxml.jackson.annotation.JsonProperty;

class DataDiffMatrixItemJson {

    @JsonProperty("row")
    int row;

    @JsonProperty("col")
    int col;

    public DataDiffMatrixItemJson() {

    }

    public DataDiffMatrixItemJson(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
