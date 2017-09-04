package com.znt.diange.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.http.HttpHelper;
import com.znt.diange.http.HttpType;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.utils.MyToast;
import com.znt.diange.utils.StringUtils;
import com.znt.diange.utils.SystemUtils;
import com.znt.diange.utils.UrlUtils;

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
		
		if(!StringUtils.isMobileNO(account))//!StringUtils.isEmail(account) && 
		{
			showToast("请输入正确的手机号码");
			return;
		}
		
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
       /* params.add(new BasicNameValuePair("token", getLocalData().getToken()));
        params.add(new BasicNameValuePair("id", getLocalData().getUserId()));
        params.add(new BasicNameValuePair("oldPwd", oldPwd));
        params.add(new BasicNameValuePair("password", newPwd));*/
		
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
	public void getSpeakerMusic(String terminalId)
	{
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("terminalId", terminalId));
		
		httpHelper.startHttp(HttpType.GetSpeakerMusic, params);
	}
	
	public void getBindSpeakers()
	{
		String uid = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(TextUtils.isEmpty(uid))
			return;
		if(httpHelper == null)
			httpHelper = new HttpHelper(handler, activity);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", uid));
		
		httpHelper.startHttp(HttpType.GetBindSpeakers, params);
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
