package com.znt.speaker.p;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.m.NetWorkModel;
import com.znt.speaker.player.CheckSsidTimer;
import com.znt.speaker.util.LogFactory;
import com.znt.speaker.util.NetWorkUtils;
import com.znt.speaker.util.SystemUtils;
import com.znt.speaker.util.XmlUtils;
import com.znt.speaker.v.INetWorkView;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

public class NetWorkPresenter
{
	private Activity activity = null;
	private INetWorkView iNetWorkView = null;
	private NetWorkModel netWorkModel = null;
	
	public NetWorkPresenter(Activity activity, INetWorkView iNetWorkView)
	{
		this.activity = activity;
		this.iNetWorkView = iNetWorkView;
		netWorkModel = new NetWorkModel(activity, iNetWorkView);
		
	}
	
	public void startCheckNetwork()
	{
		netWorkModel.startCheckNetwork();
	}
	public void openWifi()
	{
		netWorkModel.openWifi();
	}
	public void startScanWifi()
	{
		netWorkModel.startScanWifi();
	}
	public void connectWifi(String name, String pwd)
	{
		netWorkModel.connectWifi(name, pwd);
	}
	
	public void createWifiAp()
	{
		netWorkModel.createWifiAp();
	}
	public void closeWifiAp(Context context)
	{
		netWorkModel.closeWifiAp(context);
	}
	public String getWifiApName()
	{
		return netWorkModel.getWifiApName();
	}
	
	public boolean isWifiEnabled()
	{
		return netWorkModel.isWifiEnabled();
	}
	
	public void stopCheckSSID()
	{
		netWorkModel.stopCheckSSID();
	}
	
	private void checkWifiStatus()
	{
		
	}
	
	public void getWifiSetInfoFromUsb(String path, DeviceInfor deviceInforRecv)
	{
		netWorkModel.getWifiSetInfoFromUsb(path, deviceInforRecv);
	}
}
