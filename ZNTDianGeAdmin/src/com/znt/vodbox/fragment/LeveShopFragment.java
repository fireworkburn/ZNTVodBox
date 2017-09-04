/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2017-6-6 下午11:20:03 
* @Version V1.1   
*/ 

package com.znt.vodbox.fragment; 

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.znt.diange.mina.entity.UserInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.activity.ShopActivity;
import com.znt.vodbox.adapter.UserAdapter;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.http.HttpResult;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: LeveShopFragment 
 * @Description: TODO
 * @author yan.yu 
 * @date 2017-6-6 下午11:20:03  
 */
public class LeveShopFragment extends BaseFragment implements IXListViewListener, OnItemClickListener
{
	
	private View parentView = null;
	private LJListView listView = null;
	private HttpFactory httpFactory = null;
	private List<UserInfor> userList = new ArrayList<UserInfor>();
	
	private UserAdapter userAdapter = null;
	private boolean isRunning = false;
	private boolean isLoadFinish = false;
	private boolean isLoaded = false;
	private boolean isPrepared = false;
	private int total = 0;
	private int pageNum = 1;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.GET_SECOND_LEVELS_START)
			{
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_SECOND_LEVELS_SUCCESS)
			{
				HttpResult httpResult = (HttpResult)msg.obj;
				total = httpResult.getTotal();
				
				List<UserInfor> tempList = (List<UserInfor>)httpResult.getReuslt();
				if(pageNum == 1)
					userList.clear();
				if(tempList.size() > 0)
					userList.addAll(tempList);
				if(userList.size() >= total)
				{
					listView.showFootView(false);
					isLoadFinish = true;
				}
				else
				{
					listView.showFootView(true);
					isLoadFinish = false;
				}
				if(userList.size() == 0)
				{
					showNoDataView("该账户下没有店铺");
					listView.showFootView(false);
				}
				userAdapter.notifyDataSetChanged();
				hideHintView();
				pageNum ++;
				onLoad(0);
				isRunning = false;
				isLoaded = true;
			}
			else if(msg.what == HttpMsg.GET_SECOND_LEVELS_FAIL)
			{
				isRunning = false;
				onLoad(0);
				//showHint("获取数据失败");
				showToast("获取数据失败，请下拉刷新");
			}
		};
	};
	/**
	*callbacks
	*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(parentView == null)
		{
			parentView = getContentView(R.layout.fragment_level);
			//planView = (PlanView)parentView.findViewById(R.id.pv_cur_plan);
			
			listView = (LJListView)parentView.findViewById(R.id.ptrl_level_devices);
			listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
			listView.getListView().setDividerHeight(1);
			listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
			listView.setPullRefreshEnable(true);
			listView.setIsAnimation(true); 
			listView.setXListViewListener(this);
			listView.showFootView(false);
			listView.setRefreshTime();
			listView.setOnItemClickListener(this);
			userAdapter = new UserAdapter(getActivity(), userList);
			listView.setAdapter(userAdapter);
			
			showTopView(false);
			httpFactory = new HttpFactory(getActivity(), handler);
			
			getRightView().setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0) 
				{
					// TODO Auto-generated method stub
					/*if(deviceList.size() == 0)
					{
						showToast("您没有绑定设备，不能创建计划");
						return;
					}
					Bundle bundle = new Bundle();
					bundle.putSerializable("PlanInfor", planInfor);
					ViewUtils.startActivity(getActivity(), PlanListActivity.class, bundle);*/
				}
			});
			isPrepared = true;
			
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
	
	public void loadData()
	{
		if(!isLoaded)
			listView.onFresh();
	}
	private void getSecondLevels()
	{
		if(!isRunning)
			httpFactory.getSecondLevels(pageNum);
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
	protected void lazyLoad() 
	{
		// TODO Auto-generated method stub
		if(isPrepared)
		{
			
		}
	}

	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
	{
		// TODO Auto-generated method stub
		if(pos > 0)
			pos = pos - 1;
		UserInfor tempInfor = userList.get(pos);
		Bundle bundle = new Bundle();
		bundle.putSerializable("UserInfor", tempInfor);
		ViewUtils.startActivity(getActivity(), ShopActivity.class, bundle);
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

}
 
