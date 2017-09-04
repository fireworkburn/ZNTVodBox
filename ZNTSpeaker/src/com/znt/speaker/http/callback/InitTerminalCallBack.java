package com.znt.speaker.http.callback;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhy.http.okhttp.callback.Callback;
import com.znt.speaker.entity.LocalDataEntity;

import android.app.Activity;
import android.text.TextUtils;
import okhttp3.Response;

public abstract class InitTerminalCallBack extends Callback<String>
{
	private Activity activity = null;
	public InitTerminalCallBack(Activity activity)
	{
		this.activity = activity;
	}
	
    @Override
    public String parseNetworkResponse(Response response,int requestId) throws IOException
    {
    	String systemTime = "";
    	if(response.isSuccessful())
    	{
    		String string = response.body().string();
    		try
    		{
    			JSONObject jsonObject = new JSONObject(string);
    			int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					String infor = getInforFromJason(jsonObject, RESULT_INFO);
					JSONObject json1 = new JSONObject(infor);
					
					systemTime = getInforFromJason(json1, "systemTime");
					String playingPos = getInforFromJason(json1, "playingPos");
					if(!TextUtils.isEmpty(playingPos))
					{
						int pos = Integer.parseInt(playingPos);
						if(pos > 0)
							pos = pos - 1;
						LocalDataEntity.newInstance(activity).setMusicIndex(pos);
					}
					//String playingPos = getInforFromJason(jsonObj, "playingPos");
				}
    		}
    		catch (JSONException e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
        return systemTime;
    }
}