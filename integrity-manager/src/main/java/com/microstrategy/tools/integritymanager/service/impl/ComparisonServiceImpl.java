package com.microstrategy.tools.integritymanager.service.impl;

import com.google.common.collect.MapDifference;
import com.microstrategy.tools.integritymanager.comparator.ReportJsonComparator;
import com.microstrategy.tools.integritymanager.service.intf.ComparisonService;
import org.springframework.stereotype.Service;

@Service
public class ComparisonServiceImpl implements ComparisonService {
    @Override
    public Object compareResult(String source, String target) {
        return ReportJsonComparator.difference(source, target);
    }

    @Override
    public void printDifferent(Object difference) {
        ReportJsonComparator.printDifference((MapDifference<String, Object>) difference);
    }
}
