package com.znt.speaker.util;

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
import com.znt.speaker.entity.DownloadFileInfo;


public class DownHelper_old
{
	private Activity activity = null;
	private boolean isStop = false;
	
	private final int IO_BUFFER_SIZE = 8 * 1024;
	
	private FileDownloadListener fileDownloadListener = null;
	
    public DownHelper_old()
    {
        
    }
    public DownHelper_old(Activity activity)
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
    private final int RETRY_MAX = 5;
    private int retryCount = 0;
    public void downloadFile(final DownloadFileInfo downloadFileInfo)
    {
    	isStop = false;
    	if(downloadFileInfo == null)
    		return;
    	if (TextUtils.isEmpty(downloadFileInfo.getMediaUrl()))
    	{
    		return;
    	}
    	if (TextUtils.isEmpty(downloadFileInfo.getLocalFileNamePath()))
    	{
    		return;
    	}
    	
    	InputStream in = null;
		FileOutputStream out = null;
		// TODO Auto-generated method stub
		if(fileDownloadListener != null && !isStop)
			fileDownloadListener.onDownloadStart(downloadFileInfo);
		HttpURLConnection conn = null;
		try
		{
			conn = getHttpConnecttioin(downloadFileInfo);
			if (conn!= null && conn.getResponseCode() == 200)
			{
				in = conn.getInputStream();
			}
			else
			{
				if(fileDownloadListener != null && !isStop)
					fileDownloadListener.onDownloadError(downloadFileInfo, "下载失败:" + conn.getResponseCode());
				return;
			}
			/*boolean isConnectSuccess = false;
			retryCount = 0;
			while(retryCount < RETRY_MAX)
			{
				if(isStop)
					break;
				retryCount ++;
				if (conn!= null && conn.getResponseCode() == 200)
				{
					in = conn.getInputStream();
					isConnectSuccess = true;
					break;
				}
				else
					conn = getHttpConnecttioin(downloadFileInfo);
			}
			if(!isConnectSuccess)
			{
				if(fileDownloadListener != null && !isStop)
					fileDownloadListener.onDownloadError(downloadFileInfo, "下载失败:" + conn.getResponseCode());
				return;
			}*/
			
			if(in == null)
				return;
			byte[] data = new byte[IO_BUFFER_SIZE];
			
			downloadFileInfo.createTempFile();//创建临时文件
			
			out = new FileOutputStream(downloadFileInfo.getTempFile());
			int read = 0;
			int contentLength = conn.getContentLength(); 
			/*int progress = 0;*/
			while(true)
			{
				if(isStop)
				{
					if(fileDownloadListener != null)
						fileDownloadListener.onDownloadExit(downloadFileInfo);
					break;
				}
				read = in.read(data);
				if (read == -1)
				{
					break;
				}
				out.write(data, 0, read);
			}
			out.flush();
			long localLen = downloadFileInfo.getTempFileLen();
			if(contentLength == localLen)
			{
				//下载成功，文件重命名
				boolean result = downloadFileInfo.renameDownloadFile();
				if(result)
					saveFileToDb(downloadFileInfo);
			}
			else
			{
				downloadFileInfo.deleteTempFile();
				if(fileDownloadListener != null)
					fileDownloadListener.onDownloadError(downloadFileInfo, "文件未下载完成   contentLength-->" + contentLength + "  localLen-->"+localLen);
				return;
			}
			//下载成功，文件重命名
			/*boolean result = downloadFileInfo.renameDownloadFile();
			if(result)
				saveFileToDb(downloadFileInfo);*/
			if(fileDownloadListener != null && !isStop)
				fileDownloadListener.onDownloadFinish(downloadFileInfo);
		} 
		catch (Exception e)
		{
			/*try
			{
				in.close();
				out.close();
				if(conn != null)
					conn.disconnect();
			} catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			downloadFileInfo.deleteTempFile();
			if(fileDownloadListener != null && !isStop)
				fileDownloadListener.onDownloadError(downloadFileInfo, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            try {
                if(out!=null){
                	out.close();
                }
                if(in!=null) {
                	in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

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
			conn.setRequestProperty("contentType", "UTF-8");  
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(15000);// 设置getInputStream超时时间
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(fileDownloadListener != null && !isStop)
				fileDownloadListener.onDownloadError(downloadFileInfo, e.getMessage());
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
