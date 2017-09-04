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
import com.znt.diange.entity.CommentInfor;
import com.znt.diange.entity.SongRecordInfor;

/** 
 * @ClassName: SongRecordAdapter 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-5-25 下午11:45:54  
 */
public class CommentAdapter extends BaseAdapter
{
	private Activity activity = null;
	private List<CommentInfor> commentList = new ArrayList<CommentInfor>();

	public CommentAdapter(Activity activity, List<CommentInfor> commentList)
	{
		this.activity = activity;
		this.commentList = commentList;
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount() 
	{
		// TODO Auto-generated method stub
		return commentList.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0) 
	{
		// TODO Auto-generated method stub
		return commentList.get(arg0);
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.view_comment_item, null);
			
			vh = new ViewHolder();
			
			vh.ivUserHead = (CircleImageView)convertView.findViewById(R.id.iv_comment_item_user_head);
			vh.tvMsg = (TextView)convertView.findViewById(R.id.tv_comment_item_content);
			vh.tvUser = (TextView)convertView.findViewById(R.id.tv_comment_item_user_name);
			vh.tvTime = (TextView)convertView.findViewById(R.id.tv_comment_item_time);
			
			convertView.setTag(vh);
			
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		CommentInfor tempInfor = commentList.get(pos);
		
		
		if(!TextUtils.isEmpty(tempInfor.getUserHead()))
			Picasso.with(activity).load(tempInfor.getUserHead()).into(vh.ivUserHead);
		vh.tvUser.setText(tempInfor.getUserName());
		vh.tvMsg.setText(tempInfor.getContent());
		vh.tvTime.setText(tempInfor.getCommentTime());
		// TODO Auto-generated method stub
		return convertView;
	}

	private class ViewHolder
	{
		CircleImageView ivUserHead = null;
		TextView tvMsg = null;
		TextView tvUser = null;
		TextView tvTime = null;
	}
}
 
