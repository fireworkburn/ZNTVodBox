/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-18 下午11:50:07 
* @Version V1.1   
*/ 

package com.znt.vodbox.adapter; 

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.ResoureType;
import com.znt.vodbox.R;
import com.znt.vodbox.dialog.AlbumListDialog;
import com.znt.vodbox.dialog.MyProgressDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.utils.NetWorkUtils;

/** 
 * @ClassName: MusicSearchAdapter 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-18 下午11:50:07  
 */
public class MusicSearchAdapter  extends BaseAdapter
{
	private List<MediaInfor> list = null;
	private Activity mContext;
	private String albumId = "";
	private String terminalId = "";
	private int resourceType = ResoureType.WANGYI;
	private boolean isCheckUrl = false;
	
	private final int CHECK_MUSIC_URL = -100;
	
	private HttpFactory httpFactory = null;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.ADD_MUSIC_TO_ALBUM_START)
			{
				showProgressDialog(mContext, "正在处理...");
			}
			else if(msg.what == HttpMsg.ADD_MUSIC_TO_ALBUM_SUCCESS)
			{
				Toast.makeText(mContext, "添加成功", 0).show();
				Constant.isAlbumMusicUpdated = true;
				dismissDialog();
			}
			else if(msg.what == HttpMsg.ADD_MUSIC_TO_ALBUM_FAIL)
			{
				Toast.makeText(mContext, "添加失败", 0).show();
				dismissDialog();
			}
			else if(msg.what == HttpMsg.PUSH_MUSIC_START)
			{
				showProgressDialog(mContext, "正在处理...");
			}
			else if(msg.what == HttpMsg.PUSH_MUSIC_SUCCESS)
			{
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(mContext, "插播成功，开始准备播放", 0).show();
						dismissDialog();
					}
				}, Constant.SUC_DELAY_TIME);
			}
			else if(msg.what == HttpMsg.PUSH_MUSIC_FAIL)
			{
				String error = (String) msg.obj;
				if(TextUtils.isEmpty(error))
					error = "操作失败";
				Toast.makeText(mContext, error, 0).show();
				dismissDialog();
			}
			else if(msg.what == CHECK_MUSIC_URL)
			{
				/*int type = (Integer) msg.obj;
				if(type == 0)
				{
					
				}
				else if(type == 1)
				{
					
				}
				else if(type == 2)
				{
					
				}*/
			}
		};
	};
	
	public MusicSearchAdapter(Activity mContext, List<MediaInfor> list, String albumId)
	{
		this.mContext = mContext;
		this.list = list;
		this.albumId = albumId;
		
		httpFactory = new HttpFactory(mContext, handler);
	}
	
	public void setTerminalId(String terminalId)
	{
		this.terminalId = terminalId;
	}
	public void setResoureType(int resourceType)
	{
		this.resourceType = resourceType;
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
			view = LayoutInflater.from(mContext).inflate(R.layout.view_music_search_item, null);
			viewHolder.tvName = (TextView) view.findViewById(R.id.tv_music_search_name);
			viewHolder.tvArtist = (TextView) view.findViewById(R.id.tv_music_search_artist);
			viewHolder.tvOperation = (TextView) view.findViewById(R.id.tv_music_search_item_operation);
			viewHolder.ivOperation = (ImageView) view.findViewById(R.id.iv_music_search_item_operation);
			viewHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_music_search_icon);
			viewHolder.viewOperation = view.findViewById(R.id.view_music_search_operation);
			
			if(isPushMusic())
			{
				viewHolder.tvOperation.setText("插播");
				viewHolder.ivOperation.setImageResource(R.drawable.icon_song_item_chabo);
			}
			else
			{
				viewHolder.tvOperation.setText("添加");
				viewHolder.ivOperation.setImageResource(R.drawable.icon_add_on);
			}
			
			viewHolder.viewOperation.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					int index = (Integer)v.getTag();
					final MediaInfor infor = list.get(index);
					if(infor.isAvailable())
					{
						if(resourceType == ResoureType.WANGYI)
						{
							if(isCheckUrl)
								return;
							isCheckUrl = true;
							new Thread(new Runnable() 
							{
								@Override
								public void run() 
								{
									// TODO Auto-generated method stub
									mContext.runOnUiThread(new Runnable() {
										
										@Override
										public void run() {
											// TODO Auto-generated method stub
											showProgressDialog(mContext, "正在检查歌曲信息");
										}
									});
									final boolean isUrlValid = NetWorkUtils.checkURL(infor.getMediaUrl());
									mContext.runOnUiThread(new Runnable() 
									{
										@Override
										public void run() 
										{
											// TODO Auto-generated method stub
											dismissDialog();
											if(isUrlValid)
											{
												if(isPushMusic())
													pushMusicToShop(terminalId, infor);
												else if(!TextUtils.isEmpty(albumId))
													startAddMusic(albumId, infor);
												else
													showAlbumSelectDialog(mContext, infor);
											}
											else
												Toast.makeText(mContext, "该歌曲链接已失效", 0).show();
											isCheckUrl = false;
										}
									});
								}
							}).start();
						}
						else
						{
							if(isPushMusic())
								pushMusicToShop(terminalId, infor);
							else if(!TextUtils.isEmpty(albumId))
								startAddMusic(albumId, infor);
							else
								showAlbumSelectDialog(mContext, infor);
						}
					}
					else
						Toast.makeText(mContext, "该文件已失效", 0).show();
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
		if(infor.isAvailable())
		{
			viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.text_black_on));
		}
		else
		{
			viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.text_black_off));
		}
		if(!TextUtils.isEmpty(infor.getArtist()))
		{
			viewHolder.tvArtist.setText(infor.getArtist());
		}
		else	
		{
			viewHolder.tvArtist.setText("未知");
		}
		
		return view;
	}
	
	final static class ViewHolder 
	{
		TextView tvName;
		TextView tvArtist;
		TextView tvOperation;
		ImageView ivOperation = null;
		ImageView ivIcon = null;
		View viewOperation = null;
	}
	
	
	private void pushMusicToShop(String terminalId, MediaInfor mediaInfor)
	{
		httpFactory.pushMusic(terminalId, mediaInfor);
	}
	
	private void startAddMusic(String albumId, MediaInfor mediaInfor)
	{
		httpFactory.addMusicToAlbum(albumId, mediaInfor);
	}
	private boolean isPushMusic()
	{
		return !TextUtils.isEmpty(terminalId);
	}
	private void showAlbumSelectDialog(Activity activity, MediaInfor infor) 
	{
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		List<MediaInfor> selectedList = new ArrayList<MediaInfor>();
		selectedList.add(infor);
		AlbumListDialog mSongAlertDialog = new AlbumListDialog(activity, selectedList, albumId);
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
}


 
