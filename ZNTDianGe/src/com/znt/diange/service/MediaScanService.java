package com.znt.diange.service;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.znt.diange.dlna.mediaserver.util.CommonLog;
import com.znt.diange.dlna.mediaserver.util.FileHelper;
import com.znt.diange.dlna.mediaserver.util.LogFactory;
import com.znt.diange.entity.Constant;
import com.znt.diange.media.IMediaScanListener;
import com.znt.diange.media.MediaScannerCenter;
import com.znt.diange.media.MediaScannerCenter.ILocalMusicScanListener;
import com.znt.diange.receiver.CustomMountingReceiver;
import com.znt.diange.receiver.CustomMountingReceiver.IMediaChangeListener;

public class MediaScanService extends Service implements ILocalMusicScanListener, IMediaScanListener
{

	private static final CommonLog log = LogFactory.createLog();
	

	public static final String SCAN_MUSIC_FINISH = "com.znt.diange.server.dms.SCAN_MUSIC_FINISH";
	public static final String SCAN_MUSIC_START = "com.znt.diange.server.dms.SCAN_MUSIC_START";
	
	private MediaScannerCenter mMediaScannerCenter;
	
	private Map<String, String> mMediaStoreMap = new HashMap<String, String>();
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();		
		registerSdReceiver();
		mMediaScannerCenter = MediaScannerCenter.getInstance(getApplicationContext());
		scanLocalMedias();
		log.e("MediaServerService onCreate");
	}
	
	private void scanLocalMedias()
	{
		mMediaScannerCenter.startScanThread(this);
	}
	
	@Override
	public void onDestroy() 
	{
		unInitService();	
		unRegisterSdReceiver();
		log.e("MediaServerService onDestroy");
		super.onDestroy();
	
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	* @Description: 添加一个文件后，直接调用一下这个函数，对添加的文件进行扫描，
	* 由于只是扫描了我们添加的文件，并非对整个媒体库文件进行扫描，因此，效率最高。
	* @param @param filename   
	* @return void 
	* @throws
	 */
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
				scanLocalMedias();
			}
		});
        this.registerReceiver(receiver, filter);
    }
    public void unRegisterSdReceiver()
    {
    	this.unregisterReceiver(receiver);
    }

	/**
	*callbacks
	*/
	@Override
	public void onScanStart()
	{
		// TODO Auto-generated method stub
		sendMusicScanStart();
	}

	/**
	*callbacks
	*/
	@Override
	public void onScanFinish()
	{
		// TODO Auto-generated method stub
		sendMusicScanResult();
	}

	/**
	*callbacks
	*/
	@Override
	public void onScanDoing()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	*callbacks
	*/
	@Override
	public void mediaScan(int mediaType, String mediaPath, String mediaName) 
	{
		// TODO Auto-generated method stub
		String mediaDir = removeHeadDir(mediaPath);
		
		boolean result = FileHelper.createDirectory(mediaDir);
		if(result)
		{
			String webPath = mediaDir + "/" + mediaName;
			mMediaStoreMap.put(mediaPath, webPath);
		}
	}
	
	private String removeHeadDir(String reDir)
	{
		if(reDir.contains("/"))
		{
			String[] subDirs = reDir.split("/");
			if(subDirs.length > 1)
				reDir = subDirs[subDirs.length - 2];
		}
		return reDir;
	}
}
