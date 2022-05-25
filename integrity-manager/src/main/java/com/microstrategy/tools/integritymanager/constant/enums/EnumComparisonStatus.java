package com.microstrategy.tools.integritymanager.constant.enums;

import java.io.Serializable;

/**
 * EnumComparisonStatus is an enumeration of all the possible comparison results
 * that the application can have. 
 * 
 * @author AWong
 * 
 */
public class EnumComparisonStatus implements Comparable<EnumComparisonStatus>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5186092549875829551L;


	/**
	 * the value for the not compared status
	 */
	public static final int NOT_COMPARED_VAL = 0;

	/**
	 * the value for the matched status
	 */
	public static final int MATCHED_VAL = 1;

	/**
	 * the value for the not matched status
	 */
	public static final int NOT_MATCHED_VAL = 2;
	
	/**
	 * the value for the error status
	 */
	public static final int ERROR_VAL = 3;
	
	/**
	 * the value of the comparison result
	 */
	private int compResultVal;


	/**
	 * A comparison yields a not matched analysis
	 */
	public static final EnumComparisonStatus NOT_MATCHED = new EnumComparisonStatus(NOT_MATCHED_VAL);

	/**
	 * A comparison yields a matched analysis
	 */
	public static final EnumComparisonStatus MATCHED = new EnumComparisonStatus(MATCHED_VAL);

	/**
	 * Comparison is not available. 
	 * Either the analyzer is not selected or the report does not have graph def but 
	 * the user selects graph analyzer
	 */
	public static final EnumComparisonStatus NOT_COMPARED = new EnumComparisonStatus(NOT_COMPARED_VAL);

	/**
	 * Cannot compare due to errors
	 */
	public static final EnumComparisonStatus ERROR = new EnumComparisonStatus(ERROR_VAL);
	
	/**
	 * Creates a new EnumComparisonStatus out of some type
	 * @param type NOT_COMPARED_VAL, MATCHED_VAL, NOT_MATCHED_VAL or ERROR_VAL
	 */
	private EnumComparisonStatus(int type) {
		this.compResultVal = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
    public String toString() {
		switch (compResultVal) {
		//TQMS 344628 WLIAO we should return localized string.
		case NOT_COMPARED_VAL:
			return "not compared";
		case MATCHED_VAL:
			return "matched";
		case NOT_MATCHED_VAL:
			return "not matched";
		case ERROR_VAL:
			return "error";
		default:
			return "No Status";
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(EnumComparisonStatus status) {
		return (compResultVal - status.compResultVal);
	}

	/**
	 * Type retriever
	 * @return  either NOT_COMPARED_VAL, MATCHED_VAL, NOT_MATCHED_VAL or ERROR_VAL 
	 */
	public int getType() {
		return compResultVal;
	}

	/**
	 * Translates a description into an instance of EnumComparisonStatus
	 * @param desc the result of the resulting EnumComparisonStatus toString() method, not null. 
	 * @return  a EnumComparisonStatus if there is a String match or null if there is not. 
	 * @deprecated TQMS 280243
	 */
	@Deprecated
    public static EnumComparisonStatus getEnumComparisonStatusByDesc(String desc) {
		if (EnumComparisonStatus.MATCHED.toString().compareTo(desc) == 0)
			return EnumComparisonStatus.MATCHED;
		else if (EnumComparisonStatus.NOT_COMPARED.toString().compareTo(desc) == 0)
			return EnumComparisonStatus.NOT_COMPARED;
		else if (EnumComparisonStatus.NOT_MATCHED.toString().compareTo(desc) == 0)
			return EnumComparisonStatus.NOT_MATCHED;
		else if (EnumComparisonStatus.ERROR.toString().compareTo(desc) == 0)
			return EnumComparisonStatus.ERROR;
		//remove this before RC - this is only for backward compatibility with beta build
		else if (desc.compareTo("Not Compared") == 0)
			return EnumComparisonStatus.NOT_COMPARED;
		else
			return null;
	}
	
	public static EnumComparisonStatus getEnumComparsionStatusByType(int type) throws IllegalArgumentException{
		switch (type) {
		case NOT_COMPARED_VAL:
			return EnumComparisonStatus.NOT_COMPARED;
		case MATCHED_VAL:
			return EnumComparisonStatus.MATCHED;
		case NOT_MATCHED_VAL:
			return EnumComparisonStatus.NOT_MATCHED;
		case ERROR_VAL:
			return EnumComparisonStatus.ERROR;
		default:
			throw new IllegalArgumentException("No comparsion status found by value " + type + " .");
		}
	}
}
