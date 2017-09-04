
package com.znt.diange.mina.client; 

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;

import com.znt.diange.dmc.engine.OnConnectHandler;
import com.znt.diange.entity.Constant;
import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.factory.DiangeManger;
import com.znt.diange.mina.cmd.CmdType;
import com.znt.diange.mina.cmd.DeleteSongCmd;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.cmd.DeviceSetCmd;
import com.znt.diange.mina.cmd.GetPlayPermissionCmd;
import com.znt.diange.mina.cmd.GetPlayResCmd;
import com.znt.diange.mina.cmd.GetSongInforCmd;
import com.znt.diange.mina.cmd.GetSongListCmd;
import com.znt.diange.mina.cmd.GetSongRecordCmd;
import com.znt.diange.mina.cmd.PlayCmd;
import com.znt.diange.mina.cmd.PlayNextCmd;
import com.znt.diange.mina.cmd.PlayPermissionCmd;
import com.znt.diange.mina.cmd.PlayResCmd;
import com.znt.diange.mina.cmd.PlayResUpdateCmd;
import com.znt.diange.mina.cmd.PlayStateCmd;
import com.znt.diange.mina.cmd.RegisterCmd;
import com.znt.diange.mina.cmd.SpeakerMusicCmd;
import com.znt.diange.mina.cmd.StopCmd;
import com.znt.diange.mina.cmd.VolumeGetCmd;
import com.znt.diange.mina.cmd.VolumeSetCmd;
import com.znt.diange.mina.coder.MinaCoderFactory;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.utils.MyLog;
import com.znt.diange.utils.SystemUtils;

/** 
 * @ClassName: MinaClient 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-8-11 下午3:29:29  
 */
public class MinaClient
{
	private SocketConnector connector;
    private ConnectFuture future;
    private DefaultIoFilterChainBuilder filterChain = null;
    private ClientHandler clientHandler = null;
    private IoSession session;
    private OnConnectListener onConnectListener = null;
    private ConnectThread connectThread = null;
    
    private String ip = "";
    private String WIFI_HOT_IP = "192.168.43.1";
    private int connectTime = 0;
    private final int TIME_OUT = 12;
    private boolean isChange = false;
    
    private DeviceInfor selectDeviceInfor = null;
    
    private Activity activity = null;
    
    private static MinaClient INSTANCE = null;
    
    public MinaClient()
    {
    	
    }
    
    public static MinaClient getInstance()
    {
    	if(INSTANCE == null)
    		INSTANCE = new MinaClient();
    	return INSTANCE;
    }
    
    public void setOnConnectListener(Activity activity, Handler handler)
    {
    	
    	this.onConnectListener = new OnConnectHandler(activity, handler);
    	
    	this.activity = activity;
    	
    	setHandler(activity, handler);
    }
    
    public void setHandler(Activity activity, Handler handler)
    {
    	if(clientHandler == null)
    		clientHandler = new ClientHandler();
		this.clientHandler.setHandler(activity, handler);
    }
    
    public void setConnectStop()
    {
    	stopConnect();
    	/*if(clientHandler != null)
    		clientHandler.setHandler(activity, null);
    	clientHandler = null;*/
    	this.onConnectListener = null;
    }
    
    private int getPort()
	{
		if(isChange)
			return 9997;
		else
			return 9998;
		//return 9998;
	}
    
    public void startClient() 
    {
    	connectTime = 0;
    	
    	if(connectThread == null)
    		connectThread = new ConnectThread();
    	else
    	{
    		connectThread.stop();
    		connectThread = null;
    	}
    	
    	connectThread = new ConnectThread();
    	
    	if(clientHandler == null)
		{
			clientHandler = new ClientHandler();
		}
    	
    	new Thread(connectThread).start();
    	
    }
    
    public void stopConnect()
    {
    	if(connectThread != null)
    		connectThread.stop();
    }
    
    private void initClient()
    {
    	// 创建一个socket连接
		connector = new NioSocketConnector();
        // 设置链接超时时间
        connector.setConnectTimeoutMillis(3000);
        connector.getSessionConfig().setReceiveBufferSize(10240);   // 设置接收缓冲区的大小  
        connector.getSessionConfig().setSendBufferSize(10240);// 设置输出缓冲区的大小  
        connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 3000);  //读写都空闲时间:30秒  
        connector.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, 4000);//读(接收通道)空闲时间:40秒  
        connector.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 5000);//写(发送通道)空闲时间:50秒  
        connector.getSessionConfig().setTcpNoDelay(true);  
		// 消息核心处理器
        connector.setHandler(clientHandler);
        connector.setConnectTimeoutMillis(3000);
        // 获取过滤器链
        filterChain = connector.getFilterChain();
        // 添加编码过滤器 处理乱码、编码问题
        filterChain.addLast("codec", new ProtocolCodecFilter(new MinaCoderFactory()));
        //filterChain.addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
    }
    
    public boolean isChangeAvailable(String destIp)
    {
    	if(isConnected())
    	{
    		if(ip.equals(destIp))
    			return false;
    		else
    		{
    			this.ip = destIp;
    			reConnect(activity);
    			return true;
    		}
    	}
    	else if(!TextUtils.isEmpty(destIp))
    	{
    		this.ip = destIp;
    		startClient();
    		return true;
    	}
    	return false;
    }
    public boolean isSameAddr(String destIp)
    {
    	return ip.equals(destIp);
    }
    
    private String getIp()
    {
    	String temp = "";
		if(SystemUtils.getConnectWifiSsid(activity).endsWith(Constant.UUID_TAG))//热点
			temp = WIFI_HOT_IP;
		else
		{
			temp = LocalDataEntity.newInstance(activity).getDeviceIp();
		}
		return temp;
    }
    
    private void connect()
    {
    	try
		{
            // 连接服务器，知道端口、地址
            //ip = DLNAContainer.getInstance().getIpFromDevice();
    		/*if(isWifiHot)
    			ip = WIFI_HOT_IP;
    		else
    			ip = DnsContainer.getInstance(activity).getIpFromDevice();*/
    		ip = getIp();
            if(TextUtils.isEmpty(ip))//设备丢失
            {
            	if(onConnectListener != null)
                	onConnectListener.onConnectFail(true);
            	close();
            }
            else 
            {
            	initClient();
            	
            	future = connector.connect(new InetSocketAddress(ip, getPort()));
                // 等待连接创建完成
                future.awaitUninterruptibly();
                // 获取当前session
                session = future.getSession();
                
                if(onConnectListener != null && session != null && session.isConnected())
                {
                	connectThread.stop();
                	onConnectListener.onConnectted();
                }
                selectDeviceInfor = LocalDataEntity.newInstance(activity).getDeviceInfor();
                selectDeviceInfor.setAdmin(isAdminDevice());
                session.getCloseFuture().awaitUninterruptibly();
                connector.dispose();
                
            }
		} 
    	catch (Exception e)
		{
    		
    		if(connectThread.isStop)
    			return;
			// TODO: handle exception
    		if(onConnectListener != null)
    		{
    			if(connectTime >= 15)//重连15次还没有连接上就退出
    			{
    				onConnectListener.onConnectFail(false);
    				connectThread.stop();
    			}
    			else
    			{
    	    		connectTime ++;
    	    		onConnectListener.onConnectting(connectTime);
    			}
    		}
    		isChange = !isChange;	
			MyLog.e("***********connect error-->"+e.getMessage() + "\n ip-->"+ip);
			try
			{
				Thread.sleep(1000);
			} 
    		catch (InterruptedException e1)
			{
				// TODO Auto-generated catch block
    			onConnectListener.onConnectFail(false);
				e1.printStackTrace();
			}
		}
    }
    
    private boolean isAdminDevice()
    {
    	/*if(Constant.deviceInfor != null && Constant.deviceInfor.isAdmin())
    		return true;
    	boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);
    	if(isAdmin)
    		return isAdmin;*/
    	String bindDeviceId = LocalDataEntity.newInstance(activity).getUserInfor().getBindDevices();
		String localDeviceId = LocalDataEntity.newInstance(activity).getDeviceId();
		if(!TextUtils.isEmpty(localDeviceId)
				&& !TextUtils.isEmpty(bindDeviceId)
				&& bindDeviceId.contains(localDeviceId))
		{
			return true;
		}
    	return false;
    }
    
    public DeviceInfor getDeviceInfor()
    {
    	return selectDeviceInfor;
    }
    
    public boolean isConnected()
    {
    	if(session == null)
    		return false;
    	return session.isConnected();
    }
    
    public void setAttribute(Object key, Object value) 
    {
        session.setAttribute(key, value);
    }
    
    private WriteFuture send(JSONObject obj) 
    {
    	if(clientHandler != null && clientHandler.getTimerUtils() != null)
    		clientHandler.getTimerUtils().delayTime(TIME_OUT);//开启超时检测
    	
        return session.write(obj.toString() + Constant.PKG_END);
    }
    
    public void reConnect(Activity activity)
    {
    	close();
    	activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				startClient();
			}
		});
    }
    
    @SuppressWarnings("deprecation")
	public boolean close() 
    {
    	if(clientHandler != null && clientHandler.getTimerUtils() != null)
    	{
    		clientHandler.getTimerUtils().cancel();
    		//clientHandler = null;
    	}
    	if(connectThread != null)
    		connectThread.stop();
    	if(session != null)
    	{
    		CloseFuture future = session.getCloseFuture();
            //future.awaitUninterruptibly(1000);
            connector.dispose();
            connector = null;
            
            session.close();
            session = null;
            
            future.setClosed();
            future = null;
            
            filterChain.clear();
            filterChain = null;
            
            return true;
    	}
    	else
    		return false;
    }
 
    public SocketConnector getConnector()
    {
        return connector;
    }
 
    public IoSession getSession() 
    {
        return session;
    }
    
    /************发送命令***********/
    public boolean register(Activity activity)
    {
    	//连接服务器后注册
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	RegisterCmd registerCmd = new RegisterCmd();
    	registerCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
    	registerCmd.getUserInfor().setUserIp(getLocalAddr());
    	WriteFuture wf = send(registerCmd.toJson());
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public boolean sendGetPlayList(Activity activity, int pageNum)
    {
    	//发送获取播放歌曲列表命令
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	GetSongListCmd getSongListCmd = new GetSongListCmd();
    	getSongListCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
    	getSongListCmd.setPageNum(pageNum + "");
    	getSongListCmd.setPageSize(Constant.ONE_PAGE_SIZE + "");
    	getSongListCmd.getUserInfor().setUserIp(getLocalAddr());
    	
    	WriteFuture wf = send(getSongListCmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public boolean sendGetRecordList(Activity activity, int pageNum)
    {
    	//发送获取播放歌曲列表命令
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	GetSongRecordCmd getSongListCmd = new GetSongRecordCmd();
    	getSongListCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
    	getSongListCmd.setPageNum(pageNum + "");
    	getSongListCmd.setPageSize(Constant.ONE_PAGE_SIZE + "");
    	getSongListCmd.getUserInfor().setUserIp(getLocalAddr());
    	
    	WriteFuture wf = send(getSongListCmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public boolean sendPlay(Activity activity, int type, SongInfor songInfor)
    {
    	//发送播放命令
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	PlayCmd playCmd = new PlayCmd();
    	playCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
    	String ipAddr = getLocalAddr();
    	songInfor.getUserInfor().setUserIp(ipAddr);
    	playCmd.setSongInfor(songInfor);
    	playCmd.setType(type + "");
    	playCmd.getUserInfor().setUserIp(ipAddr);
    	WriteFuture wf = send(playCmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public boolean sendGetPlayMusic(Activity activity)
    {
    	//发送获取当前播放歌曲信息命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	GetSongInforCmd getSongInforCmd = new GetSongInforCmd();
    	getSongInforCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
    	getSongInforCmd.getUserInfor().setUserIp(getLocalAddr());
    	WriteFuture wf = send(getSongInforCmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public boolean sendDelete(Activity activity, SongInfor songInfor)
    {
    	//发送播放命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	DeleteSongCmd deleteSongCmd = new DeleteSongCmd();
    	deleteSongCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
    	deleteSongCmd.getUserInfor().setUserIp(getLocalAddr());
    	WriteFuture wf = send(deleteSongCmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public boolean sendStop(Activity activity, SongInfor songInfor)
    {
    	//发送播放命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	StopCmd stopCmd = new StopCmd();
    	stopCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
    	stopCmd.setSongInfor(songInfor);
    	stopCmd.getUserInfor().setUserIp(getLocalAddr());
    	WriteFuture wf = send(stopCmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public boolean sendDeviceSet(Activity activity, DeviceInfor deviceInfor)
    {
    	//发送播放命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	DeviceSetCmd seviceSetCmd = new DeviceSetCmd();
    	seviceSetCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
    	seviceSetCmd.setDeviceInfor(deviceInfor);
    	seviceSetCmd.getUserInfor().setUserIp(getLocalAddr());
    	
    	WriteFuture wf = send(seviceSetCmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public boolean sendVolumeSet(Activity activity, String volume)
    {
    	//发送设置音量命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	VolumeSetCmd volumeSetCmd = new VolumeSetCmd();
		volumeSetCmd.setDeviceId(LocalDataEntity.newInstance(activity).getDeviceInfor().getId());
		volumeSetCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
		volumeSetCmd.setVolume(volume);
		volumeSetCmd.getUserInfor().setUserIp(getLocalAddr());
    	WriteFuture wf = send(volumeSetCmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    public boolean sendVolumeGet(Activity activity)
    {
    	//发送设置音量命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	VolumeGetCmd volumeGetCmd = new VolumeGetCmd();
    	volumeGetCmd.setDeviceId(LocalDataEntity.newInstance(activity).getDeviceInfor().getId());
    	volumeGetCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
    	volumeGetCmd.getUserInfor().setUserIp(getLocalAddr());
    	WriteFuture wf = send(volumeGetCmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    public boolean sendPlayStateSet(Activity activity, int playState)
    {
    	//发送设置音量命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	PlayStateCmd playStateCmd = new PlayStateCmd();
    	playStateCmd.setDeviceId(LocalDataEntity.newInstance(activity).getDeviceInfor().getId());
    	playStateCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
    	playStateCmd.setCmdType(CmdType.SET_PLAY_STATE);
    	playStateCmd.setPlayState(playState + "");
    	playStateCmd.getUserInfor().setUserIp(getLocalAddr());
    	WriteFuture wf = send(playStateCmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    public boolean sendPlayStateGet(Activity activity)
    {
    	//发送设置音量命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	PlayStateCmd playStateCmd = new PlayStateCmd();
    	playStateCmd.setDeviceId(LocalDataEntity.newInstance(activity).getDeviceInfor().getId());
    	playStateCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
    	playStateCmd.getUserInfor().setUserIp(getLocalAddr());
    	WriteFuture wf = send(playStateCmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    public boolean sendPlayNext(Activity activity)
    {
    	//发送切歌命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	PlayNextCmd playNextCmd = new PlayNextCmd();
    	WriteFuture wf = send(playNextCmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    public boolean sendGetSpeakerMusic(Activity activity, String key)
    {
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	SpeakerMusicCmd speakerMusic = new SpeakerMusicCmd();
    	if(key == null)
    		speakerMusic.setRequestType(SpeakerMusicCmd.RequestDir);
    	else
    	{
    		speakerMusic.setRequestType(SpeakerMusicCmd.RequestMedia);
    		speakerMusic.setRequestKey(key);
    	}
    	/*speakerMusic.setPageNum(pageNum + "");
    	speakerMusic.setPageSize(pageSize + "");*/
    	WriteFuture wf = send(speakerMusic.toJson());
    	
    	/*SongInfor songInfor = new SongInfor();
    	songInfor.setMediaName("蔡依林&陶喆-今天你要嫁给我(Live)");
    	songInfor.setMediaUrl("/storage/emulated/0/music_test/星语心愿（张柏芝）.ape");
    	PlayCmd playCmd = new PlayCmd();
    	playCmd.setUserInfor(LocalDataEntity.newInstance(activity).getUserInfor());
    	playCmd.setSongInfor(songInfor);
    	playCmd.getUserInfor().setUserIp(getLocalAddr());
    	playCmd.setType(0 + "");
    	WriteFuture wf = send(playCmd.toJson());*/
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public boolean sendSetPermission(Activity activity, String permission)
    {
    	//发送切歌命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	PlayPermissionCmd cmd = new PlayPermissionCmd();
    	cmd.setPermission(permission);
    	WriteFuture wf = send(cmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    public boolean sendGetPermission(Activity activity)
    {
    	//发送切歌命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	GetPlayPermissionCmd cmd = new GetPlayPermissionCmd();
    	WriteFuture wf = send(cmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public boolean sendUpdateSpeakerMusic()
    {
    	//发送更新曲库命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	PlayResUpdateCmd cmd = new PlayResUpdateCmd();
    	WriteFuture wf = send(cmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public boolean sendSpeakerResCmd(String resType)
    {
    	//发送更新曲库命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	PlayResCmd cmd = new PlayResCmd();
    	cmd.setPlayRes(resType);
    	WriteFuture wf = send(cmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public boolean sendGetSpeakerResCmd()
    {
    	//发送获取曲库模式命令
    	
    	if(session == null || !session.isConnected())
    	{
    		reConnect(activity);
    	}
    	
    	if(session == null)
    		return false;
    	
    	GetPlayResCmd cmd = new GetPlayResCmd();
    	WriteFuture wf = send(cmd.toJson());
    	
    	if(wf != null)
    		return true;
    	else
    		return false;
    }
    
    public String getLocalAddr()
    {
    	if(session != null)
    		return session.getLocalAddress().toString();
    	else
    		return "";
    }
    
    public interface OnConnectListener
    {
    	public void onConnectStart();
    	public void onConnectting(int connectTime);
    	public void onConnectted();
    	public void onConnectFail(boolean isDeviceRemove);
    }
    
    private class ConnectThread implements Runnable
    {

    	private boolean isStop = false;
    	public void stop()
    	{
    		isStop = true;
    	}
    	
    	public boolean isStop()
    	{
    		return isStop;
    	}
    	
		/**
		*callbacks
		*/
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			if(onConnectListener != null)
				onConnectListener.onConnectStart();
			while(!isStop)
			{
				connect();
			}
		}
    }
}
 
