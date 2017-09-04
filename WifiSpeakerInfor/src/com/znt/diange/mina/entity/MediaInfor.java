
package com.znt.diange.mina.entity; 

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/** 
 * @ClassName: MediaInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-1-20 上午10:15:00  
 */
public class MediaInfor implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	public final static String MEDIA_TYPE_PHONE = "0";//手机本地
	public final static String MEDIA_TYPE_SPEAKER = "1";//音响本地
	public final static String MEDIA_TYPE_NET = "2";//网络
	
	private String mediaId = "";
	private String resId = "";
	private String terminalId = "";
	private String mediaName = "";
	private String mediaUrl = "";
	private String objectclass = "";
	private long mediaSize = 0;
	private String childCount = "";
	private String mediaCover = "";
	private long date = 0;
	private String res = "";
	private int mediaDuration = 0;
	private String mediaType = MEDIA_TYPE_NET;//默认都是网络歌曲
	private String mediaResType = "1";//1-酷我 2-网易
	
	private String artist = "";
	private String albumName = "";
	private String albumUrl = "";
	private boolean isDir = false;
	private boolean isAvailable = true;
	private boolean isSelected = false;
	private boolean isFromAlbum = false;
	private boolean isPlaying = false;
	private long curPlayTime = 0;
	private int resourceType = 0;
	
	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		try
		{
			json.put("mediaId", mediaId);
			json.put("mediaName", mediaName);
			json.put("mediaUrl", mediaUrl);
			json.put("mediaType", mediaType);
			
			json.put("artist", artist);
			json.put("albumName", albumName);
			json.put("albumUrl", albumUrl);
			json.put("isDir", isDir);
			json.put("childCount", childCount);
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
		JSONObject json;
		try
		{
			json = new JSONObject(jsonStr);
			if(json.has("mediaId"))
				setMediaId(json.getString("mediaId"));
			if(json.has("mediaName"))
				setMediaName(json.getString("mediaName"));
			if(json.has("mediaUrl"))
				setMediaUrl(json.getString("mediaUrl"));
			if(json.has("mediaType"))
				setMediaType(json.getString("mediaType"));
			
			if(json.has("artist"))
				setArtist(json.getString("artist"));
			if(json.has("albumName"))
				setAlbumName(json.getString("albumName"));
			if(json.has("albumUrl"))
				setAlbumUrl(json.getString("albumUrl"));
			if(json.has("isDir"))
				setDir(json.getBoolean("isDir"));
			if(json.has("childCount"))
				setchildCount(json.getString("childCount"));
		} 
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}
	public boolean isSelected()
	{
		return isSelected;
	}
	public void setAvailable(boolean isAvailable)
	{
		this.isAvailable = isAvailable;
	}
	public boolean isAvailable()
	{
		return isAvailable;
	}
	
	public void setDir(boolean isDir)
	{
		this.isDir = isDir;
	}
	public boolean getDir()
	{
		return isDir;
	}
	
	public void setFromAlbum(boolean isFromAlbum)
	{
		this.isFromAlbum = isFromAlbum;
	}
	public boolean isFromAlbum()
	{
		return isFromAlbum;
	}
	
	public void setArtist(String artist)
	{
		this.artist = artist;
	}
	public String getArtist()
	{
		if(TextUtils.isEmpty(artist))
			artist = "未知";
		return artist;
	}
	
	public void setAlbumName(String albumName)
	{
		this.albumName = albumName;
	}
	public String getAlbumName()
	{
		if(albumName == null)
			albumName = "";
		return albumName;
	}
	
	public void setAlbumUrl(String albumUrl)
	{
		this.albumUrl = albumUrl;
	}
	public String getAlbumUrl()
	{
		if(albumUrl == null)
			albumUrl = "";
		return albumUrl;
	}
	
	public void setMediaDuration(int mediaDuration)
	{
		this.mediaDuration = mediaDuration;
	}
	public int getMediaDuration()
	{
		return mediaDuration;
	}
	
	public void setMediaType(String mediaType)
	{
		this.mediaType = mediaType;
	}
	public String getMediaType()
	{
		return mediaType;
	}
	
	public boolean isLocalMedia()
	{
		return mediaType.equals("0");
	}
	public boolean isNetworkMedia()
	{
		return mediaType.equals("1");
	}

	public void setMediaResType(String mediaResType)
	{
		this.mediaResType = mediaResType;
	}
	public String getMediaResType()
	{
		return mediaResType;
	}
	
	public void setMediaId(String mediaId)
	{
		this.mediaId = mediaId;
	}
	public String getMediaId()
	{
		return mediaId;
	}
	
	public void setResId(String resId)
	{
		this.resId = resId;
	}
	public String getResId()
	{
		return resId;
	}
	
	public void setMediaName(String mediaName)
	{
		this.mediaName = mediaName;
	}
	public String getMediaName()
	{
		if(mediaName == null)
			mediaName = "";
		return mediaName;
	}
	
	public void setMediaUrl(String mediaUrl)
	{
		this.mediaUrl = mediaUrl;
	}
	public String getMediaUrl()
	{
		if(mediaUrl == null)
			mediaUrl = "";
		return mediaUrl;
	}
	
	public void setObjectClass(String objectclass)
	{
		this.objectclass = objectclass;
	}
	public String getObjectClass()
	{
		return objectclass;
	}

	public void setMediaSize(long mediaSize)
	{
		this.mediaSize = mediaSize;
	}
	public long getMediaSize()
	{
		return mediaSize;
	}
	
	public void setMediaCover(String mediaCover)
	{
		this.mediaCover = mediaCover;
	}
	public String getMediaCover()
	{
		return mediaCover;
	}
	
	public String getchildCount() 
	{
		return childCount;
	}
	public void setchildCount(String childCount) 
	{
		this.childCount = (childCount != null ? childCount : "");
	}
	
	public long getDate() 
	{
		return date;
	}
	public void setDate(long date) 
	{
		this.date = date;
	}
	
	public void setRes(String res) 
	{
		this.res = (res != null ? res : "");
	}
	public String getRes() 
	{
		return res;
	}

	public void setResourceType(int resourceType)
	{
		this.resourceType = resourceType;
	}
	public int getResourceType()
	{
		return resourceType;
	}
	
	public void setTerminalId(String terminalId)
	{
		this.terminalId = terminalId;
	}
	public String getTerminalId()
	{
		return terminalId;
	}
	
	public void setIsPlaying(boolean isPlaying)
	{
		this.isPlaying = isPlaying;
	}
	public boolean isPlaying()
	{
		return isPlaying;
	}
	
	public void setCurPlayTime(long curPlayTime)
	{
		this.curPlayTime = curPlayTime;
	}
	public long getCurPlayTime()
	{
		return curPlayTime;
	}
	public String getCurPlayTimeFormat()
	{
		if(curPlayTime <= 0)
			return "";
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");  
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));  
        return formatter.format(curPlayTime);  
	}
}
 
