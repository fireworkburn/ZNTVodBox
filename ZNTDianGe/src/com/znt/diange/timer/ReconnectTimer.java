
package com.znt.diange.timer; 

import android.content.Context;

/** 
 * @ClassName: ReconnectTimer 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-21 下午2:20:11  
 */
public class ReconnectTimer extends AbstractTimer
{
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public ReconnectTimer(Context context)
	{
		super(context);
		setTimeInterval(1000);
		// TODO Auto-generated constructor stub
	}

	private int maxTime = 0;
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
 
