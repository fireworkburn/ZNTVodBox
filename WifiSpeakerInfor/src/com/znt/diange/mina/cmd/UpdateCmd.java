
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
public class UpdateCmd extends BaseCmd implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	public static final String SongList = "0";
	public static final String PlayInfor = "1";
	public static final String All = "2";
	
	private String updateType = SongList;//0,更新列表 ； 1，更新播放信息；2，列表和信息都更新
	
	public UpdateCmd()
	{
		setCmdType(CmdType.UPDATE_INFOR);
	}
	
	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		try
		{
			json.put("head", getHeader());
			json.put("end", getEnd());
			json.put("updateType", updateType);
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
		setUpdateType(getInforFromStr("updateType", jsonStr));
		
		UserInfor tempUser = new UserInfor();
		String userInfor = getInforFromStr("userInfor", jsonStr);
		if(!TextUtils.isEmpty(userInfor))
		{
			tempUser.toClass(userInfor);
			setUserInfor(tempUser);
		}
	}
	
	public void setUpdateType(String updateType)
	{
		this.updateType = updateType;
	}
	public String getUpdateType()
	{
		return updateType;
	}
}