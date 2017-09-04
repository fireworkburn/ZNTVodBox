package com.znt.speaker.activity;

import java.util.List;

import com.squareup.leakcanary.watcher.RefWatcher;
import com.znt.diange.mina.cmd.CmdType;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.cmd.DeviceSetCmd;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.R;
import com.znt.speaker.center.DMRCenter;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.CurPlanInfor;
import com.znt.speaker.entity.DeviceStatusInfor;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.factory.CurPlanFactory;
import com.znt.speaker.factory.LocationFactory;
import com.znt.speaker.factory.MediaScanFactory;
import com.znt.speaker.factory.UIManager;
import com.znt.speaker.http.HttpRequestID;
import com.znt.speaker.mina.server.MinaServer;
import com.znt.speaker.mina.server.ServerHandler;
import com.znt.speaker.mina.server.ServerHandler.OnMessageReceiveListener;
import com.znt.speaker.p.DevStatusPresenter;
import com.znt.speaker.p.HttpPresenter;
import com.znt.speaker.p.MusicPlayPresenter;
import com.znt.speaker.p.MusicPlayReceiverPresenter;
import com.znt.speaker.p.NetWorkPresenter;
import com.znt.speaker.p.SDCardMountPresenter;
import com.znt.speaker.update.UpdateManager;
import com.znt.speaker.util.FileDownLoadUtil;
import com.znt.speaker.util.MyToast;
import com.znt.speaker.util.SystemUtils;
import com.znt.speaker.v.IDevStatusView;
import com.znt.speaker.v.IHttpRequestView;
import com.znt.speaker.v.IMusicReceiverView;
import com.znt.speaker.v.INetWorkView;
import com.znt.speaker.v.ISDCardMountView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

public class MainActivity extends BaseActivity implements IHttpRequestView, INetWorkView, 
														IDevStatusView, ISDCardMountView,
														IMusicReceiverView
{
	
	private UIManager mUIManager;
	
	private NetWorkPresenter netWorkPresenter  = null;
	private DevStatusPresenter devStatusPresenter = null;
	private SDCardMountPresenter sDCardMountPresenter = null;
	private MusicPlayReceiverPresenter musicPlayReceiverPresenter = null;
	private HttpPresenter httpPresenter = null;
	private MusicPlayPresenter musicPlayPresenter = null;
	
	private MediaScanFactory mediaScanFactory = null;
	private LocationFactory locationFactory = null;
	private UpdateManager updateManager = null;
	private CurPlanFactory curPlanFactory = null;
	
	private DeviceInfor deviceInforRecv = null;
	private boolean isNetWorkInit = true;
	private boolean isCurPlanFinished = false;
	private boolean isInitTerminalFinished = false;
	private boolean isRegisterFinished = false;
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		setContentView(R.layout.music_player_layout);
		mUIManager = new UIManager(getActivity());
		
		RefWatcher refWatcher = MApplication.getRefWatcher(this);
	    refWatcher.watch(this);
		
		netWorkPresenter = new NetWorkPresenter(getActivity(), this);
		devStatusPresenter = new DevStatusPresenter(getActivity(), this, mUIManager);
		sDCardMountPresenter = new SDCardMountPresenter(getActivity(), this);
		musicPlayReceiverPresenter = new MusicPlayReceiverPresenter(getActivity(), this);
		httpPresenter = new HttpPresenter(getActivity(), this);
		musicPlayPresenter = new MusicPlayPresenter(getActivity(), mUIManager, httpPresenter);
		//long tId = Thread.currentThread().getId();
		//延迟5秒开启，主要是805的盒子问题
		mHandler.postDelayed(new Runnable() 
		{
			@Override
			public void run() 
			{
				initSqlite();
				initMinaServer();//初始化mina服务器
				initData();
			}
		}, 5000);
	}
	
	/**
	 * 初始化数据库
	 */
	private void initSqlite()
	{
		if(getLocalData().isInit())
		{
			getLocalData().setIsInit(false);
			DBManager.newInstance(getActivity()).deleteDbFile();
			DBManager.newInstance(getActivity()).openDatabase();
		}
		else
		{
			int dbVersion = LocalDataEntity.newInstance(getActivity()).getDbVersion();
			if(dbVersion < Constant.DB_VERSION)
			{
				DBManager.newInstance(getActivity()).deleteDbFile();
				DBManager.newInstance(getActivity()).openDatabase();
				LocalDataEntity.newInstance(getActivity()).setDbVersion(Constant.DB_VERSION);
			}
		}
	}
	
	/**
	 * 初始化mina
	 */
	private void initMinaServer()
	{
		OnMessageReceiveListener messageReceiveListener = new DMRCenter(this);
		ServerHandler.setOnMessageReceiveListener(messageReceiveListener);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData()
	{
		Constant.PlayPermission = LocalDataEntity.newInstance(getActivity()).getPlayPermission();
		Constant.PlayRes = LocalDataEntity.newInstance(getActivity()).getPlayRes();
	
		musicPlayReceiverPresenter.registerReceiver();//注册推送服务
		sDCardMountPresenter.registerStorageMount();//注册SD卡服务
		devStatusPresenter.startDevStatusService();//开启设备状态检查服务，心跳
		
		if(SystemUtils.isNetConnected(getActivity()))
			httpPresenter.register();
		
		mediaScanFactory = new MediaScanFactory(getActivity());
		mediaScanFactory.scanLocalMedias();
		
		locationFactory = new LocationFactory(getActivity());
		
		//mPlayerEngineImpl.getMediaPlayer().setDisplay(mUIManager.getSurfaceView());
		
		updateManager = new UpdateManager(getActivity());
		updateManager.readLuncherFile();
		
		
		curPlanFactory = new CurPlanFactory(getActivity(), mUIManager);
		//mCheckDelayTimer.startTimer();
		
		
		mUIManager.showPrepareLoadView(true);
		
		scanWifiList();
		
	}
	
	private void scanWifiList()
	{
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				netWorkPresenter.startScanWifi();
				mHandler.postDelayed(new Runnable() 
				{
					@Override
					public void run() 
					{
						// TODO Auto-generated method stub
						netWorkPresenter.startCheckNetwork();
					}
				}, 3000);
			}
		}).start();
	}
	
	private void doWifiConnectSusscess()
	{
		isNetWorkInit = false;
	
		if(deviceInforRecv != null)
			getLocalData().setDeviceInfor(deviceInforRecv);
		dismissDialog();
		showToast("网络连接成功");
		
		/*if(!NetWorkUtils.checkEthernet(getActivity()))
			*/httpPresenter.register();
    	locationFactory.startLocation();
	}
	
    private void doSystemSetFinish()
    {
    	if(deviceInforRecv != null)
    	{
    		getLocalData().setDeviceInfor(deviceInforRecv);
    		
    		DeviceSetCmd deviceSetCmd = new DeviceSetCmd();
    		deviceSetCmd.setCmdType(CmdType.SET_DEVICE_FB);
    		deviceSetCmd.setDeviceInfor(deviceInforRecv);
    		
    		/*if(!isServerRestart)
    			MinaServer.getInstance().sendConMessage(deviceSetCmd.toJson());*/
    		locationFactory.startLocation();
    		httpPresenter.register();
    	}
    }
    
    private boolean isWifiAp(String ssid)
    {
    	return ssid.equals(netWorkPresenter.getWifiApName());
    }
    
    //FIXME
    private void doSystemSet(DeviceInfor devInfor)
	{
		String ssidCon = SystemUtils.getConnectWifiSsid(this);
		
		if(ssidCon != null && ssidCon.equals(devInfor.getWifiName()) || isWifiAp(devInfor.getWifiName()))
		{
			doSystemSetFinish();
		}
		else
		{
			if(!TextUtils.isEmpty(devInfor.getWifiName()))
			{
				isNetWorkInit = true;
				showProgressDialog(getActivity(), "", "正在连接wifi " + devInfor.getWifiName() + " ...");
				netWorkPresenter.connectWifi(devInfor.getWifiName(), devInfor.getWifiPwd());
			}
		}
	}
    
	@Override
	protected void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		closeAll();
	}
	/**
	*callbacks
	*/
	@Override
	public void onBackPressed()
	{
		//moveTaskToBack(true);  
		closeAll();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) 
	{
		int keyCode = event.getKeyCode();
		int keyAction = event.getAction();
		MyToast.getInstance(getActivity()).show("code:" + keyCode);
		switch(keyCode)
		{
			case KeyEvent.KEYCODE_0:
				if (keyAction == KeyEvent.ACTION_UP)
				{
					netWorkPresenter.createWifiAp();
					return true;
				}
				break;
		}
		return super.dispatchKeyEvent(event);
	}
	
	private void doGetDevSuccessProcess(DeviceStatusInfor devStatus)
	{
		devStatusPresenter.resetFailCount();//获取状态成功了，重置失败次数
	
		if(!isRegisterFinished)
		{
			httpPresenter.register();
			return;
		}
		
		if(!isInitTerminalFinished)
		{
			httpPresenter.initTerminal();
			return;
		}
		updateManager.checkUpdate(devStatus);//检查更新
		
		doVolumeSet(devStatus);//设置音量
		
		doGetPushMusic(devStatus);//获取推送歌曲列表
		
		doSetVideoWhirl(devStatus);//设置屏幕方向
		
		if(!isCurPlanFinished)
		{
			httpPresenter.getCurPlan();
			return;
		}
		
		doDownloadAllMusic(devStatus);
		
		String planTime = devStatus.getPlanTime();
		if(TextUtils.isEmpty(planTime))
		{
			handleProcessWhenNonePlayTime();
			return;
		}
		
		String planId = devStatus.getPlanId();
		String musicUpdateTime = devStatus.getMusicLastUpdate();
		
		String localPlanId = getLocalData().getPlayId();
		if((TextUtils.isEmpty(localPlanId) || !localPlanId.equals(planId)))
		{
			//planId
			getLocalData().setPlanId(planId);
			getLocalData().setMusicUpdateTime(musicUpdateTime);
			getLocalData().setPlanTime(planTime);
			//getLocalData().setMusicIndex(0);
			//isIndexInit = true;
			httpPresenter.getPlanMusics();
		}
		else
		{
			String localMusicUpdateTime = getLocalData().getMusicUpdateTime();
			String localPlanTime = getLocalData().getPlayTime();
			if(!TextUtils.isEmpty(musicUpdateTime) && !musicUpdateTime.equals(localMusicUpdateTime))
			{
				getLocalData().setPlanId(planId);
				getLocalData().setMusicUpdateTime(musicUpdateTime);
				getLocalData().setPlanTime(planTime);
				//getLocalData().setMusicIndex(0);
				//isIndexInit = true;
				httpPresenter.getPlanMusics();
			}
			else if(!TextUtils.isEmpty(planTime) && !planTime.equals(localPlanTime))
			{
				getLocalData().setPlanId(planId);
				getLocalData().setMusicUpdateTime(musicUpdateTime);
				getLocalData().setPlanTime(planTime);
				//getLocalData().setMusicIndex(0);
				httpPresenter.getPlanMusics();
			}
			else 
			{
				if(musicPlayPresenter.isPlayListNone())
				{
					//if(!isGetPlayListRunning)
						//getLocalPlayListAndPlay();
					httpPresenter.getPlanMusics();
				}
			}
		}
	}
	
	private volatile boolean isHandleProcessWhenNonePlayTime = false;
	private void handleProcessWhenNonePlayTime()
	{
		musicPlayPresenter.handleProcessWhenNonePlayTime();
		getLocalData().setMusicIndex(0);//当前时间段内没有歌曲，将pos重置，全部从头开始播放
		
		//空闲时间   并且已经获取到了计划   下载全部歌曲
		if(isCurPlanFinished)
		{
			FileDownLoadUtil.getInstance(getActivity()).downloadAllPlanMusics(true);
		}
		else if(!httpPresenter.isRunning(HttpRequestID.GET_CUR_PLAN) && !isHandleProcessWhenNonePlayTime)
		{
			isHandleProcessWhenNonePlayTime = true;
			new Thread(new Runnable() 
			{
				@Override
				public void run() 
				{
					// TODO Auto-generated method stub
					final boolean isLocalHasPlan = DBManager.newInstance(getActivity()).isLocalHasPlan();
					runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							// TODO Auto-generated method stub
							if(!isLocalHasPlan)
								httpPresenter.getCurPlan();
							else 
								FileDownLoadUtil.getInstance(getActivity()).downloadAllPlanMusics(true);
							isHandleProcessWhenNonePlayTime = false;
						}
					});
				}
			}).start();
		}
	}
	
	private void doGetPushMusic(DeviceStatusInfor devStatus)
	{
		String pushStatus = devStatus.getPushStatus();
		if(pushStatus.equals("1"))
		{
			httpPresenter.getPushMusics();
		}
	}
	private void doSetVideoWhirl(DeviceStatusInfor devStatus)
	{
		String videoWhirl = devStatus.getVideoWhirl();
		if(!videoWhirl.equals(getLocalData().getVideoWhirl()))
		{
			getLocalData().setVideoWhirl(videoWhirl);
			//mUIManager.setSurfaceViewOritation(videoWhirl);
			setOritation(videoWhirl);
		}
	}
	private void setOritation(String videoWhirl)
	{
		mUIManager.setSurfaceViewOritation(videoWhirl);
	}
	private void doDownloadAllMusic(DeviceStatusInfor devStatus)
	{
		/*String localDownloadFlag = getLocalData().getDownloadFlag();
		if(!localDownloadFlag.equals(devStatus.getDownloadFlag()))
		{
			if(devStatus.isDownloadEnable())//
				httpPresenter.getCurPlan();
			else if(devStatus.isDownloadStop())//
				fileDownLoadUtil.stopDownLoadFiles();
			getLocalData().setDownloadFlag(devStatus.getDownloadFlag());
		}*/
	}
	private boolean isNeedSynchro = true;
	private void doGetPlayListProcess(List<SongInfor> tempList)
	{
		
		if(tempList.size() > 0)
		{
			if(isNeedSynchro)
			{
				isNeedSynchro = false;
			}
			else
			{
				LocalDataEntity.newInstance(getActivity()).setMusicIndex(0);
				LocalDataEntity.newInstance(getActivity()).setSeekPos(0);
				LocalDataEntity.newInstance(getActivity()).setCurLastUpdateTime(0);
			}
			
			musicPlayPresenter.addPlayList(tempList);
			FileDownLoadUtil.getInstance(getActivity()).downloadCurPlanMusics(tempList);
			
			musicPlayPresenter.startPlaySong();
		}
		
		dismissDialog();
		
		//httpPresenter.getCurPlan();//获取播放计划
		
	}
	
	public void closeAll()
	{
		if(mUIManager != null)
		{
			mUIManager.stop();
		}
		
		devStatusPresenter.stopDevStatusService();
		showProgressDialog(getActivity(), null, "正在退出...");
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				locationFactory.stopLocation();
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						// TODO Auto-generated method stub
						MinaServer.getInstance().closeServer();
						
						DBManager.newInstance(getActivity()).close();
						
						//stopService(new Intent(mContext, DMSService.class));
						
						musicPlayReceiverPresenter.unregisterReceiver();
						sDCardMountPresenter.unregisterStorageReceiver();
						
						mUIManager.unInit();
						musicPlayPresenter.closePlayer();
						netWorkPresenter.stopCheckSSID();
						
						dismissDialog();
						System.exit(0);
					}
				});
			}
		}).start();
	}
	
	@Override
	public void onReceive(Intent intent) 
	{
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(action.equals(DMRCenter.SET_DEVICE))//璁剧疆闊冲搷 
		{
			deviceInforRecv = (DeviceInfor)intent.getSerializableExtra("DeviceInfor");
			/*Bundle bundle = new Bundle();
			bundle.putSerializable("DeviceInfor", tempInfor);
			ViewUtils.startActivity(getActivity(), SetActivity.class, bundle, 1);*/
			doSystemSet(deviceInforRecv);
		}
		else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) 
		{
			
			if(isNetWorkInit)
        	{
				isNetWorkInit = false;
				return;
        	}
			
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();  
            if(info != null && info.isAvailable()) 
            {
            	if(info.getType() == ConnectivityManager.TYPE_ETHERNET)
            		showToast("有线网络连接成功");
            	else if(info.getType() == ConnectivityManager.TYPE_WIFI)
            		showToast("无线网络连接成功");
            } 
		}
	}

	@Override
	public void onMediaChange(boolean isAdd, String path) 
	{
		// TODO Auto-generated method stub
		netWorkPresenter.getWifiSetInfoFromUsb(path, deviceInforRecv);
	}

	@Override
	public void createApStart() 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createApFail() 
	{
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				showToast("WIFI热点创建失败");
			}
		});
	}

	@Override
	public void createApSuccess() 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void connectWifiSatrt(final String wifiName)
	{
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				showProgressDialog(getActivity(), null, "正在连接wifi-->" + wifiName);
			}
		});
	}

	@Override
	public void connectWifiFailed() 
	{
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				dismissDialog();
			}
		});
	}

	@Override
	public void connectWifiSuccess() 
	{
		// TODO Auto-generated method stub
		/*if(isWifiReconnected)
		{
			isWifiReconnected = false;
			if(mPlayerEngineImpl.isPause())
				mPlayerEngineImpl.play();
			else
			{
				if(playList.size() == 0)
					getCurPlan(true);
				else
					startPlaySong();
			}
		}
		else*/
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				doWifiConnectSusscess();
			}
		});
	}

	@Override
	public void requestStart(int requestId)
	{
		// TODO Auto-generated method stub
		if(requestId == HttpRequestID.GET_DEVICE_STATUS)
		{
			Log.d("", "正在获取设备状态...");
		}
		else if(requestId == HttpRequestID.GET_PLAN_MUSICS)
		{
			showProgressDialog(getActivity(), null, "正在获取播放列表...");
		}
	}

	@Override
	public void requestError(int requestId) 
	{
		// TODO Auto-generated method stub
		if(requestId == HttpRequestID.GET_DEVICE_STATUS)
		{
			devStatusPresenter.setFailCount();//获取状态失败了，就累加状态计数
			curPlanFactory.setOffline(devStatusPresenter.isOffLine());//设置掉线状态
		}
		else if(requestId == HttpRequestID.GET_PLAN_MUSICS)
		{
			getLocalData().setPlanId("");
		}
		/*else if(requestId == HttpRequestID.GET_PLAN_MUSICS)
		{
			
		}*/
	}
	@Override
	public void requestSuccess(Object obj, int requestId)
	{
		// TODO Auto-generated method stub
		if(requestId == HttpRequestID.GET_DEVICE_STATUS)
		{
			DeviceStatusInfor response = (DeviceStatusInfor) obj;
			doGetDevSuccessProcess(response);
			curPlanFactory.setOffline(devStatusPresenter.isOffLine());//设置掉线状态
		}
		else if(requestId == HttpRequestID.GET_CUR_PLAN)
		{
			isCurPlanFinished = true;
			dismissDialog();
			CurPlanInfor curPlanInfor = (CurPlanInfor)obj;
			if(curPlanInfor != null)
			{
				curPlanFactory.setCurPlanInfor(curPlanInfor);
				
				FileDownLoadUtil.getInstance(getActivity()).downloadAllPlanMusics(true);
				FileDownLoadUtil.getInstance(getActivity()).setDownloadAllUnFinish();
			}
			
		}
		else if(requestId == HttpRequestID.INIT_TERMINAL)
		{
			isInitTerminalFinished = true;
			
			String systemTime = (String) obj;
			if(!TextUtils.isEmpty(systemTime))
				mUIManager.setCurTime(Long.parseLong(systemTime));
			
			musicPlayPresenter.startPlaySong();
		}
		else if(requestId == HttpRequestID.REGISTER || requestId == HttpRequestID.UPDATE_SPEAKER_INFOR)
		{
			isRegisterFinished = true;
			httpPresenter.initTerminal();
			mUIManager.showDeviceInfor();
		}
		else if(requestId == HttpRequestID.GET_PLAN_MUSICS)
		{
			List<SongInfor> tempList = (List<SongInfor>)obj;
			if(tempList != null)
			{
				doGetPlayListProcess(tempList);
			}
			else
				requestError(requestId);
		}
		else if(requestId == HttpRequestID.GET_PUSH_MUSIC)
		{
			musicPlayPresenter.startPlaySong();
		}
	}

	@Override
	public void requestNetWorkError() 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetDevStatus()
	{
		// TODO Auto-generated method stub
		httpPresenter.getDevStatus(musicPlayPresenter.getCurPosition(), musicPlayPresenter.getCurPlaySongName(), musicPlayPresenter.getCurPlaySongType());
	}

	@Override
	public void onCheckDelay() 
	{
		// TODO Auto-generated method stub
		musicPlayPresenter.checkDelay();
	}

	@Override
	public void getLocalPlayListAndPlay(List<SongInfor> tempSongList) 
	{
		// TODO Auto-generated method stub
		musicPlayPresenter.handlerLocalPlayListDo(tempSongList);
	}
}
