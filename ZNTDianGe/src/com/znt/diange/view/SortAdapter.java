package com.znt.diange.view;

import java.util.List;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.activity.BaseActivity;
import com.znt.diange.dialog.MusicOperationDialog;
import com.znt.diange.entity.Constant;
import com.znt.diange.mina.client.MinaClient;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.ResoureType;

public class SortAdapter extends BaseAdapter
{
	private List<MediaInfor> list = null;
	private Activity mContext;
	private BaseActivity baseActivity;
	private boolean isLocalMusic = false;
	private int resourceType = ResoureType.KUWO;
	
	public SortAdapter(BaseActivity baseActivity, List<MediaInfor> list)
	{
		this.baseActivity = baseActivity;
		this.mContext = baseActivity.getActivity();
		this.list = list;
		
	}
	
	public void setIsLocalMusic(boolean isLocalMusic)
	{
		this.isLocalMusic = isLocalMusic;
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
			view = LayoutInflater.from(mContext).inflate(R.layout.view_music_item, null);
			viewHolder.tvName = (TextView) view.findViewById(R.id.tv_music_name);
			viewHolder.tvArtist = (TextView) view.findViewById(R.id.tv_music_artist);
			viewHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_music_icon);
			viewHolder.viewOperation = view.findViewById(R.id.view_music_operation);
			
			viewHolder.viewOperation.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					int index = (Integer)v.getTag();
					MediaInfor infor = list.get(index);
					//showPlayDialog(infor);
					showOperationDialog(infor);
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
		
		if(isLocalMusic)
		{
			/*if(UpnpUtil.isAudioItem(infor) || TextUtils.isEmpty(infor.getchildCount()))
			{
				viewHolder.ivIcon.setImageResource(R.drawable.icon_music);				
				viewHolder.viewOperation.setVisibility(View.VISIBLE);
			}
			else if(!UpnpUtil.isVideoItem(infor) && !UpnpUtil.isPictureItem(infor)
					&& !TextUtils.isEmpty(infor.getchildCount()))
			{
				viewHolder.ivIcon.setImageResource(R.drawable.icon_dir);
				viewHolder.viewOperation.setVisibility(View.GONE);
				
				String count = infor.getchildCount();
				viewHolder.tvArtist.setVisibility(View.VISIBLE);
				viewHolder.tvArtist.setText("文件  "+ count + " 个");
			}*/
		}
		
		return view;

	}
	
	final static class ViewHolder 
	{
		TextView tvName;
		TextView tvArtist;
		ImageView ivIcon = null;
		View viewOperation = null;
	}
	
	private void showOperationDialog(final MediaInfor infor)
	{
		infor.setResourceType(resourceType);
		DeviceInfor tempInfor = Constant.deviceInfor;
		if(tempInfor == null)
			tempInfor = MinaClient.getInstance().getDeviceInfor();
		final MusicOperationDialog dialog = new MusicOperationDialog(mContext, infor,tempInfor);
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(dialog.isShowing())
			dialog.dismiss();
		dialog.show();
		/*playDialog.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				playDialog.dismiss();
				Bundle bundle = new Bundle();
				bundle.putSerializable("MediaInfor", infor);
				ViewUtils.startActivity(mContext, SongPrepareActivity.class, bundle);
			}
		});*/
		
		WindowManager windowManager = ((Activity) mContext).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		dialog.getWindow().setAttributes(lp);
	}

}