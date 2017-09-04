/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2017-6-6 下午11:19:22 
* @Version V1.1   
*/ 

package com.znt.vodbox.fragment; 

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.activity.ShopDetailActivity;
import com.znt.vodbox.adapter.ShopAdapter;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.factory.LocationFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.http.HttpResult;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.HintView.OnHintListener;
import com.znt.vodbox.view.PlanView;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: AllShopFragment 
 * @Description: TODO
 * @author yan.yu 
 * @date 2017-6-6 下午11:19:22  
 */
public class AllShopFragment extends BaseFragment implements IXListViewListener, OnItemClickListener
{
	private View parentView = null;
	
	private LJListView listView = null;
	private PlanView planView = null;
	
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
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.GET_ALL_SPEAKER_START)
			{
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_ALL_SPEAKER_SUCCESS)
			{
				HttpResult httpResult = (HttpResult)msg.obj;
				total = httpResult.getTotal();
				
				List<DeviceInfor> tempList = (List<DeviceInfor>)httpResult.getReuslt();
				
				
				planView.showShopCount("店铺总数:" + deviceList.size());
				isRunning = false;
			}
			else if(msg.what == HttpMsg.GET_ALL_SPEAKER_FAIL)
			{
				isRunning = false;
				onLoad(0);
				//showHint("获取数据失败");
				showToast("获取数据失败，请下拉刷新");
			}
			else if(msg.what == HttpMsg.GET_BIND_SPEAKERS_START)
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
				setCenterString("我的店铺("+total+")");
			}
			else if(msg.what == HttpMsg.GET_BIND_SPEAKERS_FAIL)
			{
				isRunning = false;
				onLoad(0);
				//showHint("获取店铺列表失败");
				showNoDataView("获取数据失败，请重试");
				//showToast("获取数据失败，请下拉刷新");
			}
			else if(msg.what == HttpMsg.GET_CUR_PLAN_START)
			{
				planView.showErrorHint("正在获取播放计划...");
				//showToast("获取数据失败，请下拉刷新");
			}
			else if(msg.what == HttpMsg.GET_CUR_PLAN_SUCCESS)
			{
				/*planInfor = (PlanInfor)msg.obj;
				List<SubPlanInfor> subPlanList = planInfor.getSubPlanList();
				if(subPlanList.size() > 0)
				{
					planView.setPlanInfor(planInfor);
				}
				else
					planView.showErrorHint("您还没有播放计划");
				Constant.isPlanUpdated = false;*/
			}
			else if(msg.what == HttpMsg.GET_CUR_PLAN_FAIL)
			{
				isRunning = false;
				onLoad(0);
				//showHint("获取播放计划失败");
				planView.showErrorHint("获取播放计划失败，请重试");
				//showToast("获取数据失败，请下拉刷新");
			}
		};
	};
	
	public AllShopFragment()
	{
		
	}
	public static ShopFragment getInstance()
	{
		return new ShopFragment();
	}
	
	/**
	*callbacks
	*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		
		if(parentView == null)
		{
			parentView = getContentView(R.layout.fragment_all_shop);
			//planView = (PlanView)parentView.findViewById(R.id.pv_cur_plan);
			
			planView = new PlanView(getActivity());
			
			listView = (LJListView)parentView.findViewById(R.id.ptrl_net_devices);
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
			
			showTopView(false);
			httpFactory = new HttpFactory(getActivity(), handler);
			
			oldDeviceInfor = getLocalData().getDeviceInfor();
			
			if(locationFactory == null)
				locationFactory = new LocationFactory(getActivity());
			locationFactory.startLocation();
			
			isPrepared = true;
			listView.onFresh();
			
		}
		else
		{
			ViewGroup parent = (ViewGroup) parentView.getParent();
			if(parent != null)
				parent.removeView(parentView);
		}
		
		// TODO Auto-generated method stub
		return parentView;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onResume() 
	{
		// TODO Auto-generated method stub
		
		if(Constant.isPlanUpdated)
		{
			//httpFactory.getCurPlan();
			Constant.isPlanUpdated = false;
		}
		if(Constant.isShopUpdated && !isFirstEnter)
		{
			reloadShops();
			Constant.isShopUpdated = false;
		}
		isFirstEnter = false;
		
		super.onResume();
	}
	
	public void reloadShops()
	{
		pageNum = 1;
		getBindSpeakers();
	}
	
	public void showAlbumNames(String albumName)
	{
		planView.showPlanAlbum(albumName);
	}
	
	private void onLoad(int updateCount) 
	{
		listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();
	}
	
	
	private void getBindSpeakers()
	{
		if(!isRunning)
			httpFactory.getBindSpeakers(pageNum,Constant.ONE_PAGE_SIZE);
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
			getBindSpeakers();
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
			getBindSpeakers();
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
	
	/**
	*callbacks
	*/
	@Override
	protected void lazyLoad() 
	{
		// TODO Auto-generated method stub
		if(isPrepared)
		{
			
		}
	}

}
 