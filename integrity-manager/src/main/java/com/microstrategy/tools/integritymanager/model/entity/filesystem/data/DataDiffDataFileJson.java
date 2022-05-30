package com.microstrategy.tools.integritymanager.model.entity.filesystem.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

class DataDiffDataFileJson {

    @JsonProperty("enabled")
    boolean enabled;

    @JsonProperty("fragment_size")
    int fragementSize;

    @JsonProperty("files")
    List<String> files;
}
