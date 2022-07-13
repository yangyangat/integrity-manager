package com.microstrategy.tools.integritymanager.model.bo;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

public class DossierExecutionResult {
    @Setter
    @Getter
    private DossierData data;

    @Setter
    @Getter
    private JsonNode queryDetails;
}
