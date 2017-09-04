
package com.znt.diange.activity; 

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.dmc.engine.OnConnectHandler;
import com.znt.diange.entity.Constant;
import com.znt.diange.mina.client.MinaClient;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.netset.WifiFactory;
import com.znt.diange.timer.CheckSsidTimer;
import com.znt.diange.utils.SystemUtils;

/** 
 * @ClassName: NetWorkChangeActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-20 上午9:35:16  
 */
public class NetWorkChangeActivity extends BaseActivity implements OnClickListener
{
	private ImageView ivLoading = null;
	private ImageView ivHint = null;
	private TextView tvHint = null;
	private TextView tvReconnect = null;
	
	private WifiFactory mWifiFactory = null;
	private DeviceInfor deviceInfor = null;
	private CheckSsidTimer checkSsidTimer = null;
	
	private String currentSSID = "";
	
	private final int CHECK_SSID = 1;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == CHECK_SSID)
			{
				if(checkSsidTimer.isOver())
				{
					doConnectFail();
				}
				else
				{
					currentSSID = SystemUtils.getConnectWifiSsid(getActivity());
					if(deviceInfor.getWifiName().equals(currentSSID))
					{
						checkSsidTimer.stopTimer();
						doConnectSuccess();
					}
				}
				
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				if(isWifiApConnect())//如果是热点，就连接固定ip
					getLocalData().setWifiInfor(getCurrentSsid(), Constant.WIFI_HOT_PWD);
				Constant.isSongUpdate = true;
				//ViewUtils.sendMessage(handler, START_REGISET);
				finish();
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNETC_FAIL)
			{
				dismissDialog();
				
				boolean isDeviceRemove = (Boolean)msg.obj;
				if(isDeviceRemove)
					showToast("设备断开连接，请重新选择设备");
				else
					showToast("连接服务器失败，请重试");
				doConnectFail();
			}
		};
	};
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_net_work_change);
		
		ivLoading = (ImageView)findViewById(R.id.iv_net_work_change_loading);
		ivHint = (ImageView)findViewById(R.id.iv_net_work_change_hint);
		tvHint = (TextView)findViewById(R.id.tv_net_work_change_loading);
		tvReconnect = (TextView)findViewById(R.id.tv_net_work_change_reconnect);
		
		tvReconnect.setOnClickListener(this);
		
		setCenterString("网络连接");
		
		mWifiFactory = WifiFactory.newInstance(getActivity());
		
		checkSsidTimer = new CheckSsidTimer(getActivity());
		checkSsidTimer.setHandler(handler, CHECK_SSID);
		
		deviceInfor = (DeviceInfor)getIntent().getSerializableExtra("DEVICE_INFOR");
		if(deviceInfor == null)
			deviceInfor = getLocalData().getDeviceInfor();
		
		connectWifi();
	}
	
	private void startCheckSSID()
	{
		checkSsidTimer.setMaxTime(18);
		checkSsidTimer.startTimer();
	}
	
	private void connectWifi()
	{
		if(deviceInfor == null)
			return;
		showConnectingView();
		if(!getCurrentSsid().equals(deviceInfor.getWifiName()))
		{
			mWifiFactory.connectWifi(deviceInfor.getWifiName(), deviceInfor.getWifiPwd());
			startCheckSSID();
		}
		else
			doConnectSuccess();
			
	}
	
	private void showConnectingView()
	{
		ivHint.setImageDrawable(getResources().getDrawable(R.drawable.icon_net_work));
		ivLoading.setVisibility(View.VISIBLE);
		tvReconnect.setVisibility(View.GONE);
		startAnim();
		tvHint.setText("正在切换网络，请稍后...");
	}
	
	private void showConnectFailView()
	{
		ivHint.setImageDrawable(getResources().getDrawable(R.drawable.icon_net_work_error));
		ivLoading.setVisibility(View.GONE);
		tvReconnect.setVisibility(View.VISIBLE);
		stopAnim();
		tvHint.setText("网络切换失败，请重新配置");
	}
	
	private void startAnim()
    {
    	Animation anim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setRepeatCount(Animation.INFINITE); // 设置INFINITE，对应值-1，代表重复次数为无穷次
        anim.setDuration(1000);                  // 设置该动画的持续时间，毫秒单位
        anim.setInterpolator(new LinearInterpolator());	// 设置一个插入器，或叫补间器，用于完成从动画的一个起始到结束中间的补间部分
        ivLoading.startAnimation(anim);
    }
	private void stopAnim()
	{
		ivLoading.clearAnimation();
	}
	
	private void doConnectSuccess()
	{
		if(checkSsidTimer != null)
			checkSsidTimer.stopTimer();
		
		if(deviceInfor.isAvailable())
		{
			connectServer();
		}
		else
		{
			finish();
		}
	}
	
	private void connectServer()
	{
		if(!MinaClient.getInstance().isConnected())
		{
			MinaClient.getInstance().setOnConnectListener(getActivity(), handler);
			MinaClient.getInstance().close();
			if(isWifiApConnect())//如果是热点，就连接固定ip
				getLocalData().setWifiInfor(getCurrentSsid(), Constant.WIFI_HOT_PWD);
			MinaClient.getInstance().startClient();
		}
	}
	
	private void doConnectFail()
	{
		if(checkSsidTimer != null)
			checkSsidTimer.stopTimer();
		showToast("网络连接失败,请重试");
		showConnectFailView();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onPause()
	{
		if(checkSsidTimer != null)
			checkSsidTimer.stopTimer();
		MinaClient.getInstance().stopConnect();
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == tvReconnect)
		{
			connectWifi();
		}
	}
}
 
