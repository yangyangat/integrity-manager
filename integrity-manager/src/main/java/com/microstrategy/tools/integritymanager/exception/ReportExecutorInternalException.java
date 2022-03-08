package com.microstrategy.tools.integritymanager.exception;

public class ReportExecutorInternalException extends Exception {

    public ReportExecutorInternalException(String msg) {
        super(msg);
    }

    public ReportExecutorInternalException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
