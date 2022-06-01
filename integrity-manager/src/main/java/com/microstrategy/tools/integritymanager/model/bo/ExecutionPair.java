package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.model.entity.mstr.MSTRAuthToken;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ExecutionPair {
    String sourceObjectId;
    int sourceObjectType;
    MSTRAuthToken sourceToken;

    String targetObjectId;
    int targetObjectType;
    MSTRAuthToken targetToken;

    int executionId;
}
