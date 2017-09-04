
package com.znt.diange.utils; 

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.znt.diange.R;
import com.znt.diange.activity.MyActivityManager;
import com.znt.diange.mina.client.MinaClient;

/** 
 * @ClassName: NotificationExtend 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-10-9 下午2:05:35  
 */
public class NotificationExtend
{
	private Activity context;
    
    public NotificationExtend(Activity context) 
    {
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    
    // 显示Notification
    public void showNotification() 
    {
    	// 得到通知管理器
    	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	//构建通知
    	Notification notification = new Notification();
    	notification.icon = R.drawable.logo_76;//图标
    	notification.flags |= Notification.FLAG_NO_CLEAR;
    	notification.flags |= Notification.FLAG_ONGOING_EVENT;
    	RemoteViews contentView = new RemoteViews(context.getPackageName(),R.layout.view_notification);
    	notification.contentView = contentView;
    	
    	String title = "";
    	String content = "";
    	/*if(infor != null)
    	{
    		title = infor.getMediaName();
    		content = infor.getArtist();
    	}*/
    	if(TextUtils.isEmpty(title))
			title = context.getResources().getString(R.string.app_name);
    	if(TextUtils.isEmpty(content))
    		content = context.getResources().getString(R.string.slogan);
    	
    	contentView.setTextViewText(R.id.tv_notif_title, title);
    	contentView.setTextViewText(R.id.tv_notif_content, content);
    	contentView.setImageViewResource(R.id.iv_notif_close, R.drawable.icon_close);
    	IntentFilter filter = new IntentFilter();
		filter.addAction(STATUS_BAR_COVER_CLICK_ACTION);
		context.registerReceiver(onClickReceiver, filter);
		Intent buttonIntent = new Intent(STATUS_BAR_COVER_CLICK_ACTION);
		PendingIntent pendButtonIntent = PendingIntent.getBroadcast(context, 0, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.iv_notif_close, pendButtonIntent);
    	
    	//设置通知的点击事件
    	Intent intent = new Intent(context, context.getClass());
    	//解决在部分手机上点击不能跳转问题
    	intent.setAction(Intent.ACTION_MAIN);
    	intent.addCategory(Intent.CATEGORY_LAUNCHER);
    	//点击返回当前界面状态
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    	//intent.setClass(this, MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	PendingIntent contentIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	notification.contentIntent = contentIntent;
    	
    	//发送通知
    	notificationManager.notify(0, notification);
    }
    
    // 取消通知
    public void cancelNotification()
    {
        NotificationManager notificationManager = (
                NotificationManager) context.getSystemService(
                        android.content.Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
    
    private final String STATUS_BAR_COVER_CLICK_ACTION = "STATUS_BAR_COVER_CLICK_ACTION";
    private BroadcastReceiver onClickReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context arg0, Intent intent)
		{
			// TODO Auto-generated method stub
			if (intent.getAction().equals(STATUS_BAR_COVER_CLICK_ACTION))
			{
				cancelNotification();
				stopAll(true);
			}
		}
	};
	
	private void stopAll(boolean sendStop)
	{
		if(MinaClient.getInstance().isConnected())
			MinaClient.getInstance().close();
		
		MyActivityManager.getScreenManager().popAllActivity();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}
	
}
 
