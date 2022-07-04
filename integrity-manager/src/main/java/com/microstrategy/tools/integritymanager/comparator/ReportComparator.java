package com.microstrategy.tools.integritymanager.comparator;

import com.microstrategy.MSTRTester.Interval;
import com.microstrategy.MSTRTester.IntervalUtils;
import com.microstrategy.MSTRTester.analyzers.rwquerydetail.RwQueryDetailAnalyzer;
import com.microstrategy.tools.integritymanager.comparator.analyzers.RestDataAnalyzer;
import com.microstrategy.MSTRTester.utils.BooleanHolder;
import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.model.bo.ComparisonResult;
import com.microstrategy.tools.integritymanager.model.bo.ReportExecutionResult;
import com.microstrategy.tools.integritymanager.model.entity.convertor.DataConvertor;

import java.util.List;

public class ReportComparator {
    public static Object difference(ReportExecutionResult source, ReportExecutionResult target) {
        //TODO, redundant data format conversion here, need to optimize. The same conversion happens when persisting to local baseline files.
        List<List<Object>> sourceData = DataConvertor.restToFileSystem(source.getReport());
        List<List<Object>> targetData = DataConvertor.restToFileSystem(target.getReport());

        //TODO 2, need to take different format into consideration here. The format info is already in ReportExecutionResult instance.


        ComparisonResult comparisonResult = new ComparisonResult();

        try {
            //Data comparison
            BooleanHolder isDataEquivalent = new BooleanHolder();
            comparisonResult.setDiff(RestDataAnalyzer.markDifferentGridCells(sourceData, targetData, isDataEquivalent));

            //SQL/CSI comparison
            String sourceSqlView = source.getSqlStatement();
            String targetSqlView = target.getSqlStatement();
            RwQueryDetailAnalyzer rwQueryDetailAnalyzer = new RwQueryDetailAnalyzer(sourceSqlView, targetSqlView);
            Interval[][] diffIntervals = rwQueryDetailAnalyzer.diff();

            comparisonResult.setSourceSqlDiff(IntervalUtils.intervalsToArray(diffIntervals[0]));
            comparisonResult.setTargetSqlDiff(IntervalUtils.intervalsToArray(diffIntervals[1]));

            boolean matched = isDataEquivalent.getBoolean()
                    && IntervalUtils.isIntervalsEmpty(diffIntervals[0])
                    && IntervalUtils.isIntervalsEmpty(diffIntervals[1]);

            comparisonResult.setComparisonStatus(matched ? EnumComparisonStatus.MATCHED : EnumComparisonStatus.NOT_MATCHED);
        } catch (Exception e) {
            comparisonResult.setComparisonStatus(EnumComparisonStatus.ERROR);
        }

        return comparisonResult;
    }
}
