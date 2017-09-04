
package com.znt.diange.timer;

import android.content.Context;

/** 
* @ClassName: SearchDeviceTimer 
* @Description: TODO
* @author yan.yu 
* @date 2015年12月4日 下午10:43:22  
*/
public class SearchDeviceTimer extends AbstractTimer
{

	private int maxTime = 0;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public SearchDeviceTimer(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		setTimeInterval(1000);
	}
	
	public void setMaxTime(int maxTime)
	{
		this.maxTime = maxTime;
	}
	
	public boolean isOver()
	{
		if(maxTime > 0 && countTime > 0 && countTime >= maxTime)
		{
			stopTimer();
			return true;
		}
		return false;
	}

}