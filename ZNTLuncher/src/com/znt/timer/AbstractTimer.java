package com.znt.timer;

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
		countTime = 0;
		if (mTimeTask == null)
		{
			mTimeTask = new MyTimeTask();
			mTimer.schedule(mTimeTask, 0, mTimeInterval);
		}
	}
	
	public void stopTimer()
	{
		if (mTimeTask != null)
		{
			mTimeTask.cancel();
			mTimeTask = null;
		}
		countTime = 0;
	}
	
	public void reset()
	{
		countTime = 0;
	}

	class MyTimeTask extends TimerTask
	{
		@Override
		public void run() 
		{
			if (mHandler != null)
			{
				Message msg = mHandler.obtainMessage(msgID);
				msg.sendToTarget();
				
				countTime ++;
			}
		}
	}
}
