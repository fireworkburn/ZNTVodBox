
package com.znt.vodbox.utils; 

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

/** 
 * @ClassName: TimerUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-19 上午9:32:07  
 */
public class TimerUtils
{
	private Timer timer = null;
	private TimerTask task = null;
	private OnTimeOverListner onTimeOverListner = null;
	
	private int time = 0;
	private int TIME_MAX = 0;
	private boolean isStop = false;
	
	public TimerUtils(OnTimeOverListner onTimeOverListner)
	{
		this.onTimeOverListner = onTimeOverListner;
	}
	
	public void setOnTimerOverListener(OnTimeOverListner onTimeOverListner)
	{
		this.onTimeOverListner = onTimeOverListner;
	}
	
	Handler handler = new Handler();
	Runnable timerTask = new Runnable()
	{
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			time ++;
			if(time >= TIME_MAX)
			{
				if(onTimeOverListner != null && !isStop)
					onTimeOverListner.OnTimeOver();
				cancel();
			}
			if(!isStop)
				handler.postDelayed(timerTask, 1000);
			MyLog.e("######################timer-->"+time);
		}
	};
	
	public void delayTime(int delayTime)
	{
		cancel();
		
		time = 0;
		isStop = false;
		TIME_MAX = delayTime;
		handler.post(timerTask);
		
		//MyLog.e("######################isStop-->"+isStop);
	}
	
	public void cancel()
	{
		time = 0;
		isStop = true;
		if(handler != null)
			handler.removeCallbacks(timerTask);
		//MyLog.e("######################timer has cancelled");
	}
	
	
	/*public void delayTime(int delayTime)
	{
		cancel();
		isStop = false;
		if(task == null)
		{
			task = new TimerTask()
			{
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					if(onTimeOverListner != null && !isStop)
						onTimeOverListner.OnTimeOver();
				}
			};
			if(timer == null)
				timer = new Timer();
			timer.schedule(task, delayTime * 1000);
		}
	}
	
	public void cancel()
	{
		isStop = true;
		if(task != null)
		{
			task.cancel();
			task = null;
		}
		if(timer != null)
		{
			timer.cancel();
			timer.purge();
	        timer = null;
		}
	}*/
	
	public interface OnTimeOverListner
	{
		public void OnTimeOver();
	}
}
 
