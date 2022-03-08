package com.microstrategy.tools.integritymanager.exception;

public class ReportExecutionException extends Exception {

    public ReportExecutionException(String msg) {
        super(msg);
    }
    public ReportExecutionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
