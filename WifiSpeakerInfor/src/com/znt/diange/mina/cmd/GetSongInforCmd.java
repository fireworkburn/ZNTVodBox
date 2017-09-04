/*  
* @Project: WifiSpeakerInfor 
* @User: Administrator 
* @Description: 社区商服项目
* @Author： yan.yu
* @Company：http://www.neldtv.org/
* @Date 2015-8-3 下午2:38:47 
* @Version V1.0   
*/ 

package com.znt.diange.mina.cmd; 

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.mina.entity.UserInfor;

/** 
 * @ClassName: GetSongInforCmd 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-3 下午2:38:47  
 */
public class GetSongInforCmd extends BaseCmd implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private SongInfor songInfor = null;
	
	public GetSongInforCmd()
	{
		setCmdType(CmdType.GET_PLAY_MUSIC_INFOR);
	}
	
	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		try
		{
			json.put("head", getHeader());
			json.put("end", getEnd());
			json.put("deviceId", getDeviceId());
			json.put("cmdType", getCmdType());
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
		setDeviceId(getInforFromStr("deviceId", jsonStr));
		setCmdType(getInforFromStr("cmdType", jsonStr));
		
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
	
}
 
