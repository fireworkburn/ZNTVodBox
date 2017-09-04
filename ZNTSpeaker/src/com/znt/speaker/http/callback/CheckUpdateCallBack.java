package com.znt.speaker.http.callback;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.zhy.http.okhttp.callback.Callback;
import com.znt.diange.mina.entity.UpdateInfor;

import okhttp3.Response;

public abstract class CheckUpdateCallBack extends Callback<UpdateInfor>
{
    @Override
    public UpdateInfor parseNetworkResponse(Response response,int requestId) throws IOException
    {
    	UpdateInfor updateInfor = null;
    	if(response.isSuccessful())
    	{
    		String string = response.body().string();
    		try
    		{
    			JSONObject jsonObject = new JSONObject(string);
    			int result = jsonObject.getInt(RESULT_OK);
                if(result == 0)
                {
	               	 JSONObject json = jsonObject.getJSONObject(RESULT_INFO);
	               	 updateInfor = new UpdateInfor();
	               	 String versionName = getInforFromJason(json, "version");
	               	 String versionNum = getInforFromJason(json, "versionNum");
	               	 String apkUrl = getInforFromJason(json, "url");
	               	 String updateType  = getInforFromJason(json, "updateType");
	               	 updateInfor.setApkUrl(apkUrl);
	               	 updateInfor.setUpdateType(updateType);
	               	 updateInfor.setVersionNum(versionNum);
	               	 updateInfor.setVersionName(versionName);
                }
    		}
    		catch (JSONException e) 
    		{
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
        return updateInfor;
    }
}