
package com.znt.diange.mina.cmd; 

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.mina.entity.UserInfor;

/** 
 * @ClassName: PlayCmd 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-3 下午2:44:34  
 */
public class PlayCmd extends BaseCmd implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private SongInfor songInfor = null;
	private String playType = "0";//0,正常点播；1，更新当前点播；2，强制覆盖点播
	private String result = "0";//处理结果  0,成功；1失败; 2,歌曲不存在；3，歌曲已经存在; 4,不允许点播第三方歌曲
	protected String permission = "";
	
	public PlayCmd()
	{
		setCmdType(CmdType.PLAY);
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
			json.put("playType", playType);
			json.put("permission", permission);
			if(songInfor != null)
				json.put("songInfor", songInfor.toJson());
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
		setType(getInforFromStr("playType", jsonStr));
		setPermission(getInforFromStr("permission", jsonStr));
		
		SongInfor tempSong = new SongInfor();
		String songInfor = getInforFromStr("songInfor", jsonStr);
		if(!TextUtils.isEmpty(songInfor))
		{
			tempSong.toClass(songInfor);
			setSongInfor(tempSong);
		}
		
		UserInfor tempUser = new UserInfor();
		String userInfor = getInforFromStr("userInfor", jsonStr);
		if(!TextUtils.isEmpty(userInfor))
		{
			tempUser.toClass(userInfor);
			setUserInfor(tempUser);
		}
	}
	
	public void setSongInfor(SongInfor songInfor)
	{
		this.songInfor = songInfor;
	}
	public SongInfor getSongInfor()
	{
		return songInfor;
	}
	
	public void setType(String type)
	{
		this.playType = type;
	}
	public String getType()
	{
		return playType;
	}
	
	public void setResult(String result)
	{
		this.result = result;
	}
	public String getResult()
	{
		return result;
	}
	
	public void setPermission(String permission)
	{
		this.permission = permission;
	}
	public String getPermission()
	{
		return permission;
	}
}
 
