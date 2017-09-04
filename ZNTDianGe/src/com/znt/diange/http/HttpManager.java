package com.znt.diange.http; 

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.znt.diange.dlna.mediaserver.util.LogFactory;
import com.znt.diange.entity.LocalDataEntity;
import com.znt.diange.entity.WXInfor;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.CoinInfor;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.UpdateInfor;
import com.znt.diange.mina.entity.UserInfor;
import com.znt.diange.utils.StringUtils;
import com.znt.diange.utils.SystemUtils;
import com.znt.diange.utils.UrlUtils;

/** 
 * @ClassName: HttpManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-7-31 下午5:36:02  
 */
public class HttpManager extends HttpAPI
{
	protected boolean isStop = false;
	
	protected int HTTP_CONN_TIMEOUT = 3000;
	protected int HTTP_SOCKET_TIMEOUT = 5000;
	
	private String RESULT_INFO = "info";
	private String RESULT_OK = "result";
	
	private Context context = null;
	public void setActivity(Context context)
	{
		this.context = context;
	}
	
	/**
	 * 检测升级
	 * @param params
	 * @return
	 */
	protected HttpResult checkUpdate(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(CHECK_UPDATE, params);
		
		 if(httpResult.isSuccess() && !isStop)
         {
        	JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
        	 try
             {
                 int result = jsonObject.getInt(RESULT_OK);
                 if(result == 0)
                 {
                	 JSONObject json = jsonObject.getJSONObject(RESULT_INFO);
                	 UpdateInfor updateInfor = new UpdateInfor();
                	 String versionName = getInforFromJason(json, "version");
                	 String versionNum = getInforFromJason(json, "versionNum");
                	 String apkUrl = getInforFromJason(json, "url");
                	 String updateType  = getInforFromJason(json, "updateType");
                	 updateInfor.setApkUrl(apkUrl);
                	 updateInfor.setUpdateType(updateType);
                	 updateInfor.setVersionNum(versionNum);
                	 updateInfor.setVersionName(versionName);
                	 httpResult.setResult(true, updateInfor);
                 }
                 else
                 {
                	 httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
                 }
             } 
             catch (JSONException e)
             {
            	 httpResult.setResult(false, e.getMessage());
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
         }
		 else
			httpResult.setSuccess(false);
		
		 return httpResult;
    }
	
	/**
	 * 用户连接统计
	 * @param params
	 * @return
	 */
	protected HttpResult userConnectInfor(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(USER_CONNECT_COUNT, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, null);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	* @Description: 用户注册
	* @param @param params
	* @param @return   
	* @return HttpResult 
	* @throws
	 */
	protected HttpResult register(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(USER_REGISTER, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0 || result == 2)
				{
					String info = getInforFromJason(jsonObject, "info");
					JSONObject json = new JSONObject(info);
					String id = getInforFromJason(json, "id");
					String phone = getInforFromJason(json, "phone");
					String nickName = getInforFromJason(json, "nickName");
					/*String token = getInforFromJason(jsonObject, "token");
					String email = getInforFromJason(jsonObject, "email");
					String memberName = getInforFromJason(jsonObject, "memberName");
					String memberHead = getInforFromJason(jsonObject, "memberHead");
					String regesterTime = getInforFromJason(jsonObject, "regesterTime");
					String lastLoginTime = getInforFromJason(jsonObject, "memberName");*/
					UserInfor userInfor = new UserInfor();
					userInfor.setAccount(phone);
					userInfor.setUserId(id);
					userInfor.setUserName(nickName);
					userInfor.setPwd(params.get(1).getValue());
					httpResult.setResult(true, userInfor);
				}
				/*else if(result == 2)
				{
					httpResult.setResult(true, jsonObject.getString(RESULT_INFO));
				}*/
				else
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	* @Description: 用户登录
	* @param @param params
	* @param @return   
	* @return HttpResult 
	* @throws
	 */
	protected HttpResult login(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(USER_LOGIN, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, "info");
					JSONObject json = new JSONObject(info);
					String id = getInforFromJason(json, "id");
					String phone = getInforFromJason(json, "phone");
					String nickName = getInforFromJason(json, "nickName");
					String terminalCodes = getInforFromJason(json, "terminalCodes");
					
					UserInfor userInfor = new UserInfor();
					userInfor.setAccount(phone);
					userInfor.setUserId(id);
					userInfor.setUserName(nickName);
					userInfor.setPwd(params.get(1).getValue());
					httpResult.setResult(true, userInfor);
					userInfor.setBindDevices(terminalCodes);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	* @Description: 修改昵称
	* @param @param params
	* @param @return   
	* @return HttpResult 
	* @throws
	 */
	protected HttpResult userInforEdit(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(USER_INFOR_EDIT, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, null);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	* @Description: 获取金币
	* @param @param params
	* @param @return   
	* @return HttpResult 
	* @throws
	 */
	protected HttpResult coinGet(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(COIN_GET, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject json = new JSONObject(info);
					String balance = getInforFromJason(json, "balance");
					String freezeAmount = getInforFromJason(json, "freezeAmount");
					String sumAmount = getInforFromJason(json, "sumAmount");
					
					CoinInfor coinInfor = new CoinInfor();
					coinInfor.setBalance(balance);
					coinInfor.setFreeze(freezeAmount);
					coinInfor.setSumAmount(sumAmount);
					
					httpResult.setResult(true, coinInfor);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	* @Description: 上传金币
	* @param @param params
	* @param @return   
	* @return HttpResult 
	* @throws
	 */
	protected HttpResult coinUpload(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(COIN_UPLOAD, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, null);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description: 冻结金币
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult coinFreeze(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(COIN_FREEZE, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String trandId = getInforFromJason(jsonObject, RESULT_INFO);
					httpResult.setResult(true, trandId);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description: 冻结金币取消
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult coinFreezeCancel(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(COIN_FREEZE_CANCEL, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, null);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description: 扣除金币
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult coinRemove(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(COIN_REMOVE, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, null);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description: 获取全部音响
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getAllSpeakers(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_ALL_SPEAKERS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					List<DeviceInfor> tempList = new ArrayList<DeviceInfor>();
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONArray jsonArray = new JSONArray(info);
					int count = jsonArray.length();
					for(int i=0;i<count;i++)
					{
						JSONObject json = jsonArray.getJSONObject(i);
						String id = getInforFromJason(json, "id");
						String wifiName = getInforFromJason(json, "wifiName");
						String name = getInforFromJason(json, "name");
						String longitude = getInforFromJason(json, "longitude");
						String latitude = getInforFromJason(json, "latitude");
						String code = getInforFromJason(json, "code");
						String wifiPassword = getInforFromJason(json, "wifiPassword");
						String ip = getInforFromJason(json, "ip");
						String addr = getInforFromJason(json, "address");
						String wifiMac = getInforFromJason(json, "wifiMac");
						DeviceInfor deviceInfor = new DeviceInfor();
						//判断设备是否可用
						String curMac = SystemUtils.getWifiBSsid(context);
						if(!TextUtils.isEmpty(curMac) && !TextUtils.isEmpty(wifiMac) && curMac.equals(wifiMac))
							deviceInfor.setAvailable(true);
						//判断设备是否选中
						String localUuid = LocalDataEntity.newInstance(context).getDeviceId();
						if(!TextUtils.isEmpty(localUuid) && localUuid.equals(code))
						{
							deviceInfor.setSelected(true);
						}
						
						deviceInfor.setId(code);
						deviceInfor.setName(name);
						deviceInfor.setWifiName(wifiName);
						deviceInfor.setWifiPwd(wifiPassword);
						deviceInfor.setCode(id);
						deviceInfor.setIp(ip);
						deviceInfor.setAddr(addr);
						deviceInfor.setLat(latitude);
						deviceInfor.setLon(longitude);
						if(deviceInfor.isAvailable())
							tempList.add(0, deviceInfor);
						else
							tempList.add(deviceInfor);
					}
						
					httpResult.setResult(true, tempList);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description: 获取全部音响
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getNearBySpeakers(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_NEAR_BY_SPEAKERS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					List<DeviceInfor> tempList = new ArrayList<DeviceInfor>();
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONArray jsonArray = new JSONArray(info);
					int count = jsonArray.length();
					for(int i=0;i<count;i++)
					{
						JSONObject json = jsonArray.getJSONObject(i);
						String id = getInforFromJason(json, "id");
						String wifiName = getInforFromJason(json, "wifiName");
						String name = getInforFromJason(json, "name");
						String longitude = getInforFromJason(json, "longitude");
						String latitude = getInforFromJason(json, "latitude");
						String code = getInforFromJason(json, "code");
						String wifiPassword = getInforFromJason(json, "wifiPassword");
						String ip = getInforFromJason(json, "ip");
						String addr = getInforFromJason(json, "address");
						String wifiMac = getInforFromJason(json, "wifiMac");
						DeviceInfor deviceInfor = new DeviceInfor();
						//判断设备是否可用
						String curMac = SystemUtils.getWifiBSsid(context);
						if(!TextUtils.isEmpty(curMac)  && !TextUtils.isEmpty(wifiMac) && curMac.equals(wifiMac))
							deviceInfor.setAvailable(true);
						//判断设备是否选中
						String localUuid = LocalDataEntity.newInstance(context).getDeviceId();
						if(!TextUtils.isEmpty(localUuid) && localUuid.equals(code))
						{
							deviceInfor.setSelected(true);
						}
						deviceInfor.setWifiMac(wifiMac);
						deviceInfor.setId(code);
						deviceInfor.setName(name);
						deviceInfor.setWifiName(wifiName);
						deviceInfor.setWifiPwd(wifiPassword);
						deviceInfor.setCode(id);
						deviceInfor.setIp(ip);
						deviceInfor.setAddr(addr);
						deviceInfor.setLat(latitude);
						deviceInfor.setLon(longitude);
						if(deviceInfor.isAvailable())
							tempList.add(0, deviceInfor);
						else
							tempList.add(deviceInfor);
					}
					
					httpResult.setResult(true, tempList);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description: 获取音响信息
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getSpeakerInfor(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_SPEAKER_INFOR, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, null);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	* @Description: 获取微信信息
	* @param @param params
	* @param @return   
	* @return HttpResult 
	* @throws
	 */
	protected HttpResult getWXInfor(List<NameValuePair> params)
	{
		
		HttpResult httpResult = connect(GET_WX_INFOR,params);
		if(httpResult.isSuccess() && !isStop)
		{
			WXInfor wxInfor = new WXInfor();
			
			JSONObject jsonObj = (JSONObject)httpResult.getReuslt();
			String access_token = getInforFromJason(jsonObj, "access_token");
			String expires_in = getInforFromJason(jsonObj, "expires_in");
			String refresh_token = getInforFromJason(jsonObj, "refresh_token");
			String openid = getInforFromJason(jsonObj, "openid");
			String unionid = getInforFromJason(jsonObj, "unionid");
			
			wxInfor.setAccessToken(access_token);
			wxInfor.setRefreshToken(refresh_token);
			wxInfor.setOpenId(openid);
			wxInfor.setUnionid(unionid);
			
			if(!TextUtils.isEmpty(unionid))//成功 
			{
				
				params.clear();
				params.add(new BasicNameValuePair("access_token", access_token));
				params.add(new BasicNameValuePair("openid", openid));
				httpResult = connect(GET_WX_USER_INFOR,params);
				if(httpResult.isSuccess())
				{
					jsonObj = (JSONObject)httpResult.getReuslt();
					String nickname = getInforFromJason(jsonObj, "nickname");
					String headimgurl = getInforFromJason(jsonObj, "headimgurl");
					wxInfor.setNickName(nickname);
					wxInfor.setHeadImage(headimgurl);
					httpResult.setResult(true, wxInfor);
				}
			}
			else
				httpResult.setSuccess(false);
		}
		else
			httpResult.setSuccess(false);
		return httpResult;
	}
	
	/**
	 * @Description:申请管理员权限
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult adminApply(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(ADMIN_APPLY, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, jsonObject.getString(RESULT_INFO));
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	/**
	 * @Description:添加音乐到音响
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult addMusicToSpeaker(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(ADD_MUSIC_TO_SPEAKER, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, null);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description:删除音响音乐
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult deleteSpeakerMusic(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(DELETE_MUSIC_FROM_SPEAKER, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					httpResult.setResult(true, null);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description:获取音响音乐
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getSpeakerMusic(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_SPEAKER_MUSIC, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONArray jsonArray = new JSONArray(info);
					int size = jsonArray.length();
					List<MediaInfor> tempList = new ArrayList<MediaInfor>();
					if(size > 0)
					{
						for(int i=0;i<size;i++)
						{
							JSONObject json = jsonArray.getJSONObject(i);
							String terminalId = getInforFromJason(json, "terminalId");
							String musicDuration = getInforFromJason(json, "musicDuration");
							String id = getInforFromJason(json, "id");
							//String status = getInforFromJason(json, "status");
							String musicAuther = getInforFromJason(json, "musicAuther");
							String musicId = getInforFromJason(json, "musicId");
							String musicUrl = getInforFromJason(json, "musicUrl");
							String musicName = getInforFromJason(json, "musicName");
							String musicSing = getInforFromJason(json, "musicSing");
							
							MediaInfor tempInfor = new MediaInfor();
							if(!TextUtils.isEmpty(musicDuration))
								tempInfor.setMediaDuration(Integer.parseInt(musicDuration));
							tempInfor.setMediaId(musicId);
							tempInfor.setMediaName(musicName);
							if(!TextUtils.isEmpty(musicAuther))
								tempInfor.setResourceType(Integer.parseInt(musicAuther));
							tempInfor.setMediaUrl(UrlUtils.decodeUrl(musicUrl));
							tempInfor.setArtist(musicSing);
							tempInfor.setTerminalId(terminalId);
							tempList.add(tempInfor);
						}
					}
					
					httpResult.setResult(true, tempList);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	/**
	 * @Description:获取绑定的音响列表
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getBindSpeakers(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_BIND_SPEAKERS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					List<DeviceInfor> tempList = new ArrayList<DeviceInfor>();
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONArray jsonArray = new JSONArray(info);
					int count = jsonArray.length();
					for(int i=0;i<count;i++)
					{
						JSONObject json = jsonArray.getJSONObject(i);
						String id = getInforFromJason(json, "id");
						String wifiName = getInforFromJason(json, "wifiName");
						String name = getInforFromJason(json, "name");
						String longitude = getInforFromJason(json, "longitude");
						String latitude = getInforFromJason(json, "latitude");
						String code = getInforFromJason(json, "code");
						String wifiPassword = getInforFromJason(json, "wifiPassword");
						String ip = getInforFromJason(json, "ip");
						String addr = getInforFromJason(json, "address");
						
						DeviceInfor deviceInfor = new DeviceInfor();
						deviceInfor.setAdmin(true);
						//判断设备是否可用
						String curssid = SystemUtils.getConnectWifiSsid(context);
						if(!TextUtils.isEmpty(curssid) && curssid.equals(wifiName))
							deviceInfor.setAvailable(true);
						//判断设备是否选中
						String localUuid = LocalDataEntity.newInstance(context).getDeviceId();
						if(!TextUtils.isEmpty(localUuid) && localUuid.equals(code))
						{
							deviceInfor.setSelected(true);
						}
						
						deviceInfor.setId(code);
						deviceInfor.setName(name);
						deviceInfor.setWifiName(wifiName);
						deviceInfor.setWifiPwd(wifiPassword);
						deviceInfor.setCode(id);
						deviceInfor.setIp(ip);
						deviceInfor.setAddr(addr);
						deviceInfor.setLat(latitude);
						deviceInfor.setLon(longitude);
						if(deviceInfor.isAvailable())
							tempList.add(0, deviceInfor);
						else
							tempList.add(deviceInfor);
					}
						
					httpResult.setResult(true, tempList);
				}
				else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}
			} 
			catch (JSONException e)
			{
				httpResult.setResult(false, e.getMessage());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			httpResult.setSuccess(false);
		
		return httpResult;
	}
	
	
	private String getInforFromJason(JSONObject jsonObject, String key, boolean decode)
    {
        if(jsonObject == null || key == null)
            return null;
        if(jsonObject.has(key))
        {
            if(isStop)
                return "";
            try
            {
                String result = jsonObject.getString(key);
                if(result.equals("null"))
                    result = "";
                return decode ? StringUtils.decodeStr(result): result;
            } 
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
	
	
	private String getInforFromJason(JSONObject json, String key)
	{
		if(json == null || key == null)
			return "";
		if(json.has(key))
		{
			if(isStop)
				return "";
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
	
	/**
	* @Description: post方式访问
	* @param @param params
	* @param @return   
	* @return JSONObject 
	* @throws
	 */
	protected HttpResult connect(String url, List<NameValuePair> params)
	{
		
		HttpResult httpResult = new HttpResult();
		if(url.contains(" "))
			url = url.replace(" ", "");
		HttpPost httpRequest = new HttpPost(url);
		try
		{
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpParams httpParameters = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_CONN_TIMEOUT);
	        HttpConnectionParams.setSoTimeout(httpParameters, HTTP_SOCKET_TIMEOUT);
	        HttpResponse httpResponse = new DefaultHttpClient(httpParameters).execute(httpRequest);
	        
	        if(isStop)
	        	httpResult.setResult(false, null);
	        
	        if (httpResponse.getStatusLine().getStatusCode() == 200)
	        {
	            String strResult = EntityUtils.toString(httpResponse.getEntity());
	            httpResult.setResult(true, new JSONObject(strResult));
	        } 
	        else
	        {
	        	 httpResult.setResult(false, httpResponse.getStatusLine().toString());
	        	 LogFactory.createLog().e("network error: "+ httpResponse.getStatusLine().toString());
	        }
	      
		} 
		catch (UnsupportedEncodingException e)
		{
			httpResult.setResult(false, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ClientProtocolException e)
		{
			httpResult.setResult(false, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			httpResult.setResult(false, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			httpResult.setResult(false, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return httpResult;
	}
}
 
