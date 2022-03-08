package com.microstrategy.tools.integritymanager.model.appobject;

import com.microstrategy.tools.integritymanager.model.bizobject.MSTRAuthToken;
import lombok.Data;

@Data
public class ExecutionPair {
    public ExecutionPair(String sourceObjectId, int sourceObjectType, MSTRAuthToken sourceToken,
                         String targetObjectId, int targetObjectType, MSTRAuthToken targetToken) {
        this.sourceObjectId = sourceObjectId;
        this.sourceObjectType = sourceObjectType;
        this.sourceToken = sourceToken;
        this.targetObjectId = targetObjectId;
        this.targetObjectType = targetObjectType;
        this.targetToken = targetToken;
    }
    String sourceObjectId;
    int sourceObjectType;
    MSTRAuthToken sourceToken;

    String targetObjectId;
    int targetObjectType;
    MSTRAuthToken targetToken;
}
