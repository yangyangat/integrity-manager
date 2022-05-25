package com.microstrategy.tools.integritymanager.model.entity.filesystem.upgradeimpacts;

import com.fasterxml.jackson.annotation.JsonProperty;
//import com.microstrategy.matester.common.MiscUtilities;
//import com.microstrategy.tools.mautils.log.MAJLogger;

import java.util.List;

class UpgradeImpactJson {

    @JsonProperty("datetime")
    String dateTime;

    @JsonProperty("host")
    String host;

    @JsonProperty("server")
    String server;

    @JsonProperty("pid")
    String pid;

    @JsonProperty("thr")
    String thr;

    @JsonProperty("uid")
    String uid;

    @JsonProperty("sid")
    String sid;

    @JsonProperty("project_id")
    String projectId;

    @JsonProperty("oid")
    String objectId;

    @JsonProperty("oname")
    String oname;

    @JsonProperty("tid")
    String tid;

    @JsonProperty("tname")
    String tname;

    @JsonProperty("refid")
    String refId;

    public void populate(List<String> upgradeImpactData) {
        if (upgradeImpactData == null || upgradeImpactData.size() != 13) {
//            MAJLogger.getLogger(MiscUtilities.COMPONENT_NAME).error("Unable to parse upgrade "
//                    + "impact data: the column number is not 13.");
            return;
        }
        this.dateTime = upgradeImpactData.get(0);
        this.host = upgradeImpactData.get(1);
        this.server = upgradeImpactData.get(2);
        this.pid = upgradeImpactData.get(3);
        this.thr = upgradeImpactData.get(4);
        this.uid = upgradeImpactData.get(5);
        this.sid = upgradeImpactData.get(6);
        this.projectId = upgradeImpactData.get(7);
        this.objectId = upgradeImpactData.get(8);
        this.oname = upgradeImpactData.get(9);
        this.tid = upgradeImpactData.get(10);
        this.tname = upgradeImpactData.get(11);
        this.refId = upgradeImpactData.get(12);
    }
}
