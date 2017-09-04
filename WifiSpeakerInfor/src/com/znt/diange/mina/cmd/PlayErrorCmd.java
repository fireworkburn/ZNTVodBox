
package com.znt.diange.mina.cmd; 

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.znt.diange.mina.entity.UserInfor;

/** 
 * @ClassName: PlayFinishCmd 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-3 下午2:54:31  
 */
public class PlayErrorCmd extends BaseCmd implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String mediaName = "";
	private String mediaUrl = "";
	
	public PlayErrorCmd()
	{
		setCmdType(CmdType.PLAY_ERROR);
	}
	
	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		try
		{
			json.put("head", getHeader());
			json.put("end", getEnd());
			json.put("mediaName", mediaName);
			json.put("mediaUrl", mediaUrl);
			json.put("deviceId", getDeviceId());
			json.put("cmdType", getCmdType());
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
		setMediaName(getInforFromStr("mediaName", jsonStr));
		setMediaUrl(getInforFromStr("mediaUrl", jsonStr));
		
		UserInfor tempUser = new UserInfor();
		String userInfor = getInforFromStr("userInfor", jsonStr);
		if(!TextUtils.isEmpty(userInfor))
		{
			tempUser.toClass(userInfor);
			setUserInfor(tempUser);
		}
	}
	
	public void setMediaName(String mediaName)
	{
		this.mediaName = mediaName;
	}
	public String getMediaName()
	{
		return mediaName;
	}
	public void setMediaUrl(String mediaUrl)
	{
		this.mediaUrl = mediaUrl;
	}
	public String getMediaUrl()
	{
		return mediaUrl;
	}
}