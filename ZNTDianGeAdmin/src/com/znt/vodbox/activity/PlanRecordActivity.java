/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2017-2-16 下午3:12:56 
* @Version V1.1   
 

package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.entity.SubPlanInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.HintView.OnHintListener;

*//** 
 * @ClassName: PlanRecordActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2017-2-16 下午3:12:56  
 *//*
public class PlanRecordActivity extends BaseActivity implements OnItemClickListener
{

	private ListView listView = null;
	private ParentPlanAdapter adapter = null;
	private HttpFactory httpFactory = null;
	
	private OnHintListener onHintListener = null;
	
	private PlanInfor planInfor = null;
	private List<PlanInfor> planList = new ArrayList<PlanInfor>();
	private List<SubPlanInfor> subPlanList = new ArrayList<SubPlanInfor>();
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.ADD_PLAN_START)
			{
				showProgressDialog(getActivity(), "正在处理...");
			}
			else if(msg.what == HttpMsg.ADD_PLAN_SUCCESS)
			{
				dismissDialog();
				planInfor.setSubPlanList(subPlanList);
				showCurPlanView();
			}
			else if(msg.what == HttpMsg.ADD_PLAN_FAIL)
			{
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "操作失败";
				dismissDialog();
			}
			else if(msg.what == HttpMsg.GET_CUR_PLAN_START)
			{
				//planView.showErrorHint("正在获取播放计划...");
				//showToast("获取数据失败，请下拉刷新");
				showLoadingView(true);
			}
			else if(msg.what == HttpMsg.GET_CUR_PLAN_SUCCESS)
			{
				List<PlanInfor> tempList = (List<PlanInfor>)msg.obj;
				planList.clear();
				if(tempList.size() > 0)
				{
					planList.addAll(tempList);
				}
				else
					showNoDataView("您还没有创建任何计划哦");
				adapter.notifyDataSetChanged();
				showLoadingView(false);
				hideHintView();
				List<SubPlanInfor> subPlanList = planInfor.getSubPlanList();
				if(subPlanList.size() > 0)
				{
					planView.setPlanInfor(planInfor);
				}
				else
					planView.showErrorHint("您还没有播放计划");
				Constant.isPlanUpdated = false;
			}
			else if(msg.what == HttpMsg.GET_CUR_PLAN_FAIL)
			{
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "获取数据失败";
				showToast(error);
				showLoadingView(false);
				showRefreshView(onHintListener);
				//isRunning = false;
				//onLoad(0);
				//showHint("获取播放计划失败");
				//planView.showErrorHint("获取播放计划失败，请重试");
				//showToast("获取数据失败，请下拉刷新");
			}
		};
	};
	
	*//**
	*callbacks
	*//*
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_plan_list);
		
		setCenterString("历史计划");
		
		listView = (ListView)findViewById(R.id.lv_plan);
		
		adapter = new ParentPlanAdapter();
		httpFactory = new HttpFactory(getActivity(), handler);
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		planInfor = (PlanInfor)getIntent().getSerializableExtra("PlanInfor");
		
		if(planInfor != null && planInfor.getSubPlanList() != null && planInfor.getSubPlanList().size() != 0)
			showCurPlanView();
		
		if(planInfor == null)
			planInfor = new PlanInfor();
		
		showRightView(true);
		setRightText("新建计划");
		
		onHintListener = new OnHintListener() {
			
			@Override
			public void onHintRefresh() {
				// TODO Auto-generated method stub
				httpFactory.getCurPlan();
			}
		};
		
		getRightView().setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				//ViewUtils.startActivity(getActivity(), PlanCreateListActivity.class, null, 2);
				Bundle bundle = new Bundle();
				bundle.putBoolean("IS_EDIT", false);
				ViewUtils.startActivity(getActivity(), PlanDetailActivity.class, bundle, 2);
			}
		});
		
		httpFactory.getCurPlan();
	}
	
	private void showCurPlanView()
	{
		if(planInfor != null)
		{
			subPlanList.addAll(planInfor.getSubPlanList());
			adapter.notifyDataSetChanged();
		}
		setCenterString("当前播放计划");
		showRightView(false);
	}
	
	*//**
	*callbacks
	*//*
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		bundle.putSerializable("PlanInfor", planInfor);
		planInfor.setSelectIndex(arg2);
		bundle.putBoolean("IS_EDIT", true);
		ViewUtils.startActivity(getActivity(), PlanCreateActivity.class, bundle, 2);
		Bundle bundle = new Bundle();
		bundle.putSerializable("PlanInfor", planList.get(arg2));
		bundle.putBoolean("IS_EDIT", true);
		ViewUtils.startActivity(getActivity(), PlanDetailActivity.class, bundle, 2);
	}
	
	private class ParentPlanAdapter extends BaseAdapter
	{

		*//**
		*callbacks
		*//*
		@Override
		public int getCount() 
		{
			// TODO Auto-generated method stub
			return planList.size();
		}

		*//**
		*callbacks
		*//*
		@Override
		public Object getItem(int arg0) 
		{
			// TODO Auto-generated method stub
			return planList.get(arg0);
		}

		*//**
		*callbacks
		*//*
		@Override
		public long getItemId(int arg0) 
		{
			// TODO Auto-generated method stub
			return arg0;
		}

		*//**
		*callbacks
		*//*
		@Override
		public View getView(int pos, View convertView, ViewGroup arg2)
		{
			// TODO Auto-generated method stub
			ViewHolder vh = null;
			if(convertView == null)
			{
				vh = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_parent_plan_item, null);
				vh.tvName = (TextView)convertView.findViewById(R.id.tv_parent_plan_item_name);
				vh.tvTime = (TextView)convertView.findViewById(R.id.tv_parent_plan_item_time);
				vh.tvTypeTime = (TextView)convertView.findViewById(R.id.tv_parent_plan_item_tpye_shop);
				vh.tvTypeShop = (TextView)convertView.findViewById(R.id.tv_parent_plan_item_type_time);
				
				convertView.setTag(vh);
			}
			else
				vh = (ViewHolder)convertView.getTag();
			
			
			PlanInfor infor = planList.get(pos);
			vh.tvName.setText(infor.getPlanName());
			vh.tvTime.setText("创建于： " + infor.getPublishTimeFormat());
			
			
			String planFlag = infor.getPlanFlag();
			if(planFlag.equals("0"))
			{
				vh.tvTypeTime.setText("全部店铺");
			}
			else
			{
				vh.tvTypeTime.setText("指定店铺  " + infor.getDeviceList().size() + " 个");
			}
			
			String planType = infor.getPlanType();
			if(planType.equals("0"))
			{
				vh.tvTypeShop.setText("指定日期计划:  " + infor.getStartDate() + "~" + infor.getEndDate());
			}
			else
			{
				vh.tvTypeShop.setText("每天的计划");
			}
			
			
			return convertView;
		}
		
		private class ViewHolder
		{
			TextView tvName = null;
			TextView tvTime = null;
			TextView tvTypeTime = null;
			TextView tvTypeShop = null;
		}
	}
	
}
 
*/