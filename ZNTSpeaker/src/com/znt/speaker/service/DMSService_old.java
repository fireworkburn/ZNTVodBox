/*package com.znt.speaker.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.znt.diange.dms.center.DMSWorkThread;
import com.znt.diange.dms.center.MediaStoreCenter;
import com.znt.diange.dms.center.MediaStoreCenter.SourceType;
import com.znt.diange.dms.media.MediaScannerCenter.ILocalMusicScanListener;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.speaker.R;
import com.znt.speaker.activity.MusicActivity;
import com.znt.speaker.center.DMRCenter;
import com.znt.speaker.center.IBaseEngine;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.mina.server.MinaServer;
import com.znt.speaker.mina.server.ServerHandler;
import com.znt.speaker.mina.server.ServerHandler.OnMessageReceiveListener;
import com.znt.speaker.receiver.CustomMountingReceiver;
import com.znt.speaker.receiver.CustomMountingReceiver.IMediaChangeListener;
import com.znt.speaker.util.CommonUtil;
import com.znt.speaker.util.LogFactory;

public class DMSService_old extends Service implements IBaseEngine, ILocalMusicScanListener{

	
	public static final String START_SERVER_ENGINE = "com.znt.speaker.server.dms.start.dmsengine";
	public static final String RESTART_SERVER_ENGINE = "com.znt.speaker.server.dms.restart.dmsengine";
	
	public static final String SCAN_MUSIC_START = "com.znt.speaker.server.dms.SCAN_MUSIC_START";
	public static final String SCAN_MUSIC_FINISH = "com.znt.speaker.server.dms.SCAN_MUSIC_FINISH";

	private DMSWorkThread mWorkThread;
	private OnMessageReceiveListener mListener;

	private static final int START_ENGINE_MSG_ID = 0x0001;
	private static final int RESTART_ENGINE_MSG_ID = 0x0002;
	
	private static final int DELAY_TIME = 1000;
	
	private MediaStoreCenter mMediaStoreCenter;
	private MulticastLock mMulticastLock;
	
	private String friendName = "";
	private String uuid = "";
	private boolean scanMusic = true;
	private boolean isServerRestart = true;
	
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch(msg.what)
			{
			case START_ENGINE_MSG_ID:
				startEngine();
				break;
			case RESTART_ENGINE_MSG_ID:
				restartEngine();
				break;
			}
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();	
		LogFactory.createLog().e("***********DMSService onCreate");
		initService();	
		registerSdReceiver();
		
		//AdbUtils.adbStart(this, "5555");
	}

	@Override
	public void onDestroy() 
	{
		LogFactory.createLog().e("***********DMSService onDestroy");
		unInitService();	
		unRegisterSdReceiver();
		super.onDestroy();
	
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		LogFactory.createLog().e("***********DMSService onStartCommand");
		if (intent != null)
		{
			String actionString = intent.getAction();
			if (actionString != null)
			{		
				friendName = intent.getStringExtra("DEVICE_NAME");
				uuid = intent.getStringExtra("UUID");
				scanMusic = intent.getBooleanExtra("SCAN_MUSIC", true);
				isServerRestart = intent.getBooleanExtra("RESTART_SERVER", true);
				scanLocalMedias();
				if (actionString.equalsIgnoreCase(START_SERVER_ENGINE))
				{
					delayToSendStartMsg();
				}
				else if (actionString.equalsIgnoreCase(RESTART_SERVER_ENGINE))
				{
					delayToSendRestartMsg();
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
		
	}
	
	private void initService()
	{
		mListener = new DMRCenter(this);
		ServerHandler.setOnMessageReceiveListener(mListener);
		
		mMulticastLock = CommonUtil.openWifiBrocast(this);
		
		mWorkThread = new DMSWorkThread(this);
		mWorkThread.setRestartEnable(true);
		mMulticastLock = CommonUtil.openWifiBrocast(this);
	}

	private void scanLocalMedias()
	{
		if(mMediaStoreCenter == null)
		{
			mMediaStoreCenter = MediaStoreCenter.getInstance(this,SourceType.Speaker);
			mMediaStoreCenter.setOnLocalMusicScanListener(this);
		}
		if(scanMusic)
		{
			mMediaStoreCenter.clearAllData();
			mMediaStoreCenter.clearWebFolder();
			mMediaStoreCenter.createWebFolder();
			mMediaStoreCenter.doScanMedia();//单级目录展示
		}
	}
	
	*//**
	* @Description: 添加一个文件后，直接调用一下这个函数，对添加的文件进行扫描，
	* 由于只是扫描了我们添加的文件，并非对整个媒体库文件进行扫描，因此，效率最高。
	* @param @param filename   
	* @return void 
	* @throws
	 *//*
	private void updateMusic(String filename)//filename是我们的文件全名，包括后缀哦
	{
		MediaScannerConnection.scanFile(this,
		          new String[] { filename }, null,
		          new MediaScannerConnection.OnScanCompletedListener() 
		{
		      public void onScanCompleted(String path, Uri uri) 
		      {
		          Log.i("ExternalStorage", "Scanned " + path + ":");
		          Log.i("ExternalStorage", "-> uri=" + uri);
		      }
		 });
	}
	
	private void unInitService()
	{
		stopEngine();
		removeStartMsg();
		removeRestartMsg();
		
		if (mMulticastLock != null)
		{
			mMulticastLock.release();
			mMulticastLock = null;
		}
		
		cancelNotification();
	}

	private void delayToSendStartMsg()
	{
		removeStartMsg();
		mHandler.sendEmptyMessageDelayed(START_ENGINE_MSG_ID, DELAY_TIME);
	}
	
	private void delayToSendRestartMsg()
	{
		removeStartMsg();
		removeRestartMsg();
		mHandler.sendEmptyMessageDelayed(RESTART_ENGINE_MSG_ID, DELAY_TIME);
		
		if(isServerRestart)
			MinaServer.getInstance().restartServer();
	}
	
	private void removeStartMsg()
	{
		mHandler.removeMessages(START_ENGINE_MSG_ID);
	}
	
	private void removeRestartMsg()
	{
		mHandler.removeMessages(RESTART_ENGINE_MSG_ID);	
	}
	
	
	@Override
	public boolean startEngine() 
	{
		showNotification(null);
		
		awakeWorkThread();
		return true;
	}

	@Override
	public boolean stopEngine() 
	{
		cancelNotification();
		
		mWorkThread.setParam("", "", "");
		exitWorkThread();
		
		return true;
	}

	@Override
	public boolean restartEngine()
	{
		
		mWorkThread.setParam(mMediaStoreCenter.getRootDir(), friendName, uuid);
		if (mWorkThread.isAlive())
		{
			mWorkThread.restartEngine();
		}
		else
		{
			mWorkThread.start();
		}
		return true;
	}

	private void awakeWorkThread()
	{
		
		mWorkThread.setParam(mMediaStoreCenter.getRootDir(), friendName, uuid);
		
		
		if (mWorkThread.isAlive())
		{
			mWorkThread.awakeThread();
		}
		else
		{
			mWorkThread.start();
		}
	}
	
	private void exitWorkThread()
	{
		if (mWorkThread != null && mWorkThread.isAlive())
		{
			mWorkThread.exit();
			long time1 = System.currentTimeMillis();
			while(mWorkThread.isAlive()){
				try 
				{
					Thread.sleep(100);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			long time2 = System.currentTimeMillis();
			mWorkThread = null;
		}
	}

	private NotificationManager notificationManager = null;
	private void showNotification(MediaInfor infor)
	{
		//1 得到通知管理器
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	//2构建通知
    	Notification notification = new Notification();
    	notification.icon = R.drawable.logo_76;//图标
    	notification.flags |= Notification.FLAG_NO_CLEAR;
    	RemoteViews contentView = new RemoteViews(getPackageName(),R.layout.view_notification);
    	notification.contentView = contentView;
    	
    	String title = "";
    	String content = "";
    	if(infor != null)
    	{
    		title = infor.getMediaName();
    		content = infor.getArtist();
    	}
    	if(TextUtils.isEmpty(title))
			title = "助你店铺音乐";
    	if(TextUtils.isEmpty(content))
    		content = "好音乐，人人享";
    	
    	contentView.setTextViewText(R.id.tv_notif_title, title);
    	contentView.setTextViewText(R.id.tv_notif_content, content);
    	contentView.setImageViewResource(R.id.iv_notif_close, R.drawable.icon_close);
    	IntentFilter filter = new IntentFilter();
		filter.addAction(STATUS_BAR_COVER_CLICK_ACTION);
		registerReceiver(onClickReceiver, filter);
		Intent buttonIntent = new Intent(STATUS_BAR_COVER_CLICK_ACTION);
		PendingIntent pendButtonIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0);
		contentView.setOnClickPendingIntent(R.id.iv_notif_close, pendButtonIntent);
    	
    	//3设置通知的点击事件
    	Intent intent = new Intent();
    	//解决在部分手机上点击不能跳转问题
    	intent.setAction(Intent.ACTION_MAIN);
    	intent.addCategory(Intent.CATEGORY_LAUNCHER);
    	//点击返回当前界面状态
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    	intent.setClass(this, MusicActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 100, intent, 0);
    	notification.contentIntent = contentIntent;
    	
    	//4发送通知
    	notificationManager.notify(98, notification);
	}
	
	private final String STATUS_BAR_COVER_CLICK_ACTION = "STATUS_BAR_COVER_CLICK_ACTION";
	BroadcastReceiver onClickReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context arg0, Intent intent)
		{
			// TODO Auto-generated method stub
			if (intent.getAction().equals(STATUS_BAR_COVER_CLICK_ACTION))
			{
				stopAll(true);
			}
		}
	};
	
	public static final String CLOSE = "CLOSE";
	private void stopAll(boolean sendStop)
	{
		Intent intent = new Intent(CLOSE); 
		sendBroadcast(intent); 
		
		cancelNotification();
		unInitService();		
	}
	private void cancelNotification()
	{
		if(notificationManager != null)
			notificationManager.cancel(98);
	}
	
	private CustomMountingReceiver receiver = new CustomMountingReceiver();
    public void registerSdReceiver()
    {
    	IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.MEDIA_ADD);
        filter.addAction(Constant.MEDIA_REMOVE);
        filter.addDataScheme("file");
    	receiver.setReceiverListener(new IMediaChangeListener()
		{
			@Override
			public void onMediaChange(boolean isAdd)
			{
				// TODO Auto-generated method stub
				scanMusic = true;
				scanLocalMedias();
			}
		});
        this.registerReceiver(receiver, filter);
    }
    public void unRegisterSdReceiver()
    {
    	this.unregisterReceiver(receiver);
    }
    
    private void sendMusicScanStart()
    {
		Intent intent = new Intent(SCAN_MUSIC_START); 
		Bundle bundle = new Bundle();
		intent.putExtras(bundle);  
		sendBroadcast(intent);  
    }
    private void sendMusicScanResult()
    {
    	Intent intent = new Intent(SCAN_MUSIC_FINISH); 
    	Bundle bundle = new Bundle();
    	intent.putExtras(bundle);  
    	sendBroadcast(intent);  
    }
    
	*//**
	*callbacks
	*//*
	@Override
	public void onScanStart()
	{
		// TODO Auto-generated method stub
		//扫描开始
		sendMusicScanStart();
	}

	*//**
	*callbacks
	*//*
	@Override
	public void onScanFinish()
	{
		// TODO Auto-generated method stub
		//扫描结束
		sendMusicScanResult();
	}

	*//**
	*callbacks
	*//*
	@Override
	public void onScanDoing()
	{
		// TODO Auto-generated method stub
		
	}

}
*/