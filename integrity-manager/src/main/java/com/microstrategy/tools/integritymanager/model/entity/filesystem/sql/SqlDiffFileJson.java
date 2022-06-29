package com.microstrategy.tools.integritymanager.model.entity.filesystem.sql;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

class SqlDiffFileJson {

    @JsonProperty("enabled")
    boolean enabled;

    @JsonProperty("fragment_size")
    int fragmentSize;

    @JsonProperty("files")
    List<String> files;

    public SqlDiffFileJson() {

    }

    public SqlDiffFileJson(boolean enabled, int fragmentSize, List<String> files) {
        this.enabled = enabled;
        this.fragmentSize = fragmentSize;
        this.files = files;
    }
}
