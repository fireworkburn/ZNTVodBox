
package com.znt.vodbox.view; 

import java.util.ArrayList;
import java.util.List;

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
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.ResoureType;
import com.znt.vodbox.activity.BaseActivity;
import com.znt.vodbox.dialog.MusicOperationDialog;
import com.znt.vodbox.dialog.SongBookOperationDialog;

/** 
 * @ClassName: SongAdapter 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-16 上午11:28:23  
 */
public class SongBookAdapter extends BaseAdapter
{
	private BaseActivity baseActivity = null;
	private Activity activity = null;
	private List<MediaInfor> mediaList = new ArrayList<MediaInfor>();
	
	public SongBookAdapter(BaseActivity baseActivity, List<MediaInfor> mediaList)
	{
		this.baseActivity = baseActivity;
		this.activity = baseActivity.getActivity();
		this.mediaList = mediaList;
	}
	/**
	*callbacks
	*/
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return mediaList.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return mediaList.get(arg0);
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.view_song_book_item, null);
			vh.tvMusicName = (TextView)convertView.findViewById(R.id.tv_song_book_item_music_name);
			vh.tvArtist = (TextView)convertView.findViewById(R.id.tv_song_book_item_music_artist);
			vh.viewOperation = convertView.findViewById(R.id.view_song_book_item_shiting);
			
			vh.viewOperation.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					int index = (Integer) v.getTag();
					MediaInfor tempInfor = mediaList.get(index);
					if(baseActivity.isAdminDevice())
					{
						showAdminOperationDialog(tempInfor);
					}
					else
						showOperationDialog(tempInfor);
				}
			});
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		vh.viewOperation.setTag(pos);
		
		MediaInfor tempInfor = mediaList.get(pos);
		vh.tvMusicName.setText(tempInfor.getMediaName());
		if(TextUtils.isEmpty(tempInfor.getArtist()))
			vh.tvArtist.setText("未知");
		else
			vh.tvArtist.setText(tempInfor.getArtist());
		
		// TODO Auto-generated method stub
		return convertView;
	}
	
	private void showAdminOperationDialog(final MediaInfor infor)
	{
		final SongBookOperationDialog dialog = new SongBookOperationDialog(activity, infor);
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		dialog.setSpeakerMusic(true);
		if(dialog.isShowing())
			dialog.dismiss();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() 
		{
			@Override
			public void onDismiss(DialogInterface arg0) 
			{
				// TODO Auto-generated method stub
				if(dialog.isDeleteSuccess())
				{
					mediaList.remove(infor);
					notifyDataSetChanged();
				}
			}
		});
		
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		dialog.getWindow().setAttributes(lp);
	}
	
	private void showOperationDialog(final MediaInfor infor)
	{
		infor.setResourceType(ResoureType.WANGYI);
		final MusicOperationDialog dialog = new MusicOperationDialog(activity, infor, baseActivity.isAdminDevice());
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		dialog.setSpeakerMusic(true);
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
		
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		dialog.getWindow().setAttributes(lp);
	}
	
	private class ViewHolder
	{
		TextView tvMusicName = null;
		TextView tvArtist = null;
		View viewOperation = null;
	}
}
 
