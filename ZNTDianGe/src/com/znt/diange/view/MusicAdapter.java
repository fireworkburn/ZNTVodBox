
package com.znt.diange.view; 

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.activity.MainActivity;
import com.znt.diange.mina.entity.MediaInfor;

/** 
 * @ClassName: MusicAdapter 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-10-28 下午3:10:46  
 */
public class MusicAdapter extends BaseAdapter
{
	private Context context = null;
	
	private List<MediaInfor> musicList = new ArrayList<MediaInfor>();
	
	private MediaInfor selectedMusic = null;
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			
		};
	};
	
	public MusicAdapter(Context context, List<MediaInfor> musicList)
	{
		this.context = context;
		this.musicList = musicList;
	}
	
	public MusicAdapter(Context context, List<MediaInfor> musicList, boolean isVisible)
	{
		this.context = context;
		this.musicList = musicList;
	}
	
	public void updateMusic(List<MediaInfor> musicList)
	{
		this.musicList = musicList;
		notifyDataSetChanged();
	}
	public void updateMusic(MediaInfor music)
	{
		this.musicList.add(music);
		notifyDataSetChanged();
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return musicList.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return musicList.get(arg0);
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
	public View getView(int arg0, View convertView, ViewGroup arg2)
	{
		MusicViewHolder vh = null;
		if(convertView == null)
		{
			vh = new MusicViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.music_play_list_item, null);
			
			vh.tvName = (TextView)convertView.findViewById(R.id.tv_music_name);
			vh.tvSigner = (TextView)convertView.findViewById(R.id.tv_music_singner);
			vh.ivDelete = (ImageView)convertView.findViewById(R.id.iv_play_list_delete);
			
			vh.bgView = convertView.findViewById(R.id.item_bg);
			vh.bgView.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					int index = (Integer)v.getTag();
					/*if(context instanceof MusicPlayerActivity)
					{
						//MediaInfor infor = musicList.get(index);
						((MusicPlayerActivity)context).play(index);
					}
					else */if(context instanceof MainActivity)
					{
						
					}
						
				}
			});
			vh.ivDelete.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					int index = (Integer)v.getTag();
					
				}
			});
			
			convertView.setTag(vh);
		}
		else
		{
			vh = (MusicViewHolder)convertView.getTag();
		}
		
		vh.bgView.setTag(arg0);
		vh.ivDelete.setTag(arg0);
		
		MediaInfor music = musicList.get(arg0);
		
		vh.tvName.setText(music.getMediaName());
		
		if(!TextUtils.isEmpty(music.getArtist()))
		{
			vh.tvSigner.setText(music.getArtist());
			vh.tvSigner.setVisibility(View.VISIBLE);
		}
		else
			vh.tvSigner.setVisibility(View.GONE);
		
		return convertView;
	}
	
	class MusicViewHolder
	{
		TextView tvName = null;
		TextView tvSigner = null;
		ImageView ivDelete = null;
		View bgView = null;
	}
}
 
