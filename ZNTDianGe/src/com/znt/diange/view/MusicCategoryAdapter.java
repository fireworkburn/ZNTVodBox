
package com.znt.diange.view; 

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
import com.znt.diange.R;
import com.znt.diange.mina.entity.MediaInfor;

/** 
 * @ClassName: MusicCategoryAdapter 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-16 下午6:38:05  
 */
public class MusicCategoryAdapter extends BaseAdapter
{
	private Activity activity = null;
	private List<MediaInfor> mediaList = new ArrayList<MediaInfor>();
	
	public MusicCategoryAdapter(Activity activity, List<MediaInfor> mediaList)
	{
		this.activity = activity;
		this.mediaList = mediaList;
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return mediaList.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return mediaList.get(arg0);
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.view_music_category_item, null);
			vh.imageView = (ImageView)convertView.findViewById(R.id.iv_music_category_cover);
			vh.tvTitle = (TextView)convertView.findViewById(R.id.tv_music_category_name);
			vh.tvCount = (TextView)convertView.findViewById(R.id.tv_music_category_count);
			
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		MediaInfor infor = mediaList.get(pos);
		if(!TextUtils.isEmpty(infor.getMediaCover()))
			Picasso.with(activity).load(infor.getMediaCover())  
			.into(vh.imageView);
		vh.tvTitle.setText(infor.getMediaName());
		vh.tvCount.setText(infor.getchildCount());
		
		// TODO Auto-generated method stub
		return convertView;
	}
	
	private class ViewHolder
	{
		ImageView imageView = null;
		TextView tvTitle = null;
		TextView tvCount = null;
	}
}
 
