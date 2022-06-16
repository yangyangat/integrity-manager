package com.microstrategy.tools.integritymanager.comparator.legacy.analyzers;

import java.io.Serializable;
import java.util.List;

/**
 * An Interval represents a substring by secifying the start and end offset with the
 * string. It's immutable.
 * author: yonli
 */
public final class Interval implements Comparable<Interval>, Serializable {
	
	/**
	 * 	version ID for serialization
	 */
	private static final long serialVersionUID = 151176464764674310L;

    private final int start;
    private final int end;

	public Interval() {
		this(0,0);
	}
	
	public Interval(int start, int end) {
		this.start = start;
		this.end = end;
	}

    /**
     * Given a list of intervals, append them to originIntervals taking into consideration of the offset.
     * @param originIntervals Output parameter
     * @param otherIntervals Intervals to append
     * @param offset
     */
	public static void appendIntervalsWithOffset(List<Interval> originIntervals,
                                                 List<Interval> otherIntervals,
                                                 int offset) {
	    if(originIntervals == null || otherIntervals == null) {
	        return;
        }

	    otherIntervals.forEach(interval ->
                originIntervals.add(interval.offset(offset, offset)));
    }

	public int getEnd() {
		return end;
	}

	public int getStart() {
		return start;
	}

    public Interval offset(int startOffset, int endOffset) {
        return new Interval(this.getStart() + startOffset, this.getEnd() + endOffset);
    }
	
	public boolean isSubsetOf(Interval ival) {
		if ((ival.start <= start) && (end <= ival.end) && (start <= end) && (ival.start <= ival.end))
			return true;
		return false;
	}

	public int compareTo(Interval obj) {
		return (start - obj.start);
	}
		
	@Override
    public boolean equals(Object o)
	{
		if (o instanceof Interval)
		{
			Interval that = (Interval) o;
			return (this.start == that.start && this.end == that.end);
		}
		
		return false;
	}
}
