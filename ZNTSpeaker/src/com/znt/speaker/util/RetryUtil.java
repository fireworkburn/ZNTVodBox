/*
package com.znt.speaker.util; 

import android.os.Handler;

*//** 
 * @ClassName: RetryUtil 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-20 上午9:02:52  
 *//*
public class RetryUtil
{
	private Handler handler = null;
	
	public static final int RETRY_DOING = 90;
	public static final int RETRY_FINISH = 91;
	
	private int retryMaxTime = 0;
	private int time = 0;
	private boolean isRunning = false;
	
	public RetryUtil(Handler handler)
	{
		this.handler = handler;
	}
	
	public void startRetry(int retryMaxTime)
	{
		if(isRunning)
			return;
		isRunning = true;
		this.retryMaxTime = retryMaxTime;
		handler.postDelayed(task, 1000);
	}
	public void stopRetry()
	{
		if(isRunning)
		{
			isRunning = false;
			handler.removeCallbacks(task);
		}
	}
	
	private void doRetrying()
	{
		ViewUtils.sendMessage(handler, RETRY_DOING);
	}
	private void doRetryFinish()
	{
		stopRetry();
		ViewUtils.sendMessage(handler, RETRY_FINISH);
	}
	
	private Runnable task = new Runnable()
	{
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			if(time > retryMaxTime)
			{
				time = 0;
				doRetryFinish();
				LogFactory.createLog().e("*****重试结束***");
			}
			else if(isRunning)
			{
				time ++;
				doRetrying();
				handler.postDelayed(task, 1000);
				
				LogFactory.createLog().e("********正在重试-->"+time);
			}
		}
	};
}
 
*/