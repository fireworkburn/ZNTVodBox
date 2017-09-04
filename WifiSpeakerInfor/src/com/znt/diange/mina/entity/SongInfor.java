
package com.znt.diange.mina.entity; 

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/** 
 * @ClassName: SongInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-22 涓嬪崍4:21:06  
 */
public class SongInfor extends MediaInfor implements Serializable
{
	
	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	public final static int MUSIC_PLAY_TYPE_NORMAL = 0;
	public final static int MUSIC_PLAY_TYPE_PUSH = 1;
	private int coin = 0;
	private UserInfor userInfor = null;
	private String playTime = "";
	private String playMsg = "";
	private int playState = 0;//0 鏈挱鏀撅紝 1 宸叉挱鏀�
	private String trandId = "";
	public int musicPlayType = MUSIC_PLAY_TYPE_NORMAL;//0 正常播放，1，点播
	public boolean isPlaying = false;
	
	
	public SongInfor()
	{
		
	}
	public SongInfor(MediaInfor info)
	{
		setMediaCover(info.getMediaCover());
		setMediaDuration(info.getMediaDuration());
		setMediaId(info.getMediaId());
		setMediaName(info.getMediaName());
		setMediaSize(info.getMediaSize());
		setMediaType(info.getMediaType());
		setMediaUrl(info.getMediaUrl());
		setAlbumName(info.getAlbumName());
		setAlbumUrl(info.getAlbumUrl());
		setArtist(info.getArtist());
		setResourceType(info.getResourceType());
	}
	
	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		try
		{
			json.put("mediaId", getMediaId());
			json.put("mediaName", getMediaName());
			json.put("mediaUrl", getMediaUrl());
			json.put("mediaType", getMediaType());
			
			json.put("coin", coin);
			json.put("trandId", trandId);
			json.put("playTime", playTime);
			json.put("playMsg", playMsg);
			json.put("playState", playState);
			json.put("artist", getArtist());
			json.put("albumUrl", getAlbumUrl());
			if(userInfor != null)
				json.put("userInfor", userInfor.toJson());
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
		try
		{
			JSONObject json = new JSONObject(jsonStr);
			if(json.has("mediaId"))
				setMediaId(json.getString("mediaId"));
			if(json.has("mediaName"))
				setMediaName(json.getString("mediaName"));
			if(json.has("mediaUrl"))
				setMediaUrl(json.getString("mediaUrl"));
			if(json.has("mediaType"))
				setMediaType(json.getString("mediaType"));
			
			if(json.has("coin"))
				setCoin(json.getInt("coin"));
			if(json.has("trandId"))
				setTrandId(json.getString("trandId"));
			if(json.has("playTime"))
				setPlayTime(json.getString("playTime"));
			if(json.has("playMsg"))
				setPlayMsg(json.getString("playMsg"));
			if(json.has("playState"))
				setPlayState(json.getInt("playState"));
			if(json.has("artist"))
				setArtist(json.getString("artist"));
			if(json.has("albumUrl"))
				setAlbumUrl(json.getString("albumUrl"));
			if(json.has("userInfor"))
			{
				String userStr = json.getString("userInfor");
				if(!TextUtils.isEmpty(userStr))
				{
					UserInfor tempInfor = new UserInfor();
					tempInfor.toClass(userStr);
					setUserInfor(tempInfor);
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setCoin(int coin)
	{
		this.coin = coin;
	}
	public int getCoin()
	{
		return coin;
	}
	public void setTrandId(String trandId)
	{
		this.trandId = trandId;
	}
	public String getTrandId()
	{
		return trandId;
	}

	public void setUserInfor(UserInfor userInfor)
	{
		this.userInfor = userInfor;
	}
	public UserInfor getUserInfor()
	{
		if(userInfor == null)
			userInfor = new UserInfor();
		return userInfor;
	}
	
	public void setPlayTime(String playTime)
	{
		this.playTime = playTime;
	}
	public String getPlayTime()
	{
		return playTime;
	}
	
	public void setPlayMsg(String playMsg)
	{
		this.playMsg = playMsg;
	}
	public String getPlayMsg()
	{
		return playMsg;
	}
	
	public void setPlayState(int playState)
	{
		this.playState = playState;
	}
	public int getPlayState()
	{
		return playState;
	}
	
	public void setMusicPlayType(int musicPlayType)
	{
		this.musicPlayType = musicPlayType;
	}
	public int getMusicPlayType()
	{
		return musicPlayType;
	}
	public boolean isPushMusic()
	{
		return musicPlayType == MUSIC_PLAY_TYPE_PUSH;
	}
	
	private String getInforFromStr(String key, String content)
	{
		String result = "";
		try
		{
			JSONObject json = new JSONObject(content);
			if(json.has(key))
				result = json.getString(key);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}
 
