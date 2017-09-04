package com.znt.diange.dms.media;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.content.Context;
import android.os.Environment;

import com.znt.diange.dms.media.MediaScannerCenter.ILocalMusicScanListener;
import com.znt.speaker.util.CommonLog;
import com.znt.speaker.util.FileHelper;
import com.znt.speaker.util.LogFactory;

public class MediaStoreCenter implements IMediaScanListener
{

	public enum SourceType
	{
		Phone,
		Speaker
	}

	private static final CommonLog log = LogFactory.createLog();
	
	private static  MediaStoreCenter mInstance;
	private Context mContext;
	
	private SourceType sourceType = SourceType.Phone;
	
	private String mShareRootPath = "";
	/*private String mImageFolderPath = "";
	private String mVideoFolderPath = "";
	private String mAudioFolderPath = "";*/
	
	private MediaScannerCenter mMediaScannerCenter;
	private Map<String, String> mMediaStoreMap = new HashMap<String, String>();
	private List<String> dirList = null;
	
	
	private MediaStoreCenter(Context context, SourceType sourceType) 
	{
		mContext = context;
		this.sourceType = sourceType;
		
		initData();
	}

	public static synchronized MediaStoreCenter getInstance(Context context, SourceType sourceType) 
	{
		if (mInstance == null)
		{
			mInstance  = new MediaStoreCenter(context, sourceType);
		}
		return mInstance;
	}

	private void initData()
	{
		mShareRootPath = mContext.getFilesDir().getAbsolutePath()+"/" + "rootFolder/";
		/*mImageFolderPath = mShareRootPath  + "Image";
		mVideoFolderPath = mShareRootPath  + "Video";
		mAudioFolderPath = mShareRootPath  + "Audio";*/
		mMediaScannerCenter = MediaScannerCenter.getInstance(mContext, sourceType);
	}
	
	public void setOnLocalMusicScanListener(ILocalMusicScanListener iLocalMusicScanListener)
	{
		mMediaScannerCenter.setOnLocalMusicScanListener(iLocalMusicScanListener);
	}
	
	public String getRootDir()
	{
		return mShareRootPath;
	}
	public void clearAllData()
	{
		stopScanMedia();
		clearMediaCache();
		clearWebFolder();
	}
	
	public boolean createWebFolder()
	{
		boolean ret = FileHelper.createDirectory(mShareRootPath);
		if (!ret)
		{
			return false;
		}
		
		/*FileHelper.createDirectory(mImageFolderPath);
		FileHelper.createDirectory(mVideoFolderPath);
		FileHelper.createDirectory(mAudioFolderPath);*/
		
		return true;
	}
	
	public boolean clearWebFolder()
	{

		long time = System.currentTimeMillis();
		boolean ret = FileHelper.deleteDirectory(mShareRootPath);
		long time1 = System.currentTimeMillis();
		log.e("clearWebFolder cost : " + (time1 - time));
		return ret;
	}

	public void clearMediaCache()
	{
		mMediaStoreMap.clear();
	}
	
	public void doScanMedia()
	{
		mMediaScannerCenter.startScanThread(this);
	}
	
	public void stopScanMedia()
	{
		mMediaScannerCenter.stopScanThread();
		while(!mMediaScannerCenter.isThreadOver())
		{
			try 
			{
				Thread.sleep(100);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void mediaScan(int mediaType, String mediaPath, String mediaName) 
	{
		
		switch (mediaType) 
		{
		case MediaScannerCenter.AUDIO_TYPE:
			mapAudio(mediaPath, mediaName);
			break;
		case MediaScannerCenter.VIDEO_TYPE:
			//mapVideo(mediaPath, mediaName);
			break;
		case MediaScannerCenter.IMAGE_TYPE:
		   // mapImage(mediaPath, mediaName);
			break;
		default:
			break;
		}
	}
	
	private void mapAudio( String mediaPath, String mediaName)
	{
		String mediaDir = "";
		/*if(mediaPath.contains("/"))
			mediaDir = mediaPath.substring(0, mediaPath.lastIndexOf("/"));*/
		
		/*if(resDirType == ResDirType.MultipleDir)//多级目录
		{
			mediaDir = removeRootDir(mediaDir);
		}
		else//单级目录
*/		{
			mediaDir = removeHeadDir(mediaPath);
		}
		
		mediaDir = mShareRootPath + mediaDir;
		
		boolean result = FileHelper.createDirectory(mediaDir);
		if(result)
		{
			String webPath = mediaDir + "/" + mediaName;
			mMediaStoreMap.put(mediaPath, webPath);
			//softLinkMode(mediaPath, webPath);
		}
	}
	
	/*private String removeRootDir(String reDir)
	{
		if(dirList == null || dirList.size() == 0)
			dirList = getStorageDirectoriesArrayList();
		int size = dirList.size();
		for(int i=0;i<size;i++)
		{
			String rootDir = dirList.get(i);
			if(reDir.contains(rootDir))
			{
				reDir = reDir.replace(rootDir, "");
				break;
			}
		}
		if(reDir.startsWith("/"))
			reDir = reDir.replaceFirst("/", "");
		return reDir;
	}*/
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
	
	private boolean softLinkMode(String localPath, String webPath)
	{
		int status;
		try
		{
			
			String[] commd = {"ln", "-s", localPath, webPath};
	        Runtime rt = Runtime.getRuntime() ;
			
	        Process p = rt.exec(commd);
        	releaseProcessStream(p);
			
			status = p.waitFor();		
			if (status == 0) 
			{
				return true;//success
			} 
			else 
			{
				//log.e("status = " + status + ", run ln -s failed !localPath = " + localPath);
				return false;
			}
		}
		catch (Exception e) 
		{
			log.e("Catch Exceptino run ln -s failed error--> " + e.getMessage());
			return false;
		}
	}
	
	private void releaseProcessStream(Process p) throws IOException
	{
		InputStream stderr = p.getErrorStream();
		InputStreamReader isr = new InputStreamReader(stderr);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		while ( (line = br.readLine()) != null)
			System.out.println(line);
	}
	
	
	/**
	* @Description: 获取所有的存储设备列表
	* @param @return   
	* @return ArrayList<String> 
	* @throws
	 */
	private ArrayList<String> getStorageDirectoriesArrayList()
    {
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader bufReader = null;
        try 
        {
            bufReader = new BufferedReader(new FileReader("/proc/mounts"));
            list.add(Environment.getExternalStorageDirectory().getPath());
            String line;
            while((line = bufReader.readLine()) != null) 
            {
                if(line.contains("vfat") || line.contains("exfat") ||
                   line.contains("/mnt") || line.contains("/Removable")) 
                {
                    StringTokenizer tokens = new StringTokenizer(line, " ");
                    String s = tokens.nextToken();
                    s = tokens.nextToken(); // Take the second token, i.e. mount point

                    if (list.contains(s))
                        continue;

                    if (line.contains("/dev/block/vold")) 
                    {
                        if (!line.startsWith("tmpfs") &&
                            !line.startsWith("/dev/mapper") &&
                            !s.startsWith("/mnt/secure") &&
                            !s.startsWith("/mnt/shell") &&
                            !s.startsWith("/mnt/asec") &&
                            !s.startsWith("/mnt/obb")
                            ) 
                        {
                            list.add(s);
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        finally 
        {
            if (bufReader != null) 
            {
                try 
                {
                    bufReader.close();
                }
                catch (IOException e) {}
            }
        }
        return list;
    }
}
