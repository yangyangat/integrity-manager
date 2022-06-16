package com.microstrategy.tools.integritymanager.service.intf;

import com.microstrategy.tools.integritymanager.model.bo.ReportExecutionResult;

public interface ComparisonService {
    Object compareResult(String source, String target);

    void printDifferent(Object difference);

    Object compareReportResult(ReportExecutionResult source, ReportExecutionResult target);
}
