/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-10-13 上午12:12:00 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.MusicEditType;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.http.HttpResult;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: UploadMusicActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-10-13 上午12:12:00  
 */
public class UploadMusicActivity extends BaseActivity implements IXListViewListener, OnItemClickListener, OnClickListener
{
	private LJListView mListView;
	private View viewBottom = null;
	private View viewBottomEdit = null;
	private View viewApplication = null;
	private View viewEdit = null;
	private View viewAdd = null;
	private View viewDelete = null;
	
	private HttpFactory httpfactory = null;
	private MusicAdapter adapter = null;
	private List<MediaInfor> mediaList = new ArrayList<MediaInfor>();
	private boolean isRunning = false;
	private boolean isLoadFinish = false;
	private int pageNum = 1;
	
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
				
				//showLoadingView(false);
				isRunning = false;
				onLoad(0);
				if(pageNum == 1)
					mediaList.clear();
				
				HttpResult httpResult = (HttpResult)msg.obj;
				int total = httpResult.getTotal();
				setCenterString("我上传的歌曲(" + total + ")");
				List<MediaInfor> tempList = (List<MediaInfor>)httpResult.getReuslt();
				if(tempList.size() > 0)
				{
					if(tempList.size() < 25)
					{
						mListView.showFootView(false);
						isLoadFinish = true;
					}
					else
					{
						mListView.showFootView(true);
						isLoadFinish = false;
					}
					
					mediaList.addAll(tempList);
					
					adapter.notifyDataSetChanged();
					hideHintView();
				}
				else if(pageNum == 1)
				{
					showNoDataView("该歌单没有歌曲哦~");
					mListView.showFootView(false);
				}
				
				pageNum ++;
			}
			else if(msg.what == HttpMsg.GET_ALBUM_MUSIC_FAIL)
			{
				//showLoadingView(false);
				isRunning = false;
				onLoad(0);
			}
			else if(msg.what == HttpMsg.DELETE_ALBUM_MUSIC_START)
			{
				showProgressDialog(getActivity(), "正在处理...");
			}
			else if(msg.what == HttpMsg.DELETE_ALBUM_MUSIC_SUCCESS)
			{
				adapter.removeDeleteMusic();
				dismissDialog();
				Constant.isAlbumUpdated = true;
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
		
	
		setContentView(R.layout.activity_album_music);
		
		
		setCenterString("我上传的歌曲");
		
		viewBottom = findViewById(R.id.view_album_music_bottom);
		viewBottomEdit = findViewById(R.id.view_album_music_bottom_edit);
		viewApplication = findViewById(R.id.view_album_music_application);
		viewEdit = findViewById(R.id.view_album_music_edit);
		viewAdd = findViewById(R.id.view_album_music_add);
		viewDelete = findViewById(R.id.view_album_music_delete);
		
		mListView = (LJListView)findViewById(R.id.ptrl_album_music);
		mListView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		mListView.getListView().setDividerHeight(1);
		mListView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		mListView.setPullRefreshEnable(true);
		mListView.setIsAnimation(true); 
		mListView.setXListViewListener(this);
		mListView.showFootView(false);
		mListView.setRefreshTime();
		mListView.setOnItemClickListener(this);
		
		
		adapter = new MusicAdapter(this, mediaList, handler);
		adapter.setMusicEditType(null);
		mListView.setAdapter(adapter);
		
		viewApplication.setOnClickListener(this);
		viewEdit.setOnClickListener(this);
		viewAdd.setOnClickListener(this);
		viewDelete.setOnClickListener(this);
		
		viewBottomEdit.setVisibility(View.GONE);
		viewEdit.setVisibility(View.GONE);
		
		httpfactory = new HttpFactory(getActivity(), handler);
		
		mListView.onFresh();
	}
	
	private void onLoad(int updateCount) 
	{
		mListView.setCount(updateCount);
		mListView.stopLoadMore();
		mListView.stopRefresh();
		mListView.setRefreshTime();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		if(Constant.isAlbumMusicUpdated)
		{
			Constant.isAlbumUpdated = true;
			Constant.isAlbumMusicUpdated = false;
			mListView.onFresh();
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) 
	{
		// TODO Auto-generated method stub
	
		if(pos >= 1)
			pos = pos - 1;
		final MediaInfor infor = mediaList.get(pos);
		//showPlayDialog(infor);
		
		Intent data = new Intent();
		data.putExtra("media_id", infor.getMediaId());
		data.putExtra("media_name", infor.getMediaName());
		setResult(RESULT_OK, data);
		finish();
	}
	/**
	*callbacks
	*/
	@Override
	public void onRefresh() 
	{
		// TODO Auto-generated method stub
		if(!isRunning)
		{
			pageNum = 1;
			httpfactory.getAlbumMusics("0", pageNum);
		}
	}
	/**
	*callbacks
	*/
	@Override
	public void onLoadMore() 
	{
		// TODO Auto-generated method stub
		if(!isRunning && !isLoadFinish)
		{
			httpfactory.getAlbumMusics("0", pageNum);
		}
		else
		{
			onLoad(0);
			mListView.showFootView(false);
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		if(v == viewApplication)
		{
			
		}
		else if(v == viewDelete)
		{
			if(mediaList.size() == 0)
			{
				showToast("没有可删除的歌曲了");
				return;
			}
			adapter.deleteMusics();
		}
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

}