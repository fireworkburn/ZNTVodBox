package com.znt.speaker.http.callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhy.http.okhttp.callback.Callback;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.util.UrlUtils;

import android.app.Activity;
import android.text.TextUtils;
import okhttp3.Response;

public abstract class GetPlanMusicCallBack extends Callback<List<SongInfor>>
{
	private Activity activity = null;
	public GetPlanMusicCallBack(Activity activity)
	{
		this.activity = activity;
	}
    @Override
    public List<SongInfor> parseNetworkResponse(Response response,int requestId) throws IOException
    {
    	List<SongInfor> tempList = null;
    	if(response.isSuccessful())
    	{
    		String string = response.body().string();
    		try
    		{
    			JSONObject jsonObject = new JSONObject(string);
				int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String info = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject jsonObject1 = new JSONObject(info);
					String total = getInforFromJason(jsonObject1, "listSize");
					int totalInt = 0;
					if(!TextUtils.isEmpty(total))
						totalInt = Integer.parseInt(total);
					String playingPos = getInforFromJason(jsonObject1, "playingPos");
					if(!TextUtils.isEmpty(playingPos))
					{
						int pos = Integer.parseInt(playingPos);
						/*if(pos >= 0)
							pos = pos - 1;*/
						LocalDataEntity.newInstance(activity).setMusicIndex(pos);
					}
					String seekPos = getInforFromJason(jsonObject1, "playSeek");
					if(!TextUtils.isEmpty(seekPos))
					{
						int seekPosInt = Integer.parseInt(seekPos);
						if(seekPosInt >= 0)
							LocalDataEntity.newInstance(activity).setSeekPos(seekPosInt);
					}
					String lastUpdateTime = getInforFromJason(jsonObject1, "lastUpdateTime");
					if(!TextUtils.isEmpty(lastUpdateTime))
					{
						long lastUpdateTimeInt = Long.parseLong(lastUpdateTime);
						if(lastUpdateTimeInt > 0)
							LocalDataEntity.newInstance(activity).setCurLastUpdateTime(lastUpdateTimeInt);
					}
					
					String listInfo = getInforFromJason(jsonObject1, "infoList");
					JSONArray jsonArray = new JSONArray(listInfo);
					int size = jsonArray.length();
					tempList = new ArrayList<SongInfor>();
					if(size > 0)
					{
						for(int i=0;i<size;i++)
						{
							JSONObject json = jsonArray.getJSONObject(i);
							String musicAlbum = getInforFromJason(json, "musicAlbum");
							String musicId = getInforFromJason(json, "musicId");
							String musicUrl = getInforFromJason(json, "musicUrl");
							String musicName = getInforFromJason(json, "musicName");
							String musicSing = getInforFromJason(json, "musicSing");
							
							if(!TextUtils.isEmpty(musicUrl))
								musicUrl = UrlUtils.decodeUrl(musicUrl);
							SongInfor tempInfor = new SongInfor();
							String musicInfoId = getInforFromJason(json, "musicInfoId");
							tempInfor.setMediaId(musicInfoId);
							tempInfor.setResId(musicId);
							tempInfor.setMediaName(musicName);
							tempInfor.setMediaUrl(musicUrl);
							tempInfor.setArtist(musicSing);
							tempInfor.setAlbumName(musicAlbum);
							tempList.add(tempInfor);
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
        return tempList;
    }
}