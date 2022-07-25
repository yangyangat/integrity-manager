package com.microstrategy.tools.integritymanager.service.intf;

import com.microstrategy.tools.integritymanager.model.bo.ComparisonResult;
import com.microstrategy.tools.integritymanager.model.bo.ExecutionResult;

public interface ComparisonService {
    void printDifferent(Object difference);

    ComparisonResult compareReportResult(ExecutionResult source, ExecutionResult target);

    ComparisonResult compareResult(ExecutionResult source, ExecutionResult target);
}
