/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-17 上午12:32:55 
* @Version V1.1   
*/ 

package com.znt.speaker.entity; 

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 
 * @ClassName: SubPlanInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-17 上午12:32:55  
 */
public class SubPlanInfor implements Serializable 
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String startTime = "";
	private String endTime = "";
	private String id = "";
	private List<MusicAlbumInfor> albumList = new ArrayList<MusicAlbumInfor>();
	
	public void setId(String id)
	{
		this.id = id;
	}
	public String getId()
	{
		return id;
	}
	
	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}
	public String getStartTime()
	{
		return startTime;
	}
	
	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}
	public String getEndTime()
	{
		return endTime;
	}
	
	public String getPlanTime()
	{
		return startTime + " ~ " + endTime;
	}
	
	public void setAlbumList(List<MusicAlbumInfor> albumList)
	{
		this.albumList = albumList;
	}
	public List<MusicAlbumInfor> getAlbumList()
	{
		return albumList;
	}
	public void addAlbumInfor(MusicAlbumInfor infor)
	{
		albumList.add(infor);
	}
	public String getPlanAlbumName()
	{
		String planAlbum = "";
		int size = albumList.size();
		String tag = " ,  ";
		for(int i=0;i<size;i++)
		{
			MusicAlbumInfor tempInfor = albumList.get(i);
			if(i < size - 1)
				planAlbum += tempInfor.getAlbumName() + tag;
			else
				planAlbum += tempInfor.getAlbumName();
		}
		return planAlbum;
	}
	public String getPlanAlbumFormat()
	{
		String planAlbum = "";
		int size = albumList.size();
		String tag = "-";
		for(int i=0;i<size;i++)
		{
			MusicAlbumInfor tempInfor = albumList.get(i);
			if(i < size - 1)
				planAlbum += tempInfor.getAlbumName() + tag;
			else
				planAlbum += tempInfor.getAlbumName();
		}
		return planAlbum;
	}
	public String getPlanAlbumIds()
	{
		String planAlbum = "";
		int size = albumList.size();
		String tag = "-";
		for(int i=0;i<size;i++)
		{
			MusicAlbumInfor tempInfor = albumList.get(i);
			if(i < size - 1)
				planAlbum += tempInfor.getAlbumId() + tag;
			else
				planAlbum += tempInfor.getAlbumId();
		}
		return planAlbum;
	}
	
}
 
