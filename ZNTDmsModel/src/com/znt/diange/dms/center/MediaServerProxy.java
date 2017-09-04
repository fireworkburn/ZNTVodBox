package com.znt.diange.dms.center;
/*package com.neldtv.dms.center;


import android.content.Context;
import android.content.Intent;

import com.neldtv.dms.server.DMSService;
import com.neldtv.dms.util.CommonLog;
import com.neldtv.dms.util.LogFactory;

public class MediaServerProxy implements IBaseEngine
{

	private static final CommonLog log = LogFactory.createLog();
	
	private static  MediaServerProxy mInstance;
	private Context mContext;
	
	private MediaServerProxy(Context context) 
	{
		mContext = context;
	}

	public static synchronized MediaServerProxy getInstance(Context context)
	{
		if (mInstance == null)
		{
			mInstance  = new MediaServerProxy(context);
		}
		return mInstance;
	}

	@Override
	public boolean startEngine() 
	{
		mContext.startService(new Intent(DMSService.START_SERVER_ENGINE));
		return true;
	}

	@Override
	public boolean stopEngine() 
	{
		mContext.stopService(new Intent(mContext, DMSService.class));
		return true;
	}

	@Override
	public boolean restartEngine() 
	{
		mContext.startService(new Intent(DMSService.RESTART_SERVER_ENGINE));
		return true;
	}

}
*/