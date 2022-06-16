package com.microstrategy.tools.integritymanager.comparator;

import com.microstrategy.tools.integritymanager.comparator.legacy.analyzers.BooleanHolder;
import com.microstrategy.tools.integritymanager.comparator.legacy.analyzers.DataAnalyzer;
import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.model.bo.ComparisonResult;
import com.microstrategy.tools.integritymanager.model.bo.ReportExecutionResult;
import com.microstrategy.tools.integritymanager.model.entity.convertor.DataConvertor;

import java.util.List;

public class ReportComparator {
    public static Object difference(ReportExecutionResult source, ReportExecutionResult target) {
        //TODO, redudant data format conversion here, need to optimize. The same conversion happens when persisting to local baseline files.
        List<List<Object>> sourceData = DataConvertor.restToFileSystem(source.getReport());
        List<List<Object>> targetData = DataConvertor.restToFileSystem(target.getReport());

        ComparisonResult comparisonResult = new ComparisonResult();
        BooleanHolder isEquivalent = new BooleanHolder();
        try {
            comparisonResult.setDiff(DataAnalyzer.markDifferentGridCells(sourceData, targetData, isEquivalent));
            comparisonResult.setComparisonStatus(isEquivalent.getBoolean() ? EnumComparisonStatus.MATCHED : EnumComparisonStatus.NOT_MATCHED);
        } catch (Exception e) {
            comparisonResult.setComparisonStatus(EnumComparisonStatus.ERROR);
        }

        return comparisonResult;
    }
}
