package com.microstrategy.tools.integritymanager.model.entity.mstr.dossier;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DossierFilter {
    private String key;
    private String name;
    private String summary;
    private JsonNode source;
}
