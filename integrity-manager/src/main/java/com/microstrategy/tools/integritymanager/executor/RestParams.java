package com.microstrategy.tools.integritymanager.executor;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class RestParams {
    private String authToken;
    private List<String> cookies;
    private String projectId;
    private String libraryUrl;
}
