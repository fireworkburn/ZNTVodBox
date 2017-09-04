/*  
* @Project: ZNTDianGe 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-5-26 上午1:01:51 
* @Version V1.1   
*/ 

package com.znt.diange.entity; 

import java.io.Serializable;

/** 
 * @ClassName: CommentInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-5-26 上午1:01:51  
 */
public class CommentInfor implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String userHead = null;
	private String userName = null;
	private String commentTime = null;
	private String content = null;
	
	public void setUserHead(String userHead)
	{
		this.userHead = userHead;
	}
	public String getUserHead()
	{
		return userHead;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	public String getUserName()
	{
		return userName;
	}
	
	public void setCommentTime(String commentTime)
	{
		this.commentTime = commentTime;
	}
	public String getCommentTime()
	{
		return commentTime;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
	public String getContent()
	{
		return content;
	}
}
 
