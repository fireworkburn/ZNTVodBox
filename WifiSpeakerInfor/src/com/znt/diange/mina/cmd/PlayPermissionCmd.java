
package com.znt.diange.mina.cmd; 

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.znt.diange.mina.entity.UserInfor;

/** 
 * @ClassName: PlayCmd 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-3 下午2:44:34  
 */
public class PlayPermissionCmd extends BaseCmd implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String result = "0";//处理结果  0,成功；1失败
	
	//0,开放   1，只允许播放音响本地歌曲  2,允许播放音响本地和网络歌曲  3. 允许播放音响本地和手机本地歌曲
	private String permission = "0";
	
	public PlayPermissionCmd()
	{
		setCmdType(CmdType.PLAY_PERMISSION);
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
			json.put("permission", permission);
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
		setPermission(getInforFromStr("permission", jsonStr));
		
		UserInfor tempUser = new UserInfor();
		String userInfor = getInforFromStr("userInfor", jsonStr);
		if(!TextUtils.isEmpty(userInfor))
		{
			tempUser.toClass(userInfor);
			setUserInfor(tempUser);
		}
	}
	
	public void setPermission(String permission)
	{
		this.permission = permission;
	}
	public String getPermission()
	{
		return permission;
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
 
