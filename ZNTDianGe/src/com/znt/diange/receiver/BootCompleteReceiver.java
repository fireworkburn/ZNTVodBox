package com.znt.diange.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.znt.diange.activity.SplashActivity;

public class BootCompleteReceiver extends BroadcastReceiver 
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		/*if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) 
		{
			Intent myIntent = new Intent(context, MainActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(myIntent);
		} */
		/*else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) 
		{
            String packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, "安装成功"+packageName, Toast.LENGTH_LONG).show();
        }
		else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) 
        {
            String packageName = intent.getData().getSchemeSpecificPart();
            Toast.makeText(context, "卸载成功"+packageName, Toast.LENGTH_LONG).show();
        }*/
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) 
        {
			if(intent != null && intent.getData() != null)
			{
				String packageName = intent.getData().getSchemeSpecificPart();
	            
	            if(packageName != null && packageName.equals(context.getPackageName()))
				{
					Intent myIntent = new Intent(context, SplashActivity.class);
					myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(myIntent);
				}
			}
            
            //Toast.makeText(context, "替换成功"+packageName, Toast.LENGTH_LONG).show();
        }
	}
}
