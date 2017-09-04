/*  
* @Project: ZNTDianGe 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-5-25 下午11:45:54 
* @Version V1.1   
*/ 

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
import com.znt.diange.entity.SongRecordInfor;

/** 
 * @ClassName: SongRecordAdapter 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-5-25 下午11:45:54  
 */
public class SongRecordAdapter extends BaseAdapter
{
	private Activity activity = null;
	private List<SongRecordInfor> recordList = new ArrayList<SongRecordInfor>();

	public SongRecordAdapter(Activity activity, List<SongRecordInfor> recordList)
	{
		this.activity = activity;
		this.recordList = recordList;
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount() 
	{
		// TODO Auto-generated method stub
		return recordList.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0) 
	{
		// TODO Auto-generated method stub
		return recordList.get(arg0);
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.view_song_record_item, null);
			
			vh = new ViewHolder();
			
			vh.ivCover = (ImageView)convertView.findViewById(R.id.iv_song_record_item_cover);
			vh.ivUserHead = (CircleImageView)convertView.findViewById(R.id.iv_song_record_item_user_head);
			vh.tvName = (TextView)convertView.findViewById(R.id.tv_song_record_item_music_name);
			vh.tvMsg = (TextView)convertView.findViewById(R.id.tv_song_record_item_msg);
			vh.tvUser = (TextView)convertView.findViewById(R.id.tv_song_record_item_user_name);
			vh.tvComment = (TextView)convertView.findViewById(R.id.tv_song_record_item_comment);
			vh.tvPraise = (TextView)convertView.findViewById(R.id.tv_song_record_item_praise);
			
			convertView.setTag(vh);
			
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		SongRecordInfor tempInfor = recordList.get(pos);
		
		
		if(!TextUtils.isEmpty(tempInfor.getMediaCover()))
			Picasso.with(activity).load(tempInfor.getMediaCover()).into(vh.ivCover);
		if(!TextUtils.isEmpty(tempInfor.getUserInfor().getHead()))
			Picasso.with(activity).load(tempInfor.getUserInfor().getHead()).into(vh.ivUserHead);
		vh.tvName.setText(tempInfor.getMediaName());
		vh.tvMsg.setText(tempInfor.getPlayMsg());
		vh.tvUser.setText(tempInfor.getUserInfor().getUserName());
		vh.tvPraise.setText(tempInfor.getPraiseCount());
		vh.tvComment.setText(tempInfor.getCommentCount());
		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder
	{
		ImageView ivCover = null;
		CircleImageView ivUserHead = null;
		TextView tvName = null;
		TextView tvMsg = null;
		TextView tvUser = null;
		TextView tvComment = null;
		TextView tvPraise = null;
	}
}
 
