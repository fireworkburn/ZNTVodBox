package com.znt.speaker.http.callback;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;
import com.znt.diange.mina.cmd.DeviceInfor;

import okhttp3.Response;

public abstract class ListDeviceCallBack extends Callback<List<DeviceInfor>>
{

    @Override
    public List<DeviceInfor> parseNetworkResponse(Response response,int id) throws IOException
    {
        String string = response.body().string();
        List<DeviceInfor> devices = new Gson().fromJson(string, List.class);
        return devices;
    }

}