package com.znt.diange.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.db.DBManager;
import com.znt.diange.entity.Constant;
import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.service.MediaScanService;
import com.znt.diange.utils.SystemUtils;

public class SplashActivity extends Activity
{

	private TextView tvVersion = null;
	
	private static final int DEVICE_SELECT = 0;
	private static final int DEVICE_SEARCH = 1;
	// 延迟3秒
	private static final long SPLASH_DELAY_MILLIS = 1200;

	/**
	 * Handler:跳转到不同界面
	 */
	private Handler mHandler = new Handler() 
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			case DEVICE_SEARCH:
				//scanDevice();
				login();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_splash);
		
		tvVersion = (TextView)findViewById(R.id.tv_splash_version);
		
		Constant.PHONE_UUID = SystemUtils.getDeviceId(this);
		
		try
		{
			tvVersion.setText("v " + SystemUtils.getPkgInfo(SplashActivity.this).versionName);
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(LocalDataEntity.newInstance(this).isFirstInit())
    	{
			/*File file = SystemUtils.getAvailableDir(SplashActivity.this, Constant.WORK_DIR); 
			FileUtils.deleteFolder(file);*/
			DBManager.newInstance(SplashActivity.this).deleteDbFile();
			
			DBManager.newInstance(SplashActivity.this).openDatabase(SplashActivity.this);
			
			//LocalDataEntity.newInstance(this).setFirstInit(false);
    	}
		mHandler.sendEmptyMessageDelayed(DEVICE_SEARCH, SPLASH_DELAY_MILLIS);
		
		//initService();
		
	}
	
	/*private void initService()
	{
		if(!SystemUtils.getConnectWifiSsid(this).endsWith(Constant.UUID_TAG))
			DnsContainer.getInstance(SplashActivity.this).openDns();
		startDLNAService();
		startDMS();
	}
	
	private void startDLNAService()
	{
		Intent intent = new Intent(this, DLNAService.class);
		startService(intent);
	}*/
	
	private void startDMS()
	{
    	Intent intent = new Intent(this, MediaScanService.class);
		startService(intent);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private void login() 
	{
		Intent intent = new Intent(SplashActivity.this, AccountActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onBackPressed()
	{
		// TODO Auto-generated method stub
		//super.onBackPressed();
	}
}
