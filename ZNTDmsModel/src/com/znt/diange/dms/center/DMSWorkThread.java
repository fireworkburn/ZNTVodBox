package com.znt.diange.dms.center;

import com.github.mediaserver.server.jni.DMSJniInterface;
import com.znt.diange.dms.util.CommonLog;
import com.znt.diange.dms.util.LogFactory;

import android.content.Context;
import android.text.TextUtils;


public class DMSWorkThread extends Thread implements IBaseEngine
{

	private static final CommonLog log = LogFactory.createLog();
	
	private static final int CHECK_INTERVAL = 180 * 1000; 
	
	private Context mContext = null;
	private boolean mStartSuccess = false;
	private boolean mExitFlag = false;
	private boolean isRunning = false;
	
	private String mRootdir = "";
	private String mFriendName = "";
	private String mUUID = "";	
	private boolean isRestartEnable = false;
	
	public DMSWorkThread(Context context)
	{
		mContext = context;
	}
	
	public void  setFlag(boolean flag)
	{
		mStartSuccess = flag;
	}
	
	public void setRestartEnable(boolean isRestartEnable)
	{
		this.isRestartEnable = isRestartEnable;
	}
	
	public void setParam(String rootDir, String friendName, String uuid)
	{
		mRootdir = rootDir;
		mFriendName = friendName;
		mUUID = uuid;
	}
	
	public void awakeThread()
	{
		synchronized (this) 
		{
			notifyAll();
		}
	}
	
	public void exit()
	{
		mExitFlag = true;
		awakeThread();
	}

	@Override
	public void run() 
	{

		log.e("DMSWorkThread run...");
		
		/*while(true)
		{
			if (mExitFlag)
			{
				stopEngine();
				break;
			}
			refreshNotify();
			synchronized(this)
			{				
				try
				{
					wait(CHECK_INTERVAL);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}	
			}
			if (mExitFlag)
			{
				stopEngine();
				break;
			}
		}*/
		
		log.e("DMSWorkThread over...");
		
	}
	
	public void refreshNotify()
	{
		/*if (!CommonUtil.checkNetworkState(mContext))
		{
			return ;
		}*/
		
		if (!mStartSuccess)
		{
			stopEngine();
			try 
			{
				Thread.sleep(200);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			boolean ret = startEngine();
			if (ret)
			{
				mStartSuccess = true;
			}
		}

	}
	
	@Override
	public boolean startEngine() 
	{
		if (TextUtils.isEmpty(mFriendName) || TextUtils.isEmpty(mUUID))
		{
			return false;
		}
		if(isRunning)
			return false;
		isRunning = true;
		try
		{
			/*int ret = DMSJniInterface.startServer(mRootdir, mFriendName, mUUID);
			boolean result = (ret == 0 ? true : false);*/
			/*if(result)
				Log.e("", "*************DMS开启成功-->" + mFriendName);
			  else
				Log.e("", "*************DMS开启失败了！！！");*/
		} catch (Exception e)
		{
			// TODO: handle exception
			LogFactory.createLog().e("******startEngine error-->"+e.getMessage());
		}
		isRunning = false;
		return false;
	}

	@Override
	public boolean stopEngine()
	{
		/*try
		{
			int ret = DMSJniInterface.stopServer();
			boolean result = (ret == 0 ? true : false);
			if(result)
				Log.e("", "#############DMS停止成功-->" + mFriendName);
			else
				Log.e("", "#############DMS停止失败了！！！");
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			LogFactory.createLog().e("******stopEngine error-->"+e.getMessage());
		}*/
		return true;
	}

	@Override
	public boolean restartEngine() 
	{
		setFlag(false);
		awakeThread();
		return true;
	}

}
