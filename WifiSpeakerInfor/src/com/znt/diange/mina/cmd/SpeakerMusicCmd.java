
package com.znt.diange.mina.cmd; 

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.UserInfor;

/** 
 * @ClassName: PlayNextCmd 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-11-19 下午12:04:53  
 */
public class SpeakerMusicCmd extends BaseCmd implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String totalSize = "0";
	private String pageNum = "0";
	private String pageSize = "0";
	private List<MediaInfor> musicList = new ArrayList<MediaInfor>();
	
	public static final String RequestDir = "0";
	public static final String RequestMedia = "1";
	private String requestKey = "";//请求音乐列表时的key
	
	private String requestType = RequestDir;//0,目录， 1，歌曲
	private String result = "0";//处理结果  0,成功；1失败
	
	public SpeakerMusicCmd()
	{
		setCmdType(CmdType.SPEAKER_MUSIC);
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
			json.put("totalSize", totalSize);
			json.put("requestType", requestType);
			if(!TextUtils.isEmpty(requestKey))
				json.put("requestKey", requestKey);
			if(musicList != null)
				json.put("musicList", getMusicsJsonStr());
			if(userInfor != null)
				json.put("userInfor", getUserInfor().toJson());
		} 
		catch (Exception e)
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
		setRequestType(getInforFromStr("requestType", jsonStr));
		setRequestKey(getInforFromStr("requestKey", jsonStr));
		
		UserInfor tempUser = new UserInfor();
		String userInfor = getInforFromStr("userInfor", jsonStr);
		if(!TextUtils.isEmpty(userInfor))
		{
			tempUser.toClass(userInfor);
			setUserInfor(tempUser);
		}
		
		String musicsInforStr = getInforFromStr("musicList", jsonStr);
		if(!TextUtils.isEmpty(musicsInforStr))
			setMusicList(getMusicsFromJson(musicsInforStr));
	}
	
	private String getMusicsJsonStr()
	{
		JSONArray jsonArray = new JSONArray();
		int size = musicList.size();
		for(int i=0;i<size;i++)
		{
			jsonArray.put(musicList.get(i).toJson());
		}
		
		return jsonArray.toString();
	}
	
	private List<MediaInfor> getMusicsFromJson(String musicsStr)
	{
		List<MediaInfor> musics = new ArrayList<MediaInfor>();
		try
		{
			JSONArray array = new JSONArray(musicsStr);
			int size = array.length();
			for(int i=0;i<size;i++)
			{
				JSONObject json = array.getJSONObject(i);
				MediaInfor tempInfor = new MediaInfor();
				tempInfor.toClass(json.toString());
				musics.add(tempInfor);
			}
		} 
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return musics;
	}
	
	public void setRequestType(String requestType)
	{
		this.requestType = requestType;
	}
	public String getRequestType()
	{
		return requestType;
	}
	public boolean isReqeustDir()
	{
		return requestType.equals(RequestDir);
	}
	
	public void setRequestKey(String requestKey)
	{
		this.requestKey = requestKey;
	}
	public String getRequestKey()
	{
		return requestKey;
	}

	public void setResult(String result)
	{
		this.result = result;
	}
	public String getResult()
	{
		return result;
	}
	
	public void setTotalSize(String totalSize)
	{
		this.totalSize = totalSize;
	}
	public String getTotalSize()
	{
		if(TextUtils.isEmpty(totalSize))
			totalSize = "0";
		return totalSize;
	}
	
	public void setPageNum(String pageNum)
	{
		this.pageNum = pageNum;
	}
	public String getPagNum()
	{
		if(TextUtils.isEmpty(pageNum))
			pageNum = "0";
		return pageNum;
	}
	
	public void setPageSize(String pageSize)
	{
		this.pageSize = pageSize;
	}
	public String getPagSize()
	{
		if(TextUtils.isEmpty(pageSize))
			pageSize = "0";
		return pageSize;
	}
	
	public void setMusicList(List<MediaInfor> musicList)
	{
		this.musicList = musicList;
	}
	public List<MediaInfor> getMusicList()
	{
		return musicList;
	}
}
 
