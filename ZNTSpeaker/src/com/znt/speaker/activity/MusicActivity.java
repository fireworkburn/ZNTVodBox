/*package com.znt.speaker.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.znt.diange.mina.cmd.CmdType;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.cmd.DeviceSetCmd;
import com.znt.diange.mina.cmd.PlayErrorCmd;
import com.znt.diange.mina.cmd.UpdateCmd;
import com.znt.diange.mina.entity.PlayState;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.R;
import com.znt.speaker.center.DMRCenter;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.CurPlanInfor;
import com.znt.speaker.entity.DeviceStatusInfor;
import com.znt.speaker.entity.DownloadFileInfo;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.factory.CurPlanFactory;
import com.znt.speaker.factory.HttpFactory;
import com.znt.speaker.factory.LocationFactory;
import com.znt.speaker.factory.MediaScanFactory;
import com.znt.speaker.factory.UIManager;
import com.znt.speaker.http.HttpMsg;
import com.znt.speaker.mina.server.MinaServer;
import com.znt.speaker.mina.server.ServerHandler;
import com.znt.speaker.mina.server.ServerHandler.OnMessageReceiveListener;
import com.znt.speaker.p.DevStatusPresenter;
import com.znt.speaker.p.MusicPlayReceiverPresenter;
import com.znt.speaker.p.NetWorkPresenter;
import com.znt.speaker.p.SDCardMountPresenter;
import com.znt.speaker.player.AbstractTimer;
import com.znt.speaker.player.CheckDelayTimer;
import com.znt.speaker.player.MusicPlayEngineImpl;
import com.znt.speaker.player.PlayerEngineListener;
import com.znt.speaker.player.SingleSecondTimer;
import com.znt.speaker.update.UpdateManager;
import com.znt.speaker.util.CommonLog;
import com.znt.speaker.util.FileDownLoadUtil;
import com.znt.speaker.util.LogFactory;
import com.znt.speaker.util.MyToast;
import com.znt.speaker.util.NetWorkUtils;
import com.znt.speaker.util.PlayErrorCheck;
import com.znt.speaker.util.SystemUtils;
import com.znt.speaker.util.ViewUtils;
import com.znt.speaker.v.IDevStatusView;
import com.znt.speaker.v.IHttpRequestView;
import com.znt.speaker.v.IMusicReceiverView;
import com.znt.speaker.v.INetWorkView;
import com.znt.speaker.v.ISDCardMountView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;

public class MusicActivity extends BaseActivity
									{

	private UIManager mUIManager;
	
	private UpdateManager updateManager = null;
	private HttpFactory httpFactory = null;
	
	public  static SongInfor songInforPlay = null;	
	private EthernetManager mEthManager = null;
	private FileDownLoadUtil fileDownLoadUtil = null;
	
	private AbstractTimer mPlayPosTimer;
	private LocationFactory locationFactory = null;
	
	private CurPlanFactory curPlanFactory = null;
	
	private MediaScanFactory mediaScanFactory = null;
	private List<SongInfor> playList = new ArrayList<SongInfor>();
	
	private int checkDevStatusFailCount = 0;
	private int checkUpdateCount = 0;
	private long curTimeFromNetWork = 0;
	private boolean isGetPlayListRunning = false;
	private boolean isGetCurPlanFailed = false;
	private boolean isInitTerminalRunning = false;
	private boolean isInitTerminalSuccess = false;
	private boolean isNeedSynchro = true;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch(msg.what)
			{
				case HttpMsg.REGISTER_START:
					isRegisterRunning = true;
					isRegistered = false;
					break;
				case HttpMsg.REGISTER_FAIL:
					isRegisterRunning = false;
					isRegistered = false;
					break;
				case HttpMsg.REGISTER_SUCCESS:
					
					String devCode = (String)msg.obj;
					getLocalData().setDeviceCode(devCode);
					initTerminal();
					mUIManager.showDeviceInfor();
					
					isRegisterRunning = false;
					isRegistered = true;
					
					break;
				case HttpMsg.GET_PLAY_LIST_START:
					showProgressDialog(getActivity(), null, "正在加载播放列表...");
					isGetPlayListRunning = true;
					break;
				case HttpMsg.GET_PLAY_LIST_SUCCESS:
					List<SongInfor> tempList = (List<SongInfor>)msg.obj;
					if(tempList != null)
					{
						doGetPlayListProcess(tempList);
						isGetPlayListFinished = true;
						isGetPlayListRunning = false;
					}
					break;
				case HttpMsg.GET_PLAY_LIST_FAIL:
					getLocalData().setPlanId("");
					String error = (String)msg.obj;
					if(TextUtils.isEmpty(error))
						error = "获取播放列表失败";
					showToast(error);
					dismissDialog();
					isGetPlayListRunning = false;
					isGetPlayListFinished = true;
					break;
				case HttpMsg.GET_DEVICE_STATUS_START:
					isGetDevStatusRunning = true;
					break;
				case HttpMsg.GET_DEVICE_STATUS_SUCCESS:
					DeviceStatusInfor devStatus = (DeviceStatusInfor)msg.obj;
					doGetDevSuccessProcess(devStatus);
					checkDevStatusFailCount = 0;
					curPlanFactory.setCheckStatusResult(devStatusPresenter.isCheckStatusFail());
					break;
				case HttpMsg.GET_DEVICE_STATUS_FAIL:
					isGetDevStatusRunning = false;
					checkDevStatusFailCount ++;
					if(checkDevStatusFailCount >= 2)
					{
						devStatusPresenter.setCheckStatus(true);
						curPlanFactory.setCheckStatusResult(devStatusPresenter.isCheckStatusFail());
					}
					break;
				case HttpMsg.NO_NET_WORK_CONNECT:
					break;
				case HttpMsg.GET_PUSH_MUSICS_STATR:
					
					break;
				case HttpMsg.GET_PUSH_MUSICS_SUCCESS:
					List<SongInfor> songList = getDBManager().getSongList(0, 100);
					if(songList.size() > 0)
					{
						if(!isSongPlay)
							startPlaySong();
					}
						
					break;
				case HttpMsg.GET_PUSH_MUSICS_FAIL:
					
					break;
				case HttpMsg.GET_CUR_PLAN_START:
					isGetCurPlanRunning = true;
					isGetCurPlanFailed = false;
					showProgressDialog(getActivity(), null, "正在获取播放计划...");
					break;
				case HttpMsg.GET_CUR_PLAN_SUCCESS:
					
					CurPlanInfor curPlanInfor = (CurPlanInfor)msg.obj;
					curPlanFactory.setCurPlanInfor(curPlanInfor);
					
					isGetCurPlanRunning = false;
					isGetCurPlanFinished = true;
					dismissDialog();
					
 					if(isDownloadAfterGetPlan && fileDownLoadUtil.isDownloadAllAvailable())
						fileDownLoadUtil.downloadAllPlanMusics(true);
 					fileDownLoadUtil.setDownloadAllUnFinish();
 					
					break;
				case HttpMsg.GET_CUR_PLAN_FAIL:
					isGetCurPlanRunning = false;
					isGetCurPlanFinished = false;
					isGetCurPlanFailed = true;
					dismissDialog();
					
					break;
				case HttpMsg.UPDATE_CUR_POS_START:
					isUpdateCurPosRunning = true;
					break;
				case HttpMsg.UPDATE_CUR_POS_SUCCESS:
					isUpdateCurPosRunning = false;
					break;
				case HttpMsg.UPDATE_CUR_POS_FAIL:
					isUpdateCurPosRunning = false;
					break;
				case HttpMsg.INIT_TERMINAL_START:
					isInitTerminalRunning = true;
					isInitTerminalSuccess = false;
					break;
				case HttpMsg.INIT_TERMINAL_SUCCESS:

					String systemTime = (String) msg.obj;
					if(!TextUtils.isEmpty(systemTime))
						curTimeFromNetWork = Long.parseLong(systemTime);
					mUIManager.setCurTime(curTimeFromNetWork);
					
					playMusicWhenInitFinish();
					
					isInitTerminalRunning = false;
					isInitTerminalSuccess = true;
					break;
				case HttpMsg.INIT_TERMINAL_FAIL:
					isInitTerminalRunning = false;
					isInitTerminalSuccess = false;
					break;
			}
		}
	};
	private boolean isGetCurPlanRunning = false;
	private boolean isGetCurPlanFinished = false;
	private boolean isGetDevStatusRunning = false;
	
	private void getDevStatus()
	{
		if(!isGetDevStatusRunning)
			httpFactory.getDeviceStatus();
	}
	
	private void doGetPushMusic(DeviceStatusInfor devStatus)
	{
		String pushStatus = devStatus.getPushStatus();
		if(pushStatus.equals("1"))
		{
			httpFactory.getPushMusics();
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
		String localDownloadFlag = getLocalData().getDownloadFlag();
		if(!localDownloadFlag.equals(devStatus.getDownloadFlag()))
		{
			if(devStatus.isDownloadEnable())//
				getCurPlan(true);
			else if(devStatus.isDownloadStop())//
				fileDownLoadUtil.stopDownLoadFiles();
			getLocalData().setDownloadFlag(devStatus.getDownloadFlag());
		}
	}
	private void doGetDevSuccessProcess(DeviceStatusInfor devStatus)
	{
		if(isGetCurPlanFailed)
			getCurPlan(false);
		if(!isInitTerminalSuccess)
			initTerminal();
		
		devStatusPresenter.setCheckStatus(false);
		isGetDevStatusRunning = false;
		checkDevStatusFailCount = 0;
		
		checkUpdateCount ++;
		if(checkUpdateCount > 3)
		{
			checkUpdateCount = 0;
			updateManager.checkUpdate(devStatus);
			if(!isRegistered)
				register();
		}
		
		doVolumeSet(devStatus);
		doGetPushMusic(devStatus);
		doSetVideoWhirl(devStatus);
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
			getPlayList();//
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
				getPlayList();//
			}
			else if(!TextUtils.isEmpty(planTime) && !planTime.equals(localPlanTime))
			{
				getLocalData().setPlanId(planId);
				getLocalData().setMusicUpdateTime(musicUpdateTime);
				getLocalData().setPlanTime(planTime);
				//getLocalData().setMusicIndex(0);
				isIndexInit = true;
				getPlayList();//
			}
			else 
			{
				if(playList.size() == 0)
				{
					//if(!isGetPlayListRunning)
						//getLocalPlayListAndPlay();
					getPlayList();//
				}
			}
		}
	}
	private void doGetPlayListProcess(List<SongInfor> tempList)
	{
		playList.clear();
		if(isNeedSynchro)
		{
			isNeedSynchro = false;
		}
		else
		{
			//if(isIndexInit)
			{
				isIndexInit = false;
				getLocalData().setMusicIndex(0);
				getLocalData().setSeekPos(0);
				getLocalData().setCurLastUpdateTime(0);
			}
		}
		
		if(tempList.size() > 0)
		{
			playList.addAll(tempList);
			fileDownLoadUtil.downloadCurPlanMusics(tempList);
			
			if(mPlayerEngineImpl != null && !mPlayerEngineImpl.isPlaying())
			{
				startPlaySong();
			}
		}
		
		dismissDialog();
		
		getCurPlan(true);//
		
	}
	
	private void initTerminal()
	{
		if(!isInitTerminalRunning)
			httpFactory.initTerminal();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);	
		
		RefWatcher refWatcher = MApplication.getRefWatcher(this);
	    refWatcher.watch(this);
	}
	
	private void getPlayList()
	{
		if(!isGetPlayListRunning)
			httpFactory.getPlayList();
	}
	private boolean isDownloadAfterGetPlan = false;
	private void getCurPlan(boolean isDownloadAfterGetPlan)
	{
		this.isDownloadAfterGetPlan = isDownloadAfterGetPlan;
		if(!isGetCurPlanRunning)
			httpFactory.getCurPlan();
	}
	
	private boolean isRegisterRunning = false;
	private boolean isRegistered = false;
	private void register()
	{
		if(!isRegisterRunning)
			httpFactory.register();
	}
	
	
}
*/