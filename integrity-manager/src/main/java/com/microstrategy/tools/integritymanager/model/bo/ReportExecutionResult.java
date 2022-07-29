package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.model.bo.intf.Query;
import com.microstrategy.tools.integritymanager.model.entity.convertor.DataConvertor;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ReportInstance;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ReportExecutionResult implements Query {
    private String report = "";

//    private ReportInstance reportInstance;

    private String sql;

    private String queryDetails;

    private byte[] gridDataInCSV;

    private List<List<Object>> gridData;

    public List<List<Object>> getGridData() {
        if (gridData != null) return gridData;

        if (StringUtils.hasLength(report))
            return DataConvertor.restToFileSystem(report);

        if (gridDataInCSV != null && gridDataInCSV.length > 0)
            return DataConvertor.csvToFileSystem(gridDataInCSV);

        assert false;
        return new ArrayList<>();
    }
}
