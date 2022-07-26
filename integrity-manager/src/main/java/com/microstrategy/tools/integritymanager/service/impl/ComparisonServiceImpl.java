package com.microstrategy.tools.integritymanager.service.impl;

import com.google.common.collect.MapDifference;
import com.microstrategy.tools.integritymanager.comparator.DocumentComparator;
import com.microstrategy.tools.integritymanager.comparator.ReportComparator;
import com.microstrategy.tools.integritymanager.comparator.ReportJsonComparator;
import com.microstrategy.tools.integritymanager.constant.enums.EnumExecutableType;
import com.microstrategy.tools.integritymanager.model.bo.ComparisonResult;
import com.microstrategy.tools.integritymanager.model.bo.ExecutionResult;
import com.microstrategy.tools.integritymanager.service.intf.ComparisonService;
import org.springframework.stereotype.Service;

@Service
public class ComparisonServiceImpl implements ComparisonService {
    @Override
    public void printDifferent(Object difference) {
        if (difference instanceof MapDifference) {
            ReportJsonComparator.printDifference((MapDifference<String, Object>) difference);
        }
    }

    @Override
    public ComparisonResult compareReportResult(ExecutionResult source, ExecutionResult target) {
        return ReportComparator.difference(source, target);
    }

    @Override
    public ComparisonResult compareResult(ExecutionResult source, ExecutionResult target) {
        EnumExecutableType type = source.getExecutableType();
        if (type == EnumExecutableType.ExecutableTypeReport) {
            return this.compareReportResult(source, target);
        }
        else if (type == EnumExecutableType.ExecutableTypeDossier || type == EnumExecutableType.ExecutableTypeDocument) {
            return DocumentComparator.difference(source, target);
        }

        assert false;
        return null;
    }
}
