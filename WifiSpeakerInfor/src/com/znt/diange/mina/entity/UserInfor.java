
package com.znt.diange.mina.entity; 

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * @ClassName: UserInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-11-12 下午3:54:11  
 */
public class UserInfor implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String userId = "";//用户id
	private String userName = "";//用户昵称 
	private String userIp = "";//用户的IP地址
	private String account = "";//登陆账户
	private String pwd = "";//登陆密码
	private String head = "";
	private String bindDevices = "";
	private String memberType = "";//用户类型 0-普通会员 1-店长 2-管理员
	private String pcCode = "";
	private boolean isAdmin = false;
	
	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		try
		{
			json.put("userId", userId);
			json.put("userName", userName);
			json.put("userIp", userIp);
			json.put("isAdmin", isAdmin);
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
			if(json.has("userName"))
				setUserName(json.getString("userName"));
			if(json.has("userId"))
				setUserId(json.getString("userId"));
			if(json.has("userIp"))
				setUserIp(json.getString("userIp"));
			if(json.has("isAdmin"))
				setAdmin(json.getBoolean("isAdmin"));
		} 
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setMemType(String memberType)
	{
		this.memberType = memberType;
	}
	public String getMemType()
	{
		return memberType;
	}
	
	public void setAccount(String account)
	{
		this.account = account;
	}
	public String getAccount()
	{
		return account;
	}
	
	public void setPwd(String pwd)
	{
		this.pwd = pwd;
	}
	public String getPwd()
	{
		return pwd;
	}
	
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public String getUserId()
	{
		return userId;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	public String getUserName()
	{
		return userName;
	}
	
	public void setHead(String head)
	{
		this.head = head;
	}
	public String getHead()
	{
		return head;
	}
	
	public void setAdmin(boolean isAdmin)
	{
		this.isAdmin = isAdmin;  
	}
	public boolean isAdmin()
	{
		return isAdmin;
	}
	
	public void setUserIp(String userIp)
	{
		this.userIp = userIp;
	}
	public String getUserIp()
	{
		return userIp;
	}
	
	public void setBindDevices(String bindDevices)
	{
		this.bindDevices = bindDevices;
	}
	public String getBindDevices()
	{
		return bindDevices;
	}
	
	public void setPcCode(String pcCode)
	{
		this.pcCode = pcCode;
	}
	public String getPcCode()
	{
		return pcCode;
	}
}
 
