package com.znt.speaker.center;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;

import com.znt.diange.mina.cmd.CmdType;
import com.znt.diange.mina.cmd.DeleteSongCmd;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.cmd.DeviceSetCmd;
import com.znt.diange.mina.cmd.ErrorCmd;
import com.znt.diange.mina.cmd.GetPlayResCmd;
import com.znt.diange.mina.cmd.GetSongInforCmd;
import com.znt.diange.mina.cmd.GetSongListCmd;
import com.znt.diange.mina.cmd.GetWifiListCmd;
import com.znt.diange.mina.cmd.PlayCmd;
import com.znt.diange.mina.cmd.PlayNextCmd;
import com.znt.diange.mina.cmd.PlayPermissionCmd;
import com.znt.diange.mina.cmd.PlayResCmd;
import com.znt.diange.mina.cmd.PlayResUpdateCmd;
import com.znt.diange.mina.cmd.PlayStateCmd;
import com.znt.diange.mina.cmd.RegisterCmd;
import com.znt.diange.mina.cmd.SpeakerMusicCmd;
import com.znt.diange.mina.cmd.StopCmd;
import com.znt.diange.mina.cmd.SystemUpdateCmd;
import com.znt.diange.mina.cmd.VolumeGetCmd;
import com.znt.diange.mina.cmd.VolumeSetCmd;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.PermissionType;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.factory.HttpFactory;
import com.znt.speaker.mina.server.ServerHandler.OnMessageReceiveListener;
import com.znt.speaker.util.CmdManager;
import com.znt.speaker.util.CommonLog;
import com.znt.speaker.util.LogFactory;
import com.znt.speaker.util.SystemUtils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

public class DMRCenter extends CmdManager implements OnMessageReceiveListener, IDMRAction
{

	private static final CommonLog log = LogFactory.createLog();
	
	private Context mContext;
	
	private HttpFactory httpFactory = null;
	
	
	public static final int CUR_MEDIA_TYPE_MUSCI = 0x0001;
	
	public DMRCenter(Context context)
	{
		mContext = context;
	}
	
	@Override
	public synchronized void onMsgRecv(IoSession session, JSONObject obj) 
	{
		
		String cmdType = getInforFromJson("cmdType", obj);
		try
		{
			if(cmdType.equals(CmdType.PLAY))
				recvPlay(session, obj);
			else if(cmdType.equals(CmdType.GET_PLAY_LIST))
				recvGetPlayList(session, obj);
			else if(cmdType.equals(CmdType.GET_PLAY_RECORD))
				recvGetPlayRecord(session, obj);
			else if(cmdType.equals(CmdType.GET_PLAY_MUSIC_INFOR))
				recvGetPlayInfor(session, obj);
			else if(cmdType.equals(CmdType.REGISTER))
				recvRegister(session, obj);
			else if(cmdType.equals(CmdType.DELETE_SONG))
				recvDeleteSong(session, obj);
			else if(cmdType.equals(CmdType.STOP))
				recvStopSong(session, obj);
			else if(cmdType.equals(CmdType.SET_DEVICE))
				recvSetDevice(session, obj);
			else if(cmdType.equals(CmdType.SET_DEVICE_VOLUM))
				recvSetVolume(session, obj);
			else if(cmdType.equals(CmdType.GET_DEVICE_VOLUM))
				recvGetVolume(session, obj);
			else if(cmdType.equals(CmdType.GET_PLAY_STATE))
				recvGetPlayState(session, obj);
			else if(cmdType.equals(CmdType.SET_PLAY_STATE))
				recvSetPlayState(session, obj);
			else if(cmdType.equals(CmdType.PLAY_NEXT))
				recvPlayNext(session, obj);
			else if(cmdType.equals(CmdType.SPEAKER_MUSIC))
				recvSpeakerMusic(session, obj);
			else if(cmdType.equals(CmdType.PLAY_PERMISSION))
				recvPlayPermission(session, obj);
			else if(cmdType.equals(CmdType.GET_PLAY_PERMISSION))
				recvGetPlayPermission(session, obj);
			else if(cmdType.equals(CmdType.SET_PLAY_RES))
				recvSetPlayRes(session, obj);
			else if(cmdType.equals(CmdType.RES_UPDATE))
				recvResUpdate(session, obj);
			else if(cmdType.equals(CmdType.GET_PLAY_RES))
				recvGetPlayRes(session, obj);
			else if(cmdType.equals(CmdType.GET_WIFI_LIST))
				recvGetWifiList(session, obj);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			log.e("error-->"+e.getMessage());
			sendErroCmd(session, e.getMessage());
		}
	}
	
	private void sendErroCmd(IoSession session, String error)
	{
		ErrorCmd cmd = new ErrorCmd();
		cmd.setError(error);
		sendCmdInfor(session, cmd.toJson());
	}
	
	/***************鎺ユ敹鍛戒护****************/
	@Override
	public void recvRegister(IoSession session, JSONObject obj)
	{
		//鎺ユ敹娉ㄥ唽
		
		RegisterCmd registerCmd = new RegisterCmd();
		registerCmd.toClass(obj.toString());
		registerCmd.setCmdType(CmdType.REGISTER_FB);
		registerCmd.setPermission(Constant.PlayPermission);
		String appVersion = "";
		try 
		{
			appVersion = SystemUtils.getPkgInfo(mContext).versionName;
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//鑾峰彇璇锋眰鐨勮澶囩殑鏉冮檺
		boolean adminRecv = registerCmd.getUserInfor().isAdmin();
		if(adminRecv)//鐢宠绠＄悊鍛樻潈闄�,濡傛灉璇ョ敤鎴峰叿澶囩鐞嗗憳鏉冮檺灏辨坊鍔犲埌绠＄悊鍛樺垪琛ㄤ腑
			DBManager.newInstance(mContext).insertAdmin(registerCmd.getUserInfor());
		
		String userId = registerCmd.getUserInfor().getUserId();
		boolean isAdminLocal = DBManager.newInstance(mContext).isAdminExist(userId);
		registerCmd.getUserInfor().setAdmin(isAdminLocal);//娣诲姞鏉冮檺
		
		DeviceInfor deviceInfor = LocalDataEntity.newInstance(mContext).getDeviceInfor();
		/*String curConnectWifi = SystemUtils.getConnectWifiSsid(mContext);
		if(!deviceInfor.getWifiName().equals(curConnectWifi))//鏈湴鑾峰彇鐨刉IFI涓庡綋鍓嶈繛鎺ョ殑WIFI涓嶄竴鑷�
		{
			String wifiPwd = DBManager.newInstance(mContext).getWifiPwdByName(curConnectWifi);
			deviceInfor.setWifiName(curConnectWifi);
			deviceInfor.setWifiPwd(wifiPwd);
			//鏇存柊鏈湴涓存椂鐨刉IFI淇℃伅
			LocalDataEntity.newInstance(mContext).updateWifi(curConnectWifi, wifiPwd);
		}*/
		deviceInfor.setVersion(appVersion);
		registerCmd.setDeviceInfor(deviceInfor);//鏇存柊璁惧淇℃伅
		sendCmdInfor(session, registerCmd.toJson());
		//sendCmdInfor(session, FastJsonTools.createJsonString(registerCmd));
	}
	@Override
	public void recvGetPlayList(IoSession session, JSONObject obj)
	{
		//鎺ユ敹鑾峰彇鎾斁鍒楄〃,鐩存帴鍙戦�佹挱鏀惧垪琛�
		GetSongListCmd cmdInfor = new GetSongListCmd();
		cmdInfor.toClass(obj.toString());
		cmdInfor.setCmdType(CmdType.GET_PLAY_LIST_FB);
		int total = DBManager.newInstance(mContext).getSongCount();
		int pageNum = Integer.parseInt(cmdInfor.getPageNum());
		int pageSize = Integer.parseInt(cmdInfor.getPageSize());
		cmdInfor.setTotal(total + "");
		List<SongInfor> tempList = DBManager.newInstance(mContext).getSongList(pageNum, pageSize);
		int temp = tempList.size();
		if(temp == 0)
		{
			cmdInfor.setSongList(null);
		}
		else
		{
			cmdInfor.setSongList(tempList);
		}
		sendCmdInfor(session, cmdInfor.toJson());
		//sendCmdInfor(session, FastJsonTools.createJsonString(cmdInfor));
		
	}
	@Override
	public void recvGetPlayRecord(IoSession session, JSONObject obj)
	{
		//鎺ユ敹鑾峰彇鎾斁鍒楄〃,鐩存帴鍙戦�佹挱鏀惧垪琛�
		/*GetSongRecordCmd cmdInfor = new GetSongRecordCmd();
		cmdInfor.toClass(obj.toString());
		cmdInfor.setCmdType(CmdType.GET_PLAY_RECORD_FB);
		int total = DBManager.newInstance(mContext).getRecordCount();
		int pageNum = Integer.parseInt(cmdInfor.getPageNum());
		int pageSize = Integer.parseInt(cmdInfor.getPageSize());
		cmdInfor.setTotal(total + "");
		List<SongInfor> tempList = DBManager.newInstance(mContext).getSongRecord(pageNum, pageSize);
		int temp = tempList.size();
		if(temp == 0)
		{
			cmdInfor.setSongList(null);
		}
		else
		{
			cmdInfor.setSongList(tempList);
		}
		sendCmdInfor(session, cmdInfor.toJson());*/
		//sendCmdInfor(session, FastJsonTools.createJsonString(cmdInfor));
		
	}
	@Override
	public void recvPlay(IoSession session, JSONObject obj)
	{
		PlayCmd cmdInfor = new PlayCmd();
		cmdInfor.toClass(obj.toString());
		final SongInfor songInfor = cmdInfor.getSongInfor();
		cmdInfor.setPermission(Constant.PlayPermission);
		songInfor.setUserInfor(cmdInfor.getUserInfor());
		
		int type = Integer.parseInt(cmdInfor.getType());
		if(type == 0)
		{
			if(Constant.PlayPermission.equals(PermissionType.NONE))
			{
				cmdInfor.setResult(5+"");//绂佹鐐规挱鍏ㄩ儴姝屾洸 
				sendPlayFbCmd(session, cmdInfor);
			}
			else if(Constant.PlayPermission.equals(PermissionType.ALL) 
					|| songInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_SPEAKER))
			{
				if(DBManager.newInstance(mContext).isSongExist(songInfor))
				{
					//姝屾洸宸茬粡瀛樺湪锛屾彁绀虹敤鎴�
					cmdInfor.setResult(3+"");
					sendPlayFbCmd(session, cmdInfor);
				}
				else
				{
					//鍔犲叆鏈湴鎾斁鍒楄〃涓�
					DBManager.newInstance(mContext).insertSong(songInfor);
					cmdInfor.setResult(0+"");
					sendPlayFbCmd(session, cmdInfor);
					
					/*try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					
					startPlayMusic(songInfor);
					uploadSongRecord(songInfor);
					//鎺ユ敹鎾斁
					//delayToPlayMusic(songInfor);
				}
			}
			else
			{
				//涓嶅厑璁告挱鏀剧涓夋柟鐨勬瓕鏇�
				cmdInfor.setResult(4+"");
				sendPlayFbCmd(session, cmdInfor);
			}
			
		}
		else if(type == 1)//鏇存柊鐐规挱
		{
			if(DBManager.newInstance(mContext).isSongExist(songInfor))
			{
				
				DBManager.newInstance(mContext).updateSong(songInfor);
			}
			else
				cmdInfor.setResult(2 + "");//姝屾洸涓嶅瓨鍦�
			sendPlayFbCmd(session, cmdInfor);
		}
		else if(type == 2)//寮哄埗鏇存柊鐐规挱
		{
			DBManager.newInstance(mContext).insertSong(songInfor);
			cmdInfor.setResult(0 + "");
			
			cmdInfor.setCmdType(CmdType.PLAY_FB);
			cmdInfor.setSongInfor(null);//娓呯┖姝屾洸淇℃伅
			sendCmdInfor(session, cmdInfor.toJson());
			sendPlayFbCmd(session, cmdInfor);
			
			uploadSongRecord(songInfor);
		}
		
		//鍔犲叆鏈湴鎾斁鍒楄〃涓�
		/*DBManager.newInstance(mContext).insertSong(songInfor);
		cmdInfor.setResult(0+"");
		sendPlayFbCmd(session, cmdInfor);
		
		//鎺ユ敹鎾斁
		startPlayMusic(songInfor);*/
		
		//sendCmdInfor(session, FastJsonTools.createJsonString(cmdInfor));
	}
	
	private void uploadSongRecord(SongInfor songInfor)
	{
		if(httpFactory == null)
			httpFactory = new HttpFactory(mContext);
		httpFactory.uploadSongRecord(songInfor);
	}
	
	private void sendPlayFbCmd(IoSession session, PlayCmd cmdInfor)
	{
		cmdInfor.setCmdType(CmdType.PLAY_FB);
		cmdInfor.setSongInfor(null);//娓呯┖姝屾洸淇℃伅
		sendCmdInfor(session, cmdInfor.toJson());
	}
	
	@Override
	public void recvGetPlayInfor(IoSession session, JSONObject obj)
	{
		GetSongInforCmd cmdInfor = new GetSongInforCmd();
		cmdInfor.toClass(obj.toString());
		cmdInfor.setCmdType(CmdType.GET_PLAY_MUSIC_INFOR_FB);
		/*if(MusicActivity.songInforPlay != null)
		{
			MusicActivity.songInforPlay.setPlayState(1);
			cmdInfor.setSongInfor(MusicActivity.songInforPlay);
		}
		else
		{
			cmdInfor.setSongInfor(null);
		}
		sendCmdInfor(session, cmdInfor.toJson());*/
		//sendCmdInfor(session, FastJsonTools.createJsonString(cmdInfor));
	}
	
	/**
	*callbacks
	*/
	@Override
	public void recvDeleteSong(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		DeleteSongCmd cmdInfor = new DeleteSongCmd();
		cmdInfor.toClass(obj.toString());
		cmdInfor.setCmdType(CmdType.DELETE_SONG_FB);
		int result = DBManager.newInstance(mContext).deleteSongByUserId(cmdInfor.getUserInfor().getUserId());
		if(result >= 0)
		{
			cmdInfor.setResult(0 + "");//鎴愬姛
		}
		else
		{
			cmdInfor.setResult(1 + "");//澶辫触
		}
		
		sendCmdInfor(session, cmdInfor.toJson());
		//sendCmdInfor(session, FastJsonTools.createJsonString(cmdInfor));
	}
	
	/***************鍙戦�佸懡浠�****************/
	public void sendPlayList()
	{
		//鍙戦�佹挱鏀惧垪琛�
	}
	
	public void sendPlayResult()
	{
		//鍙戦�佹挱鏀剧粨鏋�
		
	}
	public void sendPlayFinish()
	{
		//鍙戦�佹挱鏀惧畬鎴�
		
	}
	public void sendPlayMusicInfor()
	{
		//鍙戦�佸綋鍓嶆挱鏀炬瓕鏇蹭俊鎭�
		
	}

	
	private void delayToSetDevice(DeviceInfor deviceInfor)
	{
		if(deviceInfor != null)
		{
			setDevice(deviceInfor);
		}
	}
	
	public static final String CUR_PLAY_SONG = "com.znt.speaker.CUR_PLAY_SONG";//鏀跺埌鑾峰彇褰撳墠鎾斁淇℃伅
	public static final String GET_NEW_SONG = "com.znt.speaker.GET_NEW_SONG";//鏀跺埌鏂扮殑鐐规挱
	public static final String STOP_SONG = "com.znt.speaker.STOP_SONG";//鍋滄褰撳墠鐐规挱鐨勬瓕鏇� 
	public static final String SET_DEVICE = "com.znt.speaker.SET_DEVICE";//璁剧疆闊冲搷
	public static final String PLAY_NEXT = "com.znt.speaker.PLAY_NEXT";//鍒囨瓕
	public static final String UPDATE_SYSTEM = "com.znt.speaker.UPDATE_SYSTEM";//绯荤粺鍗囩骇
	public static final String UPDATE_RES = "com.znt.speaker.UPDATE_RES";//鏇存柊鏇插簱
	public static final String UPDATE_PLAY_STATE = "com.znt.speaker.UPDATE_PLAY_STATE";//鏇存柊鏇插簱
	private void startPlayMusic(SongInfor mediaInfo)
	{
		Intent intent = new Intent(GET_NEW_SONG); 
		Bundle bundle = new Bundle();
		bundle.putSerializable("SongInfor", mediaInfo);
		intent.putExtras(bundle);  
		mContext.sendBroadcast(intent);  
	}
	
	private void stopPlayMusic(SongInfor mediaInfo)
	{
		Intent intent = new Intent(STOP_SONG); 
		Bundle bundle = new Bundle();
		bundle.putSerializable("SongInfor", mediaInfo);
		intent.putExtras(bundle);  
		mContext.sendBroadcast(intent);  
	}
	
	private void setDevice(DeviceInfor deviceInfor)
	{
		Intent intent = new Intent(SET_DEVICE); 
		Bundle bundle = new Bundle();
		bundle.putSerializable("DeviceInfor", deviceInfor);
		intent.putExtras(bundle);  
		mContext.sendBroadcast(intent);  
	}
	
	private void playNext()
	{
		Intent intent = new Intent(PLAY_NEXT); 
		mContext.sendBroadcast(intent);  
	}
	
	private void updateSystem()
	{
		Intent intent = new Intent(UPDATE_SYSTEM); 
		mContext.sendBroadcast(intent);  
	}
	
	private void updatePlayRes()
	{
		Intent intent = new Intent(UPDATE_RES); 
		mContext.sendBroadcast(intent);  
	}
	private void updatePlayState(int state)
	{
		Intent intent = new Intent(UPDATE_PLAY_STATE); 
		intent.putExtra("STATE", state);
		mContext.sendBroadcast(intent);  
	}

	/**
	*callbacks
	*/
	@Override
	public void recvStopSong(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		StopCmd cmdInfor = new StopCmd();
		cmdInfor.toClass(obj.toString());
		cmdInfor.setCmdType(CmdType.STOP_FB);
		stopPlayMusic(cmdInfor.getSongInfor());
		
		cmdInfor.setSongInfor(null);
		
		sendCmdInfor(session, cmdInfor.toJson());
		//sendCmdInfor(session, FastJsonTools.createJsonString(cmdInfor));
	}

	/**
	*callbacks
	*/
	@Override
	public void recvSetDevice(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		
		DeviceSetCmd deviceSetCmd = new DeviceSetCmd();
		deviceSetCmd.toClass(obj.toString());
		deviceSetCmd.setCmdType(CmdType.SET_DEVICE_FB);
		delayToSetDevice(deviceSetCmd.getDeviceInfor());
		
		//鏀跺埌娑堟伅鍚庣洿鎺ュ弽棣�
		sendCmdInfor(session, deviceSetCmd.toJson());
		//sendCmdInfor(session, FastJsonTools.createJsonString(deviceSetCmd));
	}

	/**
	*callbacks
	*/
	@Override
	public void recvSetVolume(IoSession session,JSONObject obj)
	{
		// TODO Auto-generated method stub
		VolumeSetCmd volumeSetCmd = new VolumeSetCmd();
		volumeSetCmd.toClass(obj.toString());
		
		AudioManager mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Integer.parseInt(volumeSetCmd.getVolume()), 0); 
		
		volumeSetCmd.setCmdType(CmdType.SET_DEVICE_VOLUM_FB);
		volumeSetCmd.setResult(0 + "");
		
		sendCmdInfor(session, volumeSetCmd.toJson());
		//sendCmdInfor(session, FastJsonTools.createJsonString(volumeSetCmd));
	}

	/**
	*callbacks
	*/
	@Override
	public void recvGetVolume(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		
		// 闊抽噺鍙樺寲
		AudioManager mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
		/*mAudioManager.adjustStreamVolume(
	            AudioManager.STREAM_MUSIC,
	            AudioManager.ADJUST_RAISE,
	            AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);*/
		int curMaxVolume = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
		int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );		
		VolumeGetCmd volumeGetCmd = new VolumeGetCmd();
		volumeGetCmd.toClass(obj.toString());
		volumeGetCmd.setMaxVolume(curMaxVolume + "");
		volumeGetCmd.setVolume(current + "");
		
		sendCmdInfor(session, volumeGetCmd.toJson());
		//sendCmdInfor(session, FastJsonTools.createJsonString(volumeGetCmd));
	}

	/**
	*callbacks
	*/
	@Override
	public void recvGetPlayState(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		PlayStateCmd playStateCmd = new PlayStateCmd();
		playStateCmd.toClass(obj.toString());
		//playStateCmd.setPlayState(MusicActivity.mPlayerEngineImpl.getPlayState() + "");
		
		sendCmdInfor(session, playStateCmd.toJson());
		//sendCmdInfor(session, FastJsonTools.createJsonString(playStateCmd));
	}
	@Override
	public void recvSetPlayState(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		PlayStateCmd playStateCmd = new PlayStateCmd();
		playStateCmd.toClass(obj.toString());
		playStateCmd.setCmdType(CmdType.SET_PLAY_STATE);
		int state = Integer.parseInt(playStateCmd.getPlayState());
		/*if(state == PlayState.MPS_PLAYING)
		{
			MusicActivity.mPlayerEngineImpl.play();
		}
		else if(state == PlayState.MPS_PAUSE)
		{
			MusicActivity.mPlayerEngineImpl.pause();
		}
		else if(state == PlayState.MPS_STOP)
		{
			MusicActivity.mPlayerEngineImpl.stop();
		}*/
		updatePlayState(state);
		playStateCmd.setPlayState(state + "");
		sendCmdInfor(session, playStateCmd.toJson());
		//sendCmdInfor(session, FastJsonTools.createJsonString(playStateCmd));
	}
	
	@Override
	public void recvPlayNext(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		
		playNext();
		
		PlayNextCmd playNextCmd = new PlayNextCmd();
		playNextCmd.toClass(obj.toString());
		playNextCmd.setCmdType(CmdType.PLAY_NEXT_FB);
		
		sendCmdInfor(session, playNextCmd.toJson());
	}
	
	@Override
	public void recvSystemUpdate(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		
		updateSystem();
		
		SystemUpdateCmd systemUpdateCmd = new SystemUpdateCmd();
		systemUpdateCmd.toClass(obj.toString());
		systemUpdateCmd.setCmdType(CmdType.SYSTEM_UPDATE_FB);
		
		sendCmdInfor(session, systemUpdateCmd.toJson());
	}
	
	@Override
	public void recvSpeakerMusic(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		
		SpeakerMusicCmd speakerMusicCmd = new SpeakerMusicCmd();
		speakerMusicCmd.toClass(obj.toString());
		speakerMusicCmd.setCmdType(CmdType.SPEAKER_MUSIC_FB);
		
		List<MediaInfor> tempList = new ArrayList<MediaInfor>();
		/*if(speakerMusicCmd.isReqeustDir())
		{
			tempList = MusicManager.getInstance().getMusicInforDir();
			speakerMusicCmd.setTotalSize(MusicManager.getInstance().getDirTotal() + "");
		}
		else
		{
			String key = speakerMusicCmd.getRequestKey();
			String pageNum = speakerMusicCmd.getPagNum();
			String pageSize = speakerMusicCmd.getPagSize();
			
			tempList = MusicManager.getInstance().getMusicInforList(key);
			speakerMusicCmd.setTotalSize(MusicManager.getInstance().getMusicTotal() + "");
		}*/
		
		speakerMusicCmd.setMusicList(tempList);
		
		sendCmdInfor(session, speakerMusicCmd.toJson());
		//sendCmdInfor(session, FastJsonTools.createJsonString(speakerMusicCmd));
	}
	
	/**
	*callbacks
	*/
	@Override
	public void recvPlayPermission(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		
		PlayPermissionCmd cmd = new PlayPermissionCmd();
		cmd.toClass(obj.toString());
		String permission = cmd.getPermission();
		Constant.PlayPermission = permission;
		LocalDataEntity.newInstance(mContext).setPlayPermission(permission);
		
		sendCmdInfor(session, cmd.toJson());
	}
	
	/**
	 *callbacks
	 */
	@Override
	public void recvGetPlayPermission(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		
		PlayPermissionCmd cmd = new PlayPermissionCmd();
		cmd.toClass(obj.toString());
		cmd.setPermission(Constant.PlayPermission);
		//cmd.setPermission(LocalDataEntity.newInstance(mContext).getPlayPermission());
		
		sendCmdInfor(session, cmd.toJson());
	}
	
	/**
	 *callbacks
	 */
	@Override
	public void recvSetPlayRes(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		PlayResCmd cmd = new PlayResCmd();
		cmd.toClass(obj.toString());
		String playRes = cmd.getPlayRes();
		
		Constant.PlayRes = playRes;
		LocalDataEntity.newInstance(mContext).setPlayRes(playRes);
		
		cmd.setCmdType(CmdType.SET_PLAY_RES_FB);
		sendCmdInfor(session, cmd.toJson());
		updatePlayRes();
	}
	
	/**
	 *callbacks
	 */
	@Override
	public void recvGetPlayRes(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		GetPlayResCmd cmd = new GetPlayResCmd();
		cmd.toClass(obj.toString());
		cmd.setPlayRes(LocalDataEntity.newInstance(mContext).getPlayRes());
		sendCmdInfor(session, cmd.toJson());
	}
	/**
	 *callbacks
	 */
	@Override
	public void recvGetWifiList(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		GetWifiListCmd cmd = new GetWifiListCmd();
		cmd.toClass(obj.toString());
		cmd.setWifiList(Constant.wifiList);
		sendCmdInfor(session, cmd.toJson());
	}
	
	/**
	 *callbacks
	 */
	@Override
	public void recvResUpdate(IoSession session, JSONObject obj)
	{
		// TODO Auto-generated method stub
		PlayResUpdateCmd cmd = new PlayResUpdateCmd();
		cmd.toClass(obj.toString());
		sendCmdInfor(session, cmd.toJson());
		updatePlayRes();
	}
	
	private void sendCmdInfor(IoSession session, JSONObject json)
	{
		if(session != null)
			session.write(json.toString() + Constant.PKG_END);
	}
	
	private String getInforFromJson(String key, JSONObject json)
	{
		String result = "";
		try
		{
			if(json.has(key))
				result = json.getString(key);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}
