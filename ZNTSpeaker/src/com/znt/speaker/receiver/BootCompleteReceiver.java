package com.znt.speaker.receiver;

import com.znt.speaker.activity.MainActivity;
import com.znt.speaker.util.LogFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver 
{
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) 
		{
			//startSpeackerServer(context);
			/*Intent myIntent = new Intent(context, MusicActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(myIntent);*/
			
			
		} 
		else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) 
		{
            String packageName = intent.getData().getSchemeSpecificPart();
        }
		else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) 
        {
            String packageName = intent.getData().getSchemeSpecificPart();
        }
		else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) 
        {
			String packageName = intent.getData().getSchemeSpecificPart();
			
           if(packageName.equals(context.getPackageName()))
			{
				Intent myIntent = new Intent(context, MainActivity.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(myIntent);
			}
        }
		else if (intent.getAction().equalsIgnoreCase("install_and_start"))
		{
	            Intent intent2 = new Intent(context, MainActivity.class);
	            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            context.startActivity(intent2);
	    }
	}
	
	/*private void startSpeackerServer(Context mContext)
	{
		Intent intent = new Intent(mContext, MediaRenderService.class);
		intent.setAction(MediaRenderService.START_RENDER_ENGINE);
		mContext.startService(intent);
	}
	private void stopSpeackerServer(Context mContext)
	{
		Intent intent = new Intent(mContext, MediaRenderService.class);
		intent.setAction(MediaRenderService.RESTART_RENDER_ENGINE);
		mContext.startService(intent);
	}*/
}
