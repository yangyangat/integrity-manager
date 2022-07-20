package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.model.bo.intf.Executable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ComparedInfo {

    private EnumComparisonStatus sQLComparisonStatus;

    private EnumComparisonStatus dataComparisonStatusForNewSummary;

    private EnumComparisonStatus dataComparisonStatus;

    private int rwdDataDifferenceCount;

    private int rwdSqlDifferenceCount;

    private int rwdDataNodeCount;

    private int rwdSqlNodeCount;

    {
        sQLComparisonStatus = EnumComparisonStatus.NOT_COMPARED;
        dataComparisonStatusForNewSummary = EnumComparisonStatus.NOT_COMPARED;
        dataComparisonStatus = EnumComparisonStatus.NOT_COMPARED;
        rwdDataDifferenceCount = 0;
        rwdSqlDifferenceCount = 0;
        rwdDataNodeCount = 1;
        rwdSqlNodeCount = 1;
    }

}
