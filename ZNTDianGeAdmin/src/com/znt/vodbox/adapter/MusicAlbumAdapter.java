/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-1 下午4:11:20 
* @Version V1.1   
*/ 

package com.znt.vodbox.adapter; 

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.znt.vodbox.R;
import com.znt.vodbox.activity.AlbumMusicActivity;
import com.znt.vodbox.entity.MusicAlbumInfor;
import com.znt.vodbox.entity.MusicEditType;
import com.znt.vodbox.utils.ViewUtils;

/** 
 * @ClassName: MusicAlbumAdapter 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-1 下午4:11:20  
 */
public class MusicAlbumAdapter extends BaseAdapter
{
	private Activity baseActivity = null;
	private List<MusicAlbumInfor> list = new ArrayList<MusicAlbumInfor>();
	
	public MusicAlbumAdapter(Activity activity, List<MusicAlbumInfor> list)
	{
		this.baseActivity = activity;
		this.list = list;
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount() 
	{
		// TODO Auto-generated method stub
		int count = list.size();
		if(count%2 == 0)
			return count / 2;
		else
			return count / 2 + 1;
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0) 
	{
		// TODO Auto-generated method stub
		return list.get(arg0);
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

	private ViewHolder vh = null;
	/**
	*callbacks
	*/
	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) 
	{
		// TODO Auto-generated method stub
		if(convertView == null)
		{
			vh = new ViewHolder();
			convertView = LayoutInflater.from(baseActivity).inflate(R.layout.view_music_album_item, null);
			
			vh.tvNameLeft = (TextView)convertView.findViewById(R.id.tv_music_album_item_name_left);
			vh.ivCoverLeft = (ImageView)convertView.findViewById(R.id.iv_music_album_item_cover_left);
			vh.viewBgLeft = convertView.findViewById(R.id.view_music_album_item_left);
			vh.tvNameRight = (TextView)convertView.findViewById(R.id.tv_music_album_item_name_right);
			vh.ivCoverRight = (ImageView)convertView.findViewById(R.id.iv_music_album_item_cover_right);
			vh.viewBgRight = convertView.findViewById(R.id.view_music_album_item_right);
			
			vh.viewBgLeft.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					
					int index = (Integer) v.getTag();
					MusicAlbumInfor infor = list.get(index);
					Bundle bundle = new Bundle();
					bundle.putSerializable("MusicAlbumInfor", infor);
					bundle.putSerializable("MusicEditType", MusicEditType.Add);
					
					bundle.putBoolean("IS_COLLECT", infor.isCollected());
					ViewUtils.startActivity(baseActivity, AlbumMusicActivity.class, bundle);
				}
			});
			vh.viewBgRight.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					
					int index = (Integer) v.getTag();
					MusicAlbumInfor infor = list.get(index);
					Bundle bundle = new Bundle();
					bundle.putSerializable("MusicAlbumInfor", infor);
					bundle.putSerializable("MusicEditType", MusicEditType.Add);
					
					bundle.putBoolean("IS_COLLECT", infor.isCollected());
					ViewUtils.startActivity(baseActivity, AlbumMusicActivity.class, bundle);
				}
			});
			
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		MusicAlbumInfor inforLeft = null; 
		MusicAlbumInfor inforRight = null; 
		
		int leftPos = pos * 2;
		int rightPos = pos * 2 + 1;
		
		inforLeft = list.get(leftPos);
		
		if(rightPos < list.size())
			inforRight = list.get(rightPos);
		
		if(inforLeft != null)
		{
			vh.viewBgLeft.setVisibility(View.VISIBLE);
			vh.viewBgLeft.setTag(leftPos);
			vh.tvNameLeft.setText(inforLeft.getAlbumName() + "(" + inforLeft.getMusicCount() + ")");
			if(!TextUtils.isEmpty(inforLeft.getCover()))
				Picasso.with(baseActivity).load(inforLeft.getCover()).into(vh.ivCoverLeft);
		}
		else
			vh.viewBgLeft.setVisibility(View.INVISIBLE);
		
		if(inforRight != null)
		{
			vh.viewBgRight.setVisibility(View.VISIBLE);
			vh.viewBgRight.setTag(rightPos);
			vh.tvNameRight.setText(inforRight.getAlbumName() + "(" + inforRight.getMusicCount() + ")");
			if(!TextUtils.isEmpty(inforRight.getCover()))
				Picasso.with(baseActivity).load(inforRight.getCover()).into(vh.ivCoverRight);
		}
		else
			vh.viewBgRight.setVisibility(View.INVISIBLE);
		
		
		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder
	{
		ImageView ivCoverLeft = null;
		TextView tvNameLeft = null;
		View viewBgLeft = null;
		ImageView ivCoverRight = null;
		TextView tvNameRight = null;
		View viewBgRight = null;
	}
}
 
