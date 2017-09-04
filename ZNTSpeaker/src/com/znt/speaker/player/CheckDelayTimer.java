package com.znt.speaker.player;

import android.content.Context;

public class CheckDelayTimer extends AbstractTimer{

	private int lastPos = 0;
	
	private int maxTime = 0;
	public void setMaxTime(int maxTime)
	{
		this.maxTime = maxTime;
	}
	
	public int getCountTime()
	{
		return countTime;
	}
	
	public boolean isOver()
	{
		if(maxTime > 0 && countTime > 0 && countTime >= maxTime)
		{
			reset();
			return true;
		}
		return false;
	}
	
	public CheckDelayTimer(Context context)
	{
		super(context);
	}
	

	public void setPos(int pos)
	{
		lastPos = pos;
	}
	
	public boolean isDelay(int pos)
	{
		if (pos != lastPos)
		{
			return false;
		}
/*		if (pos == 0 || pos != lastPos)
		{
			return false;
		}
*/		
		return true;
	}
}
