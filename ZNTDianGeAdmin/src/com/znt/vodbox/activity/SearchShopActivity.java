/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-8-30 下午1:26:11 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.ShopAdapter;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.http.HttpResult;
import com.znt.vodbox.utils.SystemUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

/** 
 * @ClassName: SearchShopActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-8-30 下午1:26:11  
 */
public class SearchShopActivity extends BaseActivity implements IXListViewListener, OnItemClickListener
{
	private LJListView listView = null;
	private View searchView = null;
	private AutoCompleteTextView etSearch = null;
	private TextView tvSearch = null;
	
	private ShopAdapter deviceAdapter = null;
	private List<DeviceInfor> deviceList = new ArrayList<DeviceInfor>();
	private HttpFactory httpFactory = null;
	
	private int pageNum = 1;
	private int total = 0;
	private boolean isRunning = false;
	
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
				{
					deviceList.clear();
				}
				if((tempList.size() == 0) && (pageNum == 1))
				{
					deviceList.clear();
					deviceAdapter.notifyDataSetChanged();
					showNoDataView("未搜索到相关店铺");
					listView.showFootView(false);
				}
				else
				{
					hideHintView();
					deviceList.addAll(tempList);
					deviceAdapter.notifyDataSetChanged();
					if(deviceList.size() < total)
					{
						listView.showFootView(true);
						pageNum ++;
					}
					else
					{
						listView.showFootView(false);
					}
				}
				onLoad(0);
				isRunning = false;
				//httpFactory.getCurPlan();
				setCenterString("店铺搜索("+deviceList.size()+")");
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
		
		searchView = findViewById(R.id.view_search_shop);
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
		
		etSearch = (AutoCompleteTextView)findViewById(R.id.cet_search_shop);
		tvSearch = (TextView)findViewById(R.id.tv_search_shop);
		
		httpFactory = new HttpFactory(getActivity(), handler);
		
		setCenterString("店铺搜索");
		searchView.setVisibility(View.VISIBLE);
		
		etSearch.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
                if ((actionId == 0 || actionId == 3) && event != null) 
                {
                    //点击搜索要做的操作
                	/*hideInput();
        			listView.onFresh();*/
                	String name = etSearch.getText().toString();
    				if(!TextUtils.isEmpty(name))
    				{
    					listView.onFresh();
    					hideInput();
    				}
    				else
    					showToast("请输入搜索内容");
                }
                return false;
            }
        });
		
		etSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etSearch.setFocusable(true);
				etSearch.setFocusableInTouchMode(true);
				etSearch.requestFocus();
				etSearch.findFocus();
				
				SystemUtils.showInputView(v);
			}
		});
		
		tvSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				String name = etSearch.getText().toString();
				if(!TextUtils.isEmpty(name))
				{
					listView.onFresh();
					hideInput();
				}
				else
					showToast("请输入搜索内容");
			}
		});
		
	}
	
	private void hideInput()
	{
		etSearch.setFocusable(false);
		SystemUtils.hideInputView(getActivity());
	}
	
	private void onLoad(int updateCount) 
	{
		listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();
	}
	
	private void searchShop()
	{
		String keyWord = etSearch.getText().toString();
		httpFactory.getBindSpeakersByKey(pageNum,Constant.ONE_PAGE_SIZE, keyWord);
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
			searchShop();
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
		if(httpFactory != null && (deviceList.size() < total))
		{
			searchShop();
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
 
