package com.znt.speaker.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.http.HttpHelper;
import com.znt.speaker.http.HttpType;
import com.znt.speaker.util.NetWorkUtils;
import com.znt.speaker.util.SystemUtils;
import com.znt.speaker.util.UrlUtils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

public class HttpFactory 
{
	private Context activity = null;
	
	private Handler handler = null;
	
	public HttpFactory(Context activity)
	{
		this.activity = activity;
	}
	
	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}
	
	public void checkUpdate()
	{
		
	}
	
	public void getDeviceStatus()
	{
		String id = LocalDataEntity.newInstance(activity).getDeviceCode();
		/*if(TextUtils.isEmpty(id))
		{
			register();
			return;
		}*/
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		
		/*if(MusicActivity.mPlayerEngineImpl != null)//涓婃姤褰撳墠鎾斁杩涘害
			params.add(new BasicNameValuePair("playSeek", MusicActivity.mPlayerEngineImpl.getCurPosition() + ""));
		if(MusicActivity.mPlayerEngineImpl != null)
		{
			SongInfor songInfor = MusicActivity.mPlayerEngineImpl.getSongInfor();
			String mediaName = "";
			if(songInfor != null)
			{
				mediaName = songInfor.getMediaName();
				if(mediaName == null)
					mediaName = "";
				params.add(new BasicNameValuePair("playingSong", mediaName));
				params.add(new BasicNameValuePair("playingSongType", songInfor.getPlayType()));
			}
		}*/
		
		if(SystemUtils.isNetConnected(activity))
		{
			String netType = "";
			String ip = SystemUtils.getIP();
			if(NetWorkUtils.checkEthernet(activity))
				netType = "鏈夌嚎锛�";
			else
				netType = "鏃犵嚎锛�";
			String space = SystemUtils.getAvailabeMemorySize();
			params.add(new BasicNameValuePair("netInfo", space + "   " + netType + ip + " " + SystemUtils.getScreenOritation(activity)));
		}
		
		//params.add(new BasicNameValuePair("playingPos", LocalDataEntity.newInstance(activity).getMusicIndex() + ""));
		httpHelper.startHttp(HttpType.GetDeviceStatus, params);
	}
	
	/**
	 * 闊冲搷娉ㄥ唽
	 */
	public synchronized void register()
	{
		//String curSsid = SystemUtils.getConnectWifiSsid(activity);
		/*if(TextUtils.isEmpty(curSsid) || curSsid.contains("unknown"))//鏈繛鎺ョ綉缁�
			return;*/
		/*if(SystemUtils.getConnectWifiSsid(activity).endsWith(Constant.UUID_TAG))//杩炴帴鐨勬槸鐑偣
			return;*/
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		httpHelper.startHttp(HttpType.Register, null);
	}
	
	/**
	 * 鎵ｉ櫎閲戝竵
	 * @param songInfor
	 */
	public void coinRemove(SongInfor songInfor)
	{
		String coin = songInfor.getCoin() + "";
		String tranId = songInfor.getTrandId();
		if(TextUtils.isEmpty(coin) || coin.equals("0") || TextUtils.isEmpty(tranId))
			return;
		String userId = songInfor.getUserInfor().getUserId();
		
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", userId));
		params.add(new BasicNameValuePair("amount", coin));
		params.add(new BasicNameValuePair("tranId", tranId));
		
		httpHelper.startHttp(HttpType.CoinRemove, params);
	}
	
	/**
	 * 鍙栨秷鍐荤粨鐨勯噾甯�
	 * @param songInfor
	 */
	public void coinFreezeCancel(SongInfor songInfor)
	{
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String coin = songInfor.getCoin() + "";
		String tranId = songInfor.getTrandId();
		if(TextUtils.isEmpty(coin) || coin.equals("0") || TextUtils.isEmpty(tranId))
			return;
		String userId = songInfor.getUserInfor().getUserId();
		params.add(new BasicNameValuePair("memberId", userId));
		params.add(new BasicNameValuePair("amount", coin));
		params.add(new BasicNameValuePair("tranId", tranId));
		
		httpHelper.startHttp(HttpType.CoinFreezeCancel, params);
	}
	
	/**
	 * 鑾峰彇闊冲搷鐨勬挱鏀惧垪琛�
	 * @param songInfor
	 */
	public void getPushMusics()
	{
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(!TextUtils.isEmpty(terminalId))
		{
			params.add(new BasicNameValuePair("terminalId", terminalId));
			params.add(new BasicNameValuePair("pushFlag", "1"));
			
			httpHelper.startHttp(HttpType.GetPushMusics, params);
		}
	}
	/**
	 * 鑾峰彇闊冲搷鐨勬挱鏀惧垪琛�
	 * @param songInfor
	 */
	public void getPlayList()
	{
		/*HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(!TextUtils.isEmpty(terminalId))
		{
			params.add(new BasicNameValuePair("terminalId", terminalId));
			
			httpHelper.startHttp(HttpType.GetPlayList, params);
		}*/
		getPlanMusics();
	}
	
	/**
	 * 涓婁紶鐐规挱鐨勬瓕鏇茶褰�
	 * @param songInfor
	 */
	public void uploadSongRecord(SongInfor songInfor)
	{
		if(songInfor == null || songInfor.getUserInfor() == null)
			return;
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(!TextUtils.isEmpty(terminalId))
		{
			params.add(new BasicNameValuePair("terminalId", terminalId));
			params.add(new BasicNameValuePair("user", songInfor.getUserInfor().getUserId()));
			params.add(new BasicNameValuePair("musicId", songInfor.getMediaId()));
			params.add(new BasicNameValuePair("musicName", songInfor.getMediaName()));
			params.add(new BasicNameValuePair("musicSing", songInfor.getArtist()));
			params.add(new BasicNameValuePair("musicUrl", UrlUtils.encodeUrl(songInfor.getMediaUrl())));
			if(songInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_NET))
				params.add(new BasicNameValuePair("musicFrom", "2"));
			else 
				params.add(new BasicNameValuePair("musicFrom", "0"));
			
			httpHelper.startHttp(HttpType.UploadSongRecord, params);
		}
	}
	/**
	 * 
	 * @param 
	 */
	public void getPlanMusics()
	{
		
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(!TextUtils.isEmpty(terminalId))// && !TextUtils.isEmpty(planId))
		{
			params.add(new BasicNameValuePair("id", terminalId));
			/*if(!TextUtils.isEmpty(planId))
				params.add(new BasicNameValuePair("planId", planId));*/
			
			httpHelper.startHttp(HttpType.GetPlanMusics, params);
		}
	}
	
	public void getCurPlan()
	{
		//String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		String uid = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(TextUtils.isEmpty(uid))
			return;
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("terminalId", uid));
		//params.add(new BasicNameValuePair("status", "0"));//0-鏈夋晥璁″垝锛堟湁鐩掑瓙鍦ㄤ娇鐢級涓嶅～榛樿涓�0 1-鍘嗗彶璁″垝 
		
		httpHelper.startHttp(HttpType.GetCurPlan, params);
	}
	
	/**
	 * 
	 * @param 
	 */
	public void initTerminal()
	{
		
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(!TextUtils.isEmpty(terminalId))
		{
			params.add(new BasicNameValuePair("id", terminalId));
			httpHelper.startHttp(HttpType.InitTerminal, params);
		}
	}
	/**
	 * 
	 * @param 
	 */
	public void getCurTime()
	{
		
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		httpHelper.startHttp(HttpType.GetCurTime, params);
	}
	/**
	 * 
	 * @param 
	 */
	public void getCurPos()
	{
		
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceCode();
		params.add(new BasicNameValuePair("id", terminalId));
		httpHelper.startHttp(HttpType.GetCurPos, params);
	}
	public void updateCurPos(int playingPos, String playingMusicInfoId)
	{
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceCode();
		params.add(new BasicNameValuePair("id", terminalId));
		params.add(new BasicNameValuePair("playingPos", playingPos + ""));
		params.add(new BasicNameValuePair("playingMusicInfoId", playingMusicInfoId));
		httpHelper.startHttp(HttpType.UpdateCurPos, params);
	}
	/**
	 * 缁戝畾闊冲搷
	 * @param songInfor
	 *//*
	public void bindSpeaker()
	{
		if(songInfor == null || songInfor.getUserInfor() == null)
			return;
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(!TextUtils.isEmpty(terminalId))
		{
			params.add(new BasicNameValuePair("terminalId", terminalId));
			params.add(new BasicNameValuePair("id ", LocalDataEntity.newInstance(activity).getDeviceInfor().get));
			params.add(new BasicNameValuePair("musicId", songInfor.getMediaId()));
			params.add(new BasicNameValuePair("musicName", songInfor.getMediaName()));
			params.add(new BasicNameValuePair("musicSing", songInfor.getArtist()));
			params.add(new BasicNameValuePair("musicUrl", UrlUtils.encodeUrl(songInfor.getMediaUrl())));
			if(songInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_NET))
				params.add(new BasicNameValuePair("musicFrom", "2"));
			else 
				params.add(new BasicNameValuePair("musicFrom", "0"));
			
			httpHelper.startHttp(HttpType.BindSpeaker, params);
		}
	}*/
	
}
