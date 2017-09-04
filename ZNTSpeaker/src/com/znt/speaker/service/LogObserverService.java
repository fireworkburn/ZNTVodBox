package com.znt.speaker.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class LogObserverService extends Service implements Runnable{  
    private String TAG = "LogObserverService";  
    private boolean isObserverLog = false;  
    private StringBuffer logContent = null;  
    private Bundle mBundle = null;  
    private Intent mIntent = null;  
    private FileOutputStream out = null; 
    
    private String PATH_LOGCAT;  
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        Log.i(TAG,"onCreate");  
        mIntent = new Intent();  
        mBundle = new Bundle();  
        logContent = new StringBuffer();  
        init();
        startLogObserver();  
    }  
    
    private void init()
    {
    	if (Environment.getExternalStorageState().equals(  
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中  
            PATH_LOGCAT = Environment.getExternalStorageDirectory()  
                    .getAbsolutePath() + File.separator + "ZNTSpeaker/log";  
        } 
        else
        {// 如果SD卡不存在，就保存到本应用的目录下  
            PATH_LOGCAT = getFilesDir().getAbsolutePath()  
                    + File.separator + "ZNTSpeaker/log";  
        }  
        File file = new File(PATH_LOGCAT);  
        if (!file.exists()) {  
            file.mkdirs();  
        }  
    	try {  
            out = new FileOutputStream(new File(PATH_LOGCAT, "ZNTSpeaker-"  
                    + getFileName() + ".log"));  
        } catch (FileNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    }
    
    private String getFileName() {  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
        String date = format.format(new Date(System.currentTimeMillis()));  
        return date;// 2012年10月03日 23:41:31  
    }  
    public String getDateEN() {  
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        String date1 = format1.format(new Date(System.currentTimeMillis()));  
        return date1;// 2012-10-03 23:41:31  
    }  
  
    /** 
     * 开启检测日志 
     */  
    public void startLogObserver() {  
        Log.i(TAG,"startObserverLog");  
        isObserverLog = true;  
        Thread mTherad = new Thread(this);  
        mTherad.start();  
    }  
  
    /** 
     * 关闭检测日志 
     */  
    public void stopLogObserver() {  
        isObserverLog = false;  
    }  
  
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
        stopLogObserver();  
    }  
  
    /** 
     * 发送log内容 
     * @param logContent 
     */  
    private void saveContent(String logContent)
    {  
    	if (out != null) {  
            try 
            {
				out.write((getDateEN() + "  " + logContent + "\n")  
				        .getBytes());
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
            /*finally {  
                if (out != null) {  
                    try {  
                        out.close();  
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }  
                    out = null;  
                }  
  
            }  */
        }    
    }  
      
      
    @Override  
    public void run() {  
        Process pro = null;  
        try {  
            Runtime.getRuntime().exec("logcat -c").waitFor();  
              
            pro = Runtime.getRuntime().exec("logcat");  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
        DataInputStream dis = new DataInputStream(pro.getInputStream());  
        String line = null;  
        while (isObserverLog) {  
            try {  
                while ((line = dis.readLine()) != null) {  
                    String temp = logContent.toString();  
                    logContent.delete(0, logContent.length());  
                    logContent.append(line);  
                    logContent.append("\n");  
                    logContent.append(temp);  
                    //发送log内容  
                    saveContent(logContent.toString());  
                    Thread.yield();  
                }  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}  