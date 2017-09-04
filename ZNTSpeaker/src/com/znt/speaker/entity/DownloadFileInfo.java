package com.znt.speaker.entity;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.text.TextUtils;

import com.znt.diange.mina.entity.SongInfor;

public class DownloadFileInfo 
{
	public SongInfor songInfor = null;
	private final String TEMP_FILE_TAG = ".temp";
	private String downloadDir = Environment.getExternalStorageDirectory() + "/";
	
	public DownloadFileInfo()
	{
		createDownloadDir();
	}
	public DownloadFileInfo(SongInfor songInfor)
	{
		this.songInfor = songInfor;
		createDownloadDir();
	}
	
	public SongInfor getSongInfor()
	{
		return songInfor;
	}
	public SongInfor getSongInforNew()
	{
		songInfor.setMediaUrl(getLocalFileNamePath());
		return songInfor;
	}
	
	public boolean isFileExist()
	{
		File file = new File(getLocalFileNamePath());
		return file.exists();
	}
	
	private void createDownloadDir()
	{
		File file = new File(downloadDir);
		if(!file.exists())
			file.mkdirs();
	}
	
	public String getMediaUrl()
	{
		if(songInfor == null)
			return null;
		return songInfor.getMediaUrl();
	}
	
	public String getLocalFileNamePath()
	{
		if(songInfor == null)
			return "";
		String name = null;
		String fileType = null;
		String url = songInfor.getMediaUrl();
		if(url.contains("/"))
		{
			 name = url.substring(url.lastIndexOf("/") + 1, url.length());
		}
		if(TextUtils.isEmpty(name))
		{
			if(url.contains("."))
				fileType = url.substring(url.lastIndexOf("."), url.length());
			name = songInfor.getMediaName() + fileType;
		}
			
		/*String url = songInfor.getMediaUrl();
		if(url.contains("."))
		{
			fileType = url.substring(url.lastIndexOf("."), url.length());
		}
		if(!TextUtils.isEmpty(songInfor.getMediaName()))
			name = songInfor.getMediaName() + fileType;
		else if(url.contains("/"))
			 {
				 name = url.substring(url.lastIndexOf("/") + 1, url.length());
			 }*/
		
		return downloadDir + name;
	}
	
	public String getTempFile()
	{
		return getLocalFileNamePath() + TEMP_FILE_TAG;
	}
	public long getDoneSize()
	{
		long size = 0;
		File file = new File(getTempFile());
		if(file.exists())
			size = file.length();
		return size;
	}
	public boolean createTempFile()
	{
		File file = new File(getTempFile());
		boolean result = false;
		try 
		{
			if(!file.exists())
				//file.delete();
			result = file.createNewFile();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public void deleteTempFile()
	{
		File file = new File(getTempFile());
		if(file.exists())
			file.delete();
	}
	public long getTempFileLen()
	{
		File file = new File(getTempFile());
		if(file.exists())
			return file.length();
		return 0;
	}
	public boolean renameDownloadFile()
	{
		boolean result = false;
		File file = new File(getTempFile());
		if(file.exists())
		{
			String realFile = getTempFile();
			if(realFile.endsWith(TEMP_FILE_TAG))
				realFile = realFile.substring(0, realFile.lastIndexOf(TEMP_FILE_TAG));
			File fileNew = new File(realFile);
			result = file.renameTo(fileNew);
			fileNew.setLastModified(System.currentTimeMillis());
		}
		return result;
	}
}
