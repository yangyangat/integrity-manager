package com.microstrategy.tools.integritymanager.service.intf;

import com.microstrategy.tools.integritymanager.model.bo.ExecutionResult;

public interface ComparisonService {
    Object compareResult(String source, String target);

    void printDifferent(Object difference);

    Object compareReportResult(ExecutionResult source, ExecutionResult target);
}
