/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-1 下午4:11:38 
* @Version V1.1   
*/ 

package com.znt.speaker.entity; 

import java.io.Serializable;

/** 
 * @ClassName: MusicAlbumInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-1 下午4:11:38  
 */
public class MusicAlbumInfor implements Serializable
{
	
	public enum AlbumType
	{
		System,
		User
	}

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String albumName = "";
	private String musicCount = "";
	private String albumId = "";
	private String cover = "";
	private String description = "";
	private boolean isSelected = false;
	private String collectStauts = "0";//1-已收藏 0-未收藏
	
	public void setAlbumName(String albumName)
	{
		this.albumName = albumName;
	}
	public String getAlbumName()
	{
		return albumName;
	}
	
	public void setCollectStatus(String collectStauts)
	{
		this.collectStauts = collectStauts;
	}
	public String getCollectStatus()
	{
		return collectStauts;
	}
	public boolean isCollected()
	{
		return collectStauts.equals("1");
	}
	
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}
	public boolean isSelected()
	{
		return isSelected;
	}
	
	public void setMusicCount(String musicCount)
	{
		this.musicCount = musicCount;
	}
	public String getMusicCount()
	{
		if(musicCount == null)
			musicCount = "0";
		return musicCount;
	}
	
	public void setAlbumId(String albumId)
	{
		this.albumId = albumId;
	}
	public String getAlbumId()
	{
		return albumId;
	}

	public void setCover(String cover)
	{
		this.cover = cover;
	}
	public String getCover()
	{
		return cover;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getDescription()
	{
		return description;
	}
}
 
