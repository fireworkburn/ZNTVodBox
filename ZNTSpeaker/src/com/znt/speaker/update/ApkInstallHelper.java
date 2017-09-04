
package com.znt.speaker.update; 

import java.io.File;
import java.io.PrintWriter;

import com.znt.speaker.util.LogFactory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/** 
 * @ClassName: ApkInstallHelper 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-11-5 上午10:10:51  
 */
public class ApkInstallHelper
{ 
	
	private Activity activity = null;
	
	private static ApkInstallHelper INSTANCE = null;
	
	public ApkInstallHelper(Activity activity)
	{
		this.activity = activity;
	}
	
	public static ApkInstallHelper getInstance(Activity activity)
	{
		if(INSTANCE == null)
			INSTANCE = new ApkInstallHelper(activity);
		return INSTANCE;
	}
	
    public boolean install(String apkPath)
    {
        // 先判断手机是否有root权限
        if(hasRootPerssion())
        {
        	LogFactory.createLog().e("*******有root权限，利用静默安装实现*********");
            // 有root权限，利用静默安装实现
            return clientInstall(apkPath);
        }
        else
        {
            // 没有root权限，利用意图进行安装
            File file = new File(apkPath);
            if(!file.exists())
                return false; 
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
            activity.startActivity(intent);
            return true;
        }
    }
    
    public boolean uninstall(String packageName)
    {
        if(hasRootPerssion())
        {
            // 有root权限，利用静默卸载实现
            return clientUninstall(packageName);
        }
        else
        {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,packageURI);
            uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(uninstallIntent);
            return true;
        }
    }
     
    /**
     * 判断手机是否有root权限
     */
    private boolean hasRootPerssion()
    {
        PrintWriter PrintWriter = null;
        Process process = null;
        try 
        {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            return returnResult(value);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return false;
    }
     
    /**
     * 静默安装
     */
    private boolean clientInstall(String apkPath)
    {
        PrintWriter PrintWriter = null;
        Process process = null;
        try 
        {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("chmod 777 "+apkPath);
            PrintWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
            PrintWriter.println("pm install -r "+apkPath);
//          PrintWriter.println("exit");
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            LogFactory.createLog().e("*******静默安装成功*********");
            return returnResult(value);
        } 
        catch (Exception e) 
        {
        	LogFactory.createLog().e("*******静默安装失败--"+e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return false;
    }
     
    /**
     * 静默卸载
     */
    private boolean clientUninstall(String packageName)
    {
        PrintWriter PrintWriter = null;
        Process process = null;
        try 
        {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.println("LD_LIBRARY_PATH=/vendor/lib:/system/lib ");
            PrintWriter.println("pm uninstall "+packageName);
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();  
            return returnResult(value); 
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return false;
    }
     
    /**
     * 启动app
     * com.exmaple.client/.MainActivity
     * com.exmaple.client/com.exmaple.client.MainActivity
     */
    public boolean startApp(String packageName,String activityName)
    {
        boolean isSuccess = false;
        String cmd = "am start -n " + packageName + "/" + activityName + " \n";
        Process process = null;
        try 
        {
           process = Runtime.getRuntime().exec(cmd);
           int value = process.waitFor();  
           return returnResult(value);
        } 
        catch (Exception e) 
        {
          e.printStackTrace();
        } 
        finally
        {
            if(process!=null)
            {
                process.destroy();
            }
        }
        return isSuccess;
    }
     
     
    private boolean returnResult(int value)
    {
        // 代表成功  
        if (value == 0) 
        {
            return true;
        } 
        else if (value == 1) 
        { 
        	// 失败
            return false;
        } 
        else 
        { 
        	// 未知情况
            return false;
        }  
    }
}