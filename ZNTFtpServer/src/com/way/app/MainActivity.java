package com.way.app;

import java.io.File;
import java.net.InetAddress;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.way.ftp.Defaults;
import com.way.ftp.FtpServerService;
import com.way.slidingmenu.BaseSlidingFragmentActivity;
import com.way.slidingmenu.SlidingMenu;
import com.way.switchbtn.SwitchButton;
import com.znt.ftp.R;

public class MainActivity extends BaseSlidingFragmentActivity implements
		OnClickListener, OnCheckedChangeListener {
	private static final int HIGHTSET_REQUEST_CODE = 9;
	private static final int PATHSELECT_REQUEST_CODE = 10;

	private SlidingMenu mSlidingMenu;
	private TextView mIpText;
	private TextView mInstructionText;
	private TextView mInstructionTextPre;

	private TextView mChooseDirItem;
	private TextView mPortItem;
	private SwitchButton mNeedPasswdSwitch;
	private TextView mUserNameItem;
	private TextView mPassWdItem;
	private View mSeePasswdItem;
	private SwitchButton mSeePasswdSwitch;
	private SwitchButton mStayeAwakeSwitch;
	private TextView mFeedBackItem;
	private TextView mHelpItem;
	private TextView mAboutItem;

	private View mStartStopButton;
	private Toast mToast;
	private long mFirstPressTime;

	BroadcastReceiver mWifiChangeReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			updateUi();
			startStopServer();
			Log.e("", "*************mWifiChangeReceiver");
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSlidingMenu();
		setContentView(R.layout.above_slidingmenu);
		initViews();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateUi();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction("android.net.wifi.STATE_CHANGE");
		filter.addAction(FtpServerService.ACTION_STARTED);
		filter.addAction(FtpServerService.ACTION_STOPPED);
		registerReceiver(mWifiChangeReceiver, filter);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startStopServer();	
			}
		}, 3000);
		//
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mWifiChangeReceiver);
	}

	private void initSlidingMenu() {
		setBehindContentView(R.layout.behind_slidingmenu);
		// customize the SlidingMenu
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		mSlidingMenu.setShadowWidth(20);
		mSlidingMenu.setBehindScrollScale(0.333f);
	}

	private void initViews() {
		mIpText = (TextView) findViewById(R.id.ip_address);
		mInstructionText = (TextView) findViewById(R.id.instruction);
		mInstructionTextPre = (TextView) findViewById(R.id.instruction_pre);
		mStartStopButton = findViewById(R.id.start_stop_button);
		mStartStopButton.setOnClickListener(this);
		// quickly turn on or off wifi.
		findViewById(R.id.wifi_state_image).setOnClickListener(this);
		findViewById(R.id.ivTitleBtnLeft).setOnClickListener(this);

		mChooseDirItem = (TextView) findViewById(R.id.choose_dir_item);
		mPortItem = (TextView) findViewById(R.id.port_item);
		mNeedPasswdSwitch = (SwitchButton) findViewById(R.id.is_need_password_switch);
		mUserNameItem = (TextView) findViewById(R.id.user_name_item);
		mPassWdItem = (TextView) findViewById(R.id.password_item);
		mSeePasswdItem = findViewById(R.id.password_visiable_item);
		mSeePasswdSwitch = (SwitchButton) findViewById(R.id.password_visiable_switch);
		mStayeAwakeSwitch = (SwitchButton) findViewById(R.id.stay_awake_switch);
		mFeedBackItem = (TextView) findViewById(R.id.feedback_item);
		mHelpItem = (TextView) findViewById(R.id.help_item);
		mAboutItem = (TextView) findViewById(R.id.about_item);

		mChooseDirItem.setOnClickListener(this);
		mPortItem.setOnClickListener(this);
		mNeedPasswdSwitch.setOnCheckedChangeListener(this);
		mUserNameItem.setOnClickListener(this);
		mPassWdItem.setOnClickListener(this);
		mSeePasswdSwitch.setOnCheckedChangeListener(this);
		mStayeAwakeSwitch.setOnCheckedChangeListener(this);
		mFeedBackItem.setOnClickListener(this);
		mHelpItem.setOnClickListener(this);
		mAboutItem.setOnClickListener(this);
	}

	private void updateUi() {
		WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// int wifiState = wifiMgr.getWifiState();
		WifiInfo info = wifiMgr.getConnectionInfo();
		String wifiId = info != null ? info.getSSID() : null;
		boolean isWifiReady = FtpServerService.isConnectedUsingWifi();

		setText(R.id.wifi_state, isWifiReady ? wifiId
				: getString(R.string.no_wifi_hint));
		ImageView wifiImg = (ImageView) findViewById(R.id.wifi_state_image);
		wifiImg.setImageResource(isWifiReady ? R.drawable.wifi_state4
				: R.drawable.wifi_state0);

		boolean running = FtpServerService.isRunning();
		if (running) {
			// Put correct text in start/stop button
			// Fill in wifi status and address
			InetAddress address = FtpServerService.getLocalInetAddress();
			if (address != null) {
				String port = ":" + FtpServerService.getPort();
				mIpText.setText("ftp://" + address.getHostAddress()
						+ (FtpServerService.getPort() == 21 ? "" : port));

			} else {
				// could not get IP address, stop the service
				Context context = getApplicationContext();
				Intent intent = new Intent(context, FtpServerService.class);
				context.stopService(intent);
				mIpText.setText("");
				mIpText.setVisibility(View.GONE);
			}
		}

		mStartStopButton.setEnabled(isWifiReady);
		TextView startStopButtonText = (TextView) findViewById(R.id.start_stop_button_text);
		if (isWifiReady) {
			startStopButtonText.setText(running ? R.string.stop_server
					: R.string.start_server);
			startStopButtonText.setCompoundDrawablesWithIntrinsicBounds(
					running ? R.drawable.disconnect : R.drawable.connect, 0, 0,
					0);
			startStopButtonText.setTextColor(running ? getResources().getColor(
					R.color.remote_disconnect_text) : getResources().getColor(
					R.color.remote_connect_text));
		} else {
			if (FtpServerService.isRunning()) {
				Context context = getApplicationContext();
				Intent intent = new Intent(context, FtpServerService.class);
				context.stopService(intent);
			}

			startStopButtonText.setText(R.string.no_wifi);
			startStopButtonText.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					0, 0);
			startStopButtonText.setTextColor(Color.GRAY);
		}

		mIpText.setVisibility(running ? View.VISIBLE : View.GONE);
		mInstructionText.setVisibility(running ? View.VISIBLE : View.GONE);
		mInstructionTextPre.setVisibility(running ? View.GONE : View.VISIBLE);

		mChooseDirItem.setText("当前目录："
				+ PreferenceUtils.getPrefString(this,
						FtpServerService.CHOOSE_DIR_KEY, Defaults.chrootDir));
		mPortItem.setText("当前端口号："
				+ PreferenceUtils.getPrefInt(this, FtpServerService.PORT_KEY,
						Defaults.portNumber));
		boolean isNeedPd = PreferenceUtils.getPrefBoolean(this,
				FtpServerService.IS_NEED_PASSWORD_KEY, true);
		mNeedPasswdSwitch.setChecked(isNeedPd);
		if (isNeedPd) {
			mUserNameItem.setVisibility(View.GONE);
			mPassWdItem.setVisibility(View.GONE);
			mSeePasswdItem.setVisibility(View.GONE);
		} else {
			mUserNameItem.setVisibility(View.VISIBLE);
			mPassWdItem.setVisibility(View.VISIBLE);
			mSeePasswdItem.setVisibility(View.VISIBLE);
		}

		boolean showPassword = PreferenceUtils.getPrefBoolean(this,
				FtpServerService.PASSWORD_CAN_SEE, true);
		mUserNameItem.setText("用户名:"
				+ PreferenceUtils.getPrefString(this,
						FtpServerService.USER_NAME_KEY, Defaults.username));
		mPassWdItem.setText("密码:"
				+ transformPassword(PreferenceUtils.getPrefString(this,
						FtpServerService.PASSWORD_KEY, Defaults.password),
						showPassword));
		mSeePasswdSwitch.setChecked(showPassword);//
		mStayeAwakeSwitch.setChecked(PreferenceUtils.getPrefBoolean(this,
				FtpServerService.STAY_AWAKE_KEY, Defaults.stayAwake));

	}

	private void setText(int id, String text) {
		TextView tv = (TextView) findViewById(id);
		tv.setText(text);
	}

	private void showToast(int message) {
		if (null == mToast) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(message);
		}
		mToast.show();
	}

	private String transformPassword(String password, boolean showPassword) {
		if (showPassword == true)
			return password;
		else {
			StringBuilder sb = new StringBuilder(password.length());
			for (int i = 0; i < password.length(); ++i)
				sb.append('*');
			return sb.toString();
		}
	}

	/**
	 * 连续按两次返回键就退出
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - mFirstPressTime < 3000) {
				finish();
			} else {
				mFirstPressTime = System.currentTimeMillis();
				Toast.makeText(this,
						getResources().getString(R.string.press_again_exit),
						Toast.LENGTH_SHORT).show();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {

			if (mSlidingMenu.isMenuShowing()) {
				mSlidingMenu.showContent(true);
			} else {
				mSlidingMenu.showMenu(true);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void startStopServer()
	{
		String dir = PreferenceUtils.getPrefString(this,
				FtpServerService.CHOOSE_DIR_KEY, Defaults.chrootDir);
		File chrootDir = new File(dir);
		if (!chrootDir.isDirectory()) 
		{
			return;
		}

		Defaults.chrootDir = dir;

		Intent intent = new Intent(this, FtpServerService.class);

		if (!FtpServerService.isRunning()) 
		{
			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) 
			{
				startService(intent);
			} 
			else 
			{
				showToast(R.string.storage_warning);
			}
		}
		/*else 
		{
			stopService(intent);
		}*/
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivTitleBtnLeft:
			mSlidingMenu.showMenu(true);
			break;
		case R.id.start_stop_button:
			startStopServer();
			break;
		case R.id.wifi_state_image:
			Intent intentWifi = new Intent(
					android.provider.Settings.ACTION_WIFI_SETTINGS);
			startActivity(intentWifi);
			break;
		case R.id.choose_dir_item:
			Intent intentChoseDir = new Intent(MainActivity.this,
					PathSelect.class);
			startActivityForResult(intentChoseDir, PATHSELECT_REQUEST_CODE);
			break;
		case R.id.port_item:
		case R.id.user_name_item:
		case R.id.password_item:
			Intent highSetIntent = new Intent(MainActivity.this,
					UserPwdActivity.class);
			startActivityForResult(highSetIntent, HIGHTSET_REQUEST_CODE);
			break;
		case R.id.feedback_item:
			startActivity(new Intent(this, FeedBackActivity.class));
			break;
		case R.id.help_item:
			startActivity(new Intent(this, HelpActivity.class));
			break;
		case R.id.about_item:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.is_need_password_switch:
			PreferenceUtils.setPrefBoolean(this,
					FtpServerService.IS_NEED_PASSWORD_KEY, isChecked);
			if (isChecked) {
				mUserNameItem.setVisibility(View.GONE);
				mPassWdItem.setVisibility(View.GONE);
				mSeePasswdItem.setVisibility(View.GONE);
			} else {
				mUserNameItem.setVisibility(View.VISIBLE);
				mPassWdItem.setVisibility(View.VISIBLE);
				mSeePasswdItem.setVisibility(View.VISIBLE);
			}
			if (FtpServerService.isRunning()) {
				stopService(new Intent(this, FtpServerService.class));
				startService(new Intent(this, FtpServerService.class));
			}
			break;
		case R.id.password_visiable_switch:
			PreferenceUtils.setPrefBoolean(this,
					FtpServerService.PASSWORD_CAN_SEE, isChecked);
			mPassWdItem.setText("密码:"
					+ transformPassword(PreferenceUtils.getPrefString(this,
							FtpServerService.PASSWORD_KEY, Defaults.password),
							isChecked));
			break;
		case R.id.stay_awake_switch:
			PreferenceUtils.setPrefBoolean(this,
					FtpServerService.STAY_AWAKE_KEY, isChecked);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PATHSELECT_REQUEST_CODE:
			if (resultCode == 2) {
				updateUi();
				if (FtpServerService.isRunning()) {
					stopService(new Intent(this, FtpServerService.class));
					startService(new Intent(this, FtpServerService.class));
				}
			}
			break;
		case HIGHTSET_REQUEST_CODE:
			updateUi();
			if (FtpServerService.isRunning()) {
				stopService(new Intent(this, FtpServerService.class));
				startService(new Intent(this, FtpServerService.class));
			}
		default:
			break;
		}
	}
}
