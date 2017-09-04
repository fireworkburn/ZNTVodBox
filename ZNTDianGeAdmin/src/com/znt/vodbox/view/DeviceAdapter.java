
package com.znt.vodbox.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.vodbox.entity.LocalDataEntity;

/** 
* @ClassName: DeviceAdapter 
* @Description: TODO
* @author yan.yu 
* @date 2015年12月2日 下午11:34:20  
*/
public class DeviceAdapter extends BaseAdapter
{

	private Activity activity = null;
	private List<DeviceInfor> deviceList = new ArrayList<DeviceInfor>();
	
	private boolean isScan = true;
	
	public DeviceAdapter(Activity activity, List<DeviceInfor> deviceList, boolean isScan)
	{
		this.activity = activity;
		this.deviceList = deviceList;
		this.isScan = isScan;
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return deviceList.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return deviceList.get(arg0);
	}

	/**
	*callbacks
	*/
	@Override
	public long getItemId(int arg0)
	{
		// TODO Auto-generated method stub
		return arg0;
	}

	/**
	*callbacks
	*/
	@Override
	public View getView(int pos, View convertView, ViewGroup arg2)
	{
		ViewHolder vh = null;
		if(convertView == null)
		{
			vh = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.view_device_item, null);
			
			vh.tvName = (TextView)convertView.findViewById(R.id.tv_device_item_name);
			vh.tvSSID = (TextView)convertView.findViewById(R.id.tv_device_item_ssid);
			vh.ivStatus = (ImageView)convertView.findViewById(R.id.iv_device_item_icon);
			vh.ivSelected = (ImageView)convertView.findViewById(R.id.iv_device_item_selected);
			
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		DeviceInfor infor = deviceList.get(pos);
		vh.tvName.setText(infor.getName());
		if(isScan)
		{
			vh.tvName.setTextColor(activity.getResources().getColor(R.color.text_blue_on));
			vh.tvSSID.setTextColor(activity.getResources().getColor(R.color.text_blue_off));
			vh.ivStatus.setImageResource(R.drawable.icon_device_select_item);
			if(isSelected(infor))
				vh.ivSelected.setVisibility(View.VISIBLE);
			else
				vh.ivSelected.setVisibility(View.INVISIBLE);
			vh.tvSSID.setText(infor.getWifiName());// + "\n" + infor.getIp() + "\n" + infor.getId());
		}
		else
		{
			vh.tvSSID.setText(infor.getAddr());// + "\n" + infor.getIp() + "\n" + infor.getId());
			if(infor.isAvailable())
			{
				vh.tvName.setTextColor(activity.getResources().getColor(R.color.text_blue_on));
				vh.tvSSID.setTextColor(activity.getResources().getColor(R.color.text_blue_off));
				vh.ivStatus.setImageResource(R.drawable.icon_device_select_item);
				
				if(infor.getId().equals(LocalDataEntity.newInstance(activity).getDeviceId()))
					vh.ivSelected.setVisibility(View.VISIBLE);
				else
					vh.ivSelected.setVisibility(View.INVISIBLE);
			}
			else
			{
				vh.tvName.setTextColor(activity.getResources().getColor(R.color.text_black_on));
				vh.tvSSID.setTextColor(activity.getResources().getColor(R.color.text_black_mid));
				vh.ivStatus.setImageResource(R.drawable.icon_device_select_item_off);
				vh.ivSelected.setVisibility(View.INVISIBLE);
			}
		}
		
		// TODO Auto-generated method stub
		return convertView;
	}
	
	//根据设备id判断
	private boolean isSelected(DeviceInfor infor)
	{
		String localUuid = LocalDataEntity.newInstance(activity).getDeviceId();
		if(!TextUtils.isEmpty(localUuid) && localUuid.equals(infor.getId()))
		{
			infor.setSelected(true);
			return true;
		}
		infor.setSelected(false);
		return false;
	}
	
	private class ViewHolder
	{
		TextView tvName = null;
		TextView tvSSID = null;
		ImageView ivStatus = null;
		ImageView ivSelected = null;
		
	}
}