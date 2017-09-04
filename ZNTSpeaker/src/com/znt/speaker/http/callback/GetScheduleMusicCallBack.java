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
import com.znt.speaker.util.UrlUtils;

import android.app.Activity;
import android.text.TextUtils;
import okhttp3.Response;

public abstract class GetScheduleMusicCallBack extends Callback<List<SongInfor>>
{
	private Activity activity = null;
	private String planScheId = "";
	public GetScheduleMusicCallBack(Activity activity, String planScheId)
	{
		this.activity = activity;
		this.planScheId = planScheId;
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
					/*int totalInt = 0;
					if(!TextUtils.isEmpty(total))
						totalInt = Integer.parseInt(total);*/
					String listInfo = getInforFromJason(jsonObject1, "infoList");
					JSONArray jsonArray = new JSONArray(listInfo);
					int size = jsonArray.length();
					tempList = new ArrayList<SongInfor>();
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
						
						DBManager.newInstance(activity).addCurPlanMusic(tempInfor, planScheId);
						
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