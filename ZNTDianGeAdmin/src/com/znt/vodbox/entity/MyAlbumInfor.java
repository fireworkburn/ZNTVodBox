/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-21 下午6:11:36 
* @Version V1.1   
*/ 

package com.znt.vodbox.entity; 

import java.util.ArrayList;
import java.util.List;

/** 
 * @ClassName: MyAlbumInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-21 下午6:11:36  
 */
public class MyAlbumInfor 
{
	private List<MusicAlbumInfor> createAlbums = new ArrayList<MusicAlbumInfor>();
	private List<MusicAlbumInfor> collectAlbums = new ArrayList<MusicAlbumInfor>();
	private String createAlbumCount = "";
	private String collectAlbumCount = "";
	
	public void setCreateAlbumCount(String createAlbumCount)
	{
		this.createAlbumCount = createAlbumCount;
	}
	public String getCreateAlbumCount()
	{
		return createAlbumCount;
	}
	public void setCollectAlbumCount(String collectAlbumCount)
	{
		this.collectAlbumCount = collectAlbumCount;
	}
	public String getCollectAlbumCount()
	{
		return collectAlbumCount;
	}
	
	public void addInforToCreate(MusicAlbumInfor infor)
	{
		createAlbums.add(infor);
	}
	public void addInforToCollect(MusicAlbumInfor infor)
	{
		collectAlbums.add(infor);
	}
	
	public List<MusicAlbumInfor> getCreateAlbums()
	{
		return createAlbums;
	}
	public List<MusicAlbumInfor> getCollectAlbums()
	{
		return collectAlbums;
	}
}
 
