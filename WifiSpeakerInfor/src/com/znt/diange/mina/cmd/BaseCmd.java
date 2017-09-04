
package com.znt.diange.mina.cmd; 

import java.io.Serializable;

import org.json.JSONObject;

import com.znt.diange.mina.entity.UserInfor;

/** 
 * @ClassName: BaseCmd 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-3 上午10:51:30  
 */
public class BaseCmd implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	protected String head = "00";
	protected String end = "11";
	protected String deviceId = "";
	
	protected UserInfor userInfor = new UserInfor();
	
	protected String cmdType = "";
	
	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}
	public String getDeviceId()
	{
		return deviceId;
	}
	
	public void setUserInfor(UserInfor userInfor)
	{
		this.userInfor = userInfor;
	}
	public UserInfor getUserInfor()
	{
		return userInfor;
	}
	
	public void setHeader(String head)
	{
		this.head = head;
	}
	public String getHeader()
	{
		return head;
	}
	
	public void setEnd(String end)
	{
		this.end = end;
	}
	public String getEnd()
	{
		return end;
	}
	
	public void setCmdType(String cmdType)
	{
		this.cmdType = cmdType;
	}
	public String getCmdType()
	{
		return cmdType;
	}
	
	protected String getInforFromStr(String key, String content)
	{
		String result = "";
		try
		{
			JSONObject json = new JSONObject(content);
			if(json.has(key))
				result = json.getString(key);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
}
 
