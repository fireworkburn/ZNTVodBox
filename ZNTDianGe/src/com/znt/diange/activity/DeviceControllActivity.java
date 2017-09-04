
package com.znt.diange.activity; 

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.dmc.engine.OnConnectHandler;
import com.znt.diange.mina.client.ClientHandler;
import com.znt.diange.mina.client.MinaClient;
import com.znt.diange.mina.cmd.CmdType;
import com.znt.diange.mina.cmd.GetSongInforCmd;
import com.znt.diange.mina.cmd.PlayNextCmd;
import com.znt.diange.mina.cmd.PlayStateCmd;
import com.znt.diange.mina.cmd.UpdateCmd;
import com.znt.diange.mina.cmd.VolumeGetCmd;
import com.znt.diange.mina.cmd.VolumeSetCmd;
import com.znt.diange.mina.entity.PlayState;

/** 
 * @ClassName: DeviceControllActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-9-24 上午11:55:45  
 */
public class DeviceControllActivity extends BaseActivity
{

	private SeekBar seekBar = null;
	private TextView tvStateHint = null;
	private TextView tvState = null;
	private TextView tvMusicName = null;
	private TextView tvNext = null;
	
	private int deviceCurVolume = -1;
	private int deviceMaxVolume = -1;
	private int playState = -2;
	private boolean isGetVolumeRunning = false;
	private boolean isSetVolumeRunning = false;
	private boolean isGetStateRunning = false;
	private boolean isSetStateRunning = false;
	private boolean isSetNextRunning = false;
	
	
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == ClientHandler.RECV_VOLUME_SET_RESULT)
			{
				VolumeSetCmd volumeSetCmd = (VolumeSetCmd)msg.obj;
				if(volumeSetCmd.getCmdType().equals(CmdType.SET_DEVICE_VOLUM_FB))
				{
					if(volumeSetCmd.getResult().equals("0"))
					{
						//音量设置成功
						
					}
				}
				
				isSetVolumeRunning = false;
			}
			else if(msg.what == ClientHandler.RECV_VOLUME_GET_RESULT)
			{
				VolumeGetCmd volumeGetCmd = (VolumeGetCmd)msg.obj;
				if(volumeGetCmd.getResult().equals("0"))
				{
					//音量获取成功
					deviceMaxVolume = Integer.parseInt(volumeGetCmd.getMaxVolume());
					deviceCurVolume = Integer.parseInt(volumeGetCmd.getVolume());
					seekBar.setMax(deviceMaxVolume);
					seekBar.setProgress(deviceCurVolume);
				}
				startGetPlayState();
				isGetVolumeRunning = false;
			}
			else if(msg.what == ClientHandler.RECV_GET_PLAY_STATE)
			{
				PlayStateCmd playStateCmd = (PlayStateCmd)msg.obj;
				updatePlatStateView(playStateCmd.getPlayState());
				getCurPlaySong();
				isGetStateRunning = false;
			}
			else if(msg.what == ClientHandler.RECV_SET_PLAY_STATE)
			{
				PlayStateCmd playStateCmd = (PlayStateCmd)msg.obj;
				updatePlatStateView(playStateCmd.getPlayState());
				
				isSetStateRunning = false;
			}
			else if(msg.what == ClientHandler.RECV_PLAY_NEXT_FB)
			{
				PlayNextCmd playNextCmd = (PlayNextCmd)msg.obj;
				String result = playNextCmd.getResult();
				if(result.equals("0"))
					showToast("切换成功~");
				else if(result.equals("1"))
					showToast("切换失败~");
				else if(result.equals("2"))
					showToast("当前队列中没有歌曲了~");
				isSetNextRunning = false;
			}
			else if(msg.what == ClientHandler.RECV_PLAY_MUSIC_INFOR)
			{
				if(msg.obj != null)
				{
					GetSongInforCmd cmdInfor = (GetSongInforCmd)msg.obj;
					if(cmdInfor != null && cmdInfor.getSongInfor() != null)
						tvMusicName.setText(cmdInfor.getSongInfor().getMediaName());
				}
				else
				{
					tvMusicName.setText("空闲状态");
				}
			}
			else if(msg.what == ClientHandler.RECV_UPDATE_INFOR)
			{
				if(msg.obj != null)
				{
					UpdateCmd cmdInfor = (UpdateCmd)msg.obj;
					int type = Integer.parseInt(cmdInfor.getUpdateType());
					if(type == 0)
					{
						//更新播放列表
					}
					else if(type == 1)
					{
						//更新当前播放歌曲
						getCurPlaySong();
					}
					else if(type == 2)
					{
						//列表和歌曲都更新
						getCurPlaySong();
					}
				}
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				startGetVolume();
			}
			else if(msg.what == ClientHandler.TIME_OUT)
			{
				isGetVolumeRunning = false;
				isSetVolumeRunning = false;
				isGetStateRunning = false;
				isSetStateRunning = false;
				isSetNextRunning = false;
				showToast("请求超时");
			}
			else if(msg.what == ClientHandler.RECV_ERROR)
			{
				dismissDialog();
				String error = (String)msg.obj;
				if(error != null)
					showToast(error);
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
		
		setContentView(R.layout.activity_device_controll);
		
		setCenterString("音响控制");
		
		seekBar = (SeekBar)findViewById(R.id.sb_device_controll_voice);
		tvState = (TextView)findViewById(R.id.tv_device_controll_state);
		tvStateHint = (TextView)findViewById(R.id.tv_device_controll_state_hint);
		tvMusicName = (TextView)findViewById(R.id.tv_device_controll_next_cur_music);
		tvNext = (TextView)findViewById(R.id.tv_device_controll_next_do);
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			@Override
			public void onStopTrackingTouch(SeekBar arg0)
			{
				// TODO Auto-generated method stub
				setVolume(arg0.getProgress());
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2)
			{
				// TODO Auto-generated method stub
				//setVolume(arg1);
			}
		});
		
		tvState.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				if(playState != -2)
				{
					if(playState == PlayState.MPS_PLAYING)//处于播放状态, 设置停止
					{
						sendPlayState(PlayState.MPS_PAUSE);
					}
					else //设置播放
					{
						sendPlayState(PlayState.MPS_PLAYING);
					}
				}
				else
					showToast("未获取到播放状态");
			}
		});
		tvNext.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				if(playState == PlayState.MPS_PLAYING)
					sendPlayNext();
				else
					showToast("音响处于停止状态，不能切换");
				
			}
		});
		
		startGetVolume();
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
	
	private void getCurPlaySong()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				final boolean resut = MinaClient.getInstance().sendGetPlayMusic(getActivity());
			}
		}).start();
	}
	
	private void updatePlatStateView(String state)
	{
		playState = Integer.parseInt(state);
		
		if(playState == PlayState.MPS_PLAYING)
		{
			tvStateHint.setText("播放状态：正在播放");
			tvState.setText("停止");
		}
		else
		{
			tvStateHint.setText("播放状态：停止");
			tvState.setText("播放");
		}
	}
	
	private void sendPlayState(final int state)
	{
		if(isSetStateRunning)
			return;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				boolean result = MinaClient.getInstance().sendPlayStateSet(getActivity(), state);
				//boolean result = MinaClient.getInstance().sendGetSpeakerMusic(getActivity(), 0, 100);
				if(result)
					isSetStateRunning = false;
			}
		}).start();
	}
	private void startGetPlayState()
	{
		if(isGetStateRunning)
			return;
		if(MinaClient.getInstance().isConnected())
			isGetStateRunning = true;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				MinaClient.getInstance().sendPlayStateGet(getActivity());
			}
		}).start();
	}
	private void startGetVolume()
	{
		if(isGetVolumeRunning)
			return;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				boolean result = MinaClient.getInstance().sendVolumeGet(getActivity());
				if(result)
					isGetVolumeRunning = false;
			}
		}).start();
	}
	
	private void setVolume(final int cur)
	{
		
		/*if(isRunning)
			return;
		isRunning = true;*/
		
		if(deviceMaxVolume < 0 || deviceCurVolume < 0 )
			return;
		//final float setVolume = (float)cur / deviceMaxVolume;
		/*deviceCurVolume = (Math.round((float)deviceMaxVolume / curMaxVolume)) * cur;
		if(deviceCurVolume < 0)
			deviceCurVolume = 0;
		if(deviceCurVolume > deviceMaxVolume)
			deviceCurVolume = deviceMaxVolume;*/
		if(isSetVolumeRunning)
			return ;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				boolean result = MinaClient.getInstance().sendVolumeSet(getActivity(), cur + "");
				if(result)
					isSetVolumeRunning = false;
			}
		}).start();
	}
	
	private void sendPlayNext()
	{
		if(isSetNextRunning)
			return;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				boolean result = MinaClient.getInstance().sendPlayNext(getActivity());
				if(result)
					isSetNextRunning = false;
			}
		}).start();
	}
	
}
 
