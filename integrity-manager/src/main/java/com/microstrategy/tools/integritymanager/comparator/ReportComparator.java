package com.microstrategy.tools.integritymanager.comparator;

import com.microstrategy.MSTRTester.Interval;
import com.microstrategy.MSTRTester.IntervalUtils;
import com.microstrategy.MSTRTester.analyzers.rwquerydetail.RwQueryDetailAnalyzer;
import com.microstrategy.tools.integritymanager.comparator.analyzers.RestDataAnalyzer;
import com.microstrategy.MSTRTester.utils.BooleanHolder;
import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.constant.enums.EnumExecutableType;
import com.microstrategy.tools.integritymanager.model.bo.ComparisonResult;
import com.microstrategy.tools.integritymanager.model.bo.ExecutionResult;
import com.microstrategy.tools.integritymanager.model.bo.ReportComparisonResult;
import com.microstrategy.tools.integritymanager.model.bo.ReportExecutionResult;
import com.microstrategy.tools.integritymanager.model.entity.convertor.DataConvertor;

import java.util.List;

public class ReportComparator {
    public static ComparisonResult difference(ExecutionResult source, ExecutionResult target) {
        return new ComparisonResult().setExecutableType(EnumExecutableType.ExecutableTypeReport)
                .setReportComparisonResult(difference(source.getReportExecutionResult(), target.getReportExecutionResult()));
    }

    public static ReportComparisonResult difference(ReportExecutionResult source, ReportExecutionResult target) {
        //TODO, redundant data format conversion here, need to optimize. The same conversion happens when persisting to local baseline files.
        List<List<Object>> sourceData = DataConvertor.restToFileSystem(source.getReport());
        List<List<Object>> targetData = DataConvertor.restToFileSystem(target.getReport());

        //TODO 2, need to take different format into consideration here. The format info is already in ReportExecutionResult instance.

        ReportComparisonResult reportComparisonResult = new ReportComparisonResult();

        try {
            //Data comparison
            BooleanHolder isDataEquivalent = new BooleanHolder();
            reportComparisonResult.setDiff(RestDataAnalyzer.markDifferentGridCells(sourceData, targetData, isDataEquivalent));

            //SQL/CSI comparison
            String sourceSqlView = source.getSql();
            String targetSqlView = target.getSql();
            RwQueryDetailAnalyzer rwQueryDetailAnalyzer = new RwQueryDetailAnalyzer(sourceSqlView, targetSqlView);
            Interval[][] diffIntervals = rwQueryDetailAnalyzer.diff();

            reportComparisonResult.setSourceSqlDiff(IntervalUtils.intervalsToArray(diffIntervals[0]));
            reportComparisonResult.setTargetSqlDiff(IntervalUtils.intervalsToArray(diffIntervals[1]));

            boolean matched = isDataEquivalent.getBoolean()
                    && IntervalUtils.isIntervalsEmpty(diffIntervals[0])
                    && IntervalUtils.isIntervalsEmpty(diffIntervals[1]);

            reportComparisonResult.setComparisonStatus(matched ? EnumComparisonStatus.MATCHED : EnumComparisonStatus.NOT_MATCHED);
        } catch (Exception e) {
            reportComparisonResult.setComparisonStatus(EnumComparisonStatus.ERROR);
        }
        return reportComparisonResult;
    }

    public static boolean[][]  gridDifference(List<List<Object>> sourceData, List<List<Object>> targetData, BooleanHolder isDataEquivalent)
            throws Exception {
        return RestDataAnalyzer.markDifferentGridCells(sourceData, targetData, isDataEquivalent);
    }

    public static Interval[][] sqlDifference(String sourceSql, String targetSql, BooleanHolder isSqlEquivalent) {
        RwQueryDetailAnalyzer rwQueryDetailAnalyzer = new RwQueryDetailAnalyzer(sourceSql, sourceSql);
        Interval[][] diffIntervals = rwQueryDetailAnalyzer.diff();

        isSqlEquivalent.setBoolean(IntervalUtils.isIntervalsEmpty(diffIntervals[0]) && IntervalUtils.isIntervalsEmpty(diffIntervals[1]));

        return diffIntervals;
    }
}
