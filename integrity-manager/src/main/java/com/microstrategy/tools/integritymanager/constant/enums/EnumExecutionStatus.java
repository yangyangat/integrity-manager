package com.microstrategy.tools.integritymanager.constant.enums;

import java.io.Serializable;

/**
 * EnumExecutionStatus represents the status of a test execution. It describes 
 * each possible state in which the executable can be from the beginning in which it is
 * a testable and until it is a comparisonResult.
 * @author AWong
 *
 */
public class EnumExecutionStatus implements Comparable<EnumExecutionStatus>, Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3731376900262649022L;

	/**
	 * the value for completed status
	 */
	public static final int COMPLETED_VAL = 0;
	
	/**
	 * the value for runnable status
	 */
	public static final int RUNNABLE_VAL = 1;
	
	/**
	 * the value for running status
	 */
	public static final int RUNNING_VAL = 2;
	
	/**
	 * the value for analyzing status
	 */
	public static final int ANALYZING_VAL = 3;
	
	/**
	 * the value for not supported status
	 */
	public static final int NOT_SUPPORTED_VAL = 4;
	
	/**
	 * the value for timed out. 
	 */
	public static final int TIMED_OUT_VAL = 5;
	
	/**
	 * the value for not runnable status
	 */
	public static final int NOT_RUNNABLE_VAL = 6;
	
	/**
	 * the value for execution error status
	 */
	public static final int ERROR_VAL = 7;
	
	/**
	 * the value for execution paused status
	 * @since 8.1.2
	 */
	public static final int PAUSED_VAL = 8;

	/**
	 * the value for execution completed with errors
	 * @since 8.1.2
	 */
	public static final int COMPLETED_WITH_ERRORS_VAL = 9;
	
	/**
	 * The value for execution pending for prompt solving
	 * @since 9.0
	 */
	public static final int PROMPT_PENDING_VAL = 10;

	/**
	 * The executable is not supported
	 */
	public static final EnumExecutionStatus NOT_SUPPORTED = new EnumExecutionStatus(NOT_SUPPORTED_VAL);
	
	/**
	 * The executable timed out. 
	 */
	public static final EnumExecutionStatus TIMED_OUT = new EnumExecutionStatus(TIMED_OUT_VAL);
	
	/**
	 * Execution has run to completion
	 */
	public static final EnumExecutionStatus COMPLETED = new EnumExecutionStatus(COMPLETED_VAL);	
	
	/**
	 * Execution has not occurred
	 */
	public static final EnumExecutionStatus RUNNABLE = new EnumExecutionStatus(RUNNABLE_VAL);

	/**
	 * Execution has run to completion
	 */
	public static final EnumExecutionStatus NOT_RUNNABLE = new EnumExecutionStatus(NOT_RUNNABLE_VAL);

	/**
	 * Execution has encountered an error and has stopped
	 */
	public static final EnumExecutionStatus ERROR = new EnumExecutionStatus(ERROR_VAL);
	
	/**
	 * Execution is in progress
	 */
	public static final EnumExecutionStatus RUNNING = new EnumExecutionStatus(RUNNING_VAL);
	
	/**
	 * The executable is being analyzed (graph, data, etc.)
	 */
	public static final EnumExecutionStatus ANALYZING = new EnumExecutionStatus(ANALYZING_VAL);
	
	/**
	 * The execution of this executable is paused.
	 * @since 8.1.2
	 */
	public static final EnumExecutionStatus PAUSED = new EnumExecutionStatus(PAUSED_VAL);

	/**
	 * Execution has run with two values, complete and with errors.
	 * @since 8.1.2 
	 */
	public static final EnumExecutionStatus COMPLETED_WITH_ERRORS = new EnumExecutionStatus(COMPLETED_WITH_ERRORS_VAL);
	
	
	/**
	 * Execution is waiting for prompt resolving.
	 * @since 9.0 
	 */
	public static final EnumExecutionStatus PROMPT_PENDING = new EnumExecutionStatus(PROMPT_PENDING_VAL);
	

	/**
	 * the type of execution status. 
	 */
	private int type;

	/**
	 * Represents the state of execution
	 * @param type the state of execution
	 */
	private EnumExecutionStatus(int type) {
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */	
	@Override
    public String toString() {
		
		switch (type) {
		case COMPLETED_VAL: return "completed";
		//since 8.1.2
		case COMPLETED_WITH_ERRORS_VAL: return "completed with errors";
		
		//TQMS 274038 - we use "RUNNABLE" status for "Excluded", i.e. the user select
		//only graph to compare but the report is a grid report
		case RUNNABLE_VAL: return "runnable";
		case ANALYZING_VAL: return "analyzing";
		case NOT_RUNNABLE_VAL: return "not runnable";
		case ERROR_VAL: return "error";
		case NOT_SUPPORTED_VAL: return "not supported";
		case TIMED_OUT_VAL: return "time out";
		case RUNNING_VAL: return "running";
		case PAUSED_VAL: return "paused";
		case PROMPT_PENDING_VAL: return "prompt pending";
		
		default: throw new IllegalArgumentException("unsupported type");
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(EnumExecutionStatus rhs) {
		return (type - rhs.getType());
	}

	/**
	 * Type getter
	 * @return  either completed, runnable, running, analyzing, not supported, timed out, 
	 * 			not runnable, error, running or analyzing. 
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Translates a type into an instance of EnumExecutionStatus
	 * @param type  either completed, runnable, running, analyzing, not supported, timed out, 
	 * 			not runnable, error, running or analyzing. 
	 * @return  a valid EnumExecutionStatus
	 * @throws IllegalArgumentException  if type is invalid. 
	 */
	public static EnumExecutionStatus getStatusByType(int type) throws IllegalArgumentException {
		switch(type) {
		case COMPLETED_VAL: return COMPLETED;
		case RUNNABLE_VAL: return RUNNABLE;
		case NOT_RUNNABLE_VAL: return NOT_RUNNABLE;
		case ERROR_VAL: return ERROR;
		case RUNNING_VAL: return RUNNING;
		case NOT_SUPPORTED_VAL: return NOT_SUPPORTED;
		case TIMED_OUT_VAL: return TIMED_OUT;
		case ANALYZING_VAL: return ANALYZING;
		case PAUSED_VAL: return PAUSED;
		case COMPLETED_WITH_ERRORS_VAL: return COMPLETED_WITH_ERRORS;
		case PROMPT_PENDING_VAL: return PROMPT_PENDING;
		}

		throw new IllegalArgumentException();
	}
	/**
	 * Translate the execution status string back to Enum value
	 * @param desc
	 * @return
	 */
	public static EnumExecutionStatus getEnumExecutionStatusByDesc(String desc) {
		if (EnumExecutionStatus.COMPLETED.toString().compareTo(desc) == 0)
			return EnumExecutionStatus.COMPLETED;
		else if (EnumExecutionStatus.ERROR.toString().compareTo(desc) == 0)
			return EnumExecutionStatus.ERROR;
		else if (EnumExecutionStatus.NOT_RUNNABLE.toString().compareTo(desc) == 0)
			return EnumExecutionStatus.NOT_RUNNABLE;
		else if (EnumExecutionStatus.NOT_SUPPORTED.toString().compareTo(desc) == 0)
			return EnumExecutionStatus.NOT_SUPPORTED;
		else if (EnumExecutionStatus.RUNNABLE.toString().compareTo(desc) == 0)
			return EnumExecutionStatus.RUNNABLE;
		else if (EnumExecutionStatus.RUNNING.toString().compareTo(desc) == 0)
			return EnumExecutionStatus.RUNNING;
		else if (EnumExecutionStatus.TIMED_OUT.toString().compareTo(desc) == 0)
			return EnumExecutionStatus.TIMED_OUT;
		else if (EnumExecutionStatus.ANALYZING.toString().compareTo(desc) == 0)
			return EnumExecutionStatus.ANALYZING;
		else if (EnumExecutionStatus.PAUSED.toString().compareTo(desc) == 0)
			return EnumExecutionStatus.PAUSED;
		else if (EnumExecutionStatus.COMPLETED_WITH_ERRORS.toString().compareTo(desc) == 0)
			return EnumExecutionStatus.COMPLETED_WITH_ERRORS;
		else if (EnumExecutionStatus.PROMPT_PENDING.toString().compareTo(desc) == 0)
			return EnumExecutionStatus.PROMPT_PENDING;
		else
			return null;
	}
}
