package com.microstrategy.tools.integritymanager.executor;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class RestParams {
    private String authToken;
    private List<String> cookies;
    private String projectId;
    private String libraryUrl;

    public boolean isValid() {
        boolean validCookie = cookies.stream().allMatch(cookie -> StringUtils.hasLength(cookie));
        return StringUtils.hasLength(authToken)
                && StringUtils.hasLength(projectId)
                && StringUtils.hasLength(libraryUrl)
                && validCookie;
    }
}
