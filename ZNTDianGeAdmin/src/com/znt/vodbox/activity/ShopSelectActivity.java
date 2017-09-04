/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-7-6 上午3:34:58 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.http.HttpResult;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: ShopSelectActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-7-6 上午3:34:58  
 */
public class ShopSelectActivity extends BaseActivity implements IXListViewListener, OnItemClickListener
{
	private TextView tvHint = null;
	private LJListView listView = null;
	
	private ShopSelectAdapter deviceAdapter = null;
	private ArrayList<DeviceInfor> deviceList = new ArrayList<DeviceInfor>();
	private ArrayList<DeviceInfor> selectedList = new ArrayList<DeviceInfor>();
	private HttpFactory httpFactory = null;
	
	private PlanInfor planInfor = null;
	private int pageNum = 1;
	private int total = 0;
	private boolean isRunning = false;
	
	/*private void fileterDevices(List<DeviceInfor> tempList)
	{
		int size = tempList.size();
		for(int i=0;i<size;i++)
		{
			DeviceInfor infor = tempList.get(i);
			if(infor.isSelected())
				selectedList.add(infor);
		}
	}*/
	
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
					deviceList.clear();
					deviceAdapter.notifyDataSetChanged();
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
				initSelectShops();
				isRunning = false;
			}
			else if(msg.what == HttpMsg.GET_ALL_SPEAKER_FAIL)
			{
				isRunning = false;
				onLoad(0);
				showHint("获取数据失败");
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
					showNoDataView("该账户下没有店铺");
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
				initSelectShops();
				isRunning = false;
				//httpFactory.getCurPlan();
				setCenterString("我的店铺("+deviceList.size()+")");
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
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_shop_select);
		
		setCenterString("选择店铺");
		
		tvHint = (TextView)findViewById(R.id.tv_shop_select_hint);
		listView = (LJListView)findViewById(R.id.ptrl_shop_select);
		listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		listView.getListView().setDividerHeight(1);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();
		listView.setOnItemClickListener(this);
		deviceAdapter = new ShopSelectAdapter();
		listView.setAdapter(deviceAdapter);
		
		httpFactory = new HttpFactory(getActivity(), handler);
		planInfor = (PlanInfor)getIntent().getSerializableExtra("PlanInfor");
		
		listView.onFresh();
		
		showRightImageView(false);
		getRightView().setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				if(selectedList.size() == 0)
				{
					showToast("请选择要设置的店铺");
				}
				else
				{
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList("SELECT_SHOPS", (ArrayList<? extends Parcelable>) selectedList);
					Intent intent = new Intent();
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
		
	}
	
	private void initSelectShops()
	{
		ArrayList<DeviceInfor> tempList = (ArrayList<DeviceInfor>) planInfor.getDeviceList();
		if(tempList.size() > 0)
		{
			int size = deviceList.size();
			for(int i=0;i<size;i++)
			{
				DeviceInfor tempInfor1 = deviceList.get(i);
				for(int j=0;j<tempList.size();j++)
				{
					DeviceInfor tempInfor2 = tempList.get(j);	
					if(tempInfor1.getCode().equals(tempInfor2.getCode()))
					{
						tempInfor1.setSelected(true);
						deviceList.set(i, tempInfor1);
					}
				}
			}
			selectedList.addAll(tempList);
			updateSelectCount();
		}
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

	private void updateSelectCount()
	{
		setRightText("完成(" + selectedList.size() + ")");
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
		DeviceInfor infor = deviceList.get(arg2);
		
		infor.setSelected(!infor.isSelected());
		if(infor.isSelected())
		{
			selectedList.add(infor);
		}
		else 
		{
			if(selectedList.size() > 0)
				selectedList.remove(infor);
		}
		deviceList.set(arg2, infor);
		deviceAdapter.notifyDataSetChanged();
		updateSelectCount();
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
			httpFactory.getBindSpeakers(pageNum,Constant.ONE_PAGE_SIZE);
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
			httpFactory.getBindSpeakers(pageNum,Constant.ONE_PAGE_SIZE);
		}
	}
	
	private class ShopSelectAdapter extends BaseAdapter
	{

		/**
		*callbacks
		*/
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return deviceList.size();
		}

		/**
		*callbacks
		*/
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return deviceList.get(arg0);
		}

		/**
		*callbacks
		*/
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		/**
		*callbacks
		*/
		@Override
		public View getView(int arg0, View convertView, ViewGroup arg2) 
		{
			
			ViewHolder vh = null;
			if(convertView == null)
			{
				vh = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_shop_select_item, null);
				
				vh.tvName = (TextView)convertView.findViewById(R.id.tv_shop_select_name);
				vh.tvAddr = (TextView)convertView.findViewById(R.id.tv_shop_select_addr);
				vh.ivSelect = (ImageView)convertView.findViewById(R.id.iv_shop_select_item_edit);
				
				convertView.setTag(vh);
			}
			else
				vh = (ViewHolder)convertView.getTag();
			
			DeviceInfor infor = deviceList.get(arg0);
			vh.tvName.setText(infor.getName());
			vh.tvAddr.setText(infor.getAddr());
			if(infor.isSelected())
				vh.ivSelect.setImageResource(R.drawable.icon_selected_on);
			else
				vh.ivSelect.setImageResource(R.drawable.icon_selected_off);
			
			// TODO Auto-generated method stub
			return convertView;
		}
		
		
		private class ViewHolder
		{
			TextView tvName = null;
			TextView tvAddr = null;
			ImageView ivSelect = null;
		}
	}
	
}
 
