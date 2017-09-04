/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-19 下午8:44:30 
* @Version V1.1   
*/ 

package com.znt.vodbox.adapter; 

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.entity.SubPlanInfor;

/** 
 * @ClassName: PlanAdapter 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-19 下午8:44:30  
 */
public class PlanAdapter extends BaseAdapter
{
	private Activity activity = null;
	private PlanInfor planInfor = null;
	private List<SubPlanInfor> subPlanList = new ArrayList<SubPlanInfor>();
	private int selectIndex = 0;
	private boolean isEdit = false;
	
	public PlanAdapter(Activity activity, PlanInfor planInfor, List<SubPlanInfor> subPlanList, boolean isEdit)
	{
		this.activity = activity;
		this.isEdit = isEdit;
		this.planInfor = planInfor;
		this.subPlanList = subPlanList;
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount() 
	{
		// TODO Auto-generated method stub
		return subPlanList.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0) 
	{
		// TODO Auto-generated method stub
		return subPlanList.get(arg0);
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
		// TODO Auto-generated method stub
		
		ViewHolder vh = null;
		if(convertView == null)
		{
			vh = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.view_plan_item, null);
			vh.tvTime = (TextView)convertView.findViewById(R.id.tv_plan_item_time);
			vh.tvAlbum = (TextView)convertView.findViewById(R.id.tv_plan_item_album);
			vh.viewDelete = convertView.findViewById(R.id.view_plan_item_delete);
			
			if(isEdit)
			{
				vh.viewDelete.setVisibility(View.VISIBLE);
				vh.viewDelete.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						// TODO Auto-generated method stub
						selectIndex = (Integer) v.getTag();
						subPlanList.remove(selectIndex);
						if(planInfor == null)
							planInfor = new PlanInfor();
						planInfor.setSubPlanList(subPlanList);
						notifyDataSetChanged();
					}
				});
			}
			else
				vh.viewDelete.setVisibility(View.GONE);
			
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		vh.viewDelete.setTag(pos);
		
		SubPlanInfor infor = subPlanList.get(pos);
		vh.tvTime.setText(infor.getPlanTime());
		vh.tvAlbum.setText(infor.getPlanAlbumName());
		
		return convertView;
	}
	
	private class ViewHolder
	{
		TextView tvTime = null;
		TextView tvAlbum = null;
		View viewDelete = null;
	}
}