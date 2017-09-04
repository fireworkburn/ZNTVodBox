
package com.znt.diange.mina.cmd; 

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.znt.diange.mina.entity.PlayRes;

/** 
 * @ClassName: PlayCmd 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-3 下午2:44:34  
 */
public class GetPlayResCmd extends BaseCmd implements Serializable
{

	/** 音响曲库模式
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String result = "0";//处理结果  0,成功；1失败
	protected String playRes = PlayRes.LOCAL;
	
	public GetPlayResCmd()
	{
		setCmdType(CmdType.GET_PLAY_RES);
	}
	
	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		try
		{
			json.put("head", getHeader());
			json.put("end", getEnd());
			json.put("result", result);
			json.put("deviceId", getDeviceId());
			json.put("cmdType", getCmdType());
			json.put("playRes", playRes);
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
		setHeader(getInforFromStr("head", jsonStr));
		setEnd(getInforFromStr("end", jsonStr));
		setResult(getInforFromStr("result", jsonStr));
		setDeviceId(getInforFromStr("deviceId", jsonStr));
		setCmdType(getInforFromStr("cmdType", jsonStr));
		setPlayRes(getInforFromStr("playRes", jsonStr));
		
	}
	
	public void setResult(String result)
	{
		this.result = result;
	}
	public String getResult()
	{
		return result;
	}
	
	public void setPlayRes(String playRes)
	{
		this.playRes = playRes;
	}
	public String getPlayRes()
	{
		return playRes;
	}
}
 
