package com.microstrategy.tools.integritymanager.model.bo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ValidataionInfo {
    private String jobId;
    private String sourceLibraryUrl;
    private List<String> sourceObjectIds;
    private String targetLibraryUrl;
    private List<String> targetObjectIds;
}
