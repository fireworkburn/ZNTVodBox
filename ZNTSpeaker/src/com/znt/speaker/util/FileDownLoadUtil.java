package com.znt.speaker.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Environment;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.DownloadFileInfo;
import com.znt.speaker.util.DownHelper.FileDownloadListener;

public class FileDownLoadUtil implements FileDownloadListener
{

	public enum DonwloadType
	{
		CUR,
		ALL,
		FAIL
	}
	
	private Activity activity = null;
	private List<SongInfor> downloadList = new ArrayList<SongInfor>();
	private List<SongInfor> downloadFailList = new ArrayList<SongInfor>();
	private boolean isDownloadAllFinish = false;
	private boolean isDownloadAllRunning = false;
	private DonwloadType type = DonwloadType.CUR;
	
	public static FileDownLoadUtil INSTANCE = null;
	
	private DownHelper downHelper = null;
	
	public void setDownloadType(DonwloadType type)
	{
		this.type = type;
	}
	private FileDownLoadUtil(Activity activity)
	{
		this.activity = activity;
		downHelper = new DownHelper(activity);
		downHelper.setDownLoadListener(this);
	}
	
	public static FileDownLoadUtil getInstance(Activity activity)
	{
		if(INSTANCE == null)
		{
			synchronized (FileDownLoadUtil.class) 
			{
				if(INSTANCE == null)
					INSTANCE = new FileDownLoadUtil(activity);
			}
		}
		
		return INSTANCE;
	}
	
	public void stopDownLoadFiles()
	{
		this.downloadList.clear();
	}
	
	public void setDownloadAllUnFinish()
	{
		isDownloadAllFinish = false;
	}
	
	public void downloadCurPlanMusics(List<SongInfor> list)
	{
		if(!isDownloadAllRunning)
		{
			if(downloadList.size() == 0)
			{
				downloadList.addAll(list);
			}
			downloadAllPlanMusics(false);
		}
	}
	public void addFirstFile(SongInfor infor)
	{
		downloadList.add(0, infor);
		downloadAllPlanMusics(false);
	}
	
	public void downloadAllPlanMusics(final boolean isAll)
	{
		if(isDownloadAllAvailable())
		{
			if(isAll)
			{
				if(isDownloadAllRunning)
					return;
				type = DonwloadType.ALL;
			}
			else
				type = DonwloadType.CUR;
			
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					if(isAll)
					{
						isDownloadAllFinish = false;
						isDownloadAllRunning = true;
						List<SongInfor> tempList = DBManager.newInstance(activity).getAllPlanMusics();
						if(tempList.size() > 0)
						{
							downloadList.clear();
							downloadList.addAll(tempList);
							tempList.clear();
							downloadFiles();
						}
						isDownloadAllFinish = true;
						isDownloadAllRunning = false;
					}
					else
						downloadFiles();
				}
			}).start();
		}
	}
	
	private void downloadFiles()
	{
		while(true)
		{
			if(downloadList.size() == 0)
			{
				if(downloadFailList.size() > 0)
				{
					downloadList.addAll(downloadFailList);
					downloadFailList.clear();
					type = DonwloadType.FAIL;
				}
				else
				{
					break;
				}
			}
			else
			{
				SongInfor songInfor = downloadList.remove(0);
				if(songInfor != null)
				{
					DownloadFileInfo downloadFileInfo = new DownloadFileInfo(songInfor);
					if(!downloadFileInfo.isFileExist())
					{
						try 
						{
							Thread.sleep(10);
						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						downloadFile(downloadFileInfo);
					}
				}
			}
		}
	}
	
	public void downloadFile(final DownloadFileInfo downloadFileInfo)
	{
		downHelper.downloadFile(downloadFileInfo);
	}
	
	public boolean isDownloadAllAvailable()
	{
		int failSize = downloadFailList.size();
		LogFactory.createLog().e("*************failSize-->"+failSize);
		return isDownloadAllFinish == false && isDownloadAllRunning == false;
	}
	
	public boolean isFileExsit(SongInfor songInfor)
	{
		String url = songInfor.getMediaUrl();
		File file = new File(getMusicLocalPath(url));
		return file.exists();
	}
	private String getMusicLocalPath(String url)
	{
		String fileName = "";
		if(url.contains("."))
			fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
		String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
		
		return filePath;
	}
	@Override
	public void onDownloadStart(DownloadFileInfo info) 
	{
		// TODO Auto-generated method stub
		LogFactory.createLog().e("开始下载文件-->" + info.getSongInfor().getMediaName());
	}
	@Override
	public void onFileExist(DownloadFileInfo info) 
	{
		// TODO Auto-generated method stub
		LogFactory.createLog().e(info.getSongInfor().getMediaName() + " 文件已存在");
	}
	@Override
	public void onDownloadProgress(int progress, int size) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDownloadError(DownloadFileInfo info, String error)
	{
		// TODO Auto-generated method stub
		LogFactory.createLog().e("下载文件   " + info.getSongInfor().getMediaName() + "  失败-->" + error);
		if(type != DonwloadType.FAIL)
		{
			if(error == null || !error.contains("404"))
			{
				downloadFailList.add(info.getSongInfor());
			}
		}
	}
	@Override
	public void onDownloadFinish(DownloadFileInfo info) 
	{
		// TODO Auto-generated method stub
		LogFactory.createLog().e(info.getSongInfor().getMediaName() + "    下载完成");
	}
	@Override
	public void onDownloadExit(DownloadFileInfo info)
	{
		// TODO Auto-generated method stub
	}
}
	
	
