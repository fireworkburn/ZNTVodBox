/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-7-6 下午3:03:46 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.znt.diange.mina.entity.MediaInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.MusicAdapter;
import com.znt.vodbox.dialog.MusicPlayDialog;
import com.znt.vodbox.entity.MusicEditType;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.http.HttpResult;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: PlanAllMusicActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-7-6 下午3:03:46  
 */
public class PlanAllMusicActivity extends BaseActivity implements IXListViewListener, OnItemClickListener, OnClickListener
{
	private LJListView listView = null;
	
	private MusicAdapter adapter = null;
	
	private List<MediaInfor> mediaList = new ArrayList<MediaInfor>();
	private PlanInfor planInfor = null;
	private boolean isRunning = false;
	private int pageNo = 1;
	private int total = 0;
	
	private HttpFactory httpFactory = null;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.GET_ALBUM_MUSIC_START)
			{
				//showLoadingView(true);
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_ALBUM_MUSIC_SUCCESS)
			{
				
				HttpResult httpResult = (HttpResult)msg.obj;
				
				total = httpResult.getTotal();
				
				isRunning = false;
				onLoad(0);
				if(pageNo == 1)
				{
					setCenterString("全部计划歌曲(" + total + ")");
					mediaList.clear();
				}
				List<MediaInfor> tempList = (List<MediaInfor>)httpResult.getReuslt();
				if(tempList.size() > 0)
				{
					mediaList.addAll(tempList);
					
					adapter.notifyDataSetChanged();
					hideHintView();
				}
				else
				{
					if(pageNo == 1)
						showNoDataView("该歌单没有歌曲哦~");
				}
				if(mediaList.size() >= total)
					listView.showFootView(false);
				else
					listView.showFootView(true);
			}
			else if(msg.what == HttpMsg.GET_ALBUM_MUSIC_FAIL)
			{
				//showLoadingView(false);
				isRunning = false;
				onLoad(0);
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
		
		setContentView(R.layout.activity_plan_all_music);
		
		setCenterString("全部计划歌曲");
		
		planInfor = (PlanInfor)getIntent().getSerializableExtra("PlanInfor");
		
		listView = (LJListView)findViewById(R.id.ptrl_plan_all_music);
		listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		listView.getListView().setDividerHeight(1);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();
		listView.setOnItemClickListener(this);
		
		adapter = new MusicAdapter(this, mediaList, handler);
		adapter.setMusicEditType(MusicEditType.None);
		listView.setAdapter(adapter);
		
		httpFactory = new HttpFactory(getActivity(), handler);
		
		listView.onFresh();
		
	}
	
	private void getMusics()
	{
		httpFactory.stopHttp();
		httpFactory.getAllPlanMusics("", planInfor.getPlanId(), pageNo + "");
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
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub
		if(pos >= 1)
			pos = pos - 1;
		MediaInfor infor = mediaList.get(pos);
		showPlayDialog(infor);
	}
	
	private void showPlayDialog(final MediaInfor infor)
	{
		final MusicPlayDialog playDialog = new MusicPlayDialog(getActivity(), R.style.Theme_CustomDialog);
		
		playDialog.setInfor(infor);
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(playDialog.isShowing())
			playDialog.dismiss();
		playDialog.show();
		playDialog.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
				if(infor.getMediaType().equals(MediaInfor.MEDIA_TYPE_PHONE))
				{
					Bundle bundle = new Bundle();
					bundle.putString("KEY_WORD", infor.getMediaName());
					ViewUtils.startActivity(getActivity(), SearchMusicActivity.class, bundle);
				}
				else
				{
					
				}
				playDialog.dismiss();
			}
		});
		
		WindowManager windowManager = ((Activity) getActivity()).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = playDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		playDialog.getWindow().setAttributes(lp);
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
		pageNo = 1;
		getMusics();
	}

	/**
	*callbacks
	*/
	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		if(mediaList.size() < total)
		{
			pageNo ++;
			getMusics();
		}
	}
	
}
 
