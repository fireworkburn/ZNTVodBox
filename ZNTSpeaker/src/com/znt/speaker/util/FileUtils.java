
package com.znt.speaker.util; 

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Log;

import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.SongInfor;

/** 
 * @ClassName: MyFileUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-11 下午4:41:52  
 */
public class FileUtils
{

	/**
	* @Description: 判断文件是否有效
	* @param @param filrUrl
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean isFileValid(String filrUrl)
	{
		if(TextUtils.isEmpty(filrUrl))
			return false;
		File tempFile = new File(filrUrl);
		if(!tempFile.exists())
			return false;
		if(tempFile.length() == 0)
			return false;
		
		return true;
	}
	
	/**
	* @Description: 获取目录下的图片文件(媒体库中获取)
	* @param @param context
	* @param @return   
	* @return List<MyFileInfor> 
	* @throws
	 */
	public static List<MediaInfor> getLocalImages(Context context)
    {
    	List<MediaInfor> localPicList = new ArrayList<MediaInfor>();
    	
    	int photoIndex,photoNameIndex,photoTitleIndex,photoIDIndex,photoSizeIndex,photoOrientation;
    	String columns[] = new String[]{Media.DATA,Media.TITLE,Media._ID,Media.DISPLAY_NAME,Media.SIZE,Media.ORIENTATION};
    	Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI,columns,null
    			,null,null);
    	if(null != cursor && cursor.getCount() > 0)
    	{
    		photoIndex = cursor.getColumnIndexOrThrow(Media.DATA);
        	photoNameIndex = cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME);
        	photoIDIndex = cursor.getColumnIndexOrThrow(Media._ID);
        	photoTitleIndex = cursor.getColumnIndexOrThrow(Media.TITLE);
        	photoSizeIndex = cursor.getColumnIndexOrThrow(Media.SIZE);
        	photoOrientation = cursor.getColumnIndexOrThrow(Media.ORIENTATION);
        	for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        	{
        		MediaInfor mediaInfor = new MediaInfor();
        		String name = cursor.getString(photoNameIndex);
		    	String title = cursor.getString(photoTitleIndex);
		    	String path = cursor.getString(photoIndex);
		    	String LocalImgId = cursor.getString(photoIDIndex);
		    	long size = cursor.getLong(photoSizeIndex);
		    	float rotate = cursor.getFloat(photoOrientation);
		    	if(path.length()<5) continue;//图片名字最少4个字符
        		String folderPath = path.substring(0, path.lastIndexOf("/")); 
        		String picAlbum = null;
        		if(folderPath.contains("/"))
        			picAlbum = folderPath.substring(folderPath.lastIndexOf("/")+1);
        		else
        			picAlbum = folderPath;
        		
        		//mediaInfor.setMediaUrl(folderPath);
        		mediaInfor.setMediaName(name);
        		mediaInfor.setMediaSize(size);
        		mediaInfor.setMediaUrl(path);
        		
        		File file = new File(path);
        		
        		if(file.exists() && file.length() > 0)
        		{
        			localPicList.add(0,mediaInfor);
        		}
        	}
        }
    	if(cursor != null)
    		cursor.close();
        return localPicList;
    }
	
	/**
	* @Description: 获取目录下的文件(本地文件扫描方式)
	* @param @param fileList  获取的文件列表
	* @param @param type 0, 图片；1，音频；2，视频
	* @param @param path   目录路径
	* @return void 
	* @throws
	 */
	public static void getLocalFiles(final List<File> fileList, final int type, String path)
	{
		if(path == null || path.length() == 0)
			return ;
		File dirFile = new File(path);
		File[] files = dirFile.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				// TODO Auto-generated method stub
				if(pathname.isDirectory() && !pathname.isHidden() && pathname.canRead())
				{
					getLocalFiles(fileList, type, pathname.getAbsolutePath());
					return false;
				}
				boolean result = false;
				if(type == 0)//图片
					result = isPicture(pathname.getAbsolutePath());
				else if(type == 1)//音频
					result = isMusic(pathname.getAbsolutePath());
				else if(type == 2)//视频
					result = isVideo(pathname.getAbsolutePath());	
				if(result)
				{
					fileList.add(pathname);
				}
				return false;
			}
		});
	}
	
	/**
	* @Description: MP4文件
	* @param @param path
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean isVideo(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".mp4") || 
				path.toLowerCase().endsWith(".flv") ||
				path.toLowerCase().endsWith(".3gp") ||
				path.toLowerCase().endsWith(".wmv") ||
				path.toLowerCase().endsWith(".avi") ||
				path.toLowerCase().endsWith(".mkv") ||
				path.toLowerCase().endsWith(".rmvb") ||
				path.toLowerCase().endsWith(".mpg") ||
				path.toLowerCase().endsWith(".mpeg") ||
				path.toLowerCase().endsWith(".asf") ||
				path.toLowerCase().endsWith(".iso") ||
				path.toLowerCase().endsWith(".dat") ||
				path.toLowerCase().endsWith(".263") ||
				path.toLowerCase().endsWith(".h264") ||
				path.toLowerCase().endsWith(".mov") ||
				path.toLowerCase().endsWith(".rm") ||
				path.toLowerCase().endsWith(".ts") ||
				path.toLowerCase().endsWith(".vod") ||
				path.toLowerCase().endsWith(".mts"))
		{
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 音频文件
	* @param @param path
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean isMusic(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".mp3") || 
			path.toLowerCase().endsWith(".flac")||
			path.toLowerCase().endsWith(".wav") ||
			path.toLowerCase().endsWith(".ape") ||
			path.toLowerCase().endsWith(".aac") ||
			path.toLowerCase().endsWith(".wma") ||
			path.toLowerCase().endsWith(".ogg") ||
			path.toLowerCase().endsWith(".ac3") ||
			path.toLowerCase().endsWith(".ddp") ||
			path.toLowerCase().endsWith(".pcm")) 
		{
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 图片文件
	* @param @param path
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean isPicture(String path)
	{
		if(path == null || path.length() == 0)
			return false;
		if(path.toLowerCase().endsWith(".jpg") || 
				path.toLowerCase().endsWith(".png") ||
				path.toLowerCase().endsWith(".bmp") ||
				path.toLowerCase().endsWith(".gif"))
		{
			return true;
		}
		return false;
	}
	
	/**
	* @Description: 创建文件
	* @param @param fileUrl
	* @param @return   
	* @return int 
	* @throws
	 */
	public static int createFile(String fileUrl)
	{
		String tempStr = fileUrl;
		if(tempStr.contains("/"))
			tempStr = StringUtils.getHeadByTag("/", tempStr);
		File tempDir = new File(tempStr);
		if(!tempDir.exists())
		{
			if(!tempDir.mkdirs())
				return 1;
		}
		
		File tempFile = new File(fileUrl);
		if(tempFile.exists())
			tempFile.delete();
		try
		{
			if(tempFile.createNewFile())
				return 0;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 1;
	}
	
	/**
	* @Description: 删除本地单个文件
	* @param @param file
	* @param @return   
	* @return int 0,删除成功；1，文件不存在；2，删除失败
	* @throws
	 */
	public static int deleteFile(File file)
	{
		if(file == null || !file.exists())
			return 1;//文件不存在
		if(!file.canWrite())
			return 2;//没有写权限
		file.delete();
		return 0;
	}
	/**
	* @Description: 删除本地多个文件
	* @param @param files
	* @param @return   
	* @return int 0,删除成功；1，文件不存在；2，删除失败
	* @throws
	 */
	public static int deleteFile(List<File> files)
	{
		int result = 0;
		for(int i=0;i<files.size();i++)
		{
			result = deleteFile(files.get(0));
			if(result != 0)
			{
				break;
			}
		}
		return result;
	}
	
	/**
	* @Description: 删除本地目录
	* @param @param file
	* @param @return   
	* @return int 0,删除成功；1，文件不存在；2，删除失败
	* @throws
	 */
	public static int deleteFolder(File file)
	{
		if(file == null || !file.exists())
			return 1;// 目录不存在
		if(!file.canWrite())
			return 2;//没有写权限
		if(file.isFile())
			file.delete();
		else if(file.isDirectory())
		{
			File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0)
            {
                file.delete();
                return 0;
            }
            for(File f : childFile)
            {
            	deleteFolder(f);
            }
            file.delete();
		}
		return 0;
	}
	
	/**
	* @Description: 从流中获取字节数组
	* @param @param is
	* @param @return
	* @param @throws IOException   
	* @return byte[] 
	* @throws
	 */
	public static byte[] getBytes(InputStream is) throws IOException 
	{  
		if(is == null)
			return null;
       ByteArrayOutputStream outstream = new ByteArrayOutputStream();  
       byte[] buffer = new byte[1024]; // 用数据装  
       int len = -1;  
       while ((len = is.read(buffer)) != -1) 
       {  
           outstream.write(buffer, 0, len);  
       }  
       if(outstream != null)
    	   outstream.close();  
       // 关闭流一定要记得。  
       return outstream.toByteArray();  
   } 
	
	public static String getStringFromFile(File file)
	{
		if(file == null || !file.exists())
			return "";
		String str = "";
		
		try
		{
			FileInputStream fis = new FileInputStream(file);
			
			byte[] buffer = new byte[1024]; // 用数据装  
	        int len = -1;  
	        while ((len = fis.read(buffer)) != -1) 
	        {  
	    	     String s = new String(buffer);
	    	     str += s;
	        }  
	        if(fis != null)
	    	   fis.close();  
			
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return str;
	}
	
	/**
	* @Description: 打开安装包文件
	* @param @param activity
	* @param @param file   
	* @return void 
	* @throws
	 */
	public static void openApkFile(Activity activity, File file) 
	{
        // TODO Auto-generated method stub
		if(file == null || !file.exists())
			return;
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        activity.startActivity(intent);
	}
	
	/** 
     * 复制单个文件 
     * @param oldPath String 原文件路径 如：c:/fqf.txt 
     * @param newPath String 复制后路径 如：f:/fqf.txt 
     * @return boolean 
     */ 
   public static boolean copyFile(String oldPath, String newPath) 
   { 
	   boolean isok = true;
       try 
       { 
           int bytesum = 0; 
           int byteread = 0; 
           File oldfile = new File(oldPath); 
           if (oldfile.exists()) 
           { //文件存在时 
        	   FileInputStream inStream = new FileInputStream(oldPath); //读入原文件 
               File newFile = new File(newPath);
               if(!newFile.exists())
            	   createFile(newPath);
               FileOutputStream fos = new FileOutputStream(newPath); 
               BufferedOutputStream bos = new BufferedOutputStream(fos);
               byte[] buffer = new byte[1024 * 4]; 
               while ( (byteread = inStream.read(buffer)) != -1) 
               { 
                   bytesum += byteread; //字节数 文件大小 
                   bos.write(buffer, 0, byteread); 
               } 
               bos.flush(); 
               bos.close(); 
               inStream.close(); 
           }
           else
           {
        	   isok = false;
		   }
       } 
       catch (Exception e) 
       { 
           isok = false;
       } 
       return isok;

   } 

   /** 
     * 复制整个文件夹内容 
     * @param oldPath String 原文件路径 如：c:/fqf 
     * @param newPath String 复制后路径 如：f:/fqf/ff 
     * @return boolean 
     */ 
   public static boolean copyFolder(String oldPath, String newPath) 
   { 
	   boolean isok = true;
       try 
       { 
           (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
           File oldFile = new File(oldPath); 
           String[] file= oldFile.list(); 
           File temp=null; 
           for (int i = 0; i < file.length; i++) 
           { 
               if(oldPath.endsWith(File.separator))
               { 
                   temp = new File(oldPath + file[i]); 
               } 
               else
               { 
                   temp = new File(oldPath+File.separator + file[i]); 
               } 

               if(temp.isFile())
               { 
                   FileInputStream input = new FileInputStream(temp); 
                   FileOutputStream fos = new FileOutputStream(newPath + "/" + 
                           (temp.getName()).toString()); 
                   BufferedOutputStream bos = new BufferedOutputStream(fos);
                   byte[] b = new byte[1024 * 4 ];
                   int len; 
                   while ( (len = input.read(b)) != -1) 
                   { 
                	   bos.write(b, 0, len); 
                   } 
                   bos.flush(); 
                   bos.close(); 
                   input.close(); 
               } 
               if(temp.isDirectory())
               {
            	   //如果是子文件夹 
                   copyFolder(oldPath + "/"+ file[i],newPath + "/" + file[i]); 
               } 
           } 
       } 
       catch (Exception e) 
       { 
    	    isok = false;
       } 
       return isok;
   }
   
   /**
   * @Description: 写数据到文件中
   * @param @param filePath
   * @param @param content
   * @param @return   
   * @return int 
   * @throws
    */
   public static int writeDataToFile(String filePath, String content)
	{
		File file = new File(filePath);
		if(!file.exists())
		{
			int result = createFile(filePath);
			if(result != 0)
				return result;
		}
		
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = content.getBytes("utf-8");
			fos.write(buffer);
			if(fos != null)
				fos.close();
		} 
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1;
		}
		
		return 0;
	}
   
   /**
   * @Description: 从文件中获取流
   * @param @param filePath
   * @param @return   
   * @return InputStream 
   * @throws
    */
   public static FileInputStream readDataFromFile(String filePath)
   {
	   File file = new File(filePath);
	   if(!file.exists())
		   return null;
	   try
	   {
		   FileInputStream fis = new FileInputStream(file);
		   //byte[] buffer = new byte[(int) file.length()];
		   //fis.read(buffer);
		   //content = buffer.toString();
		   return fis;
	   } 
	   catch (FileNotFoundException e)
	   {
		// TODO Auto-generated catch block
		   e.printStackTrace();
		   return null;
	   } 
	   //return content;
   }
   
   /**
   * @Description: 获取未安装apk的图标
   * @param @param context
   * @param @param apkPath
   * @param @return   
   * @return Drawable 
   * @throws
    */
   public static Drawable getApkIcon(Context context, String apkPath) 
   {
       PackageManager pm = context.getPackageManager();
       PackageInfo info = pm.getPackageArchiveInfo(apkPath,
               PackageManager.GET_ACTIVITIES);
       if (info != null) 
       {
           ApplicationInfo appInfo = info.applicationInfo;
           appInfo.sourceDir = apkPath;
           appInfo.publicSourceDir = apkPath;
           try 
           {
               return appInfo.loadIcon(pm);
           } 
           catch (OutOfMemoryError e) 
           {
               Log.e("ApkIconLoader", e.toString());
           }
       }
       return null;
   }
   
   /**
    * 保存图片
    * @param cr
    * @param name
    * @param dateTaken
    * @param directory
    * @param filename
    * @param bitmap
    * @return
    */
   public static Uri insertImage(ContentResolver cr, String name, long dateTaken, String directory, String filename) 
   { 
   	  if(!directory.endsWith("/"))
   		  directory = directory + "/";
       String filePath = directory + filename; 
    
       ContentValues values = new ContentValues(7); 
       values.put(MediaStore.Images.Media.TITLE, name); 
       values.put(MediaStore.Images.Media.DISPLAY_NAME, filename); 
       values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken); 
       values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); 
       values.put(MediaStore.Images.Media.DATA, filePath); 
       return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); 
   }
   
   public static List<SongInfor> getLocalMusics(Context mContext)
	{
		
	    List<SongInfor> tempList = new ArrayList<SongInfor>();
	   
	    String AUDIO_PATH = MediaStore.Audio.AudioColumns.DATA;
		String AUDIO_DISPLAYHNAME = MediaStore.Audio.AudioColumns.DISPLAY_NAME;
		String AUDIO_DURATION = MediaStore.Audio.AudioColumns.DURATION;
		String AUDIO_COLUMN_STRS[] = {AUDIO_PATH, AUDIO_DISPLAYHNAME, AUDIO_DURATION};
	   
		Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
				AUDIO_COLUMN_STRS, 
				null, 
				null,
				AUDIO_DISPLAYHNAME);				
		if (cursor != null)
		{
			int count = cursor.getCount();
			if (count != 0)
			{
				int _name_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
	     		int _dir_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
	     		int _duration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
	    		if (cursor.moveToFirst())
	    		{  
	         		do
	         		{ 
	         			String srcpath = cursor.getString(_dir_index);
	         			String name = cursor.getString(_name_index);
	         			String duration = cursor.getString(_duration);
	         			int time = 0;
	         			if(!TextUtils.isEmpty(duration))
	         			{
	         				time = Integer.parseInt(duration);
	         			}
	         			if(time > 10 * 1000 && time < 10 * 60 * 1000)//10秒以上并且10分钟以下的文件
	         			{
	         				SongInfor infor = new SongInfor();
		         			infor.setMediaName(name);
		         			infor.setMediaUrl(srcpath);
		         			tempList.add(infor);
	         			}
	         		} 
	         		while (cursor.moveToNext());  
	         	}  			
			}	
			else
				return null;
			cursor.close();
		}

		return tempList;
	}
}
 
