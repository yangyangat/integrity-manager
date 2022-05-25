package com.microstrategy.tools.integritymanager.model.entity.filesystem.summary;

import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.constant.enums.EnumExecutionStatus;
import com.microstrategy.tools.integritymanager.model.bo.intf.ExecutableSet;

public class SummaryJsonUtils {

    /**
     * Calculate execution status based on the status of Data and SQL comparison.
     * @param executableSet The executable set.
     * @return The calculated status.
     */
    public static EnumExecutionStatus calculateExecutionStatus(ExecutableSet executableSet) {
        EnumComparisonStatus dataComparisonStatus = executableSet.getDataComparisonStatus();
        EnumComparisonStatus sqlComparisonStatus = executableSet.getSQLComparisonStatus();

        if (isCompletedStatus(dataComparisonStatus)) {
            return EnumExecutionStatus.COMPLETED;
        } else if (dataComparisonStatus == EnumComparisonStatus.NOT_COMPARED) {
            if (sqlComparisonStatus == EnumComparisonStatus.ERROR) {
                return EnumExecutionStatus.ERROR;
            } else {
                return EnumExecutionStatus.COMPLETED;
            }
        } else {
            return EnumExecutionStatus.ERROR;
        }
    }

    static boolean isCompletedStatus(EnumComparisonStatus comparisonStatus) {
        return comparisonStatus == EnumComparisonStatus.MATCHED
                || comparisonStatus == EnumComparisonStatus.NOT_MATCHED;
    }

}
