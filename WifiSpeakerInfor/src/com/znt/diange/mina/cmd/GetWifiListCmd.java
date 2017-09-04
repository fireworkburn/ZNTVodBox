
package com.znt.diange.mina.cmd; 

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.wifi.ScanResult;

/** 
 * @ClassName: PlayCmd 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-3 下午2:44:34  
 */
public class GetWifiListCmd extends BaseCmd implements Serializable
{

	/** 音响曲库模式
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String result = "0";//处理结果  0,成功；1失败
	private List<ScanResult> wifiList = new ArrayList<ScanResult>();
	
	public GetWifiListCmd()
	{
		setCmdType(CmdType.GET_WIFI_LIST);
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
			if(wifiList != null)
				json.put("wifiList", getWifisJsonStr());
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
		setWifiList(wifiList);
		
	}
	
	public void setResult(String result)
	{
		this.result = result;
	}
	public String getResult()
	{
		return result;
	}
	
	public void setWifiList(List<ScanResult> wifiList)
	{
		this.wifiList = wifiList;
	}
	public List<ScanResult> getWifiList()
	{
		return wifiList;
	}
	
	private String getWifisJsonStr()
	{
		JSONArray jsonArray = new JSONArray();
		int size = wifiList.size();
		for(int i=0;i<size;i++)
		{
			jsonArray.put(wifiList.get(i).SSID);
		}
		
		return jsonArray.toString();
	}
}
 
