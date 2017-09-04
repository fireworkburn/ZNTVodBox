package com.znt.speaker.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.znt.diange.mina.entity.UpdateInfor;
import com.znt.speaker.R;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.DeviceStatusInfor;
import com.znt.speaker.entity.DownloadFileInfo;
import com.znt.speaker.http.HttpHelper;
import com.znt.speaker.http.HttpMsg;
import com.znt.speaker.http.HttpType;
import com.znt.speaker.util.ApkTools;
import com.znt.speaker.util.DownHelper;
import com.znt.speaker.util.DownHelper.FileDownloadListener;
import com.znt.speaker.util.LogFactory;
import com.znt.speaker.util.SystemUtils;
import com.znt.speaker.util.ViewUtils;

public class UpdateManager implements FileDownloadListener
{
	private Activity activity = null;
	
	private UpdateInfor updateInfor = new UpdateInfor();
	
	private DownHelper downHelper = null;
	private HttpHelper httpHelper = null;
	
	private File apkFile = null;
	private String pkgInstallerName = "com.znt.install";
	private String pkgSpeakerName = "com.znt.speaker";
	private boolean isUpdateRunning = false;
	private boolean isUpdateFileLoading = false;
	private int checkUpdateCount = 0;
	
	private final int DOWNLOAD_FILE = 3;
	private final int DOWNLOAD_FILE_SUCCESS = 4;
	private final int DOWNLOAD_FILE_FAIL = 5;
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.CHECK_UPDATE_START)
			{
				//showToast("开始检查系统更新");
			}
			else if(msg.what == HttpMsg.CHECK_UPDATE_SUCCESS)
			{
				updateInfor = (UpdateInfor)msg.obj;
				String versionNum = updateInfor.getVersionNum();
				LogFactory.createLog().e("检查更新成功,当前版本信息：version-->"+updateInfor.getVersionName());
				if(!TextUtils.isEmpty(versionNum))
				{
					if(isVersionNew(Integer.parseInt(versionNum)))
					{
						//是新版本
						doApkInstall(updateInfor.getApkUrl());
						LogFactory.createLog().e("检查更新成功, 有新版本");
						//Toast.makeText(activity, "检测到了新版本，开始自动升级", 0).show();
					}
					else
					{
						setUpdateFinish();
						LogFactory.createLog().e("检查更新成功, 没有更新的版本");
					}
				}
			}
			else if(msg.what == HttpMsg.CHECK_UPDATE_FAIL)
			{
				LogFactory.createLog().e("检查更新失败");
				//showToast("检查更新失败");
				setUpdateFinish();
			}
			else if(msg.what == DOWNLOAD_FILE)
			{
				//showToast("开始下载安装包");
			}
			else if(msg.what == DOWNLOAD_FILE_SUCCESS)
			{
				showToast("升级文件下载成功，开始安装");
				startInstallApk();
			}
			else if(msg.what == DOWNLOAD_FILE_FAIL)
			{
				
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "下载文件失败";
				showToast(error);
				
				setUpdateFinish();
			}
		};
	};
	
	public UpdateManager(Activity activity)
	{
		this.activity = activity;
		
		initData();
	}
	
	public void checkUpdate(DeviceStatusInfor devStatus)
	{
		checkUpdateCount ++;
		if(checkUpdateCount > 3)
		{
			checkUpdateCount = 0;
			doCheckUpdate(devStatus);
		}
	}
	private void doCheckUpdate(DeviceStatusInfor devStatus)
	{
		
		String sysLastVersionNum = devStatus.getLastVersionNum();
		if(!TextUtils.isEmpty(sysLastVersionNum))
		{
			try 
			{
				int lastNum = Integer.parseInt(sysLastVersionNum);
				int curAppVersionNum = SystemUtils.getPkgInfo(activity).versionCode;
				if(lastNum > curAppVersionNum)
				{
					if(isUpdateRunning && isUpdateFileLoading)
						return;
					isUpdateRunning = true;
					
					if(!SystemUtils.isNetConnected(activity))//无网络链接
					{
						return ;
					}
					if(httpHelper != null)
					{
						httpHelper.stop();
						httpHelper = null;
					}
					httpHelper = new HttpHelper(handler, activity);
					
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("softName", "speaker"));
					httpHelper.startHttp(HttpType.CheckUpdate, params);
				}
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void setUpdateFinish()
	{
		isUpdateRunning = false;
	}
	
	private boolean isVersionNew(int updateVersionNum)
	{
		try
		{
			int curAppVersionNum = SystemUtils.getPkgInfo(activity).versionCode;
			LogFactory.createLog().e("升级版本信息-->curAppVersionNum"+ curAppVersionNum + "   updateVersionNum" + updateVersionNum);
			if(updateVersionNum > curAppVersionNum)
				return true;
			
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void initData()
	{
		downHelper = new DownHelper();
		downHelper.setDownLoadListener(this);
		File dir = SystemUtils.getAvailableDir(activity, Constant.WORK_DIR + "/update/");
		if (!dir.exists()) 
		{
			dir.mkdirs();
		}
		apkFile = SystemUtils.getAvailableDir(activity, Constant.WORK_DIR + "/update/ZNTSpeaker.apk");
	}
	
	public void readLuncherFile()
    {
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				PackageManager pm = activity.getPackageManager();
				List<PackageInfo> pakageinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
				int size = pakageinfos.size();
				int versionCode = 0;
				for(int i=0;i<size;i++)
				{
					PackageInfo pakageinfo = pakageinfos.get(i);
					String pkg = pakageinfo.packageName;
					if(pkg.equals("com.znt.luncher"))
					{
						versionCode = pakageinfo.versionCode;
						String versionName = pakageinfo.versionName;
						LogFactory.createLog().e("&&&&&&&&&&&&Luncher的版本 versionName-->"+versionName + "  versionCode-->"+versionCode);
					}
				}
				
				if(versionCode < Constant.LUCNHER_VERSION_CODE)
				{
					try 
			        {
						File dir = SystemUtils.getAvailableDir(activity, Constant.WORK_DIR + "/update/");
			            if (!dir.exists())
			                dir.mkdirs();
			            
			            final File apkFile = new File(dir.getAbsolutePath() + "/zntluncher.apk");
			            
			            if (apkFile.exists()) 
			            	apkFile.delete();
			            
			            InputStream is = activity.getResources().openRawResource(R.raw.zntluncher);
			            FileOutputStream fos = new FileOutputStream(apkFile);
			            byte[] buffer = new byte[8192];
			            int count = 0;
			            // 开始复制dictionary.db文件
			            while ((count = is.read(buffer)) > 0) 
			            {
			                fos.write(buffer, 0, count);
			            }

			            fos.close();
			            is.close();
			            activity.runOnUiThread(new Runnable() 
			            {
							@Override
							public void run() 
							{
								// TODO Auto-generated method stub
								Intent intent = activity.getPackageManager().getLaunchIntentForPackage(pkgInstallerName);
								if(intent != null)
								{
									intent.putExtra("pkg_name", "com.znt.luncher");
									intent.putExtra("apk_path", apkFile.getAbsolutePath());
									activity.startActivity(intent);
								}
							}
						});
			        } 
			        catch (Exception e) 
			        {
			        	
			        } 
				}
			}
		}).start();
    }
	
	private void doApkInstall(String url)
	{
		if(apkFile.exists() && isSignatureMatch())//检测到有新的版本就先删除本地的apk
		{
			int apkFileVersion = SystemUtils.getApkFileInfor(activity, apkFile.getAbsolutePath()).versionCode;
			if(isVersionNew(apkFileVersion))
				startInstallApk();
			else
			{
				apkFile.delete();
				LogFactory.createLog().e("本地文件的版本信息不对, 开始下载apk文件");
				downloadApkFile(url);
			}
		}
		else
			downloadApkFile(url);
	}
	
	private void downloadApkFile(final String downUrl)
	{
		downHelper.downloadFile(downUrl, apkFile.getAbsolutePath());
	}
	
	private void startInstallApk()
	{
		if(isFileValid())
		{
			//showToast("安装包验证正确，开始安装升级包");
			Intent intent = activity.getPackageManager().getLaunchIntentForPackage(pkgInstallerName);
			if(intent != null)
			{
				intent.putExtra("pkg_name", pkgSpeakerName);
				intent.putExtra("apk_path", apkFile.getAbsolutePath());
				activity.startActivity(intent);
				
				System.exit(0);
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}
		setUpdateFinish();
	}
	
	private boolean isFileValid()
	{
		if(!apkFile.exists())
		{
			LogFactory.createLog().e("升级文件不存在,请重新下载~");
			showToast("升级文件不存在,请重新下载~");
			return false;
		}
		if(apkFile.length() == 0)
		{
			LogFactory.createLog().e("升级文件无效,请重新下载~");
			showToast("升级文件无效,请重新下载~");
			apkFile.delete();
			return false;
		}
		
		
		return isSignatureMatch();
	}
	
	private boolean isSignatureMatch()
	{
		String curSign = ApkTools.getSignature(activity);
		List<String> signs = ApkTools.getSignaturesFromApk(apkFile);
		
		if(curSign == null || signs.size() == 0 
				|| signs.get(0) == null || !curSign.equals(signs.get(0)))
		{
			LogFactory.createLog().e("升级文件签名不一致,请重新下载~");
			showToast("升级文件签名不一致,请重新下载~");
			apkFile.delete();
			return false;
		}
		
		return true;
	}
	
	private void showToast(final String infor)
	{
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(activity, infor, 0).show();
			}
		});
	}

	@Override
	public void onDownloadStart(DownloadFileInfo info) 
	{
		// TODO Auto-generated method stub
		LogFactory.createLog().e("开始下载升级文件");
		ViewUtils.sendMessage(handler, DOWNLOAD_FILE);
		isUpdateFileLoading = true;
	}

	@Override
	public void onFileExist(DownloadFileInfo info)
	{
		// TODO Auto-generated method stub
		LogFactory.createLog().e("文件已经存在~");
		showToast("文件已经存在~");
		isUpdateFileLoading = false;
	}

	@Override
	public void onDownloadProgress(int progress, int size)
	{
		// TODO Auto-generated method stub
		LogFactory.createLog().d("正在下载文件-->" + size + " / " + progress);
		isUpdateFileLoading = true;
	}

	@Override
	public void onDownloadError(DownloadFileInfo info,String error)
	{
		// TODO Auto-generated method stub
		ViewUtils.sendMessage(handler, DOWNLOAD_FILE_FAIL, error);
		isUpdateFileLoading = false;
	}

	@Override
	public void onDownloadFinish(DownloadFileInfo info) 
	{
		// TODO Auto-generated method stub
		ViewUtils.sendMessage(handler, DOWNLOAD_FILE_SUCCESS);
		isUpdateFileLoading = false;
	}

	@Override
	public void onDownloadExit(DownloadFileInfo info) 
	{
		// TODO Auto-generated method stub
		isUpdateFileLoading = false;
	}
}