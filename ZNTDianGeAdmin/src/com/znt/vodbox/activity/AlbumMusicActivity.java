/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-1 下午11:07:19 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import com.znt.vodbox.entity.MusicAlbumInfor;
import com.znt.vodbox.entity.MusicEditType;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.http.HttpResult;
import com.znt.vodbox.utils.FileUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: AlbumMusicActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-1 下午11:07:19  
 */
public class AlbumMusicActivity extends BaseActivity implements IXListViewListener, OnItemClickListener, OnClickListener
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
	private MusicAlbumInfor albumInfor = null;
	private boolean isRunning = false;
	private boolean isCollect = false;
	private boolean isLoadFinish = false;
	private int pageNum = 1;
	private String terminalId = "";
	private MusicEditType musicEditType = null;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.GET_ALBUM_MUSIC_START || msg.what == HttpMsg.GET_PUSH_HISTORY_MUSIC_START)
			{
				//showLoadingView(true);
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_ALBUM_MUSIC_SUCCESS || msg.what == HttpMsg.GET_PUSH_HISTORY_MUSIC_SUCCESS)
			{
				
				//showLoadingView(false);
				isRunning = false;
				onLoad(0);
				if(pageNum == 1)
					mediaList.clear();
				
				HttpResult httpResult = (HttpResult)msg.obj;
				int total = httpResult.getTotal();
				
				if(TextUtils.isEmpty(terminalId))
					setCenterString(albumInfor.getAlbumName() + "(" + total + ")");
				else
					setCenterString("顾客推荐歌曲" + "(" + total + ")");
				List<MediaInfor> tempList = (List<MediaInfor>)httpResult.getReuslt();
				
				if(tempList.size() > 0)
					mediaList.addAll(tempList);
				
				if(mediaList.size() >= total)
				{
					mListView.showFootView(false);
					isLoadFinish = true;
				}
				else
				{
					mListView.showFootView(true);
					isLoadFinish = false;
				}
				
				adapter.notifyDataSetChanged();
				hideHintView();
				
				if(mediaList.size() == 0)
				{
					showNoDataView("该歌单没有歌曲哦~");
					mListView.showFootView(false);
				}
				
				pageNum ++;
			}
			else if(msg.what == HttpMsg.GET_ALBUM_MUSIC_FAIL || msg.what == HttpMsg.GET_PUSH_HISTORY_MUSIC_FAIL)
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
			else if(msg.what == HttpMsg.DELETE_ALBUM_MUSIC_FAIL)
			{
				dismissDialog();
				showToast("删除失败");
			}
			else if(msg.what == HttpMsg.DELETE_ALBUM_MUSICS_START)
			{
				showProgressDialog(getActivity(), "正在处理...");
			}
			else if(msg.what == HttpMsg.DELETE_ALBUM_MUSICS_SUCCESS)
			{
				mListView.onFresh();
				dismissDialog();
				showToast("删除成功");
				adapter.getSelectedList().clear();
				Constant.isAlbumUpdated = true;
			}
			else if(msg.what == HttpMsg.DELETE_ALBUM_MUSICS_FAIL)
			{
				dismissDialog();
				showToast("删除失败");
			}
			else if(msg.what == HttpMsg.DELETE_ALBUM_START)
			{
				
			}
			else if(msg.what == HttpMsg.DELETE_ALBUM_SUCCESS)
			{
				showToast("操作成功");
				isCollect = false;
				showOperationView();
				Constant.isAlbumUpdated = true;
				//setResult(RESULT_OK);
			}
			else if(msg.what == HttpMsg.DELETE_ALBUM_FAIL)
			{
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "删除失败";
				showToast(error);
			}
			else if(msg.what == HttpMsg.ALBUM_COLLECT_START)
			{
				
			}
			else if(msg.what == HttpMsg.ALBUM_COLLECT_SUCCESS)
			{
				isCollect = true;
				showOperationView();
				showToast("操作成功");
				Constant.isAlbumUpdated = true;
			}
			else if(msg.what == HttpMsg.ALBUM_COLLECT_FAIL)
			{
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "删除失败";
				showToast(error);
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
		
		albumInfor = (MusicAlbumInfor)getIntent().getSerializableExtra("MusicAlbumInfor");
		terminalId = getIntent().getStringExtra("terminalId");
		
		if(TextUtils.isEmpty(terminalId))
			setCenterString(albumInfor.getAlbumName());
		else
			setCenterString("顾客推荐歌曲");
		
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
		
		musicEditType = (MusicEditType)getIntent().getSerializableExtra("MusicEditType");
		if(musicEditType == MusicEditType.DeleteAdd)
		{
			if(!TextUtils.isEmpty(terminalId))
			{
				//showRightImageView(false);
				showRightView(false);
			}
			else
			{
				setRightText("添加");
				if(albumInfor.getAlbumId().equals("0"))
					showRightView(false);
				else
					showRightView(true);
			}
			
			getRightView().setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					// TODO Auto-generated method stub
					Bundle bundle = new Bundle();
					bundle.putString("ALBUM_ID", albumInfor.getAlbumId());
					ViewUtils.startActivity(getActivity(), SearchMusicActivity.class, bundle);
				}
			});
		}
		else if(musicEditType == MusicEditType.None)
		{
			showRightView(false);
			viewBottom.setVisibility(View.GONE);
			viewBottomEdit.setVisibility(View.GONE);
		}
		else
		{
			viewBottom.setVisibility(View.GONE);
			viewBottomEdit.setVisibility(View.GONE);
			isCollect = getIntent().getBooleanExtra("IS_COLLECT", false);
			showRightView(true);
			showRightImageView(false);
			getRightView().setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0) 
				{
					// TODO Auto-generated method stub
					if(isCollect)
						deleteAlbum();
					else
						collectAlbum();
				}
			});
			showOperationView();
			//showRightView(false);
		}
		
		adapter = new MusicAdapter(this, mediaList, handler);
		adapter.setMusicEditType(musicEditType);
		if(albumInfor != null)
			adapter.setAlbumId(albumInfor.getAlbumId());
		else
			adapter.setAlbumId("0");
		mListView.setAdapter(adapter);
		
		viewApplication.setOnClickListener(this);
		viewEdit.setOnClickListener(this);
		viewAdd.setOnClickListener(this);
		viewDelete.setOnClickListener(this);
		
		httpfactory = new HttpFactory(getActivity(), handler);
		
		mListView.onFresh();
	}
	
	private void showOperationView()
	{
		if(isCollect)
		{
			setRightText("取消收藏");
		}
		else
		{
			setRightText("收藏");
		}
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
	
	private boolean isCheckUrl = false;
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
		if(infor == null || infor.getMediaUrl() == null)
		{
			showToast("文件已失效");
			return;
		}
		if(FileUtils.isVideo(infor.getMediaUrl()))
		{
			Bundle bundle = new Bundle();
			bundle.putSerializable("MEDIA_INFOR", infor);
			ViewUtils.startActivity(getActivity(), VideoPlayActivity.class, bundle);
		}
		else if(FileUtils.isMusic(infor.getMediaUrl()))
		showPlayDialog(infor);
		/*if(isCheckUrl)
			return;
		isCheckUrl = true;
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						showProgressDialog(getActivity(), "正在检查歌曲信息");
					}
				});
				final boolean isUrlValid = NetWorkUtils.checkURL(infor.getMediaUrl());
				runOnUiThread(new Runnable() 
				{
					@Override
					public void run() 
					{
						// TODO Auto-generated method stub
						dismissDialog();
						if(isUrlValid)
						{
							showPlayDialog(infor);
						}
						else
							Toast.makeText(getActivity(), "该歌曲链接已失效", 0).show();
						isCheckUrl = false;
					}
				});
			}
		}).start();*/
		
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
			if(!TextUtils.isEmpty(terminalId))
				httpfactory.getPushHostoryMusics(terminalId, "0", "" + pageNum);
			else
				httpfactory.getAlbumMusics(albumInfor.getAlbumId(), pageNum);
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
			
			if(!TextUtils.isEmpty(terminalId))
				httpfactory.getPushHostoryMusics(terminalId, "0", "" + pageNum);
			else
				httpfactory.getAlbumMusics(albumInfor.getAlbumId(), pageNum);
		}
		else
		{
			onLoad(0);
			mListView.showFootView(false);
		}
	}
	
	private void showShopListDialog()
	{
		/*final ShopListDialog playDialog = new ShopListDialog(getActivity());
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(playDialog.isShowing())
			playDialog.dismiss();
		playDialog.show();
		
		WindowManager windowManager = ((Activity) getActivity()).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = playDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		playDialog.getWindow().setAttributes(lp);*/
	}

	private void showEditView(boolean show)
	{
		viewBottom.setVisibility(View.GONE);
		viewBottomEdit.setVisibility(View.VISIBLE);
		if(show)
		{
			adapter.setMusicEditType(MusicEditType.Select);
		}
		else
		{
			adapter.setMusicEditType(musicEditType);
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
			showShopListDialog();
		}
		else if(v == viewEdit)
		{
			if(viewBottom.isShown())
			{
				showEditView(true);
			}
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
		/*else if(v == getRightView() || v == viewAdd)
		{
			
		}*/
	}
	
	private void collectAlbum()
	{
		httpfactory.collectAlbum(albumInfor.getAlbumId());
	}
	private void deleteAlbum()
	{
		httpfactory.deleteAlbum(albumInfor.getAlbumId());
	}
	
	private void showPlayDialog(final MediaInfor infor)
	{
		infor.setFromAlbum(true);
		final MusicPlayDialog playDialog = new MusicPlayDialog(getActivity(), R.style.Theme_CustomDialog);
		
		playDialog.setInfor(infor);
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(playDialog.isShowing())
			playDialog.dismiss();
		playDialog.show();
		/*playDialog.setOnClickListener(new OnClickListener()
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
		});*/
		
		WindowManager windowManager = ((Activity) getActivity()).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = playDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		playDialog.getWindow().setAttributes(lp);
	}

}
 
