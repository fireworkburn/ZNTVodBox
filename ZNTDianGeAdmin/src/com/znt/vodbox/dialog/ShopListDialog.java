
package com.znt.vodbox.dialog; 

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.http.HttpResult;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: PlayListDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-9-19 下午3:49:41  
 */
public class ShopListDialog extends Dialog implements OnItemClickListener, IXListViewListener
{

	private Activity context = null;
	private View parentView = null;
	private ProgressBar pbLoading = null;
	
	private LJListView listView = null;
	private TextView tvCancel = null;
	private TextView tvSelected = null;
	private TextView tvConfirm = null;
	
	private List<DeviceInfor> list = new ArrayList<DeviceInfor>();
	private ShopListAdapter adapter = null;
	private HttpFactory httpFactory = null;
	private int pageNum = 1;
	private int total = 1;
	private MediaInfor mediaInfor = null;
	
	private boolean isRunning = false;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.GET_BIND_SPEAKERS_START)
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
					list.clear();
				}
				if((tempList.size() == 0) && (pageNum == 1))
				{
					list.clear();
					adapter.notifyDataSetChanged();
					listView.showFootView(false);
					dismiss();
					Toast.makeText(context, "该账户下没有店铺", 0).show();
				}
				else
				{
					list.addAll(tempList);
					adapter.notifyDataSetChanged();
					if(list.size() < total)
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
			}
			else if(msg.what == HttpMsg.GET_BIND_SPEAKERS_FAIL)
			{
				isRunning = false;
				onLoad(0);
				//showHint("获取店铺列表失败");
				Toast.makeText(context, "获取数据失败", 0).show();
				//showToast("获取数据失败，请下拉刷新");
			}
			else if(msg.what == HttpMsg.PUSH_MUSIC_START)
			{
				showProgressDialog(context, "正在处理...");
			}
			else if(msg.what == HttpMsg.PUSH_MUSIC_SUCCESS)
			{
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(context, "插播成功，开始准备播放", 0).show();
						dismissDialog();
						dismiss();
					}
				}, Constant.SUC_DELAY_TIME);
			}
			else if(msg.what == HttpMsg.PUSH_MUSIC_FAIL)
			{
				String error = (String) msg.obj;
				if(TextUtils.isEmpty(error))
					error = "操作失败";
				Toast.makeText(context, error, 0).show();
				dismissDialog();
				//dismiss();
			}
		};
	};
	
	public ShopListDialog(Activity context, MediaInfor mediaInfor) 
	{
		super(context, R.style.MMTheme_DataSheet);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mediaInfor = mediaInfor;
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_shop_list);
		
		parentView = findViewById(R.id.view_shop_list_dialog_bg);
		
		listView = (LJListView)findViewById(R.id.lv_shop_list);
		pbLoading = (ProgressBar)findViewById(R.id.pb_dialog_shop_list);
		tvCancel = (TextView)findViewById(R.id.tv_shop_cancel);
		tvSelected = (TextView)findViewById(R.id.tv_shop_selected);
		tvConfirm = (TextView)findViewById(R.id.tv_shop_confirm);
		
		listView.setOnItemClickListener(this);
		listView.getListView().setDivider(context.getResources().getDrawable(R.color.transparent));
		listView.getListView().setDividerHeight(1);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();
		listView.setOnItemClickListener(this);
		
		adapter = new ShopListAdapter();
		listView.setAdapter(adapter);
		
		httpFactory = new HttpFactory(context, handler);
		//httpFactory.getBindSpeakers(pageNum,Constant.ONE_PAGE_SIZE);
		parentView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) 
			{
				// TODO Auto-generated method stub
				dismiss();
				return false;
			}
		});
		tvCancel.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		this.setOnDismissListener(new OnDismissListener()
		{
			
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				// TODO Auto-generated method stub
				if(httpFactory != null)
					httpFactory.stopHttp();
			}
		});
		
		listView.onFresh();
		
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
		DeviceInfor devInfor = list.get(pos);
		if(devInfor.isOnline())
			pushMusicToShop(devInfor.getCode(), mediaInfor);
		else
			Toast.makeText(context, "设备已掉线，无法推送歌曲", 0).show();
	}
	
	private void pushMusicToShop(String terminalId, MediaInfor mediaInfor)
	{
		httpFactory.pushMusic(terminalId, mediaInfor);
	}

	class ShopListAdapter extends BaseAdapter
	{
		/**
		*callbacks
		*/
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return list.size();
		}

		/**
		*callbacks
		*/
		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
			return list.get(arg0);
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
			
			ViewHoler vh = null;
			if(convertView == null)
			{
				vh = new ViewHoler();
				
				convertView = LayoutInflater.from(context).inflate(R.layout.view_shop_list_dialog_item, null);
				vh.tvName = (TextView)convertView.findViewById(R.id.tv_shop_list_dialog_item_name);
				vh.tvAddr = (TextView)convertView.findViewById(R.id.tv_shop_list_dialog_item_addr);
				vh.ivSelect = (ImageView)convertView.findViewById(R.id.iv_shop_list_dialog_item);
				vh.ivSelect.setVisibility(View.GONE);
				
				convertView.setTag(vh);
			}
			else
				vh = (ViewHoler)convertView.getTag();
			
			vh.ivSelect.setTag(pos);
			
			DeviceInfor infor = list.get(pos);
			if(infor.isOnline())
				vh.tvName.setTextColor(context.getResources().getColor(R.color.text_black_on));
			else
				vh.tvName.setTextColor(context.getResources().getColor(R.color.red));
			vh.tvName.setText(infor.getName());
			vh.tvAddr.setText(infor.getAddr());
			return convertView;
		}
		
		private class ViewHoler
		{
			TextView tvName = null;
			TextView tvAddr = null;
			ImageView ivSelect = null;
		}
	}
	private MyProgressDialog mProgressDialog = null;
	private final void showProgressDialog(Activity activity,
			String message) 
	{
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		if (TextUtils.isEmpty(message)) 
		{
			message = "正在加载...";
		}
		if(mProgressDialog == null)
			mProgressDialog = new MyProgressDialog(activity, R.style.Theme_CustomDialog);
		mProgressDialog.setInfor(message);
		
		if(!mProgressDialog.isShowing())
		{
			mProgressDialog.show();
			WindowManager windowManager = (activity).getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = mProgressDialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()); //设置宽度
			lp.height = (int)(display.getHeight()); //设置高度
			mProgressDialog.getWindow().setAttributes(lp);
		}
	}
	private void dismissDialog()
	{
		if(mProgressDialog != null && mProgressDialog.isShowing())
		{
			mProgressDialog.dismiss();
		}
	}
	/*private void showAlertDialog(final int index)
	{
		final SongInfor songInfor = songList.get(index);
		context.showAlertDialog(context.getActivity(), new android.view.View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				int result = DBManager.newInstance(context.getActivity()).deleteSongByUrl(songInfor.getMediaUrl());
				if(result > 0)
				{
					songList.remove(index);
					adapter.notifyDataSetChanged();
					context.dismissDialog();
					if(songList.size() == 0)
						dismiss();
				}
				else
					context.showToast("删除失败");
			}
		}, null, "确定删除该歌曲吗?");
	}*/


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
		if(httpFactory != null && (list.size() < total))
		{
			httpFactory.getBindSpeakers(pageNum,Constant.ONE_PAGE_SIZE);
		}
	}
	
	private void onLoad(int updateCount) 
	{
		listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();
	}
	
}