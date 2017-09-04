
package com.znt.vodbox.adapter;

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

import com.squareup.picasso.Picasso;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.utils.DateUtils;

/** 
* @ClassName: DeviceAdapter 
* @Description: TODO
* @author yan.yu 
* @date 2015年12月2日 下午11:34:20  
*/
public class ShopAdapter extends BaseAdapter
{

	private Activity activity = null;
	private List<DeviceInfor> deviceList = new ArrayList<DeviceInfor>();
	
	public ShopAdapter(Activity activity, List<DeviceInfor> deviceList)
	{
		this.activity = activity;
		this.deviceList = deviceList;
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.view_shop_item, null);
			
			vh.tvName = (TextView)convertView.findViewById(R.id.tv_shop_item_name);
			vh.tvAddr = (TextView)convertView.findViewById(R.id.tv_shop_item_addr);
			vh.tvPlaySong = (TextView)convertView.findViewById(R.id.tv_shop_item_play_song);
			vh.tvPlayTime = (TextView)convertView.findViewById(R.id.tv_shop_item_play_time);
			vh.tvCount = (TextView)convertView.findViewById(R.id.tv_shop_item_count);
			vh.ivCover = (ImageView)convertView.findViewById(R.id.iv_shop_item_cover);
			
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		DeviceInfor infor = deviceList.get(pos);
		
		if(!Constant.isInnerVersion)
			vh.tvCount.setVisibility(View.GONE);
		else
			vh.tvCount.setVisibility(View.VISIBLE);
		
		if(!TextUtils.isEmpty(infor.getLastBootTime()))
		{
			long lastPlayTime = Long.parseLong(infor.getLastBootTime());
			vh.tvPlayTime.setText(DateUtils.getStringTime(lastPlayTime));
			if(!infor.isOnline())
			{
				//播放异常
				//vh.tvPlayTime.setTextColor(activity.getResources().getColor(R.color.red));
				vh.tvPlaySong.setTextColor(activity.getResources().getColor(R.color.red));
				vh.tvPlaySong.setText("未上线");
			}
			else
			{
				vh.tvPlayTime.setTextColor(activity.getResources().getColor(R.color.text_black_mid));
				vh.tvPlaySong.setTextColor(activity.getResources().getColor(R.color.text_black_mid));
				
				if(TextUtils.isEmpty(infor.getCurPlaySong()))
				{
					long endTime = System.currentTimeMillis();
					if(!TextUtils.isEmpty(infor.getEndTime()))
					{
						endTime = Long.parseLong(infor.getEndTime());
					}
					if(endTime > 0 && endTime < System.currentTimeMillis())
					{
						vh.tvPlaySong.setText("已到期");
						vh.tvPlaySong.setTextColor(activity.getResources().getColor(R.color.red));
					}
					else
					{
						vh.tvPlaySong.setText("未播放");
						vh.tvPlaySong.setTextColor(activity.getResources().getColor(R.color.text_black_mid));
					}
				}
				else
				{
					vh.tvPlaySong.setText(infor.getCurPlaySong());
					if(infor.getPlayingSongType().equals("0"))
						vh.tvPlaySong.setTextColor(activity.getResources().getColor(R.color.text_black_mid));
					else
						vh.tvPlaySong.setTextColor(activity.getResources().getColor(R.color.text_blue_off));
				}
			}
		}
			
		vh.tvName.setText(infor.getName());
		vh.tvAddr.setText(infor.getAddr());
		
		//vh.tvCount.setText(infor.getMusicCount());
		vh.tvCount.setText(infor.getSoftVersion());
		
		if(!TextUtils.isEmpty(infor.getCover()))
			Picasso.with(activity).load(infor.getCover()).into(vh.ivCover);
		
		// TODO Auto-generated method stub
		return convertView;
	}
	
	private class ViewHolder
	{
		TextView tvName = null;
		TextView tvAddr = null;
		TextView tvPlaySong = null;
		TextView tvPlayTime = null;
		TextView tvCount = null;
		ImageView ivCover = null;
		
	}
}