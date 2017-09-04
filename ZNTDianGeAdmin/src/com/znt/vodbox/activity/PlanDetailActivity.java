/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-7-5 下午10:10:04 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.ListView;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.PlanAdapter;
import com.znt.vodbox.dialog.DoubleDatePickerDialog;
import com.znt.vodbox.dialog.EditNameDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.entity.SubPlanInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.ItemTextView;
import com.znt.vodbox.view.SwitchButton;

/** 
 * @ClassName: PlanDetailActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-7-5 下午10:10:04  
 */
public class PlanDetailActivity extends BaseActivity implements OnClickListener, OnItemClickListener
{

	private View headerView = null;
	private ItemTextView itvName = null;
	private ItemTextView itvShops = null;
	private ItemTextView itvMusics = null;
	private ItemTextView itvDateStart = null;
	private ItemTextView itvDateEnd = null;
	private ListView listView = null;
	private View viewAdd = null;
	private View viewApply = null;
	private SwitchButton switchButton = null;
	private SwitchButton switchButtonDate = null;
	
	private DoubleDatePickerDialog startTimeDialog = null;
	private DoubleDatePickerDialog endTimeDialog = null;
	
	private HttpFactory httpFactory = null;
	
	private List<SubPlanInfor> subPlanList = new ArrayList<SubPlanInfor>();
	private PlanAdapter adapter = null;
	private PlanInfor planInfor = null;
	private String terminalId = "";
	private String terminalName = "";
	private String userId = "";
	private String userName = "";
	
	private boolean isEdit = false;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.EDIT_PLAN_START)
			{
				showProgressDialog(getActivity(), "正在处理...");
			}
			else if(msg.what == HttpMsg.EDIT_PLAN_SUCCESS)
			{
				/*Bundle bundle = new Bundle();
				if(!isEdit)
					planInfor.addSubPlanInfor(tempInfor);
				else
					planInfor.updateSelect(tempInfor);
				bundle.putSerializable("PlanInfor", planInfor);
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();*/
				
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						dismissDialog();
						showToast("操作成功");
						setResult(RESULT_OK);
						finish();
					}
				}, Constant.SUC_DELAY_TIME);
			}
			else if(msg.what == HttpMsg.EDIT_PLAN_FAIL)
			{
				showToast("操作失败");
				dismissDialog();
			}
			else if(msg.what == HttpMsg.ADD_PLAN_START)
			{
				showProgressDialog(getActivity(), "正在处理...");
			}
			else if(msg.what == HttpMsg.ADD_PLAN_SUCCESS)
			{
				dismissDialog();
				showToast("操作成功");
				setResult(RESULT_OK);
				finish();
			}
			else if(msg.what == HttpMsg.ADD_PLAN_FAIL)
			{
				showToast("操作失败");
				dismissDialog();
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
		
		setContentView(R.layout.activity_plan_detail);
		
		
		headerView = LayoutInflater.from(getActivity()).inflate(R.layout.view_plan_detail_header, null);
		viewApply = headerView.findViewById(R.id.view_plan_detail_apply);
		switchButton = (SwitchButton)headerView.findViewById(R.id.sb_plan_detail);
		switchButtonDate = (SwitchButton)headerView.findViewById(R.id.sb_plan_detail_date);
		
		itvName = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_name);
		itvShops = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_shops);
		itvMusics = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_musics);
		itvDateStart = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_date_select_start);
		itvDateEnd = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_date_select_end);
		viewAdd = headerView.findViewById(R.id.view_plan_detail_add);
		listView = (ListView)findViewById(R.id.lv_plan_detail_sub);
		
		listView.addHeaderView(headerView);
		
		planInfor = (PlanInfor)getIntent().getSerializableExtra("PlanInfor");
		userId = getIntent().getStringExtra("USER_ID");
		userName = getIntent().getStringExtra("USER_NAME");
		isEdit = getIntent().getBooleanExtra("IS_EDIT", false);
		terminalId = getIntent().getStringExtra("terminalId");
		terminalName = getIntent().getStringExtra("terminalName");
		adapter = new PlanAdapter(getActivity(), planInfor, subPlanList, true);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		
		if(TextUtils.isEmpty(terminalId))
		{
			setRightText("保存");
			showRightImageView(false);
			setCenterString("计划详情");
		}
		else
		{
			if(getLocalData().isAdminUser())
			{
				setCenterString(terminalName + "的计划详情");
				showRightView(false);
			}
			else
			{
				setRightText("保存");
				showRightImageView(false);
				setCenterString("计划详情");
			}
			
		}
		
		if(planInfor == null)
		{
			planInfor = new PlanInfor();
			planInfor.setPlanName(getLocalData().getUserName() + "的计划");
		}
		
		updateViewData();
		
		itvName.getFirstView().setText("计划名称");
		itvShops.getFirstView().setText("自定义店铺");
		itvMusics.getFirstView().setText("该计划所有音乐");
		itvDateStart.getFirstView().setText("请设置开始日期");
		itvDateEnd.getFirstView().setText("请设置结束日期");
		itvName.showMoreButton(true);
		itvShops.showMoreButton(true);
		itvMusics.showMoreButton(true);
		itvDateStart.showMoreButton(true);
		itvDateEnd.showMoreButton(true);
		
		itvMusics.showBottomLine(false);
		itvDateEnd.showBottomLine(false);
		
		viewAdd.setOnClickListener(this);
		itvName.getBgView().setOnClickListener(this);
		itvShops.getBgView().setOnClickListener(this);
		itvMusics.getBgView().setOnClickListener(this);
		itvDateStart.getBgView().setOnClickListener(this);
		itvDateEnd.getBgView().setOnClickListener(this);
		
		getRightView().setOnClickListener(this);
		
		switchButton.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
			{
				// TODO Auto-generated method stub
				showShops(!arg1);
			}
		});
		switchButtonDate.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
			{
				// TODO Auto-generated method stub
				showDateSelect(arg1);
			}
		});
		
		startTimeDialog = new DoubleDatePickerDialog(getActivity(), 0, new DoubleDatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
					int startDayOfMonth) {
				String textString = String.format("%d-%d-%d", startYear,
						startMonthOfYear + 1, startDayOfMonth);
				/*String start = String.format("%d-%d-%d", startYear,
						startMonthOfYear + 1, startDayOfMonth);
				String end = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);*/
				planInfor.setStartDate(textString);
				itvDateStart.getSecondView().setText("开始时间：" + textString);
			}
		},  null,"开始时间");
		endTimeDialog = new DoubleDatePickerDialog(getActivity(), 0, new DoubleDatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
					int startDayOfMonth) {
				String textString = String.format("%d-%d-%d", startYear,
						startMonthOfYear + 1, startDayOfMonth);
				/*String start = String.format("%d-%d-%d", startYear,
						startMonthOfYear + 1, startDayOfMonth);
				String end = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);*/
				planInfor.setEndDate(textString);
				itvDateEnd.getSecondView().setText("结束时间：" + textString);
			}
		}, null, "结束时间");
		
		httpFactory = new HttpFactory(getActivity(), handler);
	}
	
	private void updateViewData()
	{
		subPlanList.clear();
		subPlanList.addAll(planInfor.getSubPlanList());
		adapter.notifyDataSetChanged();
		if(TextUtils.isEmpty(userName))
			itvName.getSecondView().setText(planInfor.getPlanName());
		else
			itvName.getSecondView().setText(userName + "播放计划");
		String[] shopIds = (planInfor.getAllTerminalIds()).split(",");
		if(shopIds.length == 1 && TextUtils.isEmpty(shopIds[0]))
		{
			itvShops.getSecondView().setText("0个店铺");
		}
		else
			itvShops.getSecondView().setText(shopIds.length + "个店铺");
		//itvMusics.getSecondView().setText("音响在线曲库管理");
		
		//if(isEdit)
		{
			switchButton.setChecked(planInfor.getPlanFlag().equals("0"));
			switchButtonDate.setChecked(planInfor.getPlanType().equals(PlanInfor.PLAN_TYPE_YEAR));
			showShops(!planInfor.getPlanFlag().equals("0"));
			showDateSelect(planInfor.getPlanType().equals(PlanInfor.PLAN_TYPE_YEAR));
		}
		/*else
		{
			showShops(false);
			switchButton.setChecked(true);
			switchButtonDate.setChecked(false);
		}*/
	}
	
	private void showShops(boolean isShow)
	{
		if(isShow)
		{
			itvShops.setVisibility(View.VISIBLE);
			planInfor.setPlanFlag("1");
		}
		else
		{
			itvShops.setVisibility(View.GONE);
			planInfor.setPlanFlag("0");
		}
	}
	private void showDateSelect(boolean isShow)
	{
		if(isShow)
		{
			itvDateStart.setVisibility(View.VISIBLE);
			itvDateEnd.setVisibility(View.VISIBLE);
			planInfor.setPlanType(PlanInfor.PLAN_TYPE_YEAR);
			itvDateStart.getSecondView().setText("开始时间：" + planInfor.getStartDate());
			itvDateEnd.getSecondView().setText("结束时间：" + planInfor.getEndDate());
			//planInfor.setPlanFlag("1");
		}
		else
		{
			itvDateStart.setVisibility(View.GONE);
			itvDateEnd.setVisibility(View.GONE);
			planInfor.setPlanType(PlanInfor.PLAN_TYPE_EVERYDAY);
			//planInfor.setPlanFlag("0");
		}
	}
	
	private void updatePlan()
	{
		planInfor.setSubPlanList(subPlanList);
		if(!checkParams())
			return;
		httpFactory.editPlan(userId, planInfor);
	}
	private void startAddPlan()
	{
		planInfor.setSubPlanList(subPlanList);
		if(!checkParams())
			return;
		httpFactory.addPlan(userId, planInfor);
	}
	
	private boolean checkParams()
	{
		if(planInfor.getSubPlanList().size() == 0)
		{
			showToast("请添加播放计划");
			return false;
		}
		if(TextUtils.isEmpty(planInfor.getAllCategoryIds()))
		{
			showToast("歌单不能为空");
			return false;
		}
		if(switchButton.isChecked())
		{
			planInfor.setPlanFlag("0");
		}
		else
		{
			planInfor.setPlanFlag("1");
			if(planInfor.getDeviceList().size() == 0)
			{
				showToast("请选择要设置的店铺");
				return false;
			}
				
		}
		return true;
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		planInfor.setSelectIndex(-1);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode != RESULT_OK)
			return;
		if(requestCode == 1)
		{
			planInfor = (PlanInfor)data.getSerializableExtra("PlanInfor");
			updateViewData();
		}
		else if(requestCode == 2)
		{
			ArrayList<? extends Parcelable> selectedList = data.getParcelableArrayListExtra("SELECT_SHOPS");
			planInfor.setDeviceList((List<DeviceInfor>) selectedList);
			updateViewData();
		}
	}
	
	private EditNameDialog dialog = null;
	private void showNameEditDialog(final String name)
	{
		if(dialog == null || dialog.isDismissed())
			dialog = new EditNameDialog(getActivity());
		dialog.setInfor(name);
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(dialog.isShowing())
			dialog.dismiss();
		dialog.show();
		dialog.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				if(TextUtils.isEmpty(dialog.getContent()))
				{
					showToast("请输入计划名称");
					return;
				}
				if(dialog.getContent().equals(name))
				{
					showToast("计划名称未更改");
					return;
				}
				planInfor.setPlanName(dialog.getContent());
				itvName.getSecondView().setText(dialog.getContent());
				dialog.dismiss();
				//updatePlan();
			}
		});
		
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		dialog.getWindow().setAttributes(lp);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		if(v == viewAdd)
		{
			planInfor.setSubPlanList(subPlanList);
			Bundle bundle = new Bundle();
			bundle.putSerializable("PlanInfor", planInfor);
			bundle.putBoolean("IS_EDIT", false);
			ViewUtils.startActivity(getActivity(), PlanCreateActivity.class, bundle, 1);
		}
		else if(v == getRightView())
		{
			if(!isEdit)
				startAddPlan();
			else
			{
				if(getLocalData().isAdminUser())
					updatePlan();
				else if(!TextUtils.isEmpty(terminalId))
				{
					if(planInfor.getTerminalList().equals(terminalId))//分店计划
						updatePlan();
					else
						startAddPlan();
				}
			}
		}
		else if(v == itvMusics.getBgView())
		{
			Bundle bundle = new Bundle();
			bundle.putSerializable("PlanInfor", planInfor);
			ViewUtils.startActivity(getActivity(), PlanAllMusicActivity.class, bundle);
		}
		else if(v == itvDateStart.getBgView())
		{
			if(isEdit)
				startTimeDialog.showTimeDialog(planInfor.getStartDate());
			else
				startTimeDialog.showTimeDialog(null);
		}
		else if(v == itvDateEnd.getBgView())
		{
			if(isEdit)
				endTimeDialog.showTimeDialog(planInfor.getEndDate());
			else
				endTimeDialog.showTimeDialog(null);
		}
		else if(v == itvShops.getBgView())
		{
			Bundle bundle = new Bundle();
			bundle.putSerializable("PlanInfor", planInfor);
			ViewUtils.startActivity(getActivity(), ShopSelectActivity.class, bundle, 2);
		}
		else if(v == itvName.getBgView())
		{
			showNameEditDialog(planInfor.getPlanName());
		}
	}

	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		if(arg2 > 0)
			arg2 = arg2 - 1;
		Bundle bundle = new Bundle();
		bundle.putSerializable("PlanInfor", planInfor);
		bundle.putBoolean("IS_EDIT", true);
		planInfor.setSelectIndex(arg2);
		ViewUtils.startActivity(getActivity(), PlanCreateActivity.class, bundle, 1);
	}
	
}
 
