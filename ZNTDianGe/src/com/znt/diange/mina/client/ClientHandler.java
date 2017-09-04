package com.znt.diange.mina.client;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;

import com.znt.diange.entity.Constant;
import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.mina.cmd.CmdType;
import com.znt.diange.mina.cmd.DeleteSongCmd;
import com.znt.diange.mina.cmd.DeviceSetCmd;
import com.znt.diange.mina.cmd.GetPlayResCmd;
import com.znt.diange.mina.cmd.GetSongInforCmd;
import com.znt.diange.mina.cmd.GetSongListCmd;
import com.znt.diange.mina.cmd.PlayCmd;
import com.znt.diange.mina.cmd.PlayErrorCmd;
import com.znt.diange.mina.cmd.PlayNextCmd;
import com.znt.diange.mina.cmd.PlayPermissionCmd;
import com.znt.diange.mina.cmd.PlayResCmd;
import com.znt.diange.mina.cmd.PlayStateCmd;
import com.znt.diange.mina.cmd.RegisterCmd;
import com.znt.diange.mina.cmd.SpeakerMusicCmd;
import com.znt.diange.mina.cmd.StopCmd;
import com.znt.diange.mina.cmd.UpdateCmd;
import com.znt.diange.mina.cmd.VolumeGetCmd;
import com.znt.diange.mina.cmd.VolumeSetCmd;
import com.znt.diange.utils.MyLog;
import com.znt.diange.utils.TimerUtils;
import com.znt.diange.utils.TimerUtils.OnTimeOverListner;
import com.znt.diange.utils.ViewUtils;


/** 
 * @ClassName: ServerHandler 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-8-11 上午10:05:33  
 */
public class ClientHandler extends IoHandlerAdapter 
{

	private Handler handler = null;
	
	private TimerUtils timerUtils = null;
	
	private Activity activity = null;
	
	public void setHandler(Activity activity, Handler handler)
	{
		this.handler = handler;
		this.activity = activity;
	}
	
	public ClientHandler()
	{
		timerUtils = new TimerUtils(new OnTimeOverListner()
		{
			@Override
			public void OnTimeOver()
			{
				// TODO Auto-generated method stub
				onTimeOut();
			}
		});
	}
	
	public TimerUtils getTimerUtils()
	{
		if(timerUtils == null)
			timerUtils = new TimerUtils(new OnTimeOverListner()
			{
				@Override
				public void OnTimeOver()
				{
					// TODO Auto-generated method stub
					onTimeOut();
				}
			});
		return timerUtils;
	}
	public void cancelTimer()
	{
		if(timerUtils != null)
		{
			timerUtils.cancel();
			timerUtils = null;
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	public void sessionCreated(IoSession session) throws Exception
	{
		//MyLog.e("**********************sessionCreated-->" + session.getRemoteAddress());
		// TODO Auto-generated method stub
		super.sessionCreated(session);
	}
	
	// 当一个客端端连结到服务器后
    @Override
    public void sessionOpened(IoSession session) throws Exception 
    {
    	MyLog.e("**********************sessionOpened-->" + session.getRemoteAddress());
    	super.sessionOpened(session);
    }
    
    /**
    *callbacks
    */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
    		throws Exception
    {
    	MyLog.e("**********************exceptionCaught-->" + cause.getMessage());
    	// TODO Auto-generated method stub
    	onMinaConnectError(MinaErrorType.EXCEPTION);
    	super.exceptionCaught(session, cause);
    }
    
    // 当一个客户端关闭时
    @Override
    public void sessionClosed(IoSession session) throws Exception 
    {
    	onMinaConnectError(MinaErrorType.CLOSED);
        MyLog.e("**********************sessionClosed-->" + session.getRemoteAddress());
        super.sessionClosed(session);
    }
    
    /**
    *callbacks
    */
    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
    		throws Exception
    {
    	onMinaConnectError(MinaErrorType.IDLE);
    	MyLog.e("**********************sessionIdle-->" + session.getRemoteAddress());
    	// TODO Auto-generated method stub
    	super.sessionIdle(session, status);
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
    
    // 当服务器端发送的消息到达时:
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
    	JSONObject recvCmd = null;
    	if(message != null && message instanceof String)
		{
    		String recvStr = (String)message;
    		if(TextUtils.isEmpty(recvStr) || !recvStr.contains(Constant.PKG_END) 
    				)
			{
				return;
			}
    		recvStr = recvStr.substring(0, (recvStr.length() - Constant.PKG_END.length()));
			recvCmd = new JSONObject((String)message);
			//String head = getInforFromJson("head", recvCmd);
			
		}
    	else
    		return;
    	
    	String cmdType = getInforFromJson("cmdType", recvCmd);
    	if(cmdType.equals(CmdType.REGISTER_FB))
    	{
    		timerUtils.cancel();
    		registerResult(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.GET_PLAY_LIST_FB))
    	{
    		timerUtils.cancel();
    		//获取播放列表
    		recvGetPlayList(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.GET_PLAY_MUSIC_INFOR_FB))
    	{
    		timerUtils.cancel();
    		//接收播放结果
			recvPlayMusicInfor(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.PLAY_FB))
    	{
    		timerUtils.cancel();
    		//接收播放结果
			recvPlayResult(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.DELETE_SONG_FB))
    	{
    		timerUtils.cancel();
    		//接收删除结果命令
			recvDeleteSong(recvCmd);
			Constant.isSongUpdate = true;
    	}
    	else if(cmdType.equals(CmdType.UPDATE_INFOR))
    	{
    		timerUtils.cancel();
    		//接收更新命令
    		recvUpdateInfor(recvCmd);
    		
    		String updateType = getInforFromJson("updateType", recvCmd);
    		if(!updateType.equals("1"))
    			Constant.isSongUpdate = true;
    		else
    			Constant.isPlayUpdate = true;
    	}
    	else if(cmdType.equals(CmdType.STOP_FB))
    	{
    		timerUtils.cancel();
    		//接收更新命令
    		recvStopResult(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.SET_DEVICE_FB))
    	{
    		timerUtils.cancel();
    		//接收设备编辑反馈命令
    		recvDeviceEditResult(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.SET_DEVICE_VOLUM))
    	{
    		timerUtils.cancel();
    		//接收设备音量调节反馈命令
    		recvVolumeSetResult(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.GET_DEVICE_VOLUM))
    	{
    		timerUtils.cancel();
    		//接收设备音量调节反馈命令
    		recvVolumeGetResult(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.GET_PLAY_STATE) || cmdType.equals(CmdType.SET_PLAY_STATE))
    	{
    		timerUtils.cancel();
    		//接收播放状态
    		recvGetPlayStateResult(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.SET_PLAY_STATE))
    	{
    		timerUtils.cancel();
    		//设置播放状态
    		recvSetPlayStateResult(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.PLAY_NEXT_FB))
    	{
    		timerUtils.cancel();
    		Constant.isSongUpdate = true;
    		//切换下一首反馈
    		recvPlayNextResult(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.SPEAKER_MUSIC_FB))
    	{
    		timerUtils.cancel();
    		//获取音响本地的歌曲列表
    		recvSpeakerMusicResult(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.PLAY_ERROR))
    	{
    		timerUtils.cancel();
    		//获取点播歌曲播放失败命令
    		recvPalyError(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.PLAY_PERMISSION))
    	{
    		timerUtils.cancel();
    		//获取播放权限命令
    		recvPalyPermission(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.GET_PLAY_PERMISSION))
    	{
    		timerUtils.cancel();
    		//获取播放权限命令
    		recvGetPalyPermission(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.SET_PLAY_RES_FB))
    	{
    		timerUtils.cancel();
    		//获取设置播放曲库命令
    		recvSetPalyRes(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.GET_PLAY_RES))
    	{
    		timerUtils.cancel();
    		//获取播放曲库模式
    		recvGetPlayRes(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.RES_UPDATE))
    	{
    		timerUtils.cancel();
    		//获取曲库更新命令
    		recvSetPalyResUpdate(recvCmd);
    	}
    	else if(cmdType.equals(CmdType.ERROR))
    	{
    		timerUtils.cancel();
    		//接收错误命令
    		recvError(getInforFromJson("error", recvCmd));
    	}
    }
    
    public enum MinaErrorType
    {
    	CLOSED,
    	IDLE,
    	EXCEPTION
    }
    
    public static final int RECV_GET_PLAY_LIST = 100;
    public static final int RECV_PLAY_RESULT = 101;
    public static final int RECV_PLAY_MUSIC_INFOR = 102;
    public static final int RECV_DELETE_SONG = 103;
    public static final int RECV_UPDATE_INFOR = 104;
    public static final int RECV_STOP_RESULT = 105;
    public static final int RECV_DEVICE_EDIT_RESULT = 106;
    public static final int MINA_CONNECT_ERROR = 107;
    public static final int TIME_OUT = 108;
    public static final int RECV_VOLUME_SET_RESULT = 109;
    public static final int RECV_VOLUME_GET_RESULT = 110;
    public static final int RECV_GET_PLAY_STATE = 111;
    public static final int RECV_SET_PLAY_STATE = 112;
    public static final int RECV_PLAY_NEXT_FB = 113;
    public static final int RECV_REGISTER_FB = 114;
    public static final int RECV_SPEAKER_MUSIC_FB = 115;
    public static final int RECV_PLAY_ERROR = 116;
    public static final int RECV_PLAY_PERMISSION = 117;
    public static final int RECV_ERROR = 118;
    public static final int RECV_PALY_RES = 119;
    public static final int RECV_PALY_RES_UPDATE = 120;
    public static final int RECV_GET_PALY_RES = 121;
    public static final int RECV_GET_PLAY_PERMISSION = 122;
    
    private void registerResult(JSONObject recvCmd)
    {
    	RegisterCmd registerCmd = new RegisterCmd();
    	registerCmd.toClass(recvCmd.toString());
    	
    	boolean admin = registerCmd.getUserInfor().isAdmin();
		LocalDataEntity.newInstance(activity).setAdmin(admin);
		
		LocalDataEntity.newInstance(activity).setDeviceInfor(registerCmd.getDeviceInfor());
		
		String permission = registerCmd.getPermission();
		LocalDataEntity.newInstance(activity).setPlayPermission(permission);
		
		if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_REGISTER_FB);
    }
    private void recvGetPlayList(JSONObject recvCmd)
    {
    	GetSongListCmd cmdInfor = new GetSongListCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_GET_PLAY_LIST, cmdInfor);
    }
    private void recvPlayResult(JSONObject recvCmd)
    {
    	PlayCmd cmdInfor = new PlayCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	String permission = cmdInfor.getPermission();
		LocalDataEntity.newInstance(activity).setPlayPermission(permission);
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_PLAY_RESULT, cmdInfor);
    }
    private void recvPlayMusicInfor(JSONObject recvCmd)
    {
    	GetSongInforCmd cmdInfor = new GetSongInforCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_PLAY_MUSIC_INFOR, cmdInfor);
    }
    private void recvDeleteSong(JSONObject recvCmd)
    {
    	DeleteSongCmd cmdInfor = new DeleteSongCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_DELETE_SONG, cmdInfor);
    }
    private void recvUpdateInfor(JSONObject recvCmd)
    {
    	UpdateCmd cmdInfor = new UpdateCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_UPDATE_INFOR, cmdInfor);
    }
    private void recvStopResult(JSONObject recvCmd)
    {
    	StopCmd cmdInfor = new StopCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_STOP_RESULT, cmdInfor);
    }
    private void recvDeviceEditResult(JSONObject recvCmd)
    {
    	DeviceSetCmd cmdInfor = new DeviceSetCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	LocalDataEntity.newInstance(activity).updateDeviceName(cmdInfor.getDeviceInfor().getName());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_DEVICE_EDIT_RESULT, cmdInfor);
    }
    private void recvVolumeSetResult(JSONObject recvCmd)
    {
    	VolumeSetCmd cmdInfor = new VolumeSetCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_VOLUME_SET_RESULT, cmdInfor);
    }
    private void recvVolumeGetResult(JSONObject recvCmd)
    {
    	VolumeGetCmd cmdInfor = new VolumeGetCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_VOLUME_GET_RESULT, cmdInfor);
    }
    private void recvGetPlayStateResult(JSONObject recvCmd)
    {
    	PlayStateCmd cmdInfor = new PlayStateCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_GET_PLAY_STATE, cmdInfor);
    }
    private void recvSetPlayStateResult(JSONObject recvCmd)
    {
    	PlayStateCmd cmdInfor = new PlayStateCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_SET_PLAY_STATE, cmdInfor);
    }
    private void recvPlayNextResult(JSONObject recvCmd)
    {
    	PlayNextCmd cmdInfor = new PlayNextCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_PLAY_NEXT_FB, cmdInfor);
    }
    private void recvSpeakerMusicResult(JSONObject recvCmd)
    {
    	SpeakerMusicCmd cmdInfor = new SpeakerMusicCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_SPEAKER_MUSIC_FB, cmdInfor);
    }
    private void recvPalyError(JSONObject recvCmd)
    {
    	PlayErrorCmd cmdInfor = new PlayErrorCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_PLAY_ERROR, cmdInfor);
    }
    private void recvPalyPermission(JSONObject recvCmd)
    {
    	PlayPermissionCmd cmdInfor = new PlayPermissionCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	LocalDataEntity.newInstance(activity).setPlayPermission(cmdInfor.getPermission());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_PLAY_PERMISSION);
    }
    private void recvGetPalyPermission(JSONObject recvCmd)
    {
    	PlayPermissionCmd cmdInfor = new PlayPermissionCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	LocalDataEntity.newInstance(activity).setPlayPermission(cmdInfor.getPermission());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_GET_PLAY_PERMISSION);
    }
    private void recvGetPlayRes(JSONObject recvCmd)
    {
    	GetPlayResCmd cmdInfor = new GetPlayResCmd();
    	cmdInfor.toClass(recvCmd.toString());
    	LocalDataEntity.newInstance(activity).setPlayRes(cmdInfor.getPlayRes());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_GET_PALY_RES);
    }
    private void recvSetPalyRes(JSONObject recvCmd)
    {
    	/*PlayResCmd cmdInfor = new PlayResCmd();
    	cmdInfor.toClass(recvCmd.toString());*/
    	//LocalDataEntity.newInstance(activity).setPlayPermission(cmdInfor.getPermission());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_PALY_RES);
    }
    private void recvSetPalyResUpdate(JSONObject recvCmd)
    {
    	/*PlayResCmd cmdInfor = new PlayResCmd();
    	cmdInfor.toClass(recvCmd.toString());*/
    	//LocalDataEntity.newInstance(activity).setPlayPermission(cmdInfor.getPermission());
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_PALY_RES_UPDATE);
    }
    private void recvError(String error)
    {
    	if(handler != null)
    		ViewUtils.sendMessage(handler, RECV_ERROR, error);
    }
    private void onMinaConnectError(MinaErrorType type)
    {
    	timerUtils.cancel();
    	if(handler != null)
    		ViewUtils.sendMessage(handler, MINA_CONNECT_ERROR, type);
    }
    private void onTimeOut()
    {
    	if(handler != null)
    		ViewUtils.sendMessage(handler, TIME_OUT);
    }
}
 
