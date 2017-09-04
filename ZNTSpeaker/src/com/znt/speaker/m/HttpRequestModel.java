package com.znt.speaker.m;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.mina.entity.UpdateInfor;
import com.znt.speaker.R;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.CurPlanInfor;
import com.znt.speaker.entity.CurPlanSubInfor;
import com.znt.speaker.entity.DeviceStatusInfor;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.http.HttpAPI;
import com.znt.speaker.http.HttpRequestID;
import com.znt.speaker.http.callback.CheckUpdateCallBack;
import com.znt.speaker.http.callback.DevStatusCallBack;
import com.znt.speaker.http.callback.GetCurMusicPosCallBack;
import com.znt.speaker.http.callback.GetCurTimeCallBack;
import com.znt.speaker.http.callback.GetPlanMusicCallBack;
import com.znt.speaker.http.callback.GetPushMusicCallBack;
import com.znt.speaker.http.callback.IGetCurPllanCallBack;
import com.znt.speaker.http.callback.InitTerminalCallBack;
import com.znt.speaker.http.callback.ListDeviceCallBack;
import com.znt.speaker.http.callback.RegisterCallBack;
import com.znt.speaker.util.NetWorkUtils;
import com.znt.speaker.util.SystemUtils;
import com.znt.speaker.util.UrlUtils;
import com.znt.speaker.v.IHttpRequestView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import okhttp3.Call;
import okhttp3.CookieJar;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("UseSparseArrays")
public class HttpRequestModel extends HttpAPI
{
	
	private final String mBaseUrl = "";
	private final int MAX_PAGE_SIZE = 300;
	private Activity activity = null;
	private IHttpRequestView iHttpRequestView = null;
	
	private volatile HashMap<Integer, Boolean> runStatus = new HashMap<Integer, Boolean>();
	
	public HttpRequestModel(Activity activity, IHttpRequestView iHttpRequestView)
	{
		this.activity = activity;
		this.iHttpRequestView = iHttpRequestView;
	}
	
	private void requestStart(final int requestId)
	{
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				iHttpRequestView.requestStart(requestId);
			}
		});
	}
	private void requestError(final int requestId)
	{
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				iHttpRequestView.requestError(requestId);
			}
		});
	}
	private void requestSuccess(final Object obj, final int requestId)
	{
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				iHttpRequestView.requestSuccess(obj, requestId);
			}
		});
	}
	private void requestNetWorkError()
	{
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				iHttpRequestView.requestNetWorkError();
			}
		});
	}
	
	private void setRunStatusDoing(int key)
	{
		runStatus.put(key, true);
	}
	private void setRunStatusFinish(int key)
	{
		runStatus.put(key, false);
	}
	public boolean isRuning(int key)
	{
		if(runStatus.containsKey(key))
			return runStatus.get(key);
		return false;
	}
	/**
	 * 注册
	 * @param params
	 */
	public void register()
    {
		int requestId = HttpRequestID.REGISTER;
		if(isRuning(requestId))
			return;
		setRunStatusDoing(requestId);
		
		Map<String, String> params = new HashMap<String, String>();
		
		DeviceInfor infor = LocalDataEntity.newInstance(activity).getDeviceInfor();
		String name = infor.getName();
		String code = infor.getId();
		String wifiName = infor.getWifiName();
		String wifiPassword = infor.getWifiPwd();
		String longitude = LocalDataEntity.newInstance(activity).getDeviceLon();
		String latitude = LocalDataEntity.newInstance(activity).getDeviceLat();
		String addr = LocalDataEntity.newInstance(activity).getDeviceAddr();
		
		try 
		{
			params.put("softVersion", SystemUtils.getPkgInfo(activity).versionCode + "");
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//params.add(new BasicNameValuePair("hardVersion", Build.VERSION.RELEASE));
		params.put("volume", SystemUtils.getCurrentVolume(activity) + "");
		if(!TextUtils.isEmpty(name))
			params.put("name", name);
		params.put("code", code);
		if(!TextUtils.isEmpty(infor.getActCode()))
			params.put("activateCode", infor.getActCode());
		params.put("ip", SystemUtils.getIP());
		params.put("wifiMac", SystemUtils.getWifiBSsid(activity));
		if(!TextUtils.isEmpty(wifiName))
		params.put("wifiName", wifiName);
		//if(!TextUtils.isEmpty(wifiPassword))
			params.put("wifiPassword", wifiPassword);
		if(!TextUtils.isEmpty(longitude))
			params.put("longitude", longitude);
		if(!TextUtils.isEmpty(latitude))
			params.put("latitude", latitude);
		/*params.add(new BasicNameValuePair("longitude", "114.01666"));
		params.add(new BasicNameValuePair("latitude", "22.538146"));*/
		if(!TextUtils.isEmpty(addr))
			params.put("address", addr);
		
		OkHttpUtils//
    	.get()//
    	.url(REGISTER)//
    	.id(requestId)
    	.params(params)//
    	.build()//
    	.execute(new RegisterCallBack()//
    	{
    		@Override
    		public void onError(Call call, Exception e, int requestId)
    		{
    			//mTv.setText("onError:" + e.getMessage());
    			requestError(requestId);
    			setRunStatusFinish(requestId);
    		}
    		
    		@Override
    		public void onResponse(String response, int requestId)
    		{
    			//mTv.setText("onResponse:" + response);
    			if(response == null)
        		{
					requestError(requestId);
					return;
        		}
    			LocalDataEntity.newInstance(activity).setDeviceCode(response);
    			requestSuccess(response, requestId);
    			setRunStatusFinish(requestId);
    		}
    		
    		@Override
    		public void onBefore(Request request, int requestId)
    		{
    			// TODO Auto-generated method stub
    			super.onBefore(request, requestId);
    			requestStart(requestId);
    		}
    		
    		@Override
    		public String parseNetworkResponse(Response response, int requestId) throws IOException 
    		{
    			// TODO Auto-generated method stub
    			return super.parseNetworkResponse(response, requestId);
    		}
    		
    	});
    }
	
	/**
	 * 更新设备信息
	 * @param params
	 */
	public void updateDeviceInfor()
	{
		int requestId = HttpRequestID.UPDATE_SPEAKER_INFOR;
		if(isRuning(requestId))
			return;
		setRunStatusDoing(requestId);
		
		Map<String, String> params = new HashMap<String, String>();
		
		DeviceInfor infor = LocalDataEntity.newInstance(activity).getDeviceInfor();
		String name = infor.getName();
		String code = infor.getId();
		String wifiName = infor.getWifiName();
		String wifiPassword = infor.getWifiPwd();
		String longitude = LocalDataEntity.newInstance(activity).getDeviceLon();
		String latitude = LocalDataEntity.newInstance(activity).getDeviceLat();
		String addr = LocalDataEntity.newInstance(activity).getDeviceAddr();
		String devCode = LocalDataEntity.newInstance(activity).getDeviceCode();
		try 
		{
			params.put("softVersion", SystemUtils.getPkgInfo(activity).versionCode + "");
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//params.add(new BasicNameValuePair("hardVersion", Build.VERSION.RELEASE));
		params.put("volume", SystemUtils.getCurrentVolume(activity) + "");
		if(!TextUtils.isEmpty(name))
			params.put("name", name);
		params.put("code", code);
		if(!TextUtils.isEmpty(infor.getActCode()))
			params.put("activateCode", infor.getActCode());
		params.put("ip", SystemUtils.getIP());
		params.put("wifiMac", SystemUtils.getWifiBSsid(activity));
		if(!TextUtils.isEmpty(wifiName))
		params.put("wifiName", wifiName);
		//if(!TextUtils.isEmpty(wifiPassword))
			params.put("wifiPassword", wifiPassword);
		if(!TextUtils.isEmpty(longitude))
			params.put("longitude", longitude);
		if(!TextUtils.isEmpty(latitude))
			params.put("latitude", latitude);
		/*params.add(new BasicNameValuePair("longitude", "114.01666"));
		params.add(new BasicNameValuePair("latitude", "22.538146"));*/
		if(!TextUtils.isEmpty(addr))
			params.put("address", addr);
		
		params.put("id", devCode);
		
		OkHttpUtils//
		.get()//
		.url(UPDATE_SPEAKER_INFOR)//
		.id(requestId)
		.params(params)//
		.build()//
		.execute(new RegisterCallBack()//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				requestError(requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onResponse(String response, int requestId)
			{
				//mTv.setText("onResponse:" + response);
				if(response == null)
        		{
					requestError(requestId);
					return;
        		}
				LocalDataEntity.newInstance(activity).clearDeviceInfor();
				requestSuccess(response, requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public String parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}
	
	public void updateCurPos(int playingPos, String playingMusicInfoId)
	{
		
		int requestId = HttpRequestID.UPDATE_CUR_POS;
		if(isRuning(requestId))
			return;
		setRunStatusDoing(requestId);
		
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceCode();
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", terminalId);
		params.put("playingPos", playingPos + "");
		params.put("playingMusicInfoId", playingMusicInfoId);
		
		OkHttpUtils//
		.get()//
		.url(UPDATE_SPEAKER_INFOR)//
		.id(requestId)
		.params(params)//
		.build()//
		.execute(new RegisterCallBack()//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				requestError(requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onResponse(String response, int requestId)
			{
				//mTv.setText("onResponse:" + response);
				if(response == null)
        		{
					requestError(requestId);
					return;
        		}
				LocalDataEntity.newInstance(activity).clearDeviceInfor();
				requestSuccess(response, requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public String parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}
	
	/**
	 * 检查更新
	 * @param params
	 */
	public void checkUpdate()
	{
		
		int requestId = HttpRequestID.CHECK_UPDATE;
		if(isRuning(requestId))
			return;
		setRunStatusDoing(requestId);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("softName", "speaker");
		
		OkHttpUtils//
		.get()//
		.url(CHECK_UPDATE)//
		.id(HttpRequestID.CHECK_UPDATE)
		.params(params)//
		.build()//
		.execute(new CheckUpdateCallBack()//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				requestError(requestId);
			}
			
			@Override
			public void onResponse(UpdateInfor response, int requestId)
			{
				//mTv.setText("onResponse:" + response);
				if(response == null)
        		{
					requestError(requestId);
					return;
        		}
				requestSuccess(response, requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public UpdateInfor parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}
	/**
	 * 获取状态信息
	 * @param params
	 */
	public void getDevStatus(int playSeek, String playingSong, int playingSongType)
	{
		int requestId = HttpRequestID.GET_DEVICE_STATUS;
		if(isRuning(requestId))
			return;
		setRunStatusDoing(requestId);
		
		Map<String, String> params = new HashMap<String, String>();
		String id = LocalDataEntity.newInstance(activity).getDeviceCode();
		params.put("id", id);
		params.put("playSeek", playSeek + "");
		params.put("playingSong", playingSong);
		params.put("playingSongType", playingSongType + "");
		
		//if(SystemUtils.isNetConnected(activity))
		{
			String netType = "";
			String ip = SystemUtils.getIP();
			if(NetWorkUtils.checkEthernet(activity))
				netType = activity.getResources().getString(R.string.network_type_wired);
			else
				netType = activity.getResources().getString(R.string.network_type_wireless);
			String space = SystemUtils.getAvailabeMemorySize();
			params.put("netInfo", space + "   " + netType + ip + " " + SystemUtils.getScreenOritation(activity));
		}
		
		OkHttpUtils//
		.get()//
		.url(GET_DEVICE_STATUS)//
		.id(requestId)
		.params(params)//
		.build()//
		.execute(new DevStatusCallBack()//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				requestError(requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onResponse(DeviceStatusInfor response, int requestId)
			{
				//mTv.setText("onResponse:" + response);
				if(response == null)
        		{
					requestError(requestId);
					return;
        		}
				requestSuccess(response, requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public DeviceStatusInfor parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}
	/**
	 * 获取状态信息
	 * @param params
	 */
	public void getPushMusics()
	{
		
		int requestId = HttpRequestID.GET_PUSH_MUSIC;
		if(isRuning(requestId))
			return;
		setRunStatusDoing(requestId);
		
		Map<String, String> params = new HashMap<String, String>();
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(!TextUtils.isEmpty(terminalId))
		{
			params.put("terminalId", terminalId);
			params.put("pushFlag", "1");
		}
		
		OkHttpUtils//
		.get()//
		.url(GET_PUSH_MUSIC)//
		.id(requestId)
		.params(params)//
		.build()//
		.execute(new GetPushMusicCallBack(activity)//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				requestError(requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onResponse(String response, int requestId)
			{
				//mTv.setText("onResponse:" + response);
				if(response == null)
        		{
					requestError(requestId);
					return;
        		}
				requestSuccess(response, requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public String parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}
	/**
	 * 获取当前播放歌曲列表
	 * @param params
	 */
	public void getPlanMusics()
	{
		int requestId = HttpRequestID.GET_PLAN_MUSICS;
		if(isRuning(requestId))
			return;
		setRunStatusDoing(requestId);
		
		Map<String, String> params = new HashMap<String, String>();
		String uid = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(!TextUtils.isEmpty(uid))
		{
			params.put("id", uid);
		}
		
		planMusicPageNum = 1;
		if(tempPlanMusics == null)
			tempPlanMusics = new ArrayList<SongInfor>();
		params.put("pageSize", MAX_PAGE_SIZE + "");
		getCurPlanMusicByPage(params, requestId);
	}
	private List<SongInfor> tempPlanMusics = null;
	private int planMusicPageNum = 1;
	private void getCurPlanMusicByPage(final Map<String, String> params, int requestId)
	{
		params.put("pageNo", planMusicPageNum + "");
		
		OkHttpUtils//
		.get()//
		.url(GET_PLAN_MUSICS)//
		.id(requestId)
		.params(params)//
		.build()//
		.execute(new GetPlanMusicCallBack(activity)//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				if(tempPlanMusics.size() > 0)
					requestSuccess(tempPlanMusics, requestId);
				else
					requestError(requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onResponse(List<SongInfor> response, int requestId)
			{
				if(response == null)
        		{
					requestError(requestId);
					return;
        		}
				tempPlanMusics.addAll(response);
				if(response.size() < MAX_PAGE_SIZE)
				{
					requestSuccess(tempPlanMusics, requestId);
					setRunStatusFinish(requestId);
					tempPlanMusics.clear();
				}
				else
				{
					planMusicPageNum ++;
					getCurPlanMusicByPage(params, requestId);
				}
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public List<SongInfor> parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}
	/**
	 * 初始化设备
	 * @param params
	 */
	public void initTerminal()
	{
		
		int requestId = HttpRequestID.INIT_TERMINAL;
		if(isRuning(requestId))
			return;
		setRunStatusDoing(requestId);
		
		Map<String, String> params = new HashMap<String, String>();
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceCode();
		if(!TextUtils.isEmpty(terminalId))
		{
			params.put("id", terminalId);
		}
		
		OkHttpUtils//
		.get()//
		.url(INIT_TERMINAL)//
		.id(requestId)
		.params(params)//
		.build()//
		.execute(new InitTerminalCallBack(activity)//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				requestError(requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onResponse(String response, int requestId)
			{
				if(response == null)
        		{
					requestError(requestId);
					return;
        		}
				requestSuccess(response, requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public String parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}
	
	/**
	 * 获取当前系统时间
	 * @param params
	 */
	public void getCurTime()
	{
		
		int requestId = HttpRequestID.GET_CUR_TIME;
		if(isRuning(requestId))
			return;
		setRunStatusDoing(requestId);
		
		OkHttpUtils//
		.get()//
		.url(GET_CUR_TIME)//
		.id(requestId)
		.build()//
		.execute(new GetCurTimeCallBack()//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				requestError(requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onResponse(String response, int requestId)
			{
				if(response == null)
        		{
					requestError(requestId);
					return;
        		}
				requestSuccess(response, requestId);
				setRunStatusFinish(requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public String parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}
	/**
	 * 获取当前歌曲的播放位置
	 * @param params
	 */
	public void getCurMusicPos()
	{
		int requestId = HttpRequestID.GET_CUR_MUSIC_POS;
		if(isRuning(requestId))
			return;
		setRunStatusDoing(requestId);
		
		String terminalId = LocalDataEntity.newInstance(activity).getDeviceCode();
		Map<String, String> params = new HashMap<String, String>();
		if(!TextUtils.isEmpty(terminalId))
		{
			params.put("id", terminalId);
		}
		
		OkHttpUtils//
		.get()//
		.url(GET_CUR_MUSIC_POS)//
		.params(params)//
		.id(requestId)
		.build()//
		.execute(new GetCurMusicPosCallBack()//
		{
			@Override
			public void onError(Call call, Exception e, int requestId)
			{
				//mTv.setText("onError:" + e.getMessage());
				requestError(requestId);
			}
			
			@Override
			public void onResponse(String response, int requestId)
			{
				if(response == null)
        		{
					requestError(requestId);
					return;
        		}
				requestSuccess(response, requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				requestStart(requestId);
			}
			
			@Override
			public String parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}

    /**
     * 获取当前的播放计划
     * @param params
     */
	public void getCurPlan(Map<String, String> params, IGetCurPllanCallBack iGetCurPllanCallBack)
    {
		int requestId = HttpRequestID.GET_CUR_PLAN;
		iHttpRequestView.requestStart(requestId);
    	CurPlanInfor curPlanInfor = null;
    	try 
    	{
    		Response response = OkHttpUtils.get().url(GET_CUR_PLAN).id(requestId).params(params).build().execute();
    		if(response.isSuccessful())
        	{
        		String string = response.body().string();
        		
    			try
    			{
    				JSONObject jsonObject = new JSONObject(string);
    				int result = jsonObject.getInt(RESULT_OK);
    				if(result == 0)
    				{
    					String info = jsonObject.getString(RESULT_INFO);
    					if(TextUtils.isEmpty(info))
    					{
    						iGetCurPllanCallBack.requestSuccess(null, requestId);
    						return ;
    					}
    					curPlanInfor = new CurPlanInfor();
    					DBManager.newInstance(activity).deleteAllPlan();
    					
    					JSONObject json = new JSONObject(info);
    					/*String total = getInforFromJason(json, "total");
    					if(!TextUtils.isEmpty(total))
    						httpResult.setTotal(Integer.parseInt(total));*/
    					//String cycleList = getInforFromJason(json, "cycleList");
    					String endDate = getInforFromJason(json, "endDate");
    					String id = getInforFromJason(json, "id");
    					//String memberId = getInforFromJason(json, "memberId");
    					//String planFlag = getInforFromJason(json, "planFlag");
    					String planName = getInforFromJason(json, "planName");
    					//String planType = getInforFromJason(json, "planType");
    					//String publishTime = getInforFromJason(json, "publishTime");
    					String startDate = getInforFromJason(json, "startDate");
    					//String status = getInforFromJason(json, "status");
    					//String terminalList = getInforFromJason(json, "terminalList");
    					//String tlist = getInforFromJason(json, "tlist");
    					
    					if(!TextUtils.isEmpty(startDate))
    					{
    						//long dateLong = Long.parseLong(startDate);
    						curPlanInfor.setStartDate(startDate);
    						//planInfor.setStartDate(DateUtils.getStringTimeHead(dateLong));
    					}
    					if(!TextUtils.isEmpty(endDate))
    					{
    						//long dateLong = Long.parseLong(endDate);
    						curPlanInfor.setEndDate(endDate);
    						//planInfor.setEndDate(DateUtils.getStringTimeHead(dateLong));
    					}
    					curPlanInfor.setPlanName(planName);
    					curPlanInfor.setPlanId(id);
    					
    					
    					String pslist = getInforFromJason(json, "pslist");
    					JSONArray jsonArray = new JSONArray(pslist);
    					int len = jsonArray.length();
    					for(int i=0;i<len;i++)
    					{
    						CurPlanSubInfor curSubPlanInfor = new CurPlanSubInfor();
    						
    						JSONObject json1 = (JSONObject) jsonArray.get(i);
    						//String cycleType = getInforFromJason(json1, "cycleType");
    						String endTime = getInforFromJason(json1, "endTime");
    						String id1 = getInforFromJason(json1, "id");
    						//String musicCategoryList = getInforFromJason(json1, "musicCategoryList");
    						//String publishId = getInforFromJason(json1, "publishId");
    						String startTime = getInforFromJason(json1, "startTime");
    						
    						if(!TextUtils.isEmpty(startTime) && startTime.contains(":"))
    						{
    							//int tempS = DateUtils.timeToInt(startTime, ":");
    							curSubPlanInfor.setStartTime(startTime);
    						}
    						if(!TextUtils.isEmpty(endTime) && endTime.contains(":"))
    						{
    							//int tempE = DateUtils.timeToInt(endTime, ":");
    							curSubPlanInfor.setEndTime(endTime);
    						}
    						curSubPlanInfor.setPlanId(id1);
    						curPlanInfor.addSubPlanInfor(curSubPlanInfor);
    						long tid = Thread.currentThread().getId();
    						DBManager.newInstance(activity).addCurPlanSub(curSubPlanInfor);
    						
    						List<SongInfor> tempList = getScheduleMusics(id1);
    						curSubPlanInfor.setSongList(tempList);
    						curPlanInfor.addSubPlanInfor(curSubPlanInfor);
    					}
    					iGetCurPllanCallBack.requestSuccess(curPlanInfor, requestId);
    				}
    				else
    					iGetCurPllanCallBack.requestFail(requestId);
    			} 
    			catch (JSONException e)
    			{
    				e.printStackTrace();
    				iGetCurPllanCallBack.requestFail(requestId);
    			}
        	}
    		else
    			iGetCurPllanCallBack.requestFail(requestId);
		} 
    	catch (IOException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
			iGetCurPllanCallBack.requestFail(requestId);
		}
    }
    
    private List<SongInfor> getScheduleMusics(String planScheId)
    {
    	List<SongInfor> tempList = new ArrayList<SongInfor>();
    	int scheduleMusicPageNum = 1;
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("planScheId", planScheId);
    	params.put("pageSize", MAX_PAGE_SIZE + "");
    	params.put("pageNo", scheduleMusicPageNum + "");
    	try 
    	{
			Response response = OkHttpUtils.get().url(GET_SCHEDULE_MUSICS).id(HttpRequestID.GET_SCHEDULE_MUSICS).params(params).build().execute();
			if(response.isSuccessful())
	    	{
	    		String string = response.body().string();
	    		try
	    		{
	    			JSONObject jsonObject = new JSONObject(string);
	    			int result = jsonObject.getInt(RESULT_OK);
					if(result == 0)
					{
						String info = getInforFromJason(jsonObject, RESULT_INFO);
						JSONObject jsonObject1 = new JSONObject(info);
						String total = getInforFromJason(jsonObject1, "listSize");
						int totalInt = 0;
						if(!TextUtils.isEmpty(total))
							totalInt = Integer.parseInt(total);
						String listInfo = getInforFromJason(jsonObject1, "infoList");
						JSONArray jsonArray = new JSONArray(listInfo);
						int size = jsonArray.length();
						tempList = new ArrayList<SongInfor>();
						for(int i=0;i<size;i++)
						{
							JSONObject json = jsonArray.getJSONObject(i);
							String musicAlbum = getInforFromJason(json, "musicAlbum");
							String musicId = getInforFromJason(json, "musicId");
							String musicUrl = getInforFromJason(json, "musicUrl");
							String musicName = getInforFromJason(json, "musicName");
							String musicSing = getInforFromJason(json, "musicSing");
							if(!TextUtils.isEmpty(musicUrl))
								musicUrl = UrlUtils.decodeUrl(musicUrl);
							SongInfor tempInfor = new SongInfor();
							String musicInfoId = getInforFromJason(json, "musicInfoId");
							tempInfor.setMediaId(musicInfoId);
							tempInfor.setResId(musicId);
							tempInfor.setMediaName(musicName);
							tempInfor.setMediaUrl(musicUrl);
							tempInfor.setArtist(musicSing);
							tempInfor.setAlbumName(musicAlbum);
							tempList.add(tempInfor);
							long tid = Thread.currentThread().getId();
							DBManager.newInstance(activity).addCurPlanMusic(tempInfor, planScheId);
							
						}
					}
	    		}
	    		catch (JSONException e) 
	    		{
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    	}
		} 
    	catch (IOException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return tempList;
    }
    
    /*private int scheduleMusicPageNum = 1;
    private ScheduleMusicRequestBean scheduleMusicRequestBean = null;
    private void getScheduleMusics(final String planScheId)
    {
    	Map<String, String> params = new HashMap<String, String>();
    	int requestId = HttpRequestID.GET_SCHEDULE_MUSICS;
		if(isRuning(requestId))
			return;
		setRunStatusDoing(requestId);
    	scheduleMusicPageNum = 1;
    	if(scheduleMusicRequestBean == null)
    		scheduleMusicRequestBean = new ScheduleMusicRequestBean();
    	params.put("planScheId", planScheId);
    	params.put("pageSize", MAX_PAGE_SIZE + "");
    	getScheduleMusicsByPage(params, planScheId, requestId);
    }
    *//**
	 * @Description:
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 *//*
	protected void getScheduleMusicsByPage(final Map<String, String> params, final String planScheId, int requestId) 
	{
		
		params.put("pageNo", scheduleMusicPageNum + "");
		
		OkHttpUtils//
		.get()//
		.url(GET_SCHEDULE_MUSICS)//
		.id(requestId)
		.params(params)//
		.build()//
		.execute(new GetScheduleMusicCallBack(activity, planScheId) {
			
			@Override
			public void onResponse(List<SongInfor> response, int requestId) {
				// TODO Auto-generated method stub
				if(response == null)
        		{
					requestError(requestId);
					return;
        		}
				scheduleMusicRequestBean.addSongList(response);
				if(response.size() < MAX_PAGE_SIZE)//最后一页了
				{
					scheduleMusicRequestBean.setRequestFinish();
					requestSuccess(response, requestId);
				}
				else
				{
					scheduleMusicPageNum ++;//继续请求下一页
					getScheduleMusicsByPage(params, planScheId, requestId);
				}
			}
			
			@Override
			public void onError(Call call, Exception e, int requestId) {
				// TODO Auto-generated method stub
				scheduleMusicRequestBean.setRequestFinish();
				if(scheduleMusicRequestBean.getSongList().size() > 0)
					requestSuccess(scheduleMusicRequestBean.getSongList(), requestId);
				else
					requestError(requestId);
			}
			
			@Override
			public void onBefore(Request request, int requestId)
			{
				// TODO Auto-generated method stub
				super.onBefore(request, requestId);
				
				requestStart(requestId);
			}
			
			@Override
			public List<SongInfor> parseNetworkResponse(Response response, int requestId) throws IOException 
			{
				// TODO Auto-generated method stub
				return super.parseNetworkResponse(response, requestId);
			}
		});
	}*/
	
    public void getBindedDevices(Map<String, String> params)
    {
    	params.put("name", "zhy");
    	String url = mBaseUrl + "user!getUsers";
    	OkHttpUtils//
    	.post()//
    	.url(url)//
    	.params(params)//
    	.build()//
    	.execute(new ListDeviceCallBack()//
    	{
    		@Override
    		public void onError(Call call, Exception e, int id)
    		{
    			//mTv.setText("onError:" + e.getMessage());
    		}
    		
    		@Override
    		public void onResponse(List<DeviceInfor> response, int id)
    		{
    			//mTv.setText("onResponse:" + response);
    			if(response == null)
        		{
					requestError(id);
					return;
        		}
    		}
    		
    		@Override
    		public void onBefore(Request request, int id) {
    			// TODO Auto-generated method stub
    			super.onBefore(request, id);
    		}
    		
    		@Override
    		public void onAfter(int id) {
    			// TODO Auto-generated method stub
    			super.onAfter(id);
    		}
    		
    		@Override
    		public List<DeviceInfor> parseNetworkResponse(Response response, int id) throws IOException 
    		{
    			// TODO Auto-generated method stub
    			return super.parseNetworkResponse(response, id);
    		}
    			});
    }


    public void getHttpsHtml(View view)
    {
        String url = "https://kyfw.12306.cn/otn/";

//                "https://12
//        url =3.125.219.144:8443/mobileConnect/MobileConnect/authLogin.action?systemid=100009&mobile=13260284063&pipe=2&reqtime=1422986580048&ispin=2";
        OkHttpUtils
                .get()//
                .url(url)//
                .id(101)
                .build()//
                .execute(new MyStringCallback());

    }

    public void getImage(View view)
    {
       // mTv.setText("");
        String url = "http://images.csdn.net/20150817/1.jpg";
        OkHttpUtils
                .get()//
                .url(url)//
                .tag(this)//
                .build()//
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new BitmapCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        //mTv.setText("onError:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id)
                    {
                        Log.e("TAG", "onResponse锛歝omplete");
                       // mImageView.setImageBitmap(bitmap);
                    }
                });
    }


    public void uploadFile(View view)
    {

        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        if (!file.exists())
        {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "寮犻缚娲�");
        params.put("password", "123");

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("APP-Key", "APP-Secret222");
        headers.put("APP-Secret", "APP-Secret111");

        String url = mBaseUrl + "user!uploadFile";

        OkHttpUtils.post()//
                .addFile("mFile", "messenger_01.png", file)//
                .url(url)//
                .params(params)//
                .headers(headers)//
                .build()//
                .execute(new MyStringCallback());
    }


    public void multiFileUpload(View view)
    {
        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        File file2 = new File(Environment.getExternalStorageDirectory(), "test1#.txt");
        if (!file.exists())
        {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "寮犻缚娲�");
        params.put("password", "123");

        String url = mBaseUrl + "user!uploadFile";
        OkHttpUtils.post()//
                .addFile("mFile", "messenger_01.png", file)//
                .addFile("mFile", "test1.txt", file2)//
                .url(url)
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }


    public void downloadFile(View view)
    {
        String url = "https://github.com/hongyangAndroid/okhttp-utils/blob/master/okhttputils-2_4_1.jar?raw=true";
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "gson-2.2.1.jar")//
                {

                    @Override
                    public void onBefore(Request request, int id)
                    {
                    }

                    @Override
                    public void inProgress(float progress, long total, int id)
                    {
                       // mProgressBar.setProgress((int) (100 * progress));
                        //Log.e(TAG, "inProgress :" + (int) (100 * progress));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        //Log.e(TAG, "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File file, int id)
                    {
                        //Log.e(TAG, "onResponse :" + file.getAbsolutePath());
                    }
                });
    }


    public void otherRequestDemo(View view)
    {
        //also can use delete ,head , patch
        /*
        OkHttpUtils
                .put()//
                .url("http://11111.com")
                .requestBody
                        ("may be something")//
                .build()//
                .execute(new MyStringCallback());



        OkHttpUtils
                .head()//
                .url(url)
                .addParams("name", "zhy")
                .build()
                .execute();

       */


    }

    public void clearSession(View view)
    {
        CookieJar cookieJar = OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        if (cookieJar instanceof CookieJarImpl)
        {
            ((CookieJarImpl) cookieJar).getCookieStore().removeAll();
        }
    }

    public void cancelHttp()
    {
    	OkHttpUtils.getInstance().cancelTag(this);
    }
    
	public class MyStringCallback extends StringCallback
    {
        @Override
        public void onBefore(Request request, int id)
        {
            //setTitle("loading...");
        }

        @Override
        public void onAfter(int id)
        {
            //setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id)
        {
            e.printStackTrace();
            //mTv.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id)
        {
            //Log.e(TAG, "onResponse锛歝omplete");
            //mTv.setText("onResponse:" + response);

            switch (id)
            {
                case 100:
                    //Toast.makeText(MainActivity.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    //Toast.makeText(MainActivity.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void inProgress(float progress, long total, int id)
        {
            //Log.e(TAG, "inProgress:" + progress);
            //mProgressBar.setProgress((int) (100 * progress));
        }
    }
	
	protected String RESULT_INFO = "info";
	protected String RESULT_OK = "result";
	protected String getInforFromJason(JSONObject json, String key)
	{
		if(json == null || key == null)
			return "";
		if(json.has(key))
		{
			try
			{
				String result = json.getString(key);
				if(result.equals("null"))
					result = "";
				return result;
				//return StringUtils.decodeStr(result);
			} 
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
}