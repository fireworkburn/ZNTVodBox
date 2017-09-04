package com.znt.diange.timer;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public abstract class AbstractTimer {

	private final static int TIMER_INTERVAL = 10000;
	
	protected Context mContext;
	private Timer mTimer;	
	protected MyTimeTask mTimeTask;
	protected int mTimeInterval = TIMER_INTERVAL;
	protected Handler mHandler;
	protected int msgID;
	protected int countTime = 0;
	private boolean isStop = false;
	
	public AbstractTimer(Context context)
	{
		mContext = context;	
		mTimer = new Timer();	
	}
	
	public void setHandler( Handler handler, int msgID){
		mHandler = handler;
		this.msgID = msgID;
	}
	
	public void setTimeInterval(int interval){
		mTimeInterval = interval;
	}
	
	public void startTimer()
	{
		isStop = false;
		countTime = 0;
		if (mTimeTask == null)
		{
			mTimeTask = new MyTimeTask();
			mTimer.schedule(mTimeTask, 0, mTimeInterval);
		}
	}
	
	public boolean isStop()
	{
		return isStop;
	}
	
	public void stopTimer()
	{
		isStop = true;
		if (mTimeTask != null)
		{
			mTimeTask.cancel();
			mTimeTask = null;
		}
		countTime = 0;
	}

	
	class MyTimeTask extends TimerTask
	{
		@Override
		public void run() 
		{
			if (mHandler != null)
			{
				if(!isStop)
				{
					Message msg = mHandler.obtainMessage(msgID);
					msg.sendToTarget();
					
					countTime ++;
				}
			}
		}
		
	}
	
}
