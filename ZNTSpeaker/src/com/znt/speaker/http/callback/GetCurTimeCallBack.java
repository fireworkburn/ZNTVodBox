package com.znt.speaker.http.callback;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

public abstract class GetCurTimeCallBack extends Callback<String>
{
    @Override
    public String parseNetworkResponse(Response response,int requestId) throws IOException
    {
    	String time = null;
    	if(response.isSuccessful())
    	{
    		String string = response.body().string();
    		try
    		{
    			JSONObject jsonObject = new JSONObject(string);
    			int result = jsonObject.getInt(RESULT_OK);
				if(result == 0)
				{
					time = getInforFromJason(jsonObject, RESULT_INFO);
				}
    		}
    		catch (JSONException e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
        return time;
    }
}