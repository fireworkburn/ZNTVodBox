
package com.znt.vodbox.entity; 

import java.io.Serializable;

/** 
 * @ClassName: WXInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-1-28 下午2:48:10  
 */
public class WXInfor implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	public String openid = "";
	public String expires_in = "";
	public String scope = "";
	public String refresh_token = "";
	public String access_token = "";
	public String unionid = "";
	public String nickname = "";
	public String sex = "";
	public String headimgurl = "";
	
	public void setOpenId(String openid)
	{
		this.openid = openid;
	}
	
	public void setExpires_in(String expires_in)
	{
		this.expires_in = expires_in;
	}
	
	public void setScope(String scope)
	{
		this.scope = scope;
	}

	public void setRefreshToken(String refresh_token)
	{
		this.refresh_token = refresh_token;
	}
	public void setAccessToken(String access_token)
	{
		this.access_token = access_token;
	}
	
	public void setUnionid(String unionid)
	{
		this.unionid = unionid;
	}
	
	public void setNickName(String nickname)
	{
		this.nickname = nickname;
	}
	
	public void setHeadImage(String headimgurl)
	{
		this.headimgurl = headimgurl;
	}
}

 
