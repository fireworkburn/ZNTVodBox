/*  
* @Project: ZNTDianGe 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-5-25 下午10:47:42 
* @Version V1.1   
*/ 

package com.znt.diange.fragment; 

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
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.activity.ShopSongRecordActivity;
import com.znt.diange.dmc.engine.OnConnectHandler;
import com.znt.diange.entity.Constant;
import com.znt.diange.factory.HttpFactory;
import com.znt.diange.factory.LocationFactory;
import com.znt.diange.http.HttpMsg;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.utils.ViewUtils;
import com.znt.diange.view.DeviceAdapter;
import com.znt.diange.view.listview.LJListView;
import com.znt.diange.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: InterationFragment 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-5-25 下午10:47:42  
 */
public class InterationFragment  extends BaseFragment implements OnClickListener,IXListViewListener, OnItemClickListener
{

	private View parentView = null;
	private LJListView listView = null;
	private TextView tvHint = null;
	private DeviceAdapter deviceAdapter = null;
	private List<DeviceInfor> deviceList = new ArrayList<DeviceInfor>();
	private HttpFactory httpFactory = null;
	private LocationFactory locationFactory = null;
	
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
				List<DeviceInfor> tempList = (List<DeviceInfor>)msg.obj;
				
				if(tempList.size() == 0)
				{
					showHint("您所在的区域没有设备");
					onLoad(0);
				}
				else
				{
					hideHint();
					deviceList.clear();
					deviceList.addAll(tempList);
					deviceAdapter.notifyDataSetChanged();
					onLoad(0);
				}
				isRunning = false;
			}
			else if(msg.what == HttpMsg.GET_ALL_SPEAKER_FAIL)
			{
				isRunning = false;
				onLoad(0);
				showHint("获取数据失败");
				showToast("获取数据失败，请下拉刷新");
			}
			if(msg.what == HttpMsg.GET_NEAR_BY_SPEAKER_START)
			{
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_NEAR_BY_SPEAKER_SUCCESS)
			{
				List<DeviceInfor> tempList = (List<DeviceInfor>)msg.obj;
				
				if(tempList.size() == 0)
				{
					showHint("您所在的区域没有设备");
					onLoad(0);
				}
				else
				{
					hideHint();
					deviceList.clear();
					deviceList.addAll(tempList);
					deviceAdapter.notifyDataSetChanged();
					onLoad(0);
				}
				isRunning = false;
			}
			else if(msg.what == HttpMsg.GET_NEAR_BY_SPEAKER_FAIL)
			{
				isRunning = false;
				onLoad(0);
				showHint("获取设备列表失败");
				//showToast("获取数据失败，请下拉刷新");
			}
		};
	};
	
	public InterationFragment()
	{
		
	}
	public static InterationFragment getInstance()
	{
		return new InterationFragment();
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
			parentView = getContentView(R.layout.fragment_interation);
			tvHint = (TextView)parentView.findViewById(R.id.tv_interation_hint);
			listView = (LJListView)parentView.findViewById(R.id.ptrl_interation);
			listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
			listView.getListView().setDividerHeight(1);
			listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
			listView.setPullRefreshEnable(true);
			listView.setIsAnimation(true); 
			listView.setXListViewListener(this);
			listView.showFootView(false);
			listView.setRefreshTime();
			listView.setOnItemClickListener(this);
			deviceAdapter = new DeviceAdapter(getActivity(), deviceList, false);
			listView.setAdapter(deviceAdapter);
			
			httpFactory = new HttpFactory(getActivity(), handler);
			
			listView.onFresh();
			
			initViews();
			
			lazyLoad();
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
	
	private void initViews()
	{
		showReturnView(false);
		setCenterString("歌曲互动");
	}
	
	private void onLoad(int updateCount) 
	{
		listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();
	}
	
	private void showHint(String text)
	{
		tvHint.setVisibility(View.VISIBLE);
		tvHint.setText(text);
	}
	private void hideHint()
	{
		tvHint.setVisibility(View.GONE);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onPause() 
	{
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	*callbacks
	*/
	@Override
	protected void lazyLoad() 
	{
		// TODO Auto-generated method stub
		
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
		DeviceInfor tempInfor = deviceList.get(pos);
		Bundle bundle = new Bundle();
		bundle.putSerializable("DeviceInfor", tempInfor);
		ViewUtils.startActivity(getActivity(), ShopSongRecordActivity.class, bundle);
		
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
			httpFactory.getAllSpeakers();
			//httpFactory.getNearBySpeakers();
	}
	/**
	*callbacks
	*/
	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		
	}

}
 
