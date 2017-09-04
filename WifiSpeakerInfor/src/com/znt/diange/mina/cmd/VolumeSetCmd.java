
package com.znt.diange.mina.cmd; 

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.znt.diange.mina.entity.UserInfor;

/** 
 * @ClassName: EditDeviceCmd 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-7 下午2:14:21  
 */
public class VolumeSetCmd extends BaseCmd implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String result = "0";//处理结果  0,成功；1失败
	
	private String volume = "0";
	
	
	public VolumeSetCmd()
	{
		setCmdType(CmdType.SET_DEVICE_VOLUM);
	}
	
	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		try
		{
			json.put("head", getHeader());
			json.put("end", getEnd());
			json.put("result", result);
			json.put("deviceId", getDeviceId());
			json.put("cmdType", getCmdType());
			json.put("volume", volume);
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
		setResult(getInforFromStr("result", jsonStr));
		setDeviceId(getInforFromStr("deviceId", jsonStr));
		setCmdType(getInforFromStr("cmdType", jsonStr));
		setVolume(getInforFromStr("volume", jsonStr));
		
		UserInfor tempUser = new UserInfor();
		String userInfor = getInforFromStr("userInfor", jsonStr);
		if(!TextUtils.isEmpty(userInfor))
		{
			tempUser.toClass(userInfor);
			setUserInfor(tempUser);
		}
	}
	
	public void setVolume(String volume)
	{
		this.volume = volume;
	}
	public String getVolume()
	{
		return volume;
	}
	
	public void setResult(String result)
	{
		this.result = result;
	}
	public String getResult()
	{
		return result;
	}
}