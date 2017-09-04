
package com.znt.diange.mina.cmd; 

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.znt.diange.mina.entity.UserInfor;

/** 
 * @ClassName: GetDeviceStateCmd 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-9-15 下午5:06:28  
 */
public class GetDeviceStateCmd extends BaseCmd implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	private DeviceInfor deviceInfor = null;
	
	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		try
		{
			json.put("head", getHeader());
			json.put("end", getEnd());
			json.put("deviceId", getDeviceId());
			json.put("cmdType", getCmdType());
			if(deviceInfor != null)
				json.put("deviceInfor", deviceInfor.toJson());
			if(userInfor != null)
				json.put("userInfor", getUserInfor().toJson());
		} 
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	public void toClass(String jsonStr)
	{
		setHeader(getInforFromStr("head", jsonStr));
		setEnd(getInforFromStr("end", jsonStr));
		setDeviceId(getInforFromStr("deviceId", jsonStr));
		setCmdType(getInforFromStr("cmdType", jsonStr));
		
		DeviceInfor devInfor = new DeviceInfor();
		String deviceInfor = getInforFromStr("deviceInfor", jsonStr);
		if(!TextUtils.isEmpty(deviceInfor))
		{
			devInfor.toClass(deviceInfor);
			setDeviceInfor(devInfor);
		}
		
		UserInfor tempUser = new UserInfor();
		String userInfor = getInforFromStr("userInfor", jsonStr);
		if(!TextUtils.isEmpty(userInfor))
		{
			tempUser.toClass(userInfor);
			setUserInfor(tempUser);
		}
	}
	
	public GetDeviceStateCmd()
	{
		setCmdType(CmdType.GET_DEVICE_INFOR);
	}
	
	public void setDeviceInfor(DeviceInfor deviceInfor)
	{
		this.deviceInfor = deviceInfor;
	}
	public DeviceInfor getDeviceInfor()
	{
		return deviceInfor;
	}

}
 
