/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-12 上午1:25:10 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
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
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.entity.SubPlanInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.http.HttpResult;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.HintView.OnHintListener;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: PlanActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-12 上午1:25:10  
 */
public class PlanListActivity extends BaseActivity implements IXListViewListener, OnItemClickListener
{
	
	private LJListView listView = null;
	private TextView tvCreatePlan = null;
	private View viewLeft = null;
	private View viewRight = null;
	private View viewBottom = null;
	private TextView tvLeft = null;
	private TextView tvRight = null;
	
	private ParentPlanAdapter adapter = null;
	private HttpFactory httpFactory = null;
	
	private OnHintListener onHintListener = null;
	
	private PlanInfor planInfor = null;
	private String terminalId = null;
	private String terminalName = null;
	private List<PlanInfor> planList = new ArrayList<PlanInfor>();
	private List<SubPlanInfor> subPlanList = new ArrayList<SubPlanInfor>();
	
	private int pageNo = 1;
	private String status = "0";
	private boolean isRunning = false;
	private boolean isLoadFinish = false;
	private String userId = "";
	private String userName = "";
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
				//showLoadingView(true);
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_CUR_PLAN_SUCCESS)
			{
				HttpResult httpRequest = (HttpResult)msg.obj;
				List<PlanInfor> tempList = (List<PlanInfor>)httpRequest.getReuslt();
				if(httpRequest.getTotal() > 0)
				{
					if(TextUtils.isEmpty(userId))
						setCenterString("我的播放计划(" + httpRequest.getTotal() + ")");
					else
						setCenterString("区域计划(" + httpRequest.getTotal() + ")");
				}
				if(pageNo == 1)
					planList.clear();
				
				if(tempList.size() > 0)
					planList.addAll(tempList);
				if(planList.size() == 0)
				{
					showNoDataView("您还没有创建计划哦~");
					listView.showFootView(false);
				}
				if(planList.size() >= httpRequest.getTotal())
				{
					listView.showFootView(false);
					isLoadFinish = true;
				}
				else
				{
					listView.showFootView(true);
					isLoadFinish = false;
				}
				adapter.notifyDataSetChanged();
				hideHintView();
				
				pageNo ++;
				
				adapter.notifyDataSetChanged();
				onLoad(0);
				hideHintView();
				isRunning = false;
			}
			else if(msg.what == HttpMsg.GET_CUR_PLAN_FAIL)
			{
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "获取数据失败";
				showToast(error);
				isRunning = false;
				onLoad(0);
			}
			else if(msg.what == HttpMsg.START_OLDPLAN_START)
			{
				showProgressDialog(getActivity(), "正在启动计划...");
			}
			else if(msg.what == HttpMsg.START_OLDPLAN_SUCCESS)
			{
				getCurPlans();
				dismissDialog();
			}
			else if(msg.what == HttpMsg.START_OLDPLAN_FAIL)
			{
				dismissDialog();
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "启动计划失败";
				showToast(error);
			}
			else if(msg.what == HttpMsg.DELETE_OLDPLAN_START)
			{
				showProgressDialog(getActivity(), "正在删除计划...");
			}
			else if(msg.what == HttpMsg.DELETE_OLDPLAN_SUCCESS)
			{
				pageNo = 1;
				getCurPlans();
				dismissDialog();
			}
			else if(msg.what == HttpMsg.DELETE_OLDPLAN_FAIL)
			{
				dismissDialog();
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "删除失败";
				showToast(error);
			}
		};
	};
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_plan_list);
		
		viewBottom = findViewById(R.id.view_plan_list_head);
		viewLeft = findViewById(R.id.view_plan_list_leftopr);
		viewRight = findViewById(R.id.view_plan_list_rightopr);
		tvLeft = (TextView)findViewById(R.id.tv_plan_list_leftopr);
		tvRight = (TextView)findViewById(R.id.tv_plan_list_rightopr);
		listView = (LJListView)findViewById(R.id.lv_plan);
		listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		listView.getListView().setDividerHeight(1);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();
		listView.setOnItemClickListener(this);
		
		tvCreatePlan = (TextView)findViewById(R.id.tv_plan_list_create);
		
		adapter = new ParentPlanAdapter();
		httpFactory = new HttpFactory(getActivity(), handler);
		
		listView.setAdapter(adapter);
		
		planInfor = (PlanInfor)getIntent().getSerializableExtra("PlanInfor");
		userId = getIntent().getStringExtra("USER_ID");
		userName = getIntent().getStringExtra("USER_NAME");
		terminalId = getIntent().getStringExtra("terminalId");
		terminalName = getIntent().getStringExtra("terminalName");
		
		if(planInfor != null && planInfor.getSubPlanList() != null && planInfor.getSubPlanList().size() != 0)
			showCurPlanView();
		
		if(planInfor == null)
			planInfor = new PlanInfor();
		
		
		if(TextUtils.isEmpty(terminalName))
		{
			if(TextUtils.isEmpty(userId))
				setCenterString("我的播放计划");
			else
				setCenterString("区域计划");
			showRightView(true);
			setRightText("新建计划");
		}
		else
		{
			if(getLocalData().isAdminUser())
			{
				showRightView(false);
				setCenterString(terminalName+"的播放计划");
			}
			else 
			{
				if(TextUtils.isEmpty(userId))
					setCenterString("我的播放计划");
				else
					setCenterString("区域计划");
				showRightView(true);
				setRightText("新建计划");
			}
		}
		
		//if(getLocalData().isAdminUser())
		if(TextUtils.isEmpty(terminalId))
			viewBottom.setVisibility(View.VISIBLE);
		else
			viewBottom.setVisibility(View.GONE);
		
		onHintListener = new OnHintListener() {
			
			@Override
			public void onHintRefresh() {
				// TODO Auto-generated method stub
				pageNo = 1;
				getCurPlans();
			}
		};
		listView.onFresh();
		getRightView().setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				//ViewUtils.startActivity(getActivity(), PlanCreateListActivity.class, null, 2);
				Bundle bundle = new Bundle();
				bundle.putBoolean("IS_EDIT", false);
				bundle.putString("USER_ID", userId);
				bundle.putString("USER_NAME", userName);
				ViewUtils.startActivity(getActivity(), PlanDetailActivity.class, bundle, 2);
			}
		});
		viewLeft.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				showLeftView();
				planList.clear();
				adapter.notifyDataSetChanged();
				pageNo = 1;
				status = "0";
				listView.onFresh();
			}
		});
		viewRight.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				showRightView();
				planList.clear();
				adapter.notifyDataSetChanged();
				pageNo = 1;
				status = "1";
				listView.onFresh();
			}
		});
		showLeftView();
	}
	private void showLeftView()
	{
		viewLeft.setSelected(true);
		viewRight.setSelected(false);
		tvLeft.setTextColor(getResources().getColor(R.color.text_blue_on));
		tvRight.setTextColor(getResources().getColor(R.color.text_black_on));
	}
	private void showRightView()
	{
		viewLeft.setSelected(false);
		viewRight.setSelected(true);
		tvLeft.setTextColor(getResources().getColor(R.color.text_black_on));
		tvRight.setTextColor(getResources().getColor(R.color.text_blue_on));
	}
	
	private void getCurPlans()
	{
		if(TextUtils.isEmpty(terminalId))
			httpFactory.getCurPlan(userId, status, pageNo);
		else
		{
			/*if(status.equals("0"))
				httpFactory.getBoxPlan(status, pageNo, terminalId);
			else
				httpFactory.getCurPlan(status, pageNo);*/
			httpFactory.getBoxPlan(status, pageNo, terminalId);
		}
	}
	
	private void showCurPlanView()
	{
		if(planInfor != null)
		{
			subPlanList.addAll(planInfor.getSubPlanList());
			adapter.notifyDataSetChanged();
		}
		setCenterString("当前播放计划");
		tvCreatePlan.setText("新建播放计划");
		showRightView(false);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(resultCode != RESULT_OK)
		{
			return;
		}
		if(requestCode == 2)
		{
			/*planInfor = (PlanInfor)data.getSerializableExtra("PlanInfor");
			subPlanList.clear();
			showCurPlanView();
			Constant.isPlanUpdated = true;*/
			pageNo = 1;
			listView.onFresh();
		}
		
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		if(status.equals("0"))
		{
			if(arg2 >= 1)
				arg2 = arg2 - 1;
			Bundle bundle = new Bundle();
			bundle.putSerializable("PlanInfor", planList.get(arg2));
			bundle.putBoolean("IS_EDIT", true);
			bundle.putString("terminalId", terminalId);
			bundle.putString("terminalName", terminalName);
			ViewUtils.startActivity(getActivity(), PlanDetailActivity.class, bundle, 2);
		}
	}
	
	private class ParentPlanAdapter extends BaseAdapter
	{

		/**
		*callbacks
		*/
		@Override
		public int getCount() 
		{
			// TODO Auto-generated method stub
			return planList.size();
		}

		/**
		*callbacks
		*/
		@Override
		public Object getItem(int arg0) 
		{
			// TODO Auto-generated method stub
			return planList.get(arg0);
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
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_parent_plan_item, null);
				vh.tvName = (TextView)convertView.findViewById(R.id.tv_parent_plan_item_name);
				vh.tvTime = (TextView)convertView.findViewById(R.id.tv_parent_plan_item_time);
				vh.tvTypeTime = (TextView)convertView.findViewById(R.id.tv_parent_plan_item_tpye_shop);
				vh.tvTypeShop = (TextView)convertView.findViewById(R.id.tv_parent_plan_item_type_time);
				vh.viewUsePlan = convertView.findViewById(R.id.view_parent_plan_item_use);
				vh.viewDeletePlan = convertView.findViewById(R.id.view_parent_plan_item_delete);
				vh.viewOperation = convertView.findViewById(R.id.view_parent_plan_item_right);
				
				vh.viewUsePlan.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{
						// TODO Auto-generated method stub
						int pos = (Integer) arg0.getTag();
						PlanInfor tempInfor = planList.get(pos);
						httpFactory.startOldPlan(tempInfor.getPlanId());
					}
				});
				vh.viewDeletePlan.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View arg0) 
					{
						// TODO Auto-generated method stub
						int pos = (Integer) arg0.getTag();
						PlanInfor tempInfor = planList.get(pos);
						httpFactory.deleteOldPlan(tempInfor.getPlanId());
					}
				});
				
				convertView.setTag(vh);
			}
			else
				vh = (ViewHolder)convertView.getTag();
			
			if(status.equals("0"))
				vh.viewOperation.setVisibility(View.GONE);
			else
				vh.viewOperation.setVisibility(View.VISIBLE);
			
			vh.viewUsePlan.setTag(pos);
			vh.viewDeletePlan.setTag(pos);
			
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
			View viewUsePlan = null;
			View viewDeletePlan = null;
			View viewOperation = null;
		}
	}
	
	private void onLoad(int updateCount) 
	{
		listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();
	}

	/**
	*callbacks
	*/
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if(!isRunning)
		{
			pageNo = 1;
			getCurPlans();
		}
	}

	/**
	*callbacks
	*/
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if(!isRunning && !isLoadFinish)
		{
			getCurPlans();
		}
		else
		{
			onLoad(0);
			listView.showFootView(false);
		}
	}
}
 
