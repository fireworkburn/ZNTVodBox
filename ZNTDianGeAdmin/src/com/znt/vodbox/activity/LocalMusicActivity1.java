
package com.znt.vodbox.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.cmd.SpeakerMusicCmd;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.dialog.MusicPlayDialog;
import com.znt.vodbox.dialog.SongAlertDialog;
import com.znt.vodbox.dmc.engine.OnConnectHandler;
import com.znt.vodbox.factory.ContentManager;
import com.znt.vodbox.mina.client.ClientHandler;
import com.znt.vodbox.mina.client.MinaClient;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
* @ClassName: LocalMusicActivity 
* @Description: TODO
* @author yan.yu 
* @date 2015年7月19日 下午4:43:29  
*/
public class LocalMusicActivity1 extends BaseActivity implements OnItemClickListener, OnClickListener, IXListViewListener
{
	
	private TextView tvHint = null;
	private TextView tvRefresh = null;
	private TextView tvOnLineMusic = null;
	private View viewOnLineMusic = null;
    private View viewReload = null;
    private View viewOnlineHint = null;
    private TextView tvLoadOnile = null;
    private LJListView mListView;
    private SortAdapter1 mAdaptor;
    private DeviceInfor deviceInfor = null;
	
	private ContentManager mContentManager;
	
	private List<MediaInfor> musicList = new ArrayList<MediaInfor>();
	
	private boolean isRunning = false;
	private boolean isLocalMusic = true;
	
	private final int SCAN_MUSIC = 1;
	private final int SCAN_MUSIC_FINISH = 2;
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == ClientHandler.RECV_SPEAKER_MUSIC_FB)
			{
				SpeakerMusicCmd speakerMusicCmd = (SpeakerMusicCmd)msg.obj;
				
				clearWebMedias();
				
				List<MediaInfor> tempList = speakerMusicCmd.getMusicList();
				updateMediaViews(tempList);
				
				isRunning = false;
				
			}
			else if(msg.what == OnConnectHandler.ON_NETWORK_RECONNECTED_SUCCESS)
			{
				
			}
			else if(msg.what == SCAN_MUSIC_FINISH)
			{
				
				clearWebMedias();
				
				List<MediaInfor> tempList = (ArrayList<MediaInfor>)msg.obj;
				updateMediaViews(tempList);
				
			}
			else if(msg.what == ClientHandler.TIME_OUT)
			{
				showReloadView();
				isRunning = false;
			}
			else if(msg.what == ClientHandler.RECV_ERROR)
			{
				dismissDialog();
				String error = (String)msg.obj;
				if(error != null)
					showToast(error);
			}
		};
	};
	
	private void updateMediaViews(List<MediaInfor> tempList)
	{
		viewReload.setVisibility(View.GONE);
		showLoadingView(false);
		if(tempList != null && tempList.size() > 0)
		{
			mContentManager.pushListItem(tempList);	
			musicList.addAll(tempList);
			mAdaptor.updateListView(tempList);
			
			hideHintView();
			mListView.showFootView(false);
			mListView.setVisibility(View.VISIBLE);
			viewOnlineHint.setVisibility(View.GONE);
		}
		else
		{
			if(tempList == null)
			{
				if(viewReload != null)
				{
					showReloadView();
				}
			}
			else if(tempList.size() == 0)
			{
				mListView.showFootView(false);
				viewOnlineHint.setVisibility(View.VISIBLE);
			}
		}
		
		onLoad(0);
	}
	
	private void showReloadView()
	{
		showLoadingView(false);
		hideHintView();
		viewReload.setVisibility(View.VISIBLE);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_local_music);
		
		mListView = (LJListView)findViewById(R.id.phlv_local_music);
		tvHint = (TextView) findViewById(R.id.tv_local_music_hint);
		tvRefresh = (TextView) findViewById(R.id.tv_local_music_refresh);
		tvOnLineMusic = (TextView) findViewById(R.id.tv_speaker_music_online);
		tvLoadOnile = (TextView) findViewById(R.id.tv_local_music_online_refresh);
		viewOnLineMusic = (View) findViewById(R.id.view_speaker_music_bottom);
		viewReload = (View) findViewById(R.id.view_local_music_hint);
		viewOnlineHint = (View) findViewById(R.id.view_local_music_online_hint);
		
		mListView.getListView().setDividerHeight(0);
		
		mListView.setPullRefreshEnable(false);
		mListView.setRefreshTime();
		
		showLoadingView(true);
		
		isLocalMusic = getIntent().getBooleanExtra("IS_LOCAL", true);
		deviceInfor = (DeviceInfor)getIntent().getSerializableExtra("DeviceInfor");
		
		initViews();
		
	}
	
	private void showTitle(String text)
	{
		if(TextUtils.isEmpty(text))
		{
			if(isLocalMusic)
				setCenterString("手机本地");
			else
				setCenterString("音响本地");
		}
		else
			setCenterString(text);
	}
	
	public void stop()
	{
		handler.removeMessages(ClientHandler.RECV_SPEAKER_MUSIC_FB);
		handler.removeMessages(SCAN_MUSIC_FINISH);
		handler.removeMessages(ClientHandler.TIME_OUT);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onResume()
	{
		MinaClient.getInstance().setOnConnectListener(getActivity(), handler);
		super.onResume();
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
	protected void onDestroy()
	{
		stop();
		mContentManager.clear();
		MinaClient.getInstance().setConnectStop();
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public void updateLocalMusic()
	{
		showHintView();
	}
	
	private void showHintView()
	{
		if(viewReload != null)
		{
			showLoadingView(false);
			hideHintView();
			viewReload.setVisibility(View.VISIBLE);
		}
	}
	
	private void initViews()
	{
		mContentManager = ContentManager.getInstance();
		
		showTitle(null);
		
		tvHint.setText(R.string.local_music_hint_speacker);
		
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);
		
		tvLoadOnile.setOnClickListener(this);
		tvRefresh.setOnClickListener(this);
		tvOnLineMusic.setOnClickListener(this);
		findViewById(R.id.view_title_back).setOnClickListener(this);
		
		mAdaptor = new SortAdapter1(getActivity(), musicList);
		mListView.setAdapter(mAdaptor);
		
		if(isLocalMusic)
			viewOnLineMusic.setVisibility(View.GONE);
		else
			viewOnLineMusic.setVisibility(View.VISIBLE);
	}
	
	private void setContentlist(List<MediaInfor> list)
	{	
		if (list != null)
		{
			musicList.clear();
			musicList.addAll(list);
			mAdaptor.updateListView(list);
			
			showTitle(null);
		}
	}
	
	private void back()
	{
		mContentManager.popListItem();
		List<MediaInfor> list = mContentManager.peekListItem();
		if (list == null)
		{
			super.onBackPressed();
		}
		else
		{
			setContentlist(list);
		}	
	}

	@Override
	public void onBackPressed() 
	{
		// TODO Auto-generated method stub
		back();
	}
	
	public void getMediaList(final String key)
	{
		if(isRunning)
			return;
		isRunning = true;
		
		showLoadingView(true);
		
		new Thread(new Runnable() 
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				boolean result = MinaClient.getInstance().sendGetSpeakerMusic(getActivity(), key);
				if(!result)
				{
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							// TODO Auto-generated method stub
							showReloadView();
						}
					});
					isRunning = false;
				}
			}
		}).start();
	}
	
	public void clearWebMedias()
	{
		if(musicList != null && musicList.size() > 0)
			musicList.clear();
	}
	
	public List<MediaInfor> getMusics()
	{
		return musicList;
	}
	
	
	private void onLoad(int updateCount) 
	{
		mListView.setCount(updateCount);
		mListView.stopLoadMore();
		mListView.stopRefresh();
		mListView.setRefreshTime();
	}
	
	private void showSongSearchDialog(Activity activity
			, String title, String songName, String message) 
	{
		if (TextUtils.isEmpty(title)) 
		{
			title = "提示";
		}
		
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		SongAlertDialog mSongAlertDialog = new SongAlertDialog(activity, R.style.Theme_CustomDialog);
		mSongAlertDialog.setInfor(title, songName, message);
		if(mSongAlertDialog.isShowing())
			mSongAlertDialog.dismiss();
		mSongAlertDialog.show();
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = mSongAlertDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		mSongAlertDialog.getWindow().setAttributes(lp);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3)
	{
		// TODO Auto-generated method stub
		
		if(musicList.size() == 0)
			return;
		
		if(position > 0)
			position = position - 1;
		
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == tvRefresh)
		{
			
		}
		else if(v == tvLoadOnile)
		{
			if(deviceInfor == null || TextUtils.isEmpty(deviceInfor.getCode()))
			{
				showToast("正在重新连接设备，请重试");
			}
			else
			{
				Bundle bundle = new Bundle();
				bundle.putSerializable("DEVICE_INFOR", deviceInfor);
				ViewUtils.startActivity(getActivity(), SongBookActivity.class, bundle);
			}
		}
		else if(v.getId() == R.id.view_title_back)
		{
			back();
		}
		else if(v == tvOnLineMusic)
		{
			if(deviceInfor == null || TextUtils.isEmpty(deviceInfor.getCode()))
			{
				showToast("设备错误，请重新选择设备");
				return;
			}
			if(isWifiApConnect())
			{
				showToast("当前wifi不能访问外部网络");
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putSerializable("DEVICE_INFOR", deviceInfor);
			ViewUtils.startActivity(getActivity(), SongBookActivity.class, bundle);
		}
	}


	/**
	*callbacks
	*/
	@Override
	public void onRefresh()
	{
		// TODO Auto-generated method stub
		
	}


	/**
	*callbacks
	*/
	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		
	}
	
	private class SortAdapter1 extends BaseAdapter
	{
		private List<MediaInfor> list = null;
		private Activity mContext;
		
		public SortAdapter1(Activity mContext, List<MediaInfor> list)
		{
			this.mContext = mContext;
			this.list = list;
			
		}
		
		/**
		 * @param list
		 */
		public void updateListView(List<MediaInfor> list)
		{
			this.list = list;
			notifyDataSetChanged();
		}
		
		public int getCount() 
		{
			return this.list.size();
		}

		public Object getItem(int position) 
		{
			return list.get(position);
		}

		public long getItemId(int position)
		{
			return position;
		}

		public View getView(final int position, View view, ViewGroup arg2) 
		{
			ViewHolder viewHolder = null;
			if (view == null) 
			{
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(mContext).inflate(R.layout.view_music_item, null);
				viewHolder.tvName = (TextView) view.findViewById(R.id.tv_music_name);
				viewHolder.tvArtist = (TextView) view.findViewById(R.id.tv_music_artist);
				viewHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_music_icon);
				viewHolder.viewOperation = view.findViewById(R.id.view_music_operation);
				viewHolder.ivMore = (ImageView)view.findViewById(R.id.iv_music_item_edit);
				
				viewHolder.ivMore.setImageResource(R.drawable.icon_music_shiting);
				viewHolder.viewOperation.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// TODO Auto-generated method stub
						int index = (Integer)v.getTag();
						MediaInfor infor = list.get(index);
						showPlayDialog(infor);
					}
				});
				
				view.setTag(viewHolder);
			} 
			else 
			{
				viewHolder = (ViewHolder) view.getTag();
			}
			
			viewHolder.viewOperation.setTag(position);
			
			MediaInfor infor = list.get(position);
			viewHolder.tvName.setText(infor.getMediaName());
			
			if(!TextUtils.isEmpty(infor.getArtist()))
			{
				viewHolder.tvArtist.setText(infor.getArtist());
			}
			else	
			{
				viewHolder.tvArtist.setText("未知");
			}
			
			if(infor.getDir())
			{
				viewHolder.ivIcon.setImageResource(R.drawable.icon_dir);
				viewHolder.viewOperation.setVisibility(View.GONE);
				
				String count = infor.getchildCount();
				viewHolder.tvArtist.setVisibility(View.VISIBLE);
				viewHolder.tvArtist.setText("文件  "+ count + " 个");
			}
			else
			{
				viewHolder.ivIcon.setImageResource(R.drawable.icon_music);				
				viewHolder.viewOperation.setVisibility(View.VISIBLE);
			}
			
			return view;

		}
		
		private class ViewHolder 
		{
			TextView tvName;
			TextView tvArtist;
			ImageView ivIcon = null;
			View viewOperation = null;
			ImageView ivMore = null;
		}
		
		private void showPlayDialog(final MediaInfor infor)
		{
			final MusicPlayDialog playDialog = new MusicPlayDialog(mContext, R.style.Theme_CustomDialog);
			
			//String url = UrlUtils.getMediaPlayUrl(infor.getMediaUrl(), isLocalMusic);
			if(isLocalMusic)
				infor.setMediaType(MediaInfor.MEDIA_TYPE_PHONE);
			else
				infor.setMediaType(MediaInfor.MEDIA_TYPE_SPEAKER);
			//infor.setMediaUrl(url);
			
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
			
			WindowManager windowManager = ((Activity) mContext).getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = playDialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()); //设置宽度
			lp.height = (int)(display.getHeight()); //设置高度
			playDialog.getWindow().setAttributes(lp);
		}

	}
}
 
