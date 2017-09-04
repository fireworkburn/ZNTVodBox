
package com.znt.vodbox.view; 

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.vodbox.dialog.SongCoinSelectDialog;
import com.znt.vodbox.dialog.SongMsgEditDialog;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.fragment.SongFragment;

/** 
 * @ClassName: SongAdapter 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-16 上午11:28:23  
 */
public class SongAdapter extends BaseAdapter
{
	private SongFragment songFragment = null;
	private Activity activity = null;
	private List<SongInfor> songList = new ArrayList<SongInfor>();
	
	private boolean isMoreShow = false;
	
	public SongAdapter(SongFragment songFragment, List<SongInfor> songList)
	{
		this.songFragment = songFragment;
		this.activity = songFragment.getActivity();
		this.songList = songList;
	}
	
	public void resetMoreView()
	{
		isMoreShow = false;
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return songList.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return songList.get(arg0);
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
		ViewHolder vh = null;
		if(convertView == null)
		{
			vh = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.view_song_item, null);
			vh.tvNum = (TextView)convertView.findViewById(R.id.tv_song_item_number);
			vh.tvMusicName = (TextView)convertView.findViewById(R.id.tv_song_item_music_name);
			vh.tvArtist = (TextView)convertView.findViewById(R.id.tv_song_item_music_artist);
			vh.tvCoin = (TextView)convertView.findViewById(R.id.tv_song_item_coin);
			vh.tvUserName = (TextView)convertView.findViewById(R.id.tv_song_item_user);
			vh.ivMoreIcon = (ImageView)convertView.findViewById(R.id.iv_song_item_more_icon);
			vh.viewBg = convertView.findViewById(R.id.view_song_item_bg);
			vh.viewMore = convertView.findViewById(R.id.view_song_item_more_view);
			vh.viewDetail = convertView.findViewById(R.id.view_song_item_more_detail);
			vh.viewDelete = convertView.findViewById(R.id.view_song_item_more_delete);
			vh.viewMsg = convertView.findViewById(R.id.view_song_item_more_msg);
			vh.viewChabo = convertView.findViewById(R.id.view_song_item_more_chabo);
			
			vh.viewBg.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					int index = (Integer) v.getTag();
					SongInfor tempInfor = songList.get(index);
					if(tempInfor.getUserInfor().getUserId().equals(LocalDataEntity.newInstance(activity).getUserInfor().getUserId()))
					{
						isMoreShow = !isMoreShow;
						notifyDataSetChanged();
					}
					else
					{
						songFragment.showSongDetails(index);
					}
				}
			});
			vh.viewDetail.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					int index = (Integer) v.getTag();
					songFragment.showSongDetails(index);
				}
			});
			vh.viewDelete.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					int index = (Integer) v.getTag();
					songFragment.deleteSong(index);
				}
			});
			vh.viewMsg.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					int index = (Integer) v.getTag();
					showMsgEditDialog(index);
				}
			});
			vh.viewChabo.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					int index = (Integer) v.getTag();
					showCoinEditDialog(index);
				}
			});
			
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		vh.viewBg.setTag(pos);
		vh.viewDetail.setTag(pos);
		vh.viewDelete.setTag(pos);
		vh.viewMsg.setTag(pos);
		vh.viewChabo.setTag(pos);
		
		SongInfor songInfor = songList.get(pos);
		vh.tvNum.setText((pos + 1) + "");
		vh.tvMusicName.setText(songInfor.getMediaName());
		if(TextUtils.isEmpty(songInfor.getArtist()))
			vh.tvArtist.setText("未知");
		else
			vh.tvArtist.setText(songInfor.getArtist());
		vh.tvCoin.setText(songInfor.getCoin() + "");
		vh.tvUserName.setText(songInfor.getUserInfor().getUserName());
		
		String localId = LocalDataEntity.newInstance(activity).getUserInfor().getUserId();
		if(!TextUtils.isEmpty(localId) && songInfor.getUserInfor().getUserId().equals(localId))
		{
			vh.tvUserName.setTextColor(activity.getResources().getColor(R.color.text_blue_on));
			vh.tvMusicName.setTextColor(activity.getResources().getColor(R.color.text_blue_on));
			vh.tvNum.setTextColor(activity.getResources().getColor(R.color.text_blue_on));
			vh.tvArtist.setTextColor(activity.getResources().getColor(R.color.text_blue_on));
			
			vh.ivMoreIcon.setVisibility(View.VISIBLE);
			updateMoreView(vh);
		}
		else
		{
			vh.tvUserName.setTextColor(activity.getResources().getColor(R.color.text_black_mid));
			vh.tvNum.setTextColor(activity.getResources().getColor(R.color.text_black_on));
			vh.tvMusicName.setTextColor(activity.getResources().getColor(R.color.text_black_on));
			vh.tvArtist.setTextColor(activity.getResources().getColor(R.color.text_black_on));
			
			vh.ivMoreIcon.setVisibility(View.GONE);
			vh.viewMore.setVisibility(View.GONE);
		}
		// TODO Auto-generated method stub
		return convertView;
	}
	
	private void updateMoreView(ViewHolder vh)
	{
		if(isMoreShow)
		{
			vh.ivMoreIcon.setImageResource(R.drawable.icon_pull_up);
			
			expand(vh.viewMore);
		}
		else
		{
			vh.ivMoreIcon.setImageResource(R.drawable.icon_pull_down);
			collapse(vh.viewMore);
		}
	}
	
	private class ViewHolder
	{
		TextView tvNum = null;
		TextView tvMusicName = null;
		TextView tvArtist = null;
		TextView tvCoin = null;
		TextView tvUserName = null;
		ImageView ivMoreIcon = null;
		View viewMore = null;
		View viewBg = null;
		View viewDetail = null;
		View viewDelete = null;
		View viewMsg = null;
		View viewChabo = null;
	}
	
	private void showMsgEditDialog(int index)
	{
		
		songFragment.showActivityAnim();
		
		final SongMsgEditDialog songMsgEditDialog = new SongMsgEditDialog(activity);
		
		songMsgEditDialog.show();
		
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = songMsgEditDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		songMsgEditDialog.getWindow().setAttributes(lp);
		songMsgEditDialog.setSongInfor(songList.get(index));
		songMsgEditDialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				// TODO Auto-generated method stub
				if(songMsgEditDialog.isUpdate())
					songFragment.updateSongList();
				songFragment.updateHandler();
				songFragment.clearActivityAnim();
			}
		});
	}
	private void showCoinEditDialog(int index)
	{
		
		songFragment.showActivityAnim();
		
		final SongCoinSelectDialog songCoinSelectDialog = new SongCoinSelectDialog(activity);
		
		songCoinSelectDialog.show();
		
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = songCoinSelectDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		songCoinSelectDialog.getWindow().setAttributes(lp);
		songCoinSelectDialog.setSongInfor(songList.get(index));
		songCoinSelectDialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				// TODO Auto-generated method stub
				if(songCoinSelectDialog.isUpdate())
					songFragment.updateSongList();
				songFragment.updateHandler();
				songFragment.clearActivityAnim();
				songCoinSelectDialog.stopHttp();
			}
		});
	}
	
	private void expand(final View v) 
	{
	    v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    final int targetHeight = v.getMeasuredHeight();

	    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
	    v.getLayoutParams().height = 1;
	    v.setVisibility(View.VISIBLE);
	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) 
	        {
	            v.getLayoutParams().height = interpolatedTime == 1
	                    ? LayoutParams.WRAP_CONTENT
	                    : (int)(targetHeight * interpolatedTime);
	            v.requestLayout();
	        }

	        @Override
	        public boolean willChangeBounds() 
	        {
	            return true;
	        }
	    };

	    // 1dp/ms
	    a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
	    v.startAnimation(a);
	}

	private void collapse(final View v) 
	{
	    final int initialHeight = v.getMeasuredHeight();

	    Animation a = new Animation()
	    {
	        @Override
	        protected void applyTransformation(float interpolatedTime, Transformation t) 
	        {
	            if(interpolatedTime == 1)
	            {
	                v.setVisibility(View.GONE);
	            }
	            else
	            {
	                v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
	                v.requestLayout();
	            }
	        }

	        @Override
	        public boolean willChangeBounds() 
	        {
	            return true;
	        }
	    };

	    // 1dp/ms
	    a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
	    v.startAnimation(a);
	}
}
 
