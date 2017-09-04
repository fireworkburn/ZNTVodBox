package com.znt.vodbox.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.http.HttpHelper;
import com.znt.vodbox.http.HttpType;
import com.znt.vodbox.utils.MyToast;
import com.znt.vodbox.utils.StringUtils;
import com.znt.vodbox.utils.SystemUtils;
import com.znt.vodbox.utils.UrlUtils;

public class HttpFactory 
{
	private Context activity = null;
	private Handler handler = null;
	private HttpHelper httpHelper = null;
	
	private MyToast myToast = null;
	
	public HttpFactory(Context activity, Handler handler)
	{
		this.activity = activity;
		this.handler = handler;
		myToast = new MyToast(activity);
	}
	
	private Context getActivity()
	{
		return activity;
	}
	
	public void stopHttp()
	{
		if(httpHelper != null)
		{
			httpHelper.stop();
			httpHelper = null;
		}
	}
	
	public void login(String account, String pwd)
	{
		
		if(!SystemUtils.isNetConnected(getActivity())
				&& !SystemUtils.is3gConnected(getActivity()))
		{
			showToast("网络异常，请先连接网络");
			return;
		}
		
		/*if(!StringUtils.isMobileNO(account))//!StringUtils.isEmail(account) && 
		{
			showToast("请输入正确的手机号码");
			return;
		}*/
		
		/*if(!StringUtils.isEmail(account) && !StringUtils.isMobileNO(account))//
		{
			showToast("请输入正确的手机号码");
			return;
		}
		if(account.length() > 30)
		{
			showToast("邮箱长度不能超过30");
			return;
		}*/
			
		if(pwd.length() < 6)
		{
			showToast("密码最少6位");
			return;
		}
		else if(pwd.length() > 16)
		{
			showToast("密码最多不能超过16位");
			return;
		}
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if(account.contains(" "))
			account = account.replace(" ", "");
		if(account.contains("-"))
			account = account.replace("-", "");
        params.add(new BasicNameValuePair("loginId", account));
        params.add(new BasicNameValuePair("password", pwd));
        
        httpHelper.startHttp(HttpType.Login, params);
        
        //setHttpHelper(httpHelper);
	}
	public void doRegister(String clientId, String token, String name)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("clientId", clientId));
        params.add(new BasicNameValuePair("token", token));
        params.add(new BasicNameValuePair("nickName", name));
        //params.add(new BasicNameValuePair("password", "000000"));
        
        httpHelper.startHttp(HttpType.Register, params);
	}
	public void doRegisterByPhone(String phone, String pwd, String name)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("phone", phone));
        //params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", pwd));
        params.add(new BasicNameValuePair("nickName", name));
        
        httpHelper.startHttp(HttpType.Register, params);
	}
	public void loginByToken()
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//params.add(new BasicNameValuePair("token", getLocalData().getToken()));
        httpHelper.startHttp(HttpType.LoginByToken, params);
	}
	
	public void logout()
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//params.add(new BasicNameValuePair("token", getLocalData().getToken()));
		httpHelper.startHttp(HttpType.Logout, params);
	}
	public void editUserInfor(String nickName)
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", LocalDataEntity.newInstance(getActivity()).getUserInfor().getUserId()));
        params.add(new BasicNameValuePair("nickName", nickName));
        
        if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		httpHelper.startHttp(HttpType.UserInforEdit, params);
	}
	public void setNewPwdByPhone(String userId, String newPwd)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", userId));
		params.add(new BasicNameValuePair("password", newPwd));
		
		httpHelper.startHttp(HttpType.NewPwdByPhone, params);
	}
	public void resetPwd(String oldPwd, String newPwd, String newPwdConfirm)
	{
		
		if(oldPwd.length() < 6)
		{
			showToast("请输入6位数旧密码");
			return;
		}
		else if(oldPwd.length() > 16)
		{
			showToast("密码不能超过16位");
			return;
		}
		
		if(newPwd.length() < 6)
		{
			showToast("请输入6位数新密码");
			return;
		}
		else if(newPwd.length() > 16)
		{
			showToast("密码不能超过16位");
			return;
		}
		
		if(!newPwdConfirm.equals(newPwd))
		{
			showToast("两次新密码不一致");
			return;
		}
		else if(newPwdConfirm.length() > 16)
		{
			showToast("密码不能超过16位");
			return;
		}
		
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        //params.add(new BasicNameValuePair("token", getLocalData().getToken()));
        params.add(new BasicNameValuePair("id", LocalDataEntity.newInstance(getActivity()).getUserId()));
        params.add(new BasicNameValuePair("oldPwd", oldPwd));
        params.add(new BasicNameValuePair("password", newPwd));
		
		httpHelper.startHttp(HttpType.ResetPwd, params);
	}
	
	public void checkUpdate()
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("softName", "phone"));
		httpHelper.startHttp(HttpType.CheckUpdate, params);
		
	}
	
	public void userConnectCount(DeviceInfor deviceInfor)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		
		Build b = new Build();
		String userName = b.MODEL;
		String userId = SystemUtils.getDeviceId(getActivity());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userName", userName ));
		params.add(new BasicNameValuePair("userId", userId));
		params.add(new BasicNameValuePair("deviceName", deviceInfor.getName()));
		params.add(new BasicNameValuePair("deviceId", deviceInfor.getId()));
		httpHelper.startHttp(HttpType.UserConnetCount, params);
		
	}
	
	public void getAllSpeakers()
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//params.add(new BasicNameValuePair("softName", "phone"));
		httpHelper.startHttp(HttpType.GetAllSpeaker, params);
		
	}
	public void getNearBySpeakers()
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		String longitude = LocalDataEntity.newInstance(getActivity()).getLon();
		String latitude = LocalDataEntity.newInstance(getActivity()).getLat();
		//LogFactory.createLog().e("&&&&&&&&经度:" + longitude + "  纬度:" + latitude);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		/*params.add(new BasicNameValuePair("longitude", "114.01666"));
		params.add(new BasicNameValuePair("latitude", "22.538146"));*/
		params.add(new BasicNameValuePair("longitude", longitude));
		params.add(new BasicNameValuePair("latitude", latitude));
		httpHelper.startHttp(HttpType.GetNearBySpeaker, params);
		
	}
	
	/**
	* @Description: 参数memberId 
	返回balance=余额 freezeAmount=冻结金币 sumAmount=总共获取到的金币

	* @param @param amount   
	* @return void 
	* @throws
	 */
	public void getCoin()
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", 
				LocalDataEntity.newInstance(getActivity()).getUserInfor().getUserId() ));
		
		httpHelper.startHttp(HttpType.CoinGet, params);
		
	}
	public void uploadCoin(String amount)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, getActivity());
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("memberId", LocalDataEntity.newInstance(getActivity()).getUserInfor().getUserId() ));
        params.add(new BasicNameValuePair("amount", amount));
        
        httpHelper.startHttp(HttpType.CoinUpload, params);
	}
	public void coinFreeze(String coin)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", LocalDataEntity.newInstance(activity).getUserInfor().getUserId()));
		params.add(new BasicNameValuePair("amount", coin));
		
		httpHelper.startHttp(HttpType.CoinFreeze, params);
	}
	public void coinFreezeCancel(SongInfor songInfor)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
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
	
	public void startAdminApply(String terminalCode)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("terminalCode", terminalCode));
        params.add(new BasicNameValuePair("id", uid));
        
        httpHelper.startHttp(HttpType.AdminApply, params);
	}
	
	public void deleteSpeakerMusic(MediaInfor mediaInfor)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("terminalId", mediaInfor.getTerminalId()));
		params.add(new BasicNameValuePair("musicId", mediaInfor.getMediaId()));
		
		httpHelper.startHttp(HttpType.DeleteSpeakerMusic, params);
	}
	/**
	* @Description: TODO
	* @param @param musicId//歌曲id
	* @param @param musicUrl//歌曲地址
	* @param @param musicName//名称
	* @param @param terminalId//音箱id
	* @param @param musicSing//歌手
	* @param @param musicAuther//来源
	* @param @param musicDuration //时长  
	* @return void 
	* @throws
	 */
	public void addMusicToSpeaker(MediaInfor mediaInfor, String terminalId)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("musicId", mediaInfor.getMediaId()));
		//params.add(new BasicNameValuePair("musicUr", mediaInfor.getMediaUrl()));
		params.add(new BasicNameValuePair("musicUrl", UrlUtils.encodeUrl(mediaInfor.getMediaUrl())));
		params.add(new BasicNameValuePair("musicName", mediaInfor.getMediaName()));
		params.add(new BasicNameValuePair("terminalId", terminalId));
		params.add(new BasicNameValuePair("musicSing", mediaInfor.getArtist()));
		params.add(new BasicNameValuePair("musicAuther", mediaInfor.getResourceType() + ""));
		params.add(new BasicNameValuePair("musicDuration", mediaInfor.getMediaDuration() + ""));
		
		httpHelper.startHttp(HttpType.AddMusicToSpeaker, params);
	}
	public void getSpeakerMusic(String terminalId, int pageNo)
	{
		/*if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("terminalId", terminalId));
		
		httpHelper.startHttp(HttpType.GetSpeakerMusic, params);*/
		getPlanMusics(terminalId, pageNo);
	}
	
	/**
	 * 
	 * @param 
	 */
	public void getPlanMusics(String terminalId, int pageNo)
	{
		
		HttpHelper httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if(!TextUtils.isEmpty(terminalId))// && !TextUtils.isEmpty(planId))
		{
			params.add(new BasicNameValuePair("id", terminalId));
			params.add(new BasicNameValuePair("pageSize", "25"));
			params.add(new BasicNameValuePair("pageNo", pageNo + ""));
			/*if(!TextUtils.isEmpty(planId))
				params.add(new BasicNameValuePair("planId", planId));*/
			
			httpHelper.startHttp(HttpType.GetPlanMusics, params);
		}
	}
	
	public void getBindSpeakers(int pageNo, int pageSize)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("pageNo", pageNo + ""));
		params.add(new BasicNameValuePair("pageSize", pageSize + ""));
		params.add(new BasicNameValuePair("longitude", LocalDataEntity.newInstance(getActivity()).getLon()));
		params.add(new BasicNameValuePair("latitude", LocalDataEntity.newInstance(getActivity()).getLat()));
		
		httpHelper.startHttp(HttpType.GetBindSpeakers, params);
	}
	public void getBindSpeakers(String userId, int pageNo)
	{
		if(TextUtils.isEmpty(userId))
			userId = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(userId))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", userId));
		params.add(new BasicNameValuePair("pageNo", pageNo + ""));
		params.add(new BasicNameValuePair("pageSize", "25"));
		params.add(new BasicNameValuePair("longitude", LocalDataEntity.newInstance(getActivity()).getLon()));
		params.add(new BasicNameValuePair("latitude", LocalDataEntity.newInstance(getActivity()).getLat()));
		
		httpHelper.startHttp(HttpType.GetBindSpeakers, params);
	}
	public void getBindSpeakersFiletr(String selected, int pageNo, int pageSize)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("selected", selected));
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("pageNo", pageNo + ""));
		params.add(new BasicNameValuePair("pageSize", pageSize + ""));
		params.add(new BasicNameValuePair("longitude", LocalDataEntity.newInstance(getActivity()).getLon()));
		params.add(new BasicNameValuePair("latitude", LocalDataEntity.newInstance(getActivity()).getLat()));
		
		httpHelper.startHttp(HttpType.GetBindSpeakers, params);
	}
	public void getBindSpeakersByKey(int pageNo, int pageSize, String name)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("pageNo", pageNo + ""));
		params.add(new BasicNameValuePair("pageSize", pageSize + ""));
		params.add(new BasicNameValuePair("longitude", LocalDataEntity.newInstance(getActivity()).getLon()));
		params.add(new BasicNameValuePair("latitude", LocalDataEntity.newInstance(getActivity()).getLat()));
		
		httpHelper.startHttp(HttpType.GetBindSpeakers, params);
	}
	public void getCreateAlbums()
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		
		httpHelper.startHttp(HttpType.GetCreateAlbums, params);
	}
	public void getMusicAlbums()
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		
		httpHelper.startHttp(HttpType.GetMusicAlbums, params);
	}
	public void getParentMusicAlbums()
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		
		httpHelper.startHttp(HttpType.GetParentMusicAlbums, params);
	}
	public void getAlbumMusics(String categoryId, int pageNo)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("categoryId", categoryId));
		params.add(new BasicNameValuePair("pageNo", ""+pageNo));
		params.add(new BasicNameValuePair("pageSize", "25"));
		
		httpHelper.startHttp(HttpType.GetAlbumMusics, params);
	}
	public void createAlbum(String name)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("name", name));
		
		httpHelper.startHttp(HttpType.CreateAlbum, params);
	}
	public void editAlbum(String name, String albumId, String description)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("name", name));
		if(!TextUtils.isEmpty(description))
			params.add(new BasicNameValuePair("description", description));
		params.add(new BasicNameValuePair("id", albumId));
		
		httpHelper.startHttp(HttpType.EditAlbum, params);
	}
	public void deleteAlbum(String id)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("id", id));
		
		httpHelper.startHttp(HttpType.DeleteAlbum, params);
	}
	public void collectAlbum(String id)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("id", id));
		
		httpHelper.startHttp(HttpType.CollectAlbum, params);
	}
	public void deleteAlbumMusic(String categoryId, String id)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("categoryId", categoryId));
		params.add(new BasicNameValuePair("id", id));
		
		httpHelper.startHttp(HttpType.DeleteAlbumMusic, params);
	}
	public void deleteAlbumMusics(String categoryId, String ids)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("categoryId", categoryId));
		params.add(new BasicNameValuePair("ids", ids));
		
		httpHelper.startHttp(HttpType.DeleteAlbumMusics, params);
	}
	
	/*
	 * 获取系统推荐的歌单
	 */
	public void getSystemAlbums(String pageNo)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("pageNo", pageNo));
		params.add(new BasicNameValuePair("pageSize", "20"));
		
		httpHelper.startHttp(HttpType.GetSystemAlbums, params);
	}
	public void getCurPlan(String uid, String status, int pageNo)
	{
		if(TextUtils.isEmpty(uid))
			uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("status", status));//0-有效计划（有盒子在使用）不填默认为0 1-历史计划 
		params.add(new BasicNameValuePair("pageNo", pageNo + ""));
		params.add(new BasicNameValuePair("pageSize", "20"));
		httpHelper.startHttp(HttpType.GetCurPlan, params);
	}
	public void getBoxPlan(String status, int pageNo, String terminalId)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("terminalId", terminalId));
		params.add(new BasicNameValuePair("status", status));//0-有效计划（有盒子在使用）不填默认为0 1-历史计划 
		params.add(new BasicNameValuePair("pageNo", pageNo + ""));
		params.add(new BasicNameValuePair("pageSize", "20"));
		httpHelper.startHttp(HttpType.GetBoxPlan, params);
	}
	public void addPlan(String uid, PlanInfor planInfor)
	{
		if(TextUtils.isEmpty(uid))
		uid = LocalDataEntity.newInstance(activity).getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("startTimes", planInfor.getAllStartTimes()));
		params.add(new BasicNameValuePair("endTimes", planInfor.getAllEndTimes()));
		params.add(new BasicNameValuePair("categoryIds", planInfor.getAllCategoryIds()));
		params.add(new BasicNameValuePair("planId", planInfor.getPlanId()));
		params.add(new BasicNameValuePair("scheduleIds", "0"));//修改过的详细时段的id，如果是新添加的时间段 对应的scheduleId填0
		params.add(new BasicNameValuePair("planName", planInfor.getPlanName()));
		
		params.add(new BasicNameValuePair("planType", planInfor.getPlanType()));
		if(planInfor.getPlanType().equals(PlanInfor.PLAN_TYPE_YEAR))
		{
			/*params.add(new BasicNameValuePair("startDate", "2016-07-15"));
			params.add(new BasicNameValuePair("endDate", "2016-07-16"));*/
			params.add(new BasicNameValuePair("startDate", planInfor.getStartDate()));
			params.add(new BasicNameValuePair("endDate", planInfor.getEndDate()));
		}
		
		if(planInfor.getPlanFlag().equals("1"))//指定的
		{
			params.add(new BasicNameValuePair("terminalIds", planInfor.getAllTerminalIds()));
			params.add(new BasicNameValuePair("isAll", "0"));
		}
		else//全部的
		{
			params.add(new BasicNameValuePair("isAll", "1"));
		}
		
		if(!TextUtils.isEmpty(planInfor.getLoopAddNums()))
		{
			params.add(new BasicNameValuePair("loopAddNums", planInfor.getLoopAddNums()));
			params.add(new BasicNameValuePair("loopMusicInfoIds", planInfor.getLoopMusicInfoIds()));
		}
		httpHelper.startHttp(HttpType.AddPlan, params);
	}
	public void editPlan(String uid, PlanInfor planInfor)
	{
		if(TextUtils.isEmpty(uid))
			uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("startTimes", planInfor.getAllStartTimes()));
		params.add(new BasicNameValuePair("endTimes", planInfor.getAllEndTimes()));
		params.add(new BasicNameValuePair("categoryIds", planInfor.getAllCategoryIds()));
		params.add(new BasicNameValuePair("planId", planInfor.getPlanId()));
		params.add(new BasicNameValuePair("scheduleIds", planInfor.getAllScheduleIds()));//修改过的详细时段的id，如果是新添加的时间段 对应的scheduleId填0
		params.add(new BasicNameValuePair("planName", planInfor.getPlanName()));
		
		params.add(new BasicNameValuePair("planType", planInfor.getPlanType()));
		if(planInfor.getPlanType().equals("0"))
		{
			/*params.add(new BasicNameValuePair("startDate", "2016-07-15"));
			params.add(new BasicNameValuePair("endDate", "2016-07-16"));*/
			params.add(new BasicNameValuePair("startDate", planInfor.getStartDate()));
			params.add(new BasicNameValuePair("endDate", planInfor.getEndDate()));
		}
		
		if(planInfor.getPlanFlag().equals("1"))//指定的
		{
			params.add(new BasicNameValuePair("terminalIds", planInfor.getAllTerminalIds()));
			params.add(new BasicNameValuePair("isAll", "0"));
		}
		else//全部的
		{
			params.add(new BasicNameValuePair("isAll", "1"));
		}
		if(!TextUtils.isEmpty(planInfor.getLoopAddNums()))
		{
			params.add(new BasicNameValuePair("loopAddNums", planInfor.getLoopAddNums()));
			params.add(new BasicNameValuePair("loopMusicInfoIds", planInfor.getLoopMusicInfoIds()));
		}
		
		httpHelper.startHttp(HttpType.EditPlan, params);
	}
	
	public void addMusicToAlbum(String categoryId, MediaInfor mediaInfor)
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("categoryId", categoryId));
		params.add(new BasicNameValuePair("musicName", mediaInfor.getMediaName()));
		params.add(new BasicNameValuePair("musicSing", mediaInfor.getArtist()));
		params.add(new BasicNameValuePair("musicAlbum", mediaInfor.getAlbumName()));
		//params.add(new BasicNameValuePair("musicDuration", ));
		params.add(new BasicNameValuePair("musicSource", mediaInfor.getMediaResType()));
		params.add(new BasicNameValuePair("musicId", mediaInfor.getMediaId()));
		if(mediaInfor.getMediaResType().equals("1"))
			params.add(new BasicNameValuePair("musicUrl", UrlUtils.encodeUrl(mediaInfor.getMediaUrl())));
		else if(mediaInfor.getMediaResType().equals("2"))
			params.add(new BasicNameValuePair("musicUrl", mediaInfor.getMediaUrl()));
		
		httpHelper.startHttp(HttpType.AddMusicToAlbum, params);
	}
	public void addSysMusicToAlbum(String categoryId, List<MediaInfor> list)
	{
		String ids = "";
		int size = list.size();
		for(int i=0;i<size;i++)
		{
			MediaInfor tempInfor = list.get(i);
			if(i < size - 1)
				ids += tempInfor.getMediaId() + ",";
			else
				ids += tempInfor.getMediaId();
		}
		
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		params.add(new BasicNameValuePair("categoryId", categoryId));
		params.add(new BasicNameValuePair("ids", ids));
		
		httpHelper.startHttp(HttpType.AddSysMusicToAlbum, params);
	}
	
	public void pushMusic(String terminalId, MediaInfor mediaInfor)
	{
		
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("memberId", uid));
		
		if(mediaInfor.isFromAlbum())
		{
			params.add(new BasicNameValuePair("id", mediaInfor.getMediaId()));
			
		}
		else
		{
			params.add(new BasicNameValuePair("musicId", mediaInfor.getMediaId()));
			params.add(new BasicNameValuePair("musicUrl", mediaInfor.getMediaUrl()));
		}
		params.add(new BasicNameValuePair("musicName", mediaInfor.getMediaName()));
		params.add(new BasicNameValuePair("terminalId", terminalId));
		params.add(new BasicNameValuePair("musicSing", mediaInfor.getArtist()));
		params.add(new BasicNameValuePair("musicDuration", mediaInfor.getMediaDuration() + ""));
		params.add(new BasicNameValuePair("musicAlbum", mediaInfor.getAlbumName()));
		
		if(mediaInfor.getMediaUrl().contains("126.net"))//网易
			mediaInfor.setMediaResType("2");
		else if(mediaInfor.getMediaUrl().contains("kuwo"))//酷我
			mediaInfor.setMediaResType("1");
		else
			mediaInfor.setMediaResType("0");//上传的
		
		params.add(new BasicNameValuePair("musicAuther", mediaInfor.getMediaResType()));//歌曲源   1酷我   2网易
		params.add(new BasicNameValuePair("pushFlag", "1"));//0-推荐  1-点播 只有店长可以点播，店长权限现在我们先自己手动设置

		
		httpHelper.startHttp(HttpType.PushMusic, params);
	}
	
	
	public void getAllPlanMusics(String terminalId, String planId, String pageNo)
	{
		
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if(!TextUtils.isEmpty(terminalId))
			params.add(new BasicNameValuePair("id", terminalId));
		params.add(new BasicNameValuePair("planId", planId));
		params.add(new BasicNameValuePair("pageNo", pageNo));
		params.add(new BasicNameValuePair("pageSize", "25"));
		
		httpHelper.startHttp(HttpType.GetAllPlanMusic, params);
	}
	
	public void getPushHostoryMusics(String terminalId, String pushFlag, String pageNo)
	{
		//pushFlag 0 推荐歌曲列表   1，当前点播歌曲列表
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if(!TextUtils.isEmpty(terminalId))
			params.add(new BasicNameValuePair("terminalId", terminalId));
		params.add(new BasicNameValuePair("pushFlag", pushFlag));
		params.add(new BasicNameValuePair("pageNo", pageNo));
		params.add(new BasicNameValuePair("pageSize", "25"));
		
		httpHelper.startHttp(HttpType.GetPushHostoryMusic, params);
	}
	
	public void updateSpeakerVolume(String volume, String devId)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", devId));
		params.add(new BasicNameValuePair("volume", volume));
		
		httpHelper.startHttp(HttpType.UpdateSpeakerInfor, params);
	}
	public void updateSpeakerWhirl(String videoWhirl, String devId)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", devId));
		params.add(new BasicNameValuePair("videoWhirl", videoWhirl));
		
		httpHelper.startHttp(HttpType.UpdateSpeakerInfor, params);
	}
	public void updateSpeakerName(String name, String devId)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", devId));
		params.add(new BasicNameValuePair("name", name));
		
		httpHelper.startHttp(HttpType.UpdateSpeakerInfor, params);
	}
	public void updateSpeakerInfor(DeviceInfor infor)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		//String name = infor.getName();
		String code = infor.getId();
		//String wifiName = infor.getWifiName();
		//String wifiPassword = infor.getWifiPwd();
		String longitude = infor.getLon();
		String latitude = infor.getLat();
		String addr = infor.getAddr();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		/*if(!TextUtils.isEmpty(name))
			params.add(new BasicNameValuePair("name", name));*/
		params.add(new BasicNameValuePair("code", code));
		if(!TextUtils.isEmpty(longitude))
			params.add(new BasicNameValuePair("longitude", longitude));
		if(!TextUtils.isEmpty(latitude))
			params.add(new BasicNameValuePair("latitude", latitude));
		/*params.add(new BasicNameValuePair("longitude", "114.01666"));
		params.add(new BasicNameValuePair("latitude", "22.538146"));*/
		if(!TextUtils.isEmpty(addr))
			params.add(new BasicNameValuePair("address", addr));
		
		httpHelper.startHttp(HttpType.UpdateSpeakerInfor, params);
	}
	public void startOldPlan(String planId)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("planId", planId));
		httpHelper.startHttp(HttpType.StartOldPlan, params);
	}
	public void deleteOldPlan(String planId)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("planId", planId));
		httpHelper.startHttp(HttpType.DeleteOldPlan, params);
	}
	public void getSecondLevels(int pageNo)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", LocalDataEntity.newInstance(activity).getUserId()));
		params.add(new BasicNameValuePair("pageSize", "25"));
		params.add(new BasicNameValuePair("pageNo", pageNo + ""));
		httpHelper.startHttp(HttpType.GetSecondLevels, params);
	}
	
	public void showToast(int res)
	{
		myToast.show(res);
	}
	public void showToast(String res)
	{
		myToast.show(res);
	}
}
