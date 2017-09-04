/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-11 下午10:09:06 
* @Version V1.1   
*/ 

package com.znt.vodbox.view; 

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.ChannelActivity;
import com.znt.vodbox.activity.PlanListActivity;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.entity.SubPlanInfor;
import com.znt.vodbox.utils.ViewUtils;

/** 
 * @ClassName: PlanView 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-11 下午10:09:06  
 */
public class PlanView  extends RelativeLayout
{

	private Activity context = null;
	
	private View parentView  = null;
	private TextView tvCurPlanTime = null;
	private TextView tvCurPlanAlbum = null;
	private View viewAllPlan = null;
	private TextView tvCount = null;
	private View bgView = null;
	private View contentView = null;
	private TextView tvErrorHint = null;
	
	private SubPlanInfor subPlanInfor = null;
	private PlanInfor planInfor = null;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param attrs
	* @param defStyle 
	*/
	public PlanView(Activity context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	public PlanView(Activity context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	public PlanView(Activity context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	
	public void setPlanInfor(PlanInfor planInfor)
	{
		this.planInfor = planInfor;
		this.subPlanInfor = planInfor.getCurSubPlanInfor();
		showCurPlanInfor();
	}
	public void showCurPlanInfor()
	{
		if(subPlanInfor != null)
		{
			showPlanTime(subPlanInfor.getPlanTime());
			showPlanAlbum(subPlanInfor.getPlanAlbumName());
			showErrorHint(null);
		}
		else
		{
			showErrorHint("当前时间内没有播放计划");
		}
	}
	
	public void showPlanTime(String text)
	{
		tvCurPlanTime.setText(text);
	}
	public void showPlanAlbum(String text)
	{
		tvCurPlanAlbum.setText(text);
	}
	public void showShopCount(String count)
	{
		tvCount.setText(count);
	}
	
	private void initViews(final Activity context)
	{
		this.context = context;
		
		parentView = LayoutInflater.from(context).inflate(R.layout.view_plan, this, true);
		
		viewAllPlan = parentView.findViewById(R.id.view_plan_all);
		bgView = parentView.findViewById(R.id.view_plan_bg);
		contentView = parentView.findViewById(R.id.view_plan_content);
		tvErrorHint = (TextView)parentView.findViewById(R.id.tv_plan_error_hint);
		tvCount = (TextView)parentView.findViewById(R.id.tv_plan_shop_count);
		tvCurPlanTime = (TextView)parentView.findViewById(R.id.tv_plan_cur_time);
		tvCurPlanAlbum = (TextView)parentView.findViewById(R.id.tv_plan_cur_album);
		viewAllPlan.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putSerializable("PlanInfor", planInfor);
				ViewUtils.startActivity(context, PlanListActivity.class, bundle);
			}
		});
		bgView.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putSerializable("PlanInfor", planInfor);
				ViewUtils.startActivity(context, PlanListActivity.class, bundle);
			}
		});
		tvCurPlanTime.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putSerializable("PlanInfor", planInfor);
				ViewUtils.startActivity(context, PlanListActivity.class, bundle);
			}
		});
		tvCurPlanAlbum.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				if(planInfor.getCurSubPlanInfor() == null)
				{
					Toast.makeText(context, "当前没有播放计划", 0).show();
					return;
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable("SubPlanInfor", planInfor.getCurSubPlanInfor());
				bundle.putBoolean("IS_EDIT", true);
				bundle.putString("PLAN_ID", planInfor.getPlanId());
				ViewUtils.startActivity(context, ChannelActivity.class, bundle, 5);
			}
		});
	}
	
	public void showErrorHint(String hint)
	{
		if(!TextUtils.isEmpty(hint))
		{
			tvErrorHint.setText(hint);
			tvErrorHint.setVisibility(View.VISIBLE);
			contentView.setVisibility(View.GONE);
		}
		else
		{
			contentView.setVisibility(View.VISIBLE);
			tvErrorHint.setVisibility(View.GONE);
		}
	}

}
 
