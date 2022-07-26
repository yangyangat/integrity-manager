package com.microstrategy.tools.integritymanager.comparator;

import com.microstrategy.MSTRTester.Interval;
import com.microstrategy.MSTRTester.IntervalUtils;
import com.microstrategy.MSTRTester.analyzers.rwquerydetail.RwQueryDetailAnalyzer;
import com.microstrategy.MSTRTester.utils.BooleanHolder;
import com.microstrategy.tools.integritymanager.comparator.analyzers.RestDataAnalyzer;
import com.microstrategy.tools.integritymanager.constant.enums.EnumComparisonStatus;
import com.microstrategy.tools.integritymanager.model.bo.ComparisonResult;
import com.microstrategy.tools.integritymanager.model.bo.ExecutionResult;
import com.microstrategy.tools.integritymanager.model.bo.ReportComparisonResult;
import com.microstrategy.tools.integritymanager.model.bo.ReportExecutionResult;
import com.microstrategy.tools.integritymanager.model.entity.convertor.DataConvertor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentComparator {
    public static ComparisonResult difference(ExecutionResult source, ExecutionResult target) {
        //TODO, 1. compare the dossier/document hierarchy

        //TODO 2. get the common viz node

        //temp solution here, assuming node keys are exactly the same between source and target
        Set<String> keys = source.getMapOfVizResult().keySet();

        Map<String, ReportComparisonResult> mapOfComparisons = new HashMap<>();
        Map<String, ReportExecutionResult> sourceMapOfResult = source.getMapOfVizResult();
        Map<String, ReportExecutionResult> targetMapOfResult = target.getMapOfVizResult();
        keys.stream().forEach(key -> {
            ReportExecutionResult sourceViz = sourceMapOfResult.get(key);
            ReportExecutionResult targetViz = targetMapOfResult.get(key);

            mapOfComparisons.put(key, ReportComparator.difference(sourceViz, targetViz));
        });

        boolean allMatch = mapOfComparisons.values().stream()
                .map(ReportComparisonResult::getComparisonStatus)
                .allMatch(status ->status == EnumComparisonStatus.MATCHED);

        return new ComparisonResult()
                .setExecutableType(source.getExecutableType())
                .setMapOfComparisons(mapOfComparisons)
                .setComparisonStatus(allMatch ? EnumComparisonStatus.MATCHED : EnumComparisonStatus.NOT_MATCHED);
    }
}
