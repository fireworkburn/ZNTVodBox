package com.zhy.http.okhttp.callback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public abstract class Callback<T>
{
	
	protected String RESULT_INFO = "info";
	protected String RESULT_OK = "result";
	
    /**
     * UI Thread
     *
     * @param request
     */
    public void onBefore(Request request, int requestId)
    {
    }

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter(int requestId)
    {
    }

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress, long total , int requestId)
    {

    }

    /**
     * if you parse reponse code in parseNetworkResponse, you should make this method return true.
     *
     * @param response
     * @return
     */
    public boolean validateReponse(Response response, int requestId)
    {
        return response.isSuccessful();
    }

    /**
     * Thread Pool Thread
     *
     * @param response
     */
    public abstract T parseNetworkResponse(Response response, int requestId) throws Exception;

    public abstract void onError(Call call, Exception e, int requestId);

    public abstract void onResponse(T response, int requestId);


    @SuppressWarnings("rawtypes")
	public static Callback CALLBACK_DEFAULT = new Callback()
    {

        @Override
        public Object parseNetworkResponse(Response response, int requestId) throws Exception
        {
            return null;
        }

        @Override
        public void onError(Call call, Exception e, int requestId)
        {

        }

        @Override
        public void onResponse(Object response, int requestId)
        {

        }
    };
    
    protected String getInforFromJason(JSONObject json, String key)
	{
		if(json == null || key == null)
			return "";
		if(json.has(key))
		{
			try
			{
				String result = json.getString(key);
				if(result.equals("null"))
					result = "";
				return result;
				//return StringUtils.decodeStr(result);
			} 
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

}