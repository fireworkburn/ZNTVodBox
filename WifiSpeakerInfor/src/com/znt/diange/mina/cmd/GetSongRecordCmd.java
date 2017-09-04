
package com.znt.diange.mina.cmd; 

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.mina.entity.UserInfor;

/** 
 * @ClassName: GetSongListCmd 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-8-3 下午2:40:57  
 */
public class GetSongRecordCmd extends BaseCmd implements Serializable
{

	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	
	private String pageNum = "0";
	private String pageSize = "0";
	
	private String total = "0";
	private List<SongInfor> songList = null;
	
	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		try
		{
			json.put("head", getHeader());
			json.put("end", getEnd());
			json.put("deviceId", getDeviceId());
			json.put("cmdType", getCmdType());
			json.put("pageNum", pageNum);
			json.put("pageSize", pageSize);
			json.put("total", total);
			if(songList != null)
				json.put("songList", getSongsJsonStr());
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
		setDeviceId(getInforFromStr("deviceId", jsonStr));
		setCmdType(getInforFromStr("cmdType", jsonStr));
		setPageSize(getInforFromStr("pageSize", jsonStr));
		setPageNum(getInforFromStr("pageNum", jsonStr));
		setTotal(getInforFromStr("total", jsonStr));
		
		UserInfor tempUser = new UserInfor();
		String userInfor = getInforFromStr("userInfor", jsonStr);
		if(!TextUtils.isEmpty(userInfor))
		{
			tempUser.toClass(userInfor);
			setUserInfor(tempUser);
		}
		
		String songListStr = getInforFromStr("songList", jsonStr);
		if(!TextUtils.isEmpty(songListStr))
			setSongList(getSongsFromJson(songListStr));
	}
	
	private String getSongsJsonStr()
	{
		JSONArray jsonArray = new JSONArray();
		int size = songList.size();
		for(int i=0;i<size;i++)
		{
			jsonArray.put(songList.get(i).toJson());
		}
		
		return jsonArray.toString();
	}
	
	private List<SongInfor> getSongsFromJson(String jsonStr)
	{
		List<SongInfor> songs = new ArrayList<SongInfor>();
		try
		{
			JSONArray array = new JSONArray(jsonStr);
			int size = array.length();
			for(int i=0;i<size;i++)
			{
				JSONObject json = array.getJSONObject(i);
				SongInfor tempSong = new SongInfor();
				tempSong.toClass(json.toString());
				songs.add(tempSong);
			}
		} 
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return songs;
	}
	
	public GetSongRecordCmd()
	{
		setCmdType(CmdType.GET_PLAY_RECORD);
	}
	
	public void setTotal(String total)
	{
		this.total = total;
	}
	public String getTotal()
	{
		return total;
	}
	
	public void setSongList(List<SongInfor> songList)
	{
		this.songList = songList;
	}
	public List<SongInfor> getSongList()
	{
		return songList;
	}
	
	public void setPageNum(String pageNum)
	{
		this.pageNum = pageNum;
	}
	public void setPageSize(String pageSize)
	{
		this.pageSize = pageSize;
	}
	public String getPageSize()
	{
		return pageSize;
	}
	public String getPageNum()
	{
		return pageNum;
	}
	
}
 
