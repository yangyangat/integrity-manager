package com.microstrategy.tools.integritymanager.model.entity.filesystem.upgradeimpacts;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

class UpgradeImpactsDependency {

    @JsonProperty("oid")
    String oid;

    @JsonProperty("oname")
    String oname;

    @JsonProperty("did")
    String did;

    @JsonProperty("dname")
    String dname;

    @JsonProperty("dpath")
    String dpath;

    @JsonProperty("pid")
    String projectId;

    public void populate(String oneline, String projectId) {
        if (!StringUtils.hasLength(oneline)) {
            return;
        }
        String[] items = oneline.split(",");
        this.oid = items[0].replace("\"", "");
        this.oname = items[1].replace("\"", "");
        this.did = items[2].replace("\"", "");
        this.dname = items[3].replace("\"", "");
        this.dpath = items[4].replace("\"", "");
        this.projectId = projectId.replace("\"", "");
    }
}
