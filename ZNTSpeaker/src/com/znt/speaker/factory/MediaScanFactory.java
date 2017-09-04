package com.znt.speaker.factory;

import com.znt.diange.dms.media.MediaScannerCenter.ILocalMusicScanListener;
import com.znt.diange.dms.media.MediaStoreCenter;
import com.znt.diange.dms.media.MediaStoreCenter.SourceType;
import com.znt.speaker.util.LogFactory;

import android.app.Activity;

public class MediaScanFactory 
{

	private Activity activity = null;
	public MediaScanFactory(Activity activity)
	{
		this.activity = activity;
	}
	
	private MediaStoreCenter mMediaStoreCenter = null;
	public void scanLocalMedias()
	{
		if(mMediaStoreCenter == null)
		{
			mMediaStoreCenter = MediaStoreCenter.getInstance(activity, SourceType.Speaker);
			mMediaStoreCenter.setOnLocalMusicScanListener(new ILocalMusicScanListener() 
			{
				
				@Override
				public void onScanStart() 
				{
					// TODO Auto-generated method stub
					LogFactory.createLog().e("��ʼɨ�豾���ļ�");
				}
				
				@Override
				public void onScanFinish() 
				{
					// TODO Auto-generated method stub
					LogFactory.createLog().e("�����ļ�ɨ�����");
				}
				
				@Override
				public void onScanDoing()
				{
					// TODO Auto-generated method stub
					
				}
			});
		}
		//if(scanMusic && !isMusicScaning)
		{
			mMediaStoreCenter.clearAllData();
			mMediaStoreCenter.clearWebFolder();
			mMediaStoreCenter.createWebFolder();
			mMediaStoreCenter.doScanMedia();
		}
	}
}
