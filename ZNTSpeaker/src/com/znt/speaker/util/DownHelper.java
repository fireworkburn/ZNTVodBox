package com.znt.speaker.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.app.Activity;
import android.text.TextUtils;

import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.DownloadFileInfo;


public class DownHelper
{
	private Activity activity = null;
	private boolean isStop = false;
	
	private final int IO_BUFFER_SIZE = 8 * 1024;
	
	private FileDownloadListener fileDownloadListener = null;
	
    public DownHelper()
    {
        
    }
    public DownHelper(Activity activity)
    {
    	this.activity = activity;
    }
    
    public void setDownLoadListener(FileDownloadListener downloadListener)
    {
    	this.fileDownloadListener = downloadListener;
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
    	
        if (TextUtils.isEmpty(downUrl))
        {
            return;
        }
        if (TextUtils.isEmpty(filePath))
        {
        	return;
        }
        
        new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				 InputStream in = null;
			     FileOutputStream out = null;
				// TODO Auto-generated method stub
		        
				try
				{
					String encodedUrl = UrlUtil.getASCIIEncodedUrl(downUrl, "UTF-8");
					URL url = new URL(encodedUrl);
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					conn.setRequestProperty("contentType", "UTF-8");  
					conn.setConnectTimeout(15000);
					conn.setReadTimeout(15000);// 设置getInputStream超时时间
			        if (conn.getResponseCode() == 200)
			        {
			           in = conn.getInputStream();
			        }
			        else
			        {
			        	if(fileDownloadListener != null && !isStop)
			        		fileDownloadListener.onDownloadError(null, "下载失败:" + conn.getResponseCode());
			        	return;
			        }
			        
			        if(in == null)
			        	return;
			        byte[] data = new byte[IO_BUFFER_SIZE];
			        File file = new File(filePath);
			        if(!file.exists())
			        	file.createNewFile();
			        out = new FileOutputStream(file);
			        int read = 0;
					int contentLength = conn.getContentLength(); 
					int progress = 0;
			        do
			        {
			        	if(isStop)
			        	{
			        		in.close();
			                out.close();
			                if(fileDownloadListener != null)
			                	fileDownloadListener.onDownloadExit(null);
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
			            
			            if(fileDownloadListener != null && !isStop)
			            	fileDownloadListener.onDownloadProgress(progress, contentLength);
			            
			            out.write(data, 0, read);
			        } while (true);
			        out.flush();
			        if(fileDownloadListener != null && !isStop)
			        	fileDownloadListener.onDownloadFinish(null);
				} 
				catch (MalformedURLException e)
				{
					 if(fileDownloadListener != null && !isStop)
						 fileDownloadListener.onDownloadError(null, e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (IOException e)
				{
					if(fileDownloadListener != null && !isStop)
						fileDownloadListener.onDownloadError(null, e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
        
    }
    private String curDownloadFile = "";
    private final int RETRY_MAX = 3;
    private int retryCount = 0;
    private void setCurDownloadFile(DownloadFileInfo downloadFileInfo)
    {
    	if(downloadFileInfo == null)
    		this.curDownloadFile = "";
    	else
    		this.curDownloadFile = downloadFileInfo.getMediaUrl();
    }
    public void downloadFile(final DownloadFileInfo downloadFileInfo)
    {
    	isStop = false;
    	if(downloadFileInfo == null)
    		return;
    	if(curDownloadFile != null && curDownloadFile.equals(downloadFileInfo.getMediaUrl()))
    		return;
    	if (TextUtils.isEmpty(downloadFileInfo.getMediaUrl()))
    	{
    		return;
    	}
    	if (TextUtils.isEmpty(downloadFileInfo.getLocalFileNamePath()))
    	{
    		return;
    	}
    	
    	BufferedInputStream bis = null;
    	RandomAccessFile access = null;
		//FileOutputStream out = null;
		// TODO Auto-generated method stub
		if(fileDownloadListener != null && !isStop)
			fileDownloadListener.onDownloadStart(downloadFileInfo);
		
		setCurDownloadFile(downloadFileInfo);
		
		long mLoadedByteLength = downloadFileInfo.getDoneSize();
		HttpURLConnection conn = null;
		try
		{
			conn = getHttpConnecttioin(downloadFileInfo);
			int responseCode = -1;
			if(conn != null)
			{
				conn.connect();
				responseCode = conn.getResponseCode();
			}
			if(responseCode == 404 || responseCode < 0)
			{
				if(fileDownloadListener != null && !isStop)
					fileDownloadListener.onDownloadError(downloadFileInfo, "下载失败:" + responseCode);
				return;
			}
			boolean isConnectSuccess = false;
			retryCount = 0;
			while(retryCount < RETRY_MAX)
			{
				
				if(isStop)
					break;
				retryCount ++;
				if (conn!= null && (responseCode == 200 || responseCode == 206))
				{
					bis = new BufferedInputStream(conn.getInputStream(), IO_BUFFER_SIZE);
					isConnectSuccess = true;
					break;
				}
				else
					conn = getHttpConnecttioin(downloadFileInfo);
			}
			if(!isConnectSuccess)
			{
				if(fileDownloadListener != null && !isStop)
					fileDownloadListener.onDownloadError(downloadFileInfo, "下载失败:" + responseCode);
				setCurDownloadFile(null);
				return;
			}
			
			/*if (conn!= null && (conn.getResponseCode() == 200 || conn.getResponseCode() == 206))
			{
				bis = new BufferedInputStream(conn.getInputStream(), IO_BUFFER_SIZE);
			}
			else
			{
				if(fileDownloadListener != null && !isStop)
					fileDownloadListener.onDownloadError(downloadFileInfo, "下载失败:" + responseCode);
					setCurDownloadFile(null);
				return;
			}*/
			
			byte[] data = new byte[IO_BUFFER_SIZE];
			
			downloadFileInfo.createTempFile();//创建临时文件
			access = new RandomAccessFile(downloadFileInfo.getTempFile(), "rw");
            // 移动指针到开始位置
            access.seek(mLoadedByteLength);
			//out = new FileOutputStream(downloadFileInfo.getTempFile(), true);////true表示向打开的文件末尾追加数据
			int read = 0;
			int contentLength = conn.getContentLength(); 
			checkAndReleaseSpace(contentLength);
			/*int progress = 0;*/
			while(true)
			{
				if(isStop)
				{
					if(fileDownloadListener != null)
						fileDownloadListener.onDownloadExit(downloadFileInfo);
					setCurDownloadFile(null);
					break;
				}
				read = bis.read(data);
				if (read == -1)
				{
					break;
				}
				access.write(data, 0, read);
				//out.write(data, 0, read);
			}
			//out.flush();
			long localLen = downloadFileInfo.getTempFileLen();
			if(contentLength + mLoadedByteLength == localLen)
			{
				//下载成功，文件重命名
				boolean result = downloadFileInfo.renameDownloadFile();
				if(result)
				{
					//downloadFileInfo.songInfor.setMediaSize(localLen);
					saveFileToDb(downloadFileInfo);
				}
			}
			else
			{
				downloadFileInfo.deleteTempFile();
				if(fileDownloadListener != null)
					fileDownloadListener.onDownloadError(downloadFileInfo, "文件未下载完成   contentLength-->" + contentLength + "  localLen-->"+localLen);
				setCurDownloadFile(null);
				return;
			}
			//下载成功，文件重命名
			/*boolean result = downloadFileInfo.renameDownloadFile();
			if(result)
				saveFileToDb(downloadFileInfo);*/
			if(fileDownloadListener != null && !isStop)
				fileDownloadListener.onDownloadFinish(downloadFileInfo);
			setCurDownloadFile(null);
		} 
		catch (Exception e)
		{
			downloadFileInfo.deleteTempFile();
			if(fileDownloadListener != null && !isStop)
				fileDownloadListener.onDownloadError(downloadFileInfo, e.getMessage());
			setCurDownloadFile(null);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally 
		{
            try 
            {
                /*if(out != null){
                	out.close();
                }*/
                if(bis != null) 
                {
                	bis.close();
                }
                if(access != null) 
                {
                	access.close();
                }
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }

        }
		if(conn != null)
			conn.disconnect();  
    }
    
    private void checkAndReleaseSpace(long releaseSize)
    {
    	long localRemainSize = SystemUtils.getAvailableExternalMemorySize() / 1024;
		//long total = SystemUtils.getTotalExternalMemorySize();
    	//long destSize = localRemainSize + releaseSize / 1024;
    	//if(destSize <= 1024 * 600)//本地要保留的小于600M
		if(localRemainSize <= 1024 * 600)// || localRemainSize <= releaseSize / 1024)
		{
    		releaseSize = (1024 * 600 - localRemainSize) * 1024 + releaseSize;
			DBManager.newInstance(activity).deleteLastSongRecordBySize(releaseSize);//释放当前所需的空间
		}
    }
    
    private void saveFileToDb(DownloadFileInfo downloadFileInfo)
    {
    	DBManager.newInstance(activity).insertSongRecord(downloadFileInfo.getSongInforNew(), 0);
    }
    
    private HttpURLConnection getHttpConnecttioin(DownloadFileInfo downloadFileInfo)
    {
    	HttpURLConnection conn = null;
		try {
			String encodedUrl = UrlUtil.getASCIIEncodedUrl(downloadFileInfo.getMediaUrl(), "UTF-8");
			URL url = new URL(encodedUrl);
			
			if (downloadFileInfo.getMediaUrl().toLowerCase().startsWith("https")) 
	        {
	            // https
	            HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
	            initTrustSSL(httpsConn, downloadFileInfo);
	            conn = httpsConn;
	        } 
	        else 
	        {
	            conn = (HttpURLConnection) url.openConnection();
	        }
			//conn.setRequestProperty("Charset","UTF-8");
			 /** 
	         * Range头域可以请求实体的一个或者多个子范围。 
	         *      例如: 表示头500个字节：bytes=0-499 　　 
	         *              表示第二个500字节：bytes=500-999 　　 
	         *              表示最后500个字节：bytes=-500 　　 
	         *              表示500字节以后的范围：bytes=500- 　　 
	         *              第一个和最后一个字节：bytes=0-0,-1 　　 
	         *              同时指定几个范围：bytes=500-600,601-999 　　 
	         * 但是服务器可以忽略此请求头，如果无条件GET包含Range请求头，响应会以状态码206（PartialContent）返回而不是以200 （OK）。    
	         */  
			conn.setRequestMethod("GET");  
			long mLoadedByteLength = downloadFileInfo.getDoneSize();
			if(mLoadedByteLength > 0)
				conn.setRequestProperty("range", "bytes=" + mLoadedByteLength + "-");
			else
				conn.setRequestProperty("contentType", "UTF-8");  
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);// 设置getInputStream超时时间
			
			conn.setUseCaches(false);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(fileDownloadListener != null && !isStop)
				fileDownloadListener.onDownloadError(downloadFileInfo, e.getMessage());
			setCurDownloadFile(null);
			e.printStackTrace();
		}
		
		return conn;
    }
    
    /**
     * initTrustSSL
     */
    private void initTrustSSL(HttpsURLConnection conn, DownloadFileInfo downloadFileInfo) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                // do nothing, let the check pass
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());
            // config https
            conn.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            conn.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    // always true, let the check pass
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if(fileDownloadListener != null && !isStop)
				fileDownloadListener.onDownloadError(downloadFileInfo, e.getMessage());
            setCurDownloadFile(null);
        }
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
    
    public interface FileDownloadListener
    {
    	
    	public void onDownloadStart(DownloadFileInfo info);
    	
    	public void onFileExist(DownloadFileInfo info);
    	
    	public void onDownloadProgress(int progress, int size);
    	
    	public void onDownloadError(DownloadFileInfo info, String error);
    	
    	public void onDownloadFinish(DownloadFileInfo info);
    	
    	public void onDownloadExit(DownloadFileInfo info);
    }
}
