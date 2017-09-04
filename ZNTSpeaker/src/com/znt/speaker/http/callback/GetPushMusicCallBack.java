package com.znt.speaker.http.callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhy.http.okhttp.callback.Callback;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.db.DBManager;
import com.znt.speaker.entity.DeviceStatusInfor;
import com.znt.speaker.util.UrlUtils;

import android.app.Activity;
import okhttp3.Response;

public abstract class GetPushMusicCallBack extends Callback<String>
{
	private Activity activity = null;
	public GetPushMusicCallBack(Activity activity)
	{
		this.activity = activity;
	}
	
    @Override
    public String parseNetworkResponse(Response response,int requestId) throws IOException
    {
    	if(response.isSuccessful())
    	{
    		String string = response.body().string();
    		try
    		{
    			JSONObject jsonObject = new JSONObject(string);
    			int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, "info");
					JSONArray jasonArray = new JSONArray(info);
					int len = jasonArray.length();
					List<SongInfor> tempList = new ArrayList<SongInfor>();
					for(int i=0;i<len;i++)
					{
						JSONObject json = (JSONObject) jasonArray.get(i);
						String terminalId = getInforFromJason(json, "terminalId");
						String musicSource = getInforFromJason(json, "musicSource");
						String musicDuration = getInforFromJason(json, "musicDuration");
						String id = getInforFromJason(json, "id");
						String status = getInforFromJason(json, "status");
						String musicAuther = getInforFromJason(json, "musicAuther");
						String musicId = getInforFromJason(json, "musicId");
						String musicUrl = getInforFromJason(json, "musicUrl");
						String musicName = getInforFromJason(json, "musicName");
						String musicCategoryId = getInforFromJason(json, "musicCategoryId");
						String musicAlbum = getInforFromJason(json, "musicAlbum");
						String musicSing = getInforFromJason(json, "musicSing");
						String addTime = getInforFromJason(json, "musicAlbum");
						
						SongInfor infor = new SongInfor();
						String musicInfoId = getInforFromJason(json, "musicInfoId");
						infor.setMediaId(musicInfoId);
						infor.setResId(musicId);
						infor.setMediaName(musicName);
						infor.setMediaResType(musicAuther);
						infor.setMediaUrl(UrlUtils.decodeUrl(musicUrl));
						infor.setAlbumName(musicAlbum);
						infor.setArtist(musicSing);
						tempList.add(0,infor);
						//DBManager.newInstance(context).insertSong(infor);
					}
					/*String time = getInforFromJason(jsonObject, RESULT_INFO);
					httpResult.setResult(true, time);
					int songCount = DBManager.newInstance(context).getSongCount();
					List<SongInfor> tempList = DBManager.newInstance(context).getSongList(0, 0);*/
					if(tempList.size() > 0)
					{
						for(int i=0;i<tempList.size();i++)
						{
							DBManager.newInstance(activity).insertSong(tempList.get(i));
						}
					}
				}
    		}
    		catch (JSONException e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
        return "get push music finished";
    }
}