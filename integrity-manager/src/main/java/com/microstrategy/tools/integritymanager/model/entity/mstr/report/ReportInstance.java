package com.microstrategy.tools.integritymanager.model.entity.mstr.report;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportInstance {
    private String name;
    private String id;
    private String instanceId;
    private int status;
    private ReportDefinition definition;
    private JsonNode data;
}
