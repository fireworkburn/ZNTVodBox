package com.znt.speaker.p;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.speaker.R;
import com.znt.speaker.entity.CurPlanInfor;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.http.HttpRequestID;
import com.znt.speaker.http.callback.CurPlanCallBack;
import com.znt.speaker.http.callback.IGetCurPllanCallBack;
import com.znt.speaker.m.HttpRequestModel;
import com.znt.speaker.util.NetWorkUtils;
import com.znt.speaker.util.SystemUtils;
import com.znt.speaker.v.IHttpRequestView;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import okhttp3.Call;
import okhttp3.Request;

public class HttpPresenter
{
	
	private Activity activity = null;
	
	private HttpRequestModel httpRequestModel = null;
	private IHttpRequestView iHttpRequestView = null;
	
	public HttpPresenter(Activity activity, IHttpRequestView iHttpRequestView)
	{
		httpRequestModel = new HttpRequestModel(activity, iHttpRequestView);
		this.iHttpRequestView = iHttpRequestView;
		this.activity = activity;
		
	}
	
	public boolean isRunning(int id)
	{
		return httpRequestModel.isRuning(id);
	}
	
	private volatile boolean isCurPlanRunning = false;
	/**
	 * 获取当前播放计划
	 */
	public void getCurPlan()
	{
		if(isCurPlanRunning)
			return;
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				Map<String, String> params = new HashMap<String, String>();
				String uid = LocalDataEntity.newInstance(activity).getDeviceCode();
				if(TextUtils.isEmpty(uid))
					uid = "1";
				params.put("terminalId", uid);
				httpRequestModel.getCurPlan(params, new IGetCurPllanCallBack() 
				{
					@Override
					public void requestSuccess(CurPlanInfor curPlanInfor, int requestId) 
					{
						// TODO Auto-generated method stub
						isCurPlanRunning = false;
						iHttpRequestView.requestSuccess(curPlanInfor, requestId);
					}
					
					@Override
					public void requestStart(int requestId) 
					{
						// TODO Auto-generated method stub
						isCurPlanRunning = true;
						iHttpRequestView.requestStart(requestId);
					}
					
					@Override
					public void requestFail(int requestId) 
					{
						// TODO Auto-generated method stub
						isCurPlanRunning = false;
						iHttpRequestView.requestError(requestId);
					}
				});
			}
		}).start();
	}
	/**
	 * 注册设备
	 */
	public void register()
	{
		String devCode = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(TextUtils.isEmpty(devCode))
			httpRequestModel.register();
		else
			updateSpeakerInfor();
	}
	
	/*
	 *	更新设备信息
	 */
	public void updateSpeakerInfor()
	{
		httpRequestModel.updateDeviceInfor();
	}
	
	/**
	 * 检查更新
	 */
	public void checkUpdate()
	{
		httpRequestModel.checkUpdate();
	}
	/**
	 * 检查设备状态
	 */
	public void getDevStatus(int playSeek, String playingSong, int playingSongType)
	{
		httpRequestModel.getDevStatus(playSeek, playingSong, playingSongType);
	}
	/**
	 * 获取推送的歌曲列表
	 */
	public void getPushMusics()
	{
		httpRequestModel.getPushMusics();
	}
	
	/**
	 * 获取当前播放列表
	 * @param 
	 */
	public void getPlanMusics()
	{
		httpRequestModel.getPlanMusics();
	}
	
	/**
	 * 初始化设备
	 * @param 
	 */
	public void initTerminal()
	{
		httpRequestModel.initTerminal();
	}
	
	/**
	 * 
	 * @param 
	 */
	public void getCurTime()
	{
		httpRequestModel.getCurTime();
	}
	
	/**
	 * 
	 * @param 
	 */
	public void getCurMusicPos()
	{
		httpRequestModel.getCurMusicPos();
	}
	
	/**
	 * 更新播放位置
	 * @param playingPos
	 * @param playingMusicInfoId
	 */
	public void updateCurPos(int playingPos, String playingMusicInfoId)
	{
		httpRequestModel.updateCurPos(playingPos, playingMusicInfoId);
	}
	
}
