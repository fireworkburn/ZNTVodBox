/*  
* @Project: ZNTDianGe 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-5-25 下午11:37:42 
* @Version V1.1   
*/ 

package com.znt.diange.entity; 

import java.io.Serializable;

import com.znt.diange.mina.entity.SongInfor;

/** 
 * @ClassName: SongRecordInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-5-25 下午11:37:42  
 */
public class SongRecordInfor extends SongInfor implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	private String commentCount = "";
	private String praiseCount = "";
	
	public void setCommentCount(String commentCount)
	{
		this.commentCount = commentCount;
	}
	public String getCommentCount()
	{
		return commentCount;
	}
	
	public void setPraiseCount(String praiseCount)
	{
		this.praiseCount = praiseCount;
	}
	public String getPraiseCount()
	{
		return praiseCount;
	}
}
 
