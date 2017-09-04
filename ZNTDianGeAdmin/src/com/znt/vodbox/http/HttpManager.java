package com.znt.vodbox.http; 

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
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

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.CoinInfor;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.mina.entity.UpdateInfor;
import com.znt.diange.mina.entity.UserInfor;
import com.znt.vodbox.dlna.mediaserver.util.LogFactory;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.entity.MusicAlbumInfor;
import com.znt.vodbox.entity.MyAlbumInfor;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.entity.SubPlanInfor;
import com.znt.vodbox.entity.WXInfor;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.StringUtils;
import com.znt.vodbox.utils.SystemUtils;
import com.znt.vodbox.utils.UrlUtils;

/** 
 * @ClassName: HttpManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-7-31 下午5:36:02  
 */
public class HttpManager extends HttpAPI
{
	protected boolean isStop = false;
	
	protected int HTTP_CONN_TIMEOUT = 15 * 1000;
	protected int HTTP_SOCKET_TIMEOUT = 15 * 1000;
	
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
					String memberType = getInforFromJason(json, "memberType");
					String pcCode = getInforFromJason(json, "activateCode");
					
					UserInfor userInfor = new UserInfor();
					userInfor.setAccount(phone);
					userInfor.setUserId(id);
					userInfor.setUserName(nickName);
					userInfor.setPwd(params.get(1).getValue());
					userInfor.setMemType(memberType);//用户类型 0-普通会员 1-店长 2-管理员
					userInfor.setPcCode(pcCode);

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
	 * @Description: 修改昵称
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult resetPwd(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(RESET_PWD, params);
		
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
						String videoWhirl = getInforFromJason(json, "videoWhirl");
						DeviceInfor deviceInfor = new DeviceInfor();
						deviceInfor.setVideoWhirl(videoWhirl);
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
	 * @Description: 获取二级管理员列表
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getSecondLevels(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_SECOND_LEVEL, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					List<UserInfor> tempList = new ArrayList<UserInfor>();
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObj = new JSONObject(info);
					String rows = getInforFromJason(jsonObj, "rows");
					
					JSONArray jsonArray = new JSONArray(rows);
					int count = jsonArray.length();
					for(int i=0;i<count;i++)
					{
						JSONObject json = jsonArray.getJSONObject(i);
						String userName = getInforFromJason(json, "nickName");
						String userId = getInforFromJason(json, "id");
						UserInfor infor = new UserInfor();
						infor.setUserName(userName);
						infor.setUserId(userId);
						tempList.add(infor);
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
							tempInfor.setResId(id);
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
					JSONObject jsonObj = new JSONObject(info);
					String total = getInforFromJason(jsonObj, "total");
					if(!TextUtils.isEmpty(total))
					{
						int totalInt = Integer.parseInt(total);
						httpResult.setTotal(totalInt);
					}
					String rows = getInforFromJason(jsonObj, "rows");
					JSONArray jsonArray = new JSONArray(rows);
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
						String playingSong = getInforFromJason(json, "playingSong");
						String playingSongType = getInforFromJason(json, "playingSongType");
						String softVersion = getInforFromJason(json, "softVersion");
						String lastBootTime = getInforFromJason(json, "lastBootTime");
						String onlineStatus = getInforFromJason(json, "onlineStatus");//0，掉线   1在线
						String volume = getInforFromJason(json, "volume");//0，掉线   1在线
						String expiredTime = getInforFromJason(json, "expiredTime");//0，掉线   1在线
						String netInfo = getInforFromJason(json, "netInfo");
						String videoWhirl = getInforFromJason(json, "videoWhirl");
						if(TextUtils.isEmpty(videoWhirl))
							videoWhirl = "0";
						DeviceInfor deviceInfor = new DeviceInfor();
						deviceInfor.setVideoWhirl(videoWhirl);
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
						deviceInfor.setOnlineStatus(onlineStatus);
						deviceInfor.setLastBootTime(lastBootTime);
						deviceInfor.setSoftVersion(softVersion);
						deviceInfor.setCurPlaySong(playingSong);
						deviceInfor.setId(code);
						deviceInfor.setName(name);
						deviceInfor.setWifiName(wifiName);
						deviceInfor.setWifiPwd(wifiPassword);
						deviceInfor.setCode(id);
						deviceInfor.setIp(ip);
						deviceInfor.setAddr(addr);
						deviceInfor.setLat(latitude);
						deviceInfor.setLon(longitude);
						deviceInfor.setVolume(volume);
						deviceInfor.setEndTime(expiredTime);
						deviceInfor.setNetInfor(netInfo);
						if(TextUtils.isEmpty(playingSongType))
							playingSongType = "0";
						deviceInfor.setPlayingSongType(playingSongType);
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
	 * @Description:获取歌单列表
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getMusicAlbums(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_CREATE_AND_COLLECT, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			
			MyAlbumInfor myAlbumInfor = new MyAlbumInfor();
			
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObjectOld = new JSONObject(info);
					
					String myCollectlistSize = getInforFromJason(jsonObjectOld, "myCollectlistSize");
					myAlbumInfor.setCollectAlbumCount(myCollectlistSize);
					String myCollectList = getInforFromJason(jsonObjectOld, "myCollectList");
					JSONArray jsonArray = new JSONArray(myCollectList);
					int count = jsonArray.length();
					for(int i=0;i<count;i++)
					{
						JSONObject json = jsonArray.getJSONObject(i);
						String id = getInforFromJason(json, "id");
						String name = getInforFromJason(json, "name");
						String imageUrl = getInforFromJason(json, "imageUrl");
						String musicNumber = getInforFromJason(json, "musicNumber");
						String description = getInforFromJason(json, "description");
						
						MusicAlbumInfor infor = new MusicAlbumInfor();
						infor.setAlbumId(id);
						infor.setAlbumName(name);
						infor.setCover(imageUrl);
						infor.setMusicCount(musicNumber);
						infor.setDescription(description);
						myAlbumInfor.addInforToCollect(infor);
					}
					
					String myCreatelistSize = getInforFromJason(jsonObjectOld, "myCreatelistSize");
					myAlbumInfor.setCreateAlbumCount(myCreatelistSize);
					String myCreateList = getInforFromJason(jsonObjectOld, "myCreateList");
					JSONArray jsonArray1 = new JSONArray(myCreateList);
					int count1 = jsonArray1.length();
					for(int j=0;j<count1;j++)
					{
						JSONObject json = jsonArray1.getJSONObject(j);
						String id = getInforFromJason(json, "id");
						String name = getInforFromJason(json, "name");
						String imageUrl = getInforFromJason(json, "imageUrl");
						String musicNumber = getInforFromJason(json, "musicNumber");
						String description = getInforFromJason(json, "description");
						
						MusicAlbumInfor infor = new MusicAlbumInfor();
						infor.setAlbumId(id);
						infor.setAlbumName(name);
						infor.setCover(imageUrl);
						infor.setMusicCount(musicNumber);
						infor.setDescription(description);
						myAlbumInfor.addInforToCreate(infor);
					}
					
					
					httpResult.setResult(true, myAlbumInfor);
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
	 * @Description:获取总部歌单列表
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getMyParentMusicAlbums(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_MY_PARENT_MUSIC_CATEGORY, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			
			MyAlbumInfor myAlbumInfor = new MyAlbumInfor();
			
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObjectOld = new JSONObject(info);
					
					String myCollectlistSize = getInforFromJason(jsonObjectOld, "myCollectlistSize");
					myAlbumInfor.setCollectAlbumCount(myCollectlistSize);
					String myCollectList = getInforFromJason(jsonObjectOld, "myCollectList");
					JSONArray jsonArray = new JSONArray(myCollectList);
					int count = jsonArray.length();
					for(int i=0;i<count;i++)
					{
						JSONObject json = jsonArray.getJSONObject(i);
						String id = getInforFromJason(json, "id");
						String name = getInforFromJason(json, "name");
						String imageUrl = getInforFromJason(json, "imageUrl");
						String musicNumber = getInforFromJason(json, "musicNumber");
						String description = getInforFromJason(json, "description");
						
						MusicAlbumInfor infor = new MusicAlbumInfor();
						infor.setAlbumId(id);
						infor.setAlbumName(name);
						infor.setCover(imageUrl);
						infor.setMusicCount(musicNumber);
						infor.setDescription(description);
						myAlbumInfor.addInforToCollect(infor);
					}
					
					String myCreatelistSize = getInforFromJason(jsonObjectOld, "myCreatelistSize");
					myAlbumInfor.setCreateAlbumCount(myCreatelistSize);
					String myCreateList = getInforFromJason(jsonObjectOld, "myCreateList");
					JSONArray jsonArray1 = new JSONArray(myCreateList);
					int count1 = jsonArray1.length();
					for(int j=0;j<count1;j++)
					{
						JSONObject json = jsonArray1.getJSONObject(j);
						String id = getInforFromJason(json, "id");
						String name = getInforFromJason(json, "name");
						String imageUrl = getInforFromJason(json, "imageUrl");
						String musicNumber = getInforFromJason(json, "musicNumber");
						String description = getInforFromJason(json, "description");
						
						MusicAlbumInfor infor = new MusicAlbumInfor();
						infor.setAlbumId(id);
						infor.setAlbumName(name);
						infor.setCover(imageUrl);
						infor.setMusicCount(musicNumber);
						infor.setDescription(description);
						myAlbumInfor.addInforToCreate(infor);
					}
					
					
					httpResult.setResult(true, myAlbumInfor);
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
	 * @Description:获取系统推荐的歌单
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getCreateAlbums(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_CREATE_ALBUMS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					List<MusicAlbumInfor> tempList = new ArrayList<MusicAlbumInfor>();
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObj = new JSONObject(info);
					String total = getInforFromJason(jsonObj, "listSize");
					String content = getInforFromJason(jsonObj, "infoList");
					JSONArray jsonArray = new JSONArray(content);
					int count = jsonArray.length();
					for(int i=0;i<count;i++)
					{
						JSONObject json = jsonArray.getJSONObject(i);
						String id = getInforFromJason(json, "id");
						String name = getInforFromJason(json, "name");
						String imageUrl = getInforFromJason(json, "imageUrl");
						String musicNumber = getInforFromJason(json, "musicNumber");
						String description = getInforFromJason(json, "description");
						
						MusicAlbumInfor infor = new MusicAlbumInfor();
						infor.setAlbumId(id);
						infor.setAlbumName(name);
						infor.setCover(imageUrl);
						infor.setMusicCount(musicNumber);
						tempList.add(infor);
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
	 * @Description:获取系统推荐的歌单
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getSystemAlbums(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_SYSTEM_ALBUMS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					List<MusicAlbumInfor> tempList = new ArrayList<MusicAlbumInfor>();
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObj = new JSONObject(info);
					String total = getInforFromJason(jsonObj, "listSize");
					String content = getInforFromJason(jsonObj, "infoList");
					JSONArray jsonArray = new JSONArray(content);
					int count = jsonArray.length();
					for(int i=0;i<count;i++)
					{
						JSONObject json = jsonArray.getJSONObject(i);
						String id = getInforFromJason(json, "id");
						String name = getInforFromJason(json, "name");
						String imageUrl = getInforFromJason(json, "imageUrl");
						String musicNumber = getInforFromJason(json, "musicNumber");
						String description = getInforFromJason(json, "description");
						String collectStatus = getInforFromJason(json, "collectStatus");
						
						MusicAlbumInfor infor = new MusicAlbumInfor();
						infor.setAlbumId(id);
						infor.setAlbumName(name);
						infor.setCover(imageUrl);
						infor.setMusicCount(musicNumber);
						infor.setDescription(description);
						infor.setCollectStatus(collectStatus);
						tempList.add(infor);
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
	 * @Description:获取歌单歌曲
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getAlbumMusics(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_ALBUM_MUSIC, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					List<MediaInfor> tempList = new ArrayList<MediaInfor>();
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObj = new JSONObject(info);
					String total = getInforFromJason(jsonObj, "listSize");
					String content = getInforFromJason(jsonObj, "infoList");
					JSONArray jsonArray = new JSONArray(content);
					int count = jsonArray.length();
					for(int i=0;i<count;i++)
					{
						JSONObject json = jsonArray.getJSONObject(i);
						String id = getInforFromJason(json, "id");
						String musicId = getInforFromJason(json, "musicId");
						String musicUrl = getInforFromJason(json, "musicUrl");
						String musicName = getInforFromJason(json, "musicName");
						String musicAlbum = getInforFromJason(json, "musicAlbum");
						String musicSing = getInforFromJason(json, "musicSing");
						//String addTime = getInforFromJason(json, "addTime");
						
						MediaInfor infor = new MediaInfor();
						infor.setMediaId(id);//系统中分配的id
						infor.setResId(musicId);
						infor.setMediaName(musicName);
						infor.setMediaUrl(UrlUtils.decodeUrl(musicUrl));
						infor.setArtist(musicSing);
						infor.setAlbumName(musicAlbum);
						tempList.add(infor);
					}
					if(!TextUtils.isEmpty(total))
						httpResult.setTotal(Integer.parseInt(total));
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
	 * @Description:创建歌单
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult createAlbum(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(CREATE_ALBUM, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					httpResult.setResult(true, "创建成功");
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
	 * @Description:修改歌单
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult editAlbum(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(EDIT_ALBUM, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					httpResult.setResult(true, "创建成功");
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
	 * @Description:创建歌单
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult deleteAlbum(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(DELETE_ALBUM, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					httpResult.setResult(true, "删除成功");
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
	 * @Description:收藏歌单
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult collectAlbum(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(CLOLLECT_ALBUM, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					httpResult.setResult(true, "收藏成功");
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
	 * @Description:删除歌单歌曲
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult deleteAlbumMusic(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(DELETE_ALBUM_MUSIC, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					httpResult.setResult(true, "删除成功");
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
	 * @Description:删除歌单歌曲
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult deleteAlbumMusics(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(DELETE_ALBUM_MUSICS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					httpResult.setResult(true, "删除成功");
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
	 * 添加播放计划
	* @Description: TODO
	* @param @param params
	* @param @return   
	* @return HttpResult 
	* @throws
	 */
	protected HttpResult addPlan(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(ADD_PLAN, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					httpResult.setResult(true, "创建成功");
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
	 * 修改播放计划
	* @Description: TODO
	* @param @param params
	* @param @return   
	* @return HttpResult 
	* @throws
	 */
	protected HttpResult editPlan(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(EDIT_PLAN, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					httpResult.setResult(true, "修改成功");
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
	 * 获取当前播放计划
	* @Description: TODO
	* @param @param params
	* @param @return   
	* @return HttpResult 
	* @throws
	 */
	protected HttpResult getCurPlan(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_CUR_PLAN, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					List<PlanInfor> planList = new ArrayList<PlanInfor>();
					String info = jsonObject.getString(RESULT_INFO);
					JSONObject json = new JSONObject(info);
					String total = getInforFromJason(json, "total");
					if(!TextUtils.isEmpty(total))
						httpResult.setTotal(Integer.parseInt(total));
					String rows = getInforFromJason(json, "rows");
					JSONArray jsonArray = new JSONArray(rows);
					int len = jsonArray.length();
					for(int i=0;i<len;i++)
					{
						PlanInfor planInfor = new PlanInfor();
						List<SubPlanInfor> subPlanList = new ArrayList<SubPlanInfor>();
						JSONObject json1 = (JSONObject) jsonArray.get(i);
						String id = getInforFromJason(json1, "id");
						String pslist = getInforFromJason(json1, "pslist");
						String planFlag = getInforFromJason(json1, "planFlag");//0全部的、1 指定的
						String planType = getInforFromJason(json1, "planType");//1每天  ;0，节日，日期必须要填写
						String endDate = getInforFromJason(json1, "endDate");
						String startDate = getInforFromJason(json1, "startDate");
						String planName = getInforFromJason(json1, "planName");
						String tlist = getInforFromJason(json1, "tlist");
						String publishTime = getInforFromJason(json1, "publishTime");
						//String status = getInforFromJason(json1, "status");
						String terminalList = getInforFromJason(json1, "terminalList");
						
						//String memberId = getInforFromJason(json1, "memberId");
						planInfor.setPlanId(id);
						planInfor.setPlanFlag(planFlag);
						planInfor.setPlanName(planName);
						planInfor.setPublishTime(publishTime);
						planInfor.setTerminalList(terminalList);
						planInfor.setPlanType(planType);
						
						if(!TextUtils.isEmpty(startDate))
						{
							long dateLong = Long.parseLong(startDate);
							planInfor.setStartDate(DateUtils.getStringTimeHead(dateLong));
						}
						if(!TextUtils.isEmpty(endDate))
						{
							long dateLong = Long.parseLong(endDate);
							planInfor.setEndDate(DateUtils.getStringTimeHead(dateLong));
						}
						
						
						JSONArray jsonArray0 = new JSONArray(pslist);
						int len0 = jsonArray0.length();
						for(int j=0;j<len0;j++)
						{
							JSONObject json2 = (JSONObject) jsonArray0.get(j);
							SubPlanInfor subPlanInfor = new SubPlanInfor();
							
							String startTime = getInforFromJason(json2, "startTime");
							String id1 = getInforFromJason(json2, "id");
							/*String cycleType = getInforFromJason(json2, "cycleType");
							String publishId = getInforFromJason(json2, "publishId");
							String musicCategoryList = getInforFromJason(json2, "musicCategoryList");*/
							String endTime = getInforFromJason(json2, "endTime");
							if(!TextUtils.isEmpty(startTime) && startTime.contains(":"))
							{
								subPlanInfor.setStartTime(StringUtils.getPlanTimeShow(startTime));
							}
							if(!TextUtils.isEmpty(endTime) && endTime.contains(":"))
							{
								subPlanInfor.setEndTime(StringUtils.getPlanTimeShow(endTime));
							}
							
							String loopAddNum = getInforFromJason(json2, "loopAddNum");
							String cycleType = getInforFromJason(json2, "cycleType");
							String loopMusicName = getInforFromJason(json2, "loopMusicName");
							String loopMusicInfoId = getInforFromJason(json2, "loopMusicInfoId");
							subPlanInfor.setLoopAddNum(loopAddNum);
							subPlanInfor.setLoopMusicInfoId(loopMusicInfoId);
							subPlanInfor.setLoopMusicName(loopMusicName);
							subPlanInfor.setCycleType(cycleType);
							
							subPlanInfor.setId(id1);
							List<MusicAlbumInfor> albumList = new ArrayList<MusicAlbumInfor>();
							
							String mclist = getInforFromJason(json2, "mclist");
							JSONArray jsonArray1 = new JSONArray(mclist);
							int size1 = jsonArray1.length();
							for(int k=0;k<size1;k++)
							{
								MusicAlbumInfor musicAlbumInfor = new MusicAlbumInfor();
								JSONObject json3 = jsonArray1.getJSONObject(k);
								String id2 = getInforFromJason(json3, "id");
								//String status1 = getInforFromJason(json3, "status");
								//String remark = getInforFromJason(json3, "remark");
								String imageUrl = getInforFromJason(json3, "imageUrl");
								//String lastUpdateTime = getInforFromJason(json3, "lastUpdateTime");
								String description = getInforFromJason(json3, "description");
								String name = getInforFromJason(json3, "name");
								//String ordinal = getInforFromJason(json3, "ordinal");
								//String memberId1 = getInforFromJason(json3, "memberId");
								//String type = getInforFromJason(json3, "type");
								//String sysUserId = getInforFromJason(json3, "sysUserId");
								//String addTime = getInforFromJason(json3, "addTime");
								
								musicAlbumInfor.setAlbumId(id2);
								musicAlbumInfor.setAlbumName(name);
								musicAlbumInfor.setDescription(description);
								musicAlbumInfor.setCover(imageUrl);
								
								albumList.add(musicAlbumInfor);
								
							}
							subPlanInfor.setAlbumList(albumList);
							subPlanList.add(subPlanInfor);
							
						}
						planInfor.setSubPlanList(subPlanList);
						if(planFlag.equals("1"))//指定的店铺
						{
							List<DeviceInfor> deviceList = new ArrayList<DeviceInfor>();
							JSONArray jsonArray1 = new JSONArray(tlist);
							int len1 = jsonArray1.length();
							for(int j=0;j<len1;j++)
							{
								DeviceInfor deviceInfor = new DeviceInfor();
								JSONObject json2 = (JSONObject) jsonArray1.get(j);
								//String lastMusicUpdate = getInforFromJason(json2, "lastMusicUpdate");
								String wifiName = getInforFromJason(json2, "wifiName");
								String wifiPassword = getInforFromJason(json2, "wifiPassword");
								//String wifiMac = getInforFromJason(json2, "wifiMac");
								String playingSong = getInforFromJason(json2, "playingSong");
								String softVersion = getInforFromJason(json2, "softVersion");
								//String vodFlag = getInforFromJason(json2, "vodFlag");
								String code = getInforFromJason(json2, "code");
								String lastBootTime = getInforFromJason(json2, "lastBootTime");
								String onlineStatus = getInforFromJason(json2, "onlineStatus");
								//String geohash = getInforFromJason(json2, "geohash");
								String ip = getInforFromJason(json2, "ip");
								String id2 = getInforFromJason(json2, "id");
								//String publishPlanId = getInforFromJason(json2, "publishPlanId");
								String distance = getInforFromJason(json2, "distance");
								String address = getInforFromJason(json2, "address");
								String name = getInforFromJason(json2, "name");
								//String hardVersion = getInforFromJason(json2, "hardVersion");
								String longitude = getInforFromJason(json2, "longitude");
								String latitude = getInforFromJason(json2, "latitude");
								//String playStatus = getInforFromJason(json2, "playStatus");
								//String addTime = getInforFromJason(json2, "longitude");
								deviceInfor.setName(name);
								deviceInfor.setAddr(address);
								deviceInfor.setLat(latitude);
								deviceInfor.setLon(longitude);
								deviceInfor.setIp(ip);
								deviceInfor.setLastBootTime(lastBootTime);
								deviceInfor.setOnlineStatus(onlineStatus);
								deviceInfor.setCurPlaySong(playingSong);
								deviceInfor.setVersion(softVersion);
								deviceInfor.setCode(id2);
								deviceInfor.setWifiName(wifiName);
								deviceInfor.setWifiPwd(wifiPassword);
								deviceInfor.setId(code);
								deviceList.add(deviceInfor);
							}
							planInfor.setDeviceList(deviceList);
						}
						
						planList.add(planInfor);
					}
					httpResult.setResult(true, planList);
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
	protected HttpResult getBoxPlan(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_BOX_PLAN, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					List<PlanInfor> planList = new ArrayList<PlanInfor>();
					String info = jsonObject.getString(RESULT_INFO);
					if(!TextUtils.isEmpty(info))
					{
						
						JSONObject json = new JSONObject(info);
					
						PlanInfor planInfor = new PlanInfor();
						List<SubPlanInfor> subPlanList = new ArrayList<SubPlanInfor>();
						String id = getInforFromJason(json, "id");
						String pslist = getInforFromJason(json, "pslist");
						String planFlag = getInforFromJason(json, "planFlag");//0全部的、1 指定的
						String planType = getInforFromJason(json, "planType");//1每天  ;0，节日，日期必须要填写
						String endDate = getInforFromJason(json, "endDate");
						String startDate = getInforFromJason(json, "startDate");
						String planName = getInforFromJason(json, "planName");
						String tlist = getInforFromJason(json, "tlist");
						String publishTime = getInforFromJason(json, "publishTime");
						//String status = getInforFromJason(json1, "status");
						String terminalList = getInforFromJason(json, "terminalList");
						
						//String memberId = getInforFromJason(json1, "memberId");
						planInfor.setPlanId(id);
						planInfor.setPlanFlag(planFlag);
						planInfor.setPlanName(planName);
						planInfor.setPublishTime(publishTime);
						planInfor.setTerminalList(terminalList);
						planInfor.setPlanType(planType);
						
						if(!TextUtils.isEmpty(startDate))
						{
							long dateLong = Long.parseLong(startDate);
							planInfor.setStartDate(DateUtils.getStringTimeHead(dateLong));
						}
						if(!TextUtils.isEmpty(endDate))
						{
							long dateLong = Long.parseLong(endDate);
							planInfor.setEndDate(DateUtils.getStringTimeHead(dateLong));
						}
						
						
						JSONArray jsonArray0 = new JSONArray(pslist);
						int len0 = jsonArray0.length();
						for(int j=0;j<len0;j++)
						{
							JSONObject json2 = (JSONObject) jsonArray0.get(j);
							SubPlanInfor subPlanInfor = new SubPlanInfor();
							
							String startTime = getInforFromJason(json2, "startTime");
							String id1 = getInforFromJason(json2, "id");
							/*String cycleType = getInforFromJason(json2, "cycleType");
							String publishId = getInforFromJason(json2, "publishId");
							String musicCategoryList = getInforFromJason(json2, "musicCategoryList");*/
							String endTime = getInforFromJason(json2, "endTime");
							if(!TextUtils.isEmpty(startTime) && startTime.contains(":"))
							{
								subPlanInfor.setStartTime(StringUtils.getPlanTimeShow(startTime));
							}
							if(!TextUtils.isEmpty(endTime) && endTime.contains(":"))
							{
								subPlanInfor.setEndTime(StringUtils.getPlanTimeShow(endTime));
							}
							
							String loopAddNum = getInforFromJason(json2, "loopAddNum");
							String cycleType = getInforFromJason(json2, "cycleType");
							String loopMusicName = getInforFromJason(json2, "loopMusicName");
							String loopMusicInfoId = getInforFromJason(json2, "loopMusicInfoId");
							subPlanInfor.setLoopAddNum(loopAddNum);
							subPlanInfor.setLoopMusicInfoId(loopMusicInfoId);
							subPlanInfor.setLoopMusicName(loopMusicName);
							subPlanInfor.setCycleType(cycleType);
							
							subPlanInfor.setId(id1);
							List<MusicAlbumInfor> albumList = new ArrayList<MusicAlbumInfor>();
							
							String mclist = getInforFromJason(json2, "mclist");
							JSONArray jsonArray1 = new JSONArray(mclist);
							int size1 = jsonArray1.length();
							for(int k=0;k<size1;k++)
							{
								MusicAlbumInfor musicAlbumInfor = new MusicAlbumInfor();
								JSONObject json3 = jsonArray1.getJSONObject(k);
								String id2 = getInforFromJason(json3, "id");
								//String status1 = getInforFromJason(json3, "status");
								//String remark = getInforFromJason(json3, "remark");
								String imageUrl = getInforFromJason(json3, "imageUrl");
								//String lastUpdateTime = getInforFromJason(json3, "lastUpdateTime");
								String description = getInforFromJason(json3, "description");
								String name = getInforFromJason(json3, "name");
								//String ordinal = getInforFromJason(json3, "ordinal");
								//String memberId1 = getInforFromJason(json3, "memberId");
								//String type = getInforFromJason(json3, "type");
								//String sysUserId = getInforFromJason(json3, "sysUserId");
								//String addTime = getInforFromJason(json3, "addTime");
								
								musicAlbumInfor.setAlbumId(id2);
								musicAlbumInfor.setAlbumName(name);
								musicAlbumInfor.setDescription(description);
								musicAlbumInfor.setCover(imageUrl);
								
								albumList.add(musicAlbumInfor);
								
							}
							subPlanInfor.setAlbumList(albumList);
							subPlanList.add(subPlanInfor);
							
						}
						planInfor.setSubPlanList(subPlanList);
						if(planFlag.equals("1") && !TextUtils.isEmpty(tlist))//指定的店铺
						{
							List<DeviceInfor> deviceList = new ArrayList<DeviceInfor>();
							JSONArray jsonArray1 = new JSONArray(tlist);
							int len1 = jsonArray1.length();
							for(int j=0;j<len1;j++)
							{
								DeviceInfor deviceInfor = new DeviceInfor();
								JSONObject json2 = (JSONObject) jsonArray1.get(j);
								//String lastMusicUpdate = getInforFromJason(json2, "lastMusicUpdate");
								String wifiName = getInforFromJason(json2, "wifiName");
								String wifiPassword = getInforFromJason(json2, "wifiPassword");
								//String wifiMac = getInforFromJason(json2, "wifiMac");
								String playingSong = getInforFromJason(json2, "playingSong");
								String softVersion = getInforFromJason(json2, "softVersion");
								//String vodFlag = getInforFromJason(json2, "vodFlag");
								String code = getInforFromJason(json2, "code");
								String lastBootTime = getInforFromJason(json2, "lastBootTime");
								String onlineStatus = getInforFromJason(json2, "onlineStatus");
								//String geohash = getInforFromJason(json2, "geohash");
								String ip = getInforFromJason(json2, "ip");
								String id2 = getInforFromJason(json2, "id");
								//String publishPlanId = getInforFromJason(json2, "publishPlanId");
								String distance = getInforFromJason(json2, "distance");
								String address = getInforFromJason(json2, "address");
								String name = getInforFromJason(json2, "name");
								//String hardVersion = getInforFromJason(json2, "hardVersion");
								String longitude = getInforFromJason(json2, "longitude");
								String latitude = getInforFromJason(json2, "latitude");
								//String playStatus = getInforFromJason(json2, "playStatus");
								//String addTime = getInforFromJason(json2, "longitude");
								deviceInfor.setName(name);
								deviceInfor.setAddr(address);
								deviceInfor.setLat(latitude);
								deviceInfor.setLon(longitude);
								deviceInfor.setIp(ip);
								deviceInfor.setLastBootTime(lastBootTime);
								deviceInfor.setOnlineStatus(onlineStatus);
								deviceInfor.setCurPlaySong(playingSong);
								deviceInfor.setVersion(softVersion);
								deviceInfor.setCode(id2);
								deviceInfor.setWifiName(wifiName);
								deviceInfor.setWifiPwd(wifiPassword);
								deviceInfor.setId(code);
								deviceList.add(deviceInfor);
							}
							planInfor.setDeviceList(deviceList);
						}
						
						planList.add(planInfor);
					}
					httpResult.setResult(true, planList);
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
	 * 添加歌曲到专辑
	 * @Description: TODO
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult addMusicToAlbum(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(ADD_MUSIC_TO_ALBUM, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					httpResult.setResult(true, "添加成功");
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
	 * 添加歌曲到专辑
	 * @Description: TODO
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult addSysMusicToAlbum(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(ADD_SYS_MUSICS_TO_MY_ALBUM, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					httpResult.setResult(true, "添加成功");
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
	 * 推送歌曲
	 * @Description: TODO
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult pushMusic(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(PUSH_MUSIC, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					
					httpResult.setResult(true, "操作成功");
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
	 * @Description:获取当前播放计划的歌曲列表
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getPlanMusics(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_PLAN_MUSICS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObject1 = new JSONObject(info);
					String total = getInforFromJason(jsonObject1, "listSize");
					int totalInt = 0;
					if(!TextUtils.isEmpty(total))
						totalInt = Integer.parseInt(total);
					httpResult.setTotal(totalInt);
					
					String playingPos = getInforFromJason(jsonObject1, "playingPos");
					int playingPosInt = 0;
					if(!TextUtils.isEmpty(playingPos))
					{
						playingPosInt = Integer.parseInt(playingPos);
					}
					
					String seekPos = getInforFromJason(jsonObject1, "playSeek");
					long seekPosInt = 0;
					if(!TextUtils.isEmpty(seekPos))
						seekPosInt = Integer.parseInt(seekPos);
					String lastUpdateTime = getInforFromJason(jsonObject1, "lastUpdateTime");
					if(!TextUtils.isEmpty(lastUpdateTime))
					{
						long lastUpdateTimeInt = Long.parseLong(lastUpdateTime);
						seekPosInt = seekPosInt + (System.currentTimeMillis() - lastUpdateTimeInt);
					}
					
					String listInfo = getInforFromJason(jsonObject1, "infoList");
					JSONArray jsonArray = new JSONArray(listInfo);
					int size = jsonArray.length();
					
					List<SongInfor> tempList = new ArrayList<SongInfor>();
					//List<String> urls = new ArrayList<String>();
					if(size > 0)
					{
						/*if(totalInt > 1)
							DBManager.newInstance(context).deleteAllRecord();*/
						
						for(int i=0;i<size;i++)
						{
							JSONObject json = jsonArray.getJSONObject(i);
							String musicAlbum = getInforFromJason(json, "musicAlbum");
							String musicId = getInforFromJason(json, "musicId");
							String id = getInforFromJason(json, "id");
							String musicUrl = getInforFromJason(json, "musicUrl");
							String musicName = getInforFromJason(json, "musicName");
							String musicSing = getInforFromJason(json, "musicSing");
							if(!TextUtils.isEmpty(musicUrl))
								musicUrl = UrlUtils.decodeUrl(musicUrl);
							SongInfor tempInfor = new SongInfor();
							tempInfor.setMediaId(id);
							tempInfor.setResId(musicId);
							tempInfor.setMediaName(musicName);
							tempInfor.setMediaUrl(musicUrl);
							tempInfor.setArtist(musicSing);
							tempInfor.setAlbumName(musicAlbum);
							tempList.add(tempInfor);
							if(playingPosInt == i)
							{
								tempInfor.setIsPlaying(true);
								tempInfor.setCurPlayTime(seekPosInt);
							}
							else
								tempInfor.setIsPlaying(false);
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
	 * @Description:获取全部计划的歌曲列表
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getAllPlanMusics(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_ALL_PLAN_MUSICS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObject1 = new JSONObject(info);
					String total = getInforFromJason(jsonObject1, "listSize");
					int totalInt = 0;
					if(!TextUtils.isEmpty(total))
						totalInt = Integer.parseInt(total);
					httpResult.setTotal(totalInt);
					String listInfo = getInforFromJason(jsonObject1, "infoList");
					JSONArray jsonArray = new JSONArray(listInfo);
					int size = jsonArray.length();
					List<SongInfor> tempList = new ArrayList<SongInfor>();
					//List<String> urls = new ArrayList<String>();
					if(size > 0)
					{
						/*if(totalInt > 1)
							DBManager.newInstance(context).deleteAllRecord();*/
						
						for(int i=0;i<size;i++)
						{
							JSONObject json = jsonArray.getJSONObject(i);
							String musicAlbum = getInforFromJason(json, "musicAlbum");
							String musicId = getInforFromJason(json, "musicId");
							String id = getInforFromJason(json, "id");
							String musicUrl = getInforFromJason(json, "musicUrl");
							String musicName = getInforFromJason(json, "musicName");
							String musicSing = getInforFromJason(json, "musicSing");
							if(!TextUtils.isEmpty(musicUrl))
								musicUrl = UrlUtils.decodeUrl(musicUrl);
							SongInfor tempInfor = new SongInfor();
							tempInfor.setMediaId(id);
							tempInfor.setResId(musicId);
							tempInfor.setMediaName(musicName);
							tempInfor.setMediaUrl(musicUrl);
							tempInfor.setArtist(musicSing);
							tempInfor.setAlbumName(musicAlbum);
							tempList.add(tempInfor);
							/*if(totalInt > 1)
								DBManager.newInstance(context).insertSongRecord(tempInfor);*/
							/*if(!isFileExsit(musicUrl))
								urls.add(musicUrl);*/
							//downLoadFile(musicUrl);
						}
					}
					
					/*if(urls.size() > 0)
					{
						if(fileDownLoadUtil == null)
							fileDownLoadUtil = FileDownLoadUtil.getInstance(context);
						fileDownLoadUtil.setDownloadFiles(urls);
						fileDownLoadUtil.startDownloadFiles();
					}*/
					
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
	 * @Description:获取全部计划的歌曲列表
	 * @param @param params
	 * @param @return   
	 * @return HttpResult 
	 * @throws
	 */
	protected HttpResult getPushHistoryMusics(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(GET_PUSH_HISTORY_MUSICS, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				/*int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)*/
				{
					/*String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObject1 = new JSONObject(info);
					String total = getInforFromJason(jsonObject1, "listSize");
					int totalInt = 0;
					if(!TextUtils.isEmpty(total))
						totalInt = Integer.parseInt(total);
					httpResult.setTotal(totalInt);*/
					
					String listInfo = getInforFromJason(jsonObject, "infoList");
					String total = getInforFromJason(jsonObject, "listSize");
					int totalInt = 0;
					if(!TextUtils.isEmpty(total))
						totalInt = Integer.parseInt(total);
					httpResult.setTotal(totalInt);
					JSONArray jsonArray = new JSONArray(listInfo);
					int size = jsonArray.length();
					List<SongInfor> tempList = new ArrayList<SongInfor>();
					//List<String> urls = new ArrayList<String>();
					if(size > 0)
					{
						/*if(totalInt > 1)
							DBManager.newInstance(context).deleteAllRecord();*/
						
						for(int i=0;i<size;i++)
						{
							JSONObject json = jsonArray.getJSONObject(i);
							String musicAlbum = getInforFromJason(json, "musicAlbum");
							String musicId = getInforFromJason(json, "musicId");
							String id = getInforFromJason(json, "id");
							String musicUrl = getInforFromJason(json, "musicUrl");
							String musicName = getInforFromJason(json, "musicName");
							String musicSing = getInforFromJason(json, "musicSing");
							if(!TextUtils.isEmpty(musicUrl))
								musicUrl = UrlUtils.decodeUrl(musicUrl);
							SongInfor tempInfor = new SongInfor();
							tempInfor.setMediaId(id);
							tempInfor.setResId(musicId);
							tempInfor.setMediaName(musicName);
							tempInfor.setMediaUrl(musicUrl);
							tempInfor.setArtist(musicSing);
							tempInfor.setAlbumName(musicAlbum);
							tempList.add(tempInfor);
							/*if(totalInt > 1)
								DBManager.newInstance(context).insertSongRecord(tempInfor);*/
							/*if(!isFileExsit(musicUrl))
								urls.add(musicUrl);*/
							//downLoadFile(musicUrl);
						}
					}
					
					/*if(urls.size() > 0)
					{
						if(fileDownLoadUtil == null)
							fileDownLoadUtil = FileDownLoadUtil.getInstance(context);
						fileDownLoadUtil.setDownloadFiles(urls);
						fileDownLoadUtil.startDownloadFiles();
					}*/
					
					httpResult.setResult(true, tempList);
				}
				/*else
				{
					httpResult.setResult(false, jsonObject.getString(RESULT_INFO));
				}*/
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
	 * 更新音响信息
	 * @param params
	 * @return
	 */
	protected HttpResult updateSpeakerInfor(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(UPDATE_SPEAKER_INFOR, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String code = getInforFromJason(jsonObject, RESULT_INFO);
					httpResult.setResult(true, code);
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
	 * 更新音响信息
	 * @param params
	 * @return
	 */
	protected HttpResult startOldPlan(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(PLAN_START, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String code = getInforFromJason(jsonObject, RESULT_INFO);
					httpResult.setResult(true, code);
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
	 * 更新音响信息
	 * @param params
	 * @return
	 */
	protected HttpResult deleteOldPlan(List<NameValuePair> params) 
	{
		
		HttpResult httpResult = connect(PLAN_DELETE, params);
		
		if(httpResult.isSuccess() && !isStop)
		{
			JSONObject jsonObject = (JSONObject)httpResult.getReuslt();
			try
			{
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String code = getInforFromJason(jsonObject, RESULT_INFO);
					httpResult.setResult(true, code);
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
		if(url.startsWith("https") || url.startsWith("http"))
			return initSSLAllWithHttpClient(url, params);
		
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
	
	/** 
     * HttpClient方式实现，支持所有Https免验证方式链接 
     *  
     * @throws ClientProtocolException 
     * @throws IOException 
     */  
    public HttpResult initSSLAllWithHttpClient(String url, List<NameValuePair> params)
    {  
        HttpResult httpResult = new HttpResult();
        HttpParams param = new BasicHttpParams();  
        HttpConnectionParams.setConnectionTimeout(param, HTTP_CONN_TIMEOUT);  
        HttpConnectionParams.setSoTimeout(param, HTTP_SOCKET_TIMEOUT);  
        HttpConnectionParams.setTcpNoDelay(param, true);  
        
        SchemeRegistry registry = new SchemeRegistry();  
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));  
        registry.register(new Scheme("https", TrustAllSSLSocketFactory.getDefault(), 443));  
        ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);  
        DefaultHttpClient client = new DefaultHttpClient(manager, param);  
  
        HttpPost request = new HttpPost(url);  
        //HttpGet request = new HttpGet("https://certs.cac.washington.edu/CAtest/");  
        // HttpGet request = new HttpGet("https://www.alipay.com/");
        try
		{
	        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
	        HttpResponse response = client.execute(request);  
	        if(isStop)
	        	httpResult.setResult(false, null);
	        
	        if (response.getStatusLine().getStatusCode() == 200)
	        {
	            String strResult = EntityUtils.toString(response.getEntity());
	            httpResult.setResult(true, new JSONObject(strResult));
	        } 
	        else
	        {
	        	 httpResult.setResult(false, response.getStatusLine().toString());
	        	 LogFactory.createLog().e("network error: "+ response.getStatusLine().toString());
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
 
