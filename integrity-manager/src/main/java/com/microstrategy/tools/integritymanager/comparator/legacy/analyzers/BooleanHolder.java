package com.microstrategy.tools.integritymanager.comparator.legacy.analyzers;

public class BooleanHolder {
	private boolean bool;
	
	public BooleanHolder()
	{
		bool = false;
	}
	
	public BooleanHolder(boolean b)
	{
		bool = b;
	}

	public boolean getBoolean() {
		return bool;
	}

	public void setBoolean(boolean b) {
		bool = b;
	}
	
}
