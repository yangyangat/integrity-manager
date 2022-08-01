package com.microstrategy.tools.integritymanager.model.entity.mstr.document;

import com.microstrategy.tools.integritymanager.model.bo.intf.Query;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocumentQueryGridGraph implements Query {
    private String key;
    private String name;
    private String rawName;
    private String queryDetails;
    private String sql;
}
