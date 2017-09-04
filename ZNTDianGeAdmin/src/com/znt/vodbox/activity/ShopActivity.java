/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2017-6-6 下午10:56:45 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.UserInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.ShopAdapter;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.factory.LocationFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.http.HttpResult;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.PlanView;
import com.znt.vodbox.view.HintView.OnHintListener;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: ShopActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2017-6-6 下午10:56:45  
 */
public class ShopActivity extends BaseActivity implements IXListViewListener, OnItemClickListener
{
	private PlanView planView = null;
	private LJListView listView = null;
	private ShopAdapter deviceAdapter = null;
	private List<DeviceInfor> deviceList = new ArrayList<DeviceInfor>();
	private HttpFactory httpFactory = null;
	private LocationFactory locationFactory = null;
	
	private OnHintListener onHintListener = null;
	
	private PlanInfor planInfor = null;
	private DeviceInfor oldDeviceInfor = null;
	private int pageNum = 1;
	private int total = 0;
	private boolean isRunning = false;
	private boolean isChangeDevice = false;
	private boolean isPrepared = false;
	private boolean isLoadFinish = false;
	private boolean isFirstEnter = true;
	private String areaName = "";
	private String userId = null;
	private String userName = null;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			/*if(msg.what == HttpMsg.GET_SECOND_LEVELS_START)
			{
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_SECOND_LEVELS_SUCCESS)
			{
				HttpResult httpResult = (HttpResult)msg.obj;
				total = httpResult.getTotal();
				
				List<DeviceInfor> tempList = (List<DeviceInfor>)httpResult.getReuslt();
				
				
				planView.showShopCount("店铺总数:" + deviceList.size());
				isRunning = false;
			}
			else if(msg.what == HttpMsg.GET_SECOND_LEVELS_FAIL)
			{
				isRunning = false;
				onLoad(0);
				//showHint("获取数据失败");
				showToast("获取数据失败，请下拉刷新");
			}
			else */if(msg.what == HttpMsg.GET_BIND_SPEAKERS_START)
			{
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_BIND_SPEAKERS_SUCCESS)
			{
				HttpResult httpResult = (HttpResult)msg.obj;
				total = httpResult.getTotal();
				
				List<DeviceInfor> tempList = (List<DeviceInfor>)httpResult.getReuslt();
				if(pageNum == 1)
					deviceList.clear();
				if(tempList.size() > 0)
					deviceList.addAll(tempList);
				if(deviceList.size() >= total)
				{
					listView.showFootView(false);
					isLoadFinish = true;
				}
				else
				{
					listView.showFootView(true);
					isLoadFinish = false;
				}
				if(deviceList.size() == 0)
				{
					showNoDataView("该账户下没有店铺");
					listView.showFootView(false);
				}
				deviceAdapter.notifyDataSetChanged();
				hideHintView();
				pageNum ++;
				onLoad(0);
				isRunning = false;
				//httpFactory.getCurPlan();
				setCenterString(areaName + "("+total+")");
			}
			else if(msg.what == HttpMsg.GET_BIND_SPEAKERS_FAIL)
			{
				isRunning = false;
				onLoad(0);
				//showHint("获取店铺列表失败");
				showNoDataView("获取数据失败，请重试");
				//showToast("获取数据失败，请下拉刷新");
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
		
		setContentView(R.layout.fragment_all_shop);
		//setBackViewIcon(R.drawable.icon_search_shop);
		setCenterString("我的店铺");
		showTopView(true);
		setRightText("区域计划");
		setRightTopIcon(R.drawable.icon_plan_item);
		
		UserInfor tempInfor = (UserInfor) getIntent().getSerializableExtra("UserInfor");
		if(tempInfor != null)
		{
			userName = tempInfor.getUserName();
			userId = tempInfor.getUserId();
		}
		areaName = tempInfor.getUserName();
		planView = new PlanView(getActivity());
		listView = (LJListView)findViewById(R.id.ptrl_net_devices);
		listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		listView.getListView().setDividerHeight(1);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();
		listView.setOnItemClickListener(this);
		deviceAdapter = new ShopAdapter(getActivity(), deviceList);
		listView.setAdapter(deviceAdapter);
		
		//setBackViewIcon(R.drawable.icon_search_shop);
		setCenterString(areaName);
		showTopView(true);
		if(TextUtils.isEmpty(userId))
			setRightText("播放计划");
		else
			setRightText("区域计划");
		setRightTopIcon(R.drawable.icon_plan_item);
		httpFactory = new HttpFactory(getActivity(), handler);
		
		listView.onFresh();
		
		oldDeviceInfor = getLocalData().getDeviceInfor();
		
		if(locationFactory == null)
			locationFactory = new LocationFactory(getActivity());
		locationFactory.startLocation();
		getRightView().setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				if(deviceList.size() == 0)
				{
					showToast("您没有绑定设备，不能创建计划");
					return;
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable("PlanInfor", planInfor);
				bundle.putString("USER_ID", userId);
				bundle.putString("USER_NAME", userName);
				ViewUtils.startActivity(getActivity(), PlanListActivity.class, bundle);
			}
		});
		/*getBackView().setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				ViewUtils.startActivity(getActivity(), SearchShopActivity.class,null);
			}
		});*/
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private void onLoad(int updateCount) 
	{
		listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();
	}
	
	private void getSecondLevels()
	{
		if(!isRunning)
			httpFactory.getBindSpeakers(userId, pageNum);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onRefresh() 
	{
		// TODO Auto-generated method stub
		if(isRunning)
			return;
		if(httpFactory != null)
		{
			pageNum = 1;
			getSecondLevels();
		}
	}

	/**
	*callbacks
	*/
	@Override
	public void onLoadMore() 
	{
		// TODO Auto-generated method stub
		if(isRunning)
			return;
		if(httpFactory != null)
		{
			getSecondLevels();
		}
	}

	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) 
	{
		// TODO Auto-generated method stub
		if(pos > 0)
			pos = pos - 1;
		DeviceInfor deviceInfor = deviceList.get(pos);
		Bundle bundle = new Bundle();
		bundle.putSerializable("DeviceInfor", deviceInfor);
		ViewUtils.startActivity(getActivity(), ShopDetailActivity.class, bundle);
	}
	
}
 
