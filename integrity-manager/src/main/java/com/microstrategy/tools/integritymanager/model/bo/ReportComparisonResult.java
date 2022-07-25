package com.microstrategy.tools.integritymanager.model.bo;

import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import lombok.Data;

@Data
public class ReportComparisonResult {
    private boolean[][] diff = {};

    private int[] sourceSqlDiff = {};

    private int[] targetSqlDiff = {};

    private EnumComparisonStatus comparisonStatus = EnumComparisonStatus.NOT_COMPARED;
}
