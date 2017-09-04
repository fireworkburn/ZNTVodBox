package com.znt.vodbox.entity;

import java.io.Serializable;

public class AdverInfor implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	private String title = "";
	private String content = "";
	private int imageRes = 0;
	private String url = "";
	
	public void setTile(String title)
	{
		this.title = title;
	}
	public String getTitle()
	{
		return title;
	}
	
	public void setContent(String content)
	{
		this.content = content;
	}
	public String getContent()
	{
		return content;
	}
	
	public void setImageRes(int imageRes)
	{
		this.imageRes = imageRes;
	}
	public int getImageRes()
	{
		return imageRes;
	}
	
	public void setUrl(String url)
	{
		this.url = url;
	}
	public String getUrl()
	{
		return url;
	}
	
}
