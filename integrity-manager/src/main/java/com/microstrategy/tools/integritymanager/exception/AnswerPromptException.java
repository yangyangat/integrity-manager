package com.microstrategy.tools.integritymanager.exception;

public class AnswerPromptException extends ReportExecutionException {
    public AnswerPromptException(String msg) {
        super(msg);
    }

    public AnswerPromptException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
