package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.constant.enums.EnumViewMedia;
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
    EnumViewMedia sourceViewMedia;
    MSTRAuthToken sourceToken;

    String targetObjectId;
    int targetObjectType;
    EnumViewMedia targetViewMedia;
    MSTRAuthToken targetToken;

    int executionId;
}
