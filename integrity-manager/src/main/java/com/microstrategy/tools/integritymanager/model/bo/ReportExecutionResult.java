package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.model.bo.intf.Query;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ReportInstance;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ReportExecutionResult implements Query {
    private String report = "";

//    private ReportInstance reportInstance;

    private String sql;

    private String queryDetails;
}
