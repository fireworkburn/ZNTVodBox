package com.znt.vodbox.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class DownHelper
{
	private boolean isStop = false;
	
	private final int IO_BUFFER_SIZE = 8 * 1024;
	
	private MyDownloadListener downloadListener = null;
	
    public DownHelper()
    {
        
    }
    
    public void setDownLoadListener(MyDownloadListener downloadListener)
    {
    	this.downloadListener = downloadListener;
    }
    
    public void stop()
    {
    	isStop = true;
    }
    
    public String getDlAndPath(String mid)
    {
        String downUrl = null;
        // 根据mid获取MP3dl和mp3path
        String midUrl = "http://player.kuwo.cn/webmusic/st/getNewMuiseByRid?rid=MUSIC_" + mid;
        
        URL webUrl;
        try
        {
            webUrl = new URL(midUrl);
            HttpURLConnection con = (HttpURLConnection)webUrl.openConnection();
            InputStream input = con.getInputStream();
            BufferedReader bufbr = new BufferedReader(new InputStreamReader(input));
            String strLine = null;
            String dl = null;
            String path = null;
            String size = null;
            while ((strLine = bufbr.readLine()) != null)
            {
            	if(isStop)
            	{
            		break;
            	}
            		
                if (strLine.startsWith("<mp3dl>"))
                {
                    int start = strLine.indexOf(">");
                    int end = strLine.lastIndexOf("<");
                    dl = strLine.substring(start + 1, end);
                }
                else if (strLine.startsWith("<mp3path>"))
                {
                    int start = strLine.indexOf(">");
                    int end = strLine.lastIndexOf("<");
                    path = strLine.substring(start + 1, end);
                }
                else if (strLine.startsWith("<mp3size>"))
                {
                	int start = strLine.indexOf(">");
                	int end = strLine.lastIndexOf("<");
                	size = strLine.substring(start + 1, end);
                }
            }
            downUrl = "http://" + dl + "/resource" + "/" + path;
            
            /*// 获取当前时间戳 时间以秒为单位
            long timestamp = System.currentTimeMillis() / 1000;
            // 把时间戳转换成十六进制的字符串
            String timeStr = timestamp + "";//Long.toHexString(timestamp);
            
            // 获取字符串
            String str = "kuwo_web@1906/resource/" + path + timeStr;
            // 把字符串计算成md5
            // 拿到一个MD5转换器
            MessageDigest md5;
            try
            {
                md5 = java.security.MessageDigest.getInstance("MD5");
                // 输入的字符串转换成字节数组
                byte[] b = str.getBytes();
                // b是输入字符串转换得到的字节数组
                md5.update(b);
                // 转换并返回结果，也是字节数组，包含16个元素
                byte[] by = md5.digest();
                // 字符数组转换成字符串 mUrl就是转换后mdt5的值
                String mUrl = byteArrayToHex(by);
                // 拼接下载地址
                downUrl = "http://" + dl + "/" + mUrl + "/" + timeStr + "/" + "resource" + "/" + path;
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
                return null;
            }*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return downUrl;
    }
    /*public String getVideoPath(String mid)
    {
    	String downUrl = null;
    	// 根据mid获取MP3dl和mp3path
    	String midUrl = "http://player.kuwo.cn/webmusic/st/getNewMuiseByRid?rid=MUSIC_" + mid;
    	
    	URL webUrl;
    	try
    	{
    		webUrl = new URL(midUrl);
    		HttpURLConnection con = (HttpURLConnection)webUrl.openConnection();
    		InputStream input = con.getInputStream();
    		BufferedReader bufbr = new BufferedReader(new InputStreamReader(input));
    		String strLine = null;
    		String dl = null;
    		String path = null;
    		String size = null;
    		String readStr = null;
    		while ((strLine = bufbr.readLine()) != null)
    		{
    			if(isStop)
    			{
    				break;
    			}
    			readStr += strLine;
    			if (strLine.startsWith("<mp3dl>"))
    			{
    				int start = strLine.indexOf(">");
    				int end = strLine.lastIndexOf("<");
    				dl = strLine.substring(start + 1, end);
    			}
    			else if (strLine.startsWith("<mp3path>"))
    			{
    				int start = strLine.indexOf(">");
    				int end = strLine.lastIndexOf("<");
    				path = strLine.substring(start + 1, end);
    			}
    			else if (strLine.startsWith("<mp3size>"))
    			{
    				int start = strLine.indexOf(">");
    				int end = strLine.lastIndexOf("<");
    				size = strLine.substring(start + 1, end);
    			}
    		}
    		downUrl = "http://" + dl + "/resource" + "/" + path;
    		
    		// 获取当前时间戳 时间以秒为单位
            long timestamp = System.currentTimeMillis() / 1000;
            // 把时间戳转换成十六进制的字符串
            String timeStr = timestamp + "";//Long.toHexString(timestamp);
            
            // 获取字符串
            String str = "kuwo_web@1906/resource/" + path + timeStr;
            // 把字符串计算成md5
            // 拿到一个MD5转换器
            MessageDigest md5;
            try
            {
                md5 = java.security.MessageDigest.getInstance("MD5");
                // 输入的字符串转换成字节数组
                byte[] b = str.getBytes();
                // b是输入字符串转换得到的字节数组
                md5.update(b);
                // 转换并返回结果，也是字节数组，包含16个元素
                byte[] by = md5.digest();
                // 字符数组转换成字符串 mUrl就是转换后mdt5的值
                String mUrl = byteArrayToHex(by);
                // 拼接下载地址
                downUrl = "http://" + dl + "/" + mUrl + "/" + timeStr + "/" + "resource" + "/" + path;
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
                return null;
            }
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return downUrl;
    }*/
    
    public String byteArrayToHex(byte[] byteArray)
    {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray)
        {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }
    
    public void downloadFile(final String downUrl, final String filePath)
    {
    	isStop = false;
    	
        if (downUrl == null || downUrl.equals(""))
        {
            throw new RuntimeException("下载地址为空");
        }
        if (filePath == null || filePath.equals(""))
        {
            throw new RuntimeException("保存路径为空");
        }
        
        new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				 InputStream in = null;
			     FileOutputStream out = null;
				// TODO Auto-generated method stub
				if(downloadListener != null && !isStop)
					downloadListener.onDownloadStart(0);
		        
				try
				{
					URL url = new URL(downUrl);
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			        conn.setConnectTimeout(5000);
			        if (conn.getResponseCode() == 200)
			        {
			           in = conn.getInputStream();
			        }
			        else
			        {
			        	if(downloadListener != null && !isStop)
							downloadListener.onDownloadError("下载失败:" + conn.getResponseCode());
			        	return;
			        }
			        
			        if(in == null)
			        	return;
			        byte[] data = new byte[IO_BUFFER_SIZE];
			        out = new FileOutputStream(new File(filePath));
			        int read = 0;
					int contentLength = conn.getContentLength(); 
					int progress = 0;
			        do
			        {
			        	if(isStop)
			        	{
			        		in.close();
			                out.close();
			                if(downloadListener != null)
								downloadListener.onDownloadExit();
			                break;
			        	}
			        		
			        	read = in.read(data);
			            if (read == -1)
			            {
			                in.close();
			                out.close();
			                break;
			            }
			            
			            progress += read;
			            
			            if(downloadListener != null && !isStop)
							downloadListener.onDownloadProgress(progress, contentLength);
			            
			            out.write(data, 0, read);
			        } while (true);
			        
			        if(downloadListener != null && !isStop)
						downloadListener.onDownloadFinish();
				} 
				catch (MalformedURLException e)
				{
					 if(downloadListener != null && !isStop)
							downloadListener.onDownloadError(e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (IOException e)
				{
					if(downloadListener != null && !isStop)
						downloadListener.onDownloadError(e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
        
    }
    
    public String dealBackUrl(byte[] data)
    {
        String[] lines = new String(data).split("[\r\n]");
        String dl = null;
        String path = null;
        for (String strLine : lines)
        {
            if (strLine.startsWith("<mp3dl>"))
            {
                int start = strLine.indexOf(">");
                int end = strLine.lastIndexOf("<");
                dl = strLine.substring(start + 1, end);
            }
            else if (strLine.startsWith("<mp3path>"))
            {
                int start = strLine.indexOf(">");
                int end = strLine.lastIndexOf("<");
                path = strLine.substring(start + 1, end);
            }
            
        }
        // 获取当前时间戳 时间以秒为单位
        long timestamp = System.currentTimeMillis() / 1000;
        // 把时间戳转换成十六进制的字符串
        String timeStr = Long.toHexString(timestamp);
        // 获取字符串
        String str = "kuwo_web@1906/resource/" + path + timeStr;
        // 把字符串计算成md5
        // 拿到一个MD5转换器
        MessageDigest md5;
        try
        {
            md5 = MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] b = str.getBytes();
            // b是输入字符串转换得到的字节数组
            md5.update(b);
            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] by = md5.digest();
            // 字符数组转换成字符串 mUrl就是转换后mdt5的值
            String mUrl = byteArrayToHex(by);
            // 拼接下载地址
            return "http://" + dl + "/" + mUrl + "/" + timeStr + "/" + "resource" + "/" + path;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public interface MyDownloadListener
	{
		
		public void onDownloadStart(int total);
		
		public void onFileExist(String fileName);
		
		public void onDownloadProgress(int progress, int size);

		public void onDownloadError(String error);
		
		public void onDownloadFinish();
		
		public void onDownloadExit();
	}
}
