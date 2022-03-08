package com.microstrategy.tools.integritymanager.service.intf;

public interface ComparisonService {
    Object compareResult(String source, String target);

    void printDifferent(Object difference);
}
