
package com.znt.vodbox.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.znt.vodbox.R;

/** 
* @ClassName: RecordAdapter 
* @Description: TODO
* @author yan.yu 
* @date 2015年10月31日 下午10:15:04  
*/
public class RecordAdapter extends BaseAdapter
{
	
	private Activity context = null;
	
	private List<String>  searchRecords = new ArrayList<String>();
	
	public RecordAdapter(Activity context, List<String>  searchRecords)
	{
		this.context = context;
		this.searchRecords = searchRecords;
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return searchRecords.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return searchRecords.get(arg0);
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
		TextView textView = null;
		if(convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.view_label_record_item, null);
			
			textView = (TextView)convertView.findViewById(R.id.tv_category_record_item);
			
			convertView.setTag(textView);
		}
		else
			textView = (TextView)convertView.getTag();
		
		textView.setText(searchRecords.get(arg0));
		
		// TODO Auto-generated method stub
		return convertView;
	}
	
}