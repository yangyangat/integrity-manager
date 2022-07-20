package com.microstrategy.tools.integritymanager.model.entity.mstr.dossier;

import com.microstrategy.tools.integritymanager.model.bo.intf.Query;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DossierQueryViz implements Query {
    private String key;
    private String name;
    private String queryDetails;
    private String sql;
}
