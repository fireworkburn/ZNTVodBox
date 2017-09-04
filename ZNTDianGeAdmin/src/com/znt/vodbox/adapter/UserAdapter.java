/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2017-6-6 下午11:32:13 
* @Version V1.1   
*/ 

package com.znt.vodbox.adapter; 

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.diange.mina.entity.UserInfor;
import com.znt.vodbox.R;

/** 
 * @ClassName: UserAdapter 
 * @Description: TODO
 * @author yan.yu 
 * @date 2017-6-6 下午11:32:13  
 */
public class UserAdapter extends BaseAdapter
{
	private Activity activity = null;
	private List<UserInfor> userList = null;
	public UserAdapter(Activity activity, List<UserInfor> userList)
	{
		this.activity = activity;
		this.userList = userList;
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userList.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return userList.get(arg0);
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
	public View getView(int arg0, View convertView, ViewGroup arg2)
	{
		ViewHolder vh = null;
		if(convertView == null)
		{
			vh = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.view_user_item, null);
			
			vh.tvName = (TextView)convertView.findViewById(R.id.tv_user_item_name);
			
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		UserInfor infor = userList.get(arg0);
		vh.tvName.setText(infor.getUserName());
		
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
 
