/*package com.znt.receiver;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

public class MyBroadReceiver extends BroadcastReceiver 
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) 
		{
			//startSpeackerServer(context);
			//注意此广播是在Launcher启动后发出的
			Intent myIntent = new Intent(context, MusicActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(myIntent);
		} 
		else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) 
		{
            String packageName = intent.getData().getSchemeSpecificPart();
            if(changeListener != null)
            	changeListener.onAppChanged();
            //Toast.makeText(context, "安装成功"+packageName, Toast.LENGTH_LONG).show();
        }
		else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) 
        {
            String packageName = intent.getData().getSchemeSpecificPart();
            if(changeListener != null)
            	changeListener.onAppChanged();
            //Toast.makeText(context, "卸载成功"+packageName, Toast.LENGTH_LONG).show();
        }
		else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) 
        {
			String pkg = intent.getData().getSchemeSpecificPart();
			RunApp(context, pkg);
        }
		else if (intent.getAction().equalsIgnoreCase("install_and_start"))
		{
            Intent intent2 = new Intent(context, MusicActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//注意,不能少
            context.startActivity(intent2);
			String pkg = intent.getData().getSchemeSpecificPart();
			RunApp(context, pkg);
	    }
	}
	
	private void startSpeackerServer(Context mContext)
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
	}
	
	private OnAppChangedListener changeListener = null;
	public interface OnAppChangedListener 
	{
        public void onAppChanged();
    }

    public void setOnAppChangedListener(OnAppChangedListener changeListener) 
    {
        this.changeListener = changeListener;
    }
	
	private void RunApp(Context context, String packageName)
	{  
        PackageInfo pi;  
        try 
        {  
            pi = context.getPackageManager().getPackageInfo(packageName, 0);  
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);  
            resolveIntent.setPackage(pi.packageName);  
            PackageManager pManager = context.getPackageManager();  
            List<ResolveInfo> apps = pManager.queryIntentActivities(  
                    resolveIntent, 0);  
  
            ResolveInfo ri = apps.iterator().next();  
            if (ri != null)
            {  
                packageName = ri.activityInfo.packageName;  
                String className = ri.activityInfo.name;  
                Intent intent = new Intent(Intent.ACTION_MAIN);  
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName(packageName, className);  
                intent.setComponent(cn);  
                context.startActivity(intent);  
            }  
        } 
        catch (NameNotFoundException e)
        {  
            e.printStackTrace();  
        }  
  
    }
}
*/