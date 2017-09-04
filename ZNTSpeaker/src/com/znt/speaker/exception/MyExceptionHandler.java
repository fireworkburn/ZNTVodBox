
package com.znt.speaker.exception; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.znt.diange.email.EmailSenderManager;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.util.DateUtils;
import com.znt.speaker.util.SystemUtils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;


/** 
 * @ClassName: MyExceptionHandler 
 * @Description: TODO
 * @author yan.yu 
 * @date 2013-10-14 涓嬪崍6:02:31  
 */
public class MyExceptionHandler  implements UncaughtExceptionHandler
{
	
	public static final String TAG = "CrashHandler";
	
	//绯荤粺榛樿鐨刄ncaughtException澶勭悊绫� 
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	//CrashHandler瀹炰緥
	private static MyExceptionHandler INSTANCE = new MyExceptionHandler();
	//绋嬪簭鐨凜ontext瀵硅薄
	private Context mContext;
	//鐢ㄦ潵瀛樺偍璁惧淇℃伅鍜屽紓甯镐俊鎭�
	private Map<String, String> infos = new HashMap<String, String>();

	//鐢ㄤ簬鏍煎紡鍖栨棩鏈�,浣滀负鏃ュ織鏂囦欢鍚嶇殑涓�閮ㄥ垎
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private EmailSenderManager emailManager = null;

	/** 淇濊瘉鍙湁涓�涓狢rashHandler瀹炰緥 */
	private MyExceptionHandler() {
	}

	/** 鑾峰彇CrashHandler瀹炰緥 ,鍗曚緥妯″紡 */
	public static MyExceptionHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 鍒濆鍖�
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		emailManager = new EmailSenderManager();
		//鑾峰彇绯荤粺榛樿鐨刄ncaughtException澶勭悊鍣�
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		//璁剧疆璇rashHandler涓虹▼搴忕殑榛樿澶勭悊鍣�
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 褰揢ncaughtException鍙戠敓鏃朵細杞叆璇ュ嚱鏁版潵澶勭悊
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) 
	{
		if (!handleException(ex) && mDefaultHandler != null)
		{
			//濡傛灉鐢ㄦ埛娌℃湁澶勭悊鍒欒绯荤粺榛樿鐨勫紓甯稿鐞嗗櫒鏉ュ鐞�
			mDefaultHandler.uncaughtException(thread, ex);
		} 
		else 
		{
			try 
			{
				Thread.sleep(3000);
			} 
			catch (InterruptedException e) 
			{
				Log.e(TAG, "error : ", e);
			}
			//閫�鍑虹▼搴�
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 鑷畾涔夐敊璇鐞�,鏀堕泦閿欒淇℃伅 鍙戦�侀敊璇姤鍛婄瓑鎿嶄綔鍧囧湪姝ゅ畬鎴�.
	 * 
	 * @param ex
	 * @return true:濡傛灉澶勭悊浜嗚寮傚父淇℃伅;鍚﹀垯杩斿洖false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		//浣跨敤Toast鏉ユ樉绀哄紓甯镐俊鎭�
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				//myToast.show(mContext.getString(R.string.common_exception_exit), false);
				Looper.loop();
			}
		}.start();
		//鏀堕泦璁惧鍙傛暟淇℃伅 
		collectDeviceInfo(mContext);
		//淇濆瓨鏃ュ織鏂囦欢 
		saveCrashInfo2File(ex);
		return true;
	}
	
	/**
	 * 鏀堕泦璁惧鍙傛暟淇℃伅
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) 
	{
		try
		{
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null)
			{
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
				infos.put("deviceName", android.os.Build.MODEL);
				infos.put("deviceVersion", android.os.Build.VERSION.SDK);
			}
		} catch (NameNotFoundException e) 
		{
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) 
		{
			try 
			{
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} 
			catch (Exception e) 
			{
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 淇濆瓨閿欒淇℃伅鍒版枃浠朵腑
	 * 
	 * @param ex
	 * @return	杩斿洖鏂囦欢鍚嶇О,渚夸簬灏嗘枃浠朵紶閫佸埌鏈嶅姟鍣�
	 */
	private String saveCrashInfo2File(Throwable ex) 
	{
		
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) 
		{
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) 
		{
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		String addr = LocalDataEntity.newInstance(mContext).getDeviceAddr();
		if(!TextUtils.isEmpty(addr))
			sb.append(addr);
		//Log.e("", "******鏈崟鑾风殑寮傚父淇℃伅-->"+sb);
		String devId = LocalDataEntity.newInstance(mContext).getDeviceCode();
		try 
		{
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp + ".log";
			File dir = SystemUtils.getAvailableDir(mContext, Constant.WORK_DIR + "/error/");
			if (!dir.exists()) 
			{
				dir.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath() + File.separator + fileName);
			fos.write(sb.toString().getBytes());
			fos.close();
			emailManager.sendEmail("异常_id_" + devId + "_时间:" + DateUtils.getStringDate(), sb.toString());
			return fileName;
		} 
		catch (Exception e) 
		{
			Log.e(TAG, "an error occured while writing file...", e);
		}

		return null;
	}
}

