
package com.znt.diange.entity; 

import java.io.Serializable;

/** 
 * @ClassName: MusicCategory 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-3-10 上午10:56:24  
 */
public class MusicCategory implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String categoryName = "";
	private String categoryUrl = "";

	public void setName(String categoryName)
	{
		this.categoryName = categoryName;
	}
	public String getName()
	{
		return categoryName;
	}
	
	public void setUrl(String categoryUrl)
	{
		this.categoryUrl = categoryUrl;
	}
	public String getUrl()
	{
		return categoryUrl;
	}
}
 
