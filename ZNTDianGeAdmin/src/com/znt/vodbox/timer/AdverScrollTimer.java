
package com.znt.vodbox.timer; 

import android.content.Context;

/** 
 * @ClassName: CheckSsidTimer 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-20 下午3:49:12  
 */
public class AdverScrollTimer extends AbstractTimer
{

	private int maxTime = 0;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public AdverScrollTimer(Context context)
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
 
