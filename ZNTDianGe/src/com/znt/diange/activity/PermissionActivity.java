
package com.znt.diange.activity; 

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.dmc.engine.OnConnectHandler;
import com.znt.diange.entity.Constant;
import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.mina.client.ClientHandler;
import com.znt.diange.mina.client.MinaClient;
import com.znt.diange.utils.ViewUtils;
import com.znt.diange.view.SwitchButton;

/** 
 * @ClassName: PermissionActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-12-29 上午10:20:09  
 */
public class PermissionActivity extends BaseActivity
{
	
	private TextView tvConfirm = null;
	private SwitchButton sbtnSpeaker = null;

	private String oldPermission = "0";
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == ClientHandler.RECV_PLAY_PERMISSION)
			{
				showToast("设置成功");
				dismissDialog();
			}
			else if(msg.what == ClientHandler.TIME_OUT)
			{
				showToast("请求超时");
				dismissDialog();
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				startSetPermission();
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
		
		setContentView(R.layout.activity_permission);
		
		setCenterString("权限管理");
		
		tvConfirm = (TextView)findViewById(R.id.btn_device_permission_confirm);
		sbtnSpeaker = (SwitchButton)findViewById(R.id.switch_play_speaker_music);
		
		oldPermission = LocalDataEntity.newInstance(getActivity()).getPlayPermission();
		
		sbtnSpeaker.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1)
			{
				// TODO Auto-generated method stub
				updateHintInfor(arg1);
			}
		});
		
		tvConfirm.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				startSetPermission();
			}
		});
		
		updateHintInfor(oldPermission.equals("0"));
		sbtnSpeaker.setChecked(oldPermission.equals("0"));
	}
	
	private void updateHintInfor(boolean isAllow)
	{
		if(isAllow)
		{
			sbtnSpeaker.setText("允许播放第三方歌曲");
			
		}
		else
		{
			sbtnSpeaker.setText("禁止播放第三方歌曲");
		}
	}
	
	private void startSetPermission()
	{
		final String permission = sbtnSpeaker.isChecked() ? "0" : "1";
		if(permission.equals(oldPermission))
		{
			showToast("权限未改变");
			return;
		}
		showProgressDialog(getActivity(), "正在处理...");
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				MinaClient.getInstance().sendSetPermission(getActivity(), permission);
			}
		}).start();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		
		MinaClient.getInstance().setOnConnectListener(getActivity(), handler);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		
		MinaClient.getInstance().setConnectStop();
		
	}
	
}
 
