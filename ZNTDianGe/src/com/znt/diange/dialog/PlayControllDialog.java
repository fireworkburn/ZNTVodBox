
package com.znt.diange.dialog; 

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.diange.R;
import com.znt.diange.dmc.engine.OnConnectHandler;
import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.mina.client.ClientHandler;
import com.znt.diange.mina.client.MinaClient;
import com.znt.diange.mina.entity.PermissionType;
import com.znt.diange.mina.entity.PlayRes;

/** 
 * @ClassName: PlayListDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-9-19 下午3:49:41  
 */
public class PlayControllDialog extends Dialog
{

	private View parentView = null;
	private Activity activity = null;
	private TextView tvCancel = null;
	private TextView tvTitleOne = null;
	private TextView tvSubOne = null;
	private ImageView ivOne = null;
	private TextView tvTitleTwo = null;
	private TextView tvSubTwo = null;
	private ImageView ivTwo = null;
	private View viewOne = null;
	private View viewTwo = null;
	private ProgressBar progressBar = null;
	
	private String permission = "";
	private String playRes = "";
	private boolean isPermissionSet = true;
	private boolean isPermissionRequring = false;
	private boolean isResSetRequring = false;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == 0)
			{
				
			}
			else if(msg.what == ClientHandler.RECV_PLAY_PERMISSION)
			{
				isPermissionRequring = false;
				Toast.makeText(activity, "设置成功", 0).show();
				showLoadingView(false);
				//updateSelect();
				dismiss();
			}
			else if(msg.what == ClientHandler.RECV_GET_PLAY_PERMISSION)
			{
				showLoadingView(false);
				updateSelect();
			}
			else if(msg.what == ClientHandler.RECV_PALY_RES)
			{
				isResSetRequring = false;
				Toast.makeText(activity, "设置成功", 0).show();
				showLoadingView(false);
				//updateSelect();
				dismiss();
			}
			else if(msg.what == ClientHandler.RECV_PLAY_PERMISSION)
			{
				showLoadingView(false);
				updateSelect();
			}
			else if(msg.what == ClientHandler.RECV_GET_PALY_RES)
			{
				showLoadingView(false);
				updateSelect();
			}
			else if(msg.what == ClientHandler.TIME_OUT)
			{
				isResSetRequring = false;
				isPermissionRequring = false;
				Toast.makeText(activity, "请求超时", 0).show();
				showLoadingView(false);
				//updateSelect();
				dismiss();
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				startGetControll();
			}
			
		};
	};
	
	public PlayControllDialog(Activity activity, boolean isPermissionSet) 
	{
		super(activity, R.style.MMTheme_DataSheet);
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.isPermissionSet = isPermissionSet;
		MinaClient.getInstance().setOnConnectListener(activity, handler);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_res_controll);
		
		parentView = findViewById(R.id.view_paly_res_dialog_bg);
		
		viewOne = findViewById(R.id.view_res_controll_dialog_one);
		viewTwo = findViewById(R.id.view_res_controll_dialog_two);
		tvTitleOne = (TextView)findViewById(R.id.tv_res_controll_dialog_one);
		tvSubOne = (TextView)findViewById(R.id.tv_res_controll_dialog_one_sub);
		tvTitleTwo = (TextView)findViewById(R.id.tv_res_controll_dialog_two);
		tvSubTwo = (TextView)findViewById(R.id.tv_res_controll_dialog_two_sub);
		ivOne = (ImageView)findViewById(R.id.iv_res_controll_dialog_one);
		ivTwo = (ImageView)findViewById(R.id.iv_res_controll_dialog_two);
		tvCancel = (TextView)findViewById(R.id.tv_res_controll_dialog_cancel);
		progressBar = (ProgressBar)findViewById(R.id.pb_res_controll_dialog);
		
		showHintInfor();
		
		startGetControll();
		
		parentView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) 
			{
				// TODO Auto-generated method stub
				dismiss();
				return false;
			}
		});
		tvCancel.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		viewOne.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				if(ivOne.isShown())
				{
					dismiss();
				}
				else
				{
					if(isPermissionSet)
					{
						if(permission.equals(PermissionType.SPEAKER))
							startSetPermission(PermissionType.ALL);
						else
							startSetPermission(PermissionType.SPEAKER);
					}
					else
					{
						startSetPlayRes(PlayRes.LOCAL);
					}
				}
			}
		});
		viewTwo.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				if(ivTwo.isShown())
				{
					dismiss();
				}
				else
				{
					if(isPermissionSet)
					{
						if(permission.equals(PermissionType.SPEAKER))
							startSetPermission(PermissionType.ALL);
						else
							startSetPermission(PermissionType.SPEAKER);
					}
					else
					{
						startSetPlayRes(PlayRes.ONLINE);
					}
				}
			}
		});
		
	}
	
	private void showLoadingView(final boolean isShow)
	{
		activity.runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				if(isShow)
				{
					progressBar.setVisibility(View.VISIBLE);
				}
				else
				{
					progressBar.setVisibility(View.GONE);
				}
			}
		});
	}
	
	private void startGetControll()
	{
		if(isPermissionSet)
		{
			startSendGetPermission();
		}
		else
		{
			startSendGetPlayRes();
		}
	}
	
	private void startSendGetPlayRes()
	{
		showLoadingView(true);
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				boolean result = MinaClient.getInstance().sendGetSpeakerResCmd();
				if(!result)
					showLoadingView(false);
			}
		}).start();
	}
	private void startSendGetPermission()
	{
		showLoadingView(true);
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				boolean result = MinaClient.getInstance().sendGetPermission(activity);
				if(!result)
					showLoadingView(false);
			}
		}).start();
	}
	
	private void showHintInfor()
	{
		if(isPermissionSet)
		{
			tvTitleOne.setText("允许播放三方歌曲");
			tvSubOne.setText("允许播放用户本地以及网络歌曲");
			tvTitleTwo.setText("禁止播放三方歌曲");
			tvSubTwo.setText("用户只能点播音响内置歌曲");
		}
		else
		{
			tvTitleOne.setText("播放本地歌曲");
			tvSubOne.setText("音响默认播放本地存储设备上的歌曲");
			tvTitleTwo.setText("播放在线歌曲");
			tvSubTwo.setText("音响默认播放在线歌曲（编辑的曲库）");
		}
	}
	
	private void updateSelect()
	{
		if(isPermissionSet)
		{
			permission = LocalDataEntity.newInstance(activity).getPlayPermission();
			if(permission.equals(PermissionType.ALL))
			{
				ivOne.setVisibility(View.VISIBLE);
				ivTwo.setVisibility(View.INVISIBLE);
			}
			else if(permission.equals(PermissionType.SPEAKER))
			{
				ivOne.setVisibility(View.INVISIBLE);
				ivTwo.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			playRes = LocalDataEntity.newInstance(activity).getPlayRes();
			if(playRes.equals(PlayRes.LOCAL))
			{
				ivOne.setVisibility(View.VISIBLE);
				ivTwo.setVisibility(View.INVISIBLE);
			}
			else if(playRes.equals(PlayRes.ONLINE))
			{
				ivOne.setVisibility(View.INVISIBLE);
				ivTwo.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void startSetPlayRes(final String playRes)
	{
		if(isResSetRequring)
			return;
		if(MinaClient.getInstance().isConnected())
			isResSetRequring = true;
		showLoadingView(true);
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				boolean result = MinaClient.getInstance().sendSpeakerResCmd(playRes);
				if(!result)
					showLoadingView(false);
			}
		}).start();
	}
	
	private void startSetPermission(final String permission)
	{
		if(isPermissionRequring)
			return;
		if(MinaClient.getInstance().isConnected())
			isPermissionRequring = true;
		showLoadingView(true);
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				boolean result = MinaClient.getInstance().sendSetPermission(activity, permission);
				if(!result)
					showLoadingView(false);
			}
		}).start();
	}

}