package com.znt.vodbox.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Handler;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.activity.BaseActivity;
import com.znt.vodbox.dialog.AlbumListDialog;
import com.znt.vodbox.dialog.MusicOperationDialog;
import com.znt.vodbox.entity.MusicEditType;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.utils.FileUtils;

public class UserMusicAdapter extends BaseAdapter
{
	private List<MediaInfor> list = null;
	private List<MediaInfor> selectedList = new ArrayList<MediaInfor>();
	private Activity mContext;
	private BaseActivity baseActivity;
	private MusicEditType musicEditType = null;
	private HttpFactory httpfactory = null;
	private String albumId = "";
	private int deleteAlbumIndex = 0;
	
	public UserMusicAdapter(BaseActivity baseActivity, List<MediaInfor> list, Handler handler)
	{
		this.baseActivity = baseActivity;
		this.mContext = baseActivity.getActivity();
		this.list = list;
		
		httpfactory = new HttpFactory(mContext, handler);
	}
	
	public void removeDeleteMusic()
	{
		list.remove(deleteAlbumIndex);
		notifyDataSetChanged();
	}
	
	public void setMusicEditType(MusicEditType musicEditType)
	{
		this.musicEditType = musicEditType;
		notifyDataSetChanged();
	}
	public void setAlbumId(String albumId)
	{
		this.albumId = albumId;
	}
	public void setIsLocalMusic(boolean isLocalMusic)
	{
		
	}
	
	public void setResoureType(int resourceType)
	{
		
	}
	
	public List<MediaInfor> getSelectedList()
	{
		return selectedList;
	}
	
	public void deleteMusics()
	{
		String ids = "";
		int size = selectedList.size();
		if(size == 0)
		{
			Toast.makeText(mContext, "请选择要删除的歌曲", 0).show();
			return;
		}
		for(int i=0;i<size;i++)
		{
			if(i < size - 1)
				ids += selectedList.get(i).getMediaId() + ",";
			else
				ids += selectedList.get(i).getMediaId();
			
		}
		httpfactory.deleteAlbumMusics(albumId, ids);
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

	private ViewHolder viewHolder = null;
	public View getView(final int position, View view, ViewGroup arg2) 
	{
		if (view == null) 
		{
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.view_music_item, null);
			viewHolder.tvName = (TextView) view.findViewById(R.id.tv_music_name);
			viewHolder.tvArtist = (TextView) view.findViewById(R.id.tv_music_artist);
			viewHolder.ivIcon = (ImageView) view.findViewById(R.id.iv_music_icon);
			viewHolder.ivEdit = (ImageView) view.findViewById(R.id.iv_music_item_edit);
			viewHolder.viewOperation = view.findViewById(R.id.view_music_operation);
			if(musicEditType == null)
			{
				viewHolder.viewOperation.setVisibility(View.GONE);
			}
			else
				viewHolder.viewOperation.setVisibility(View.VISIBLE);
			viewHolder.viewOperation.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					int index = (Integer)v.getTag();
					MediaInfor infor = list.get(index);
					if(musicEditType == MusicEditType.Select)
					{
						infor.setSelected(!infor.isSelected());
						if(infor.isSelected())
						{
							selectedList.add(infor);
						}
						else 
						{
							if(selectedList.size() > 0)
								selectedList.remove(infor);
						}
						list.set(index, infor);
						notifyDataSetChanged();
					}
					else if(musicEditType == MusicEditType.Delete)
					{
						if(!TextUtils.isEmpty(albumId))
						{
							deleteAlbumIndex = index;
							httpfactory.deleteAlbumMusic(albumId, infor.getMediaId());
						}
					}
					else if(musicEditType == MusicEditType.Add)
					{
						showAlbumSelectDialog(baseActivity.getActivity(), infor);
					}
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
		
		if(musicEditType == MusicEditType.Select)
		{
			if(infor.isSelected())
				viewHolder.ivEdit.setImageResource(R.drawable.icon_selected_on);
			else
				viewHolder.ivEdit.setImageResource(R.drawable.icon_selected_off);
		}
		else if(musicEditType == MusicEditType.Delete)
			viewHolder.ivEdit.setImageResource(R.drawable.icon_song_item_delete);
		else if(musicEditType == MusicEditType.Add)
			viewHolder.ivEdit.setImageResource(R.drawable.icon_add_on);
		else if(musicEditType == MusicEditType.None)
		{
			viewHolder.viewOperation.setVisibility(View.GONE);
			if(infor.isPlaying())
				viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.text_blue_on));
			else
				viewHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.text_black_on));
		}
		
		viewHolder.tvName.setText(infor.getMediaName());
		
		if(!TextUtils.isEmpty(infor.getArtist()))
		{
			viewHolder.tvArtist.setText(infor.getArtist());
		}
		else	
		{
			viewHolder.tvArtist.setText("未知");
		}
		
		if(FileUtils.isPicture(infor.getMediaUrl()))
		{
			if(!TextUtils.isEmpty(infor.getMediaUrl()))
				Picasso.with(mContext).load(infor.getMediaUrl()).into(viewHolder.ivIcon);
			//viewHolder.ivIcon.setImageResource(R.drawable.icon_video);
		}
		else if(FileUtils.isVideo(infor.getMediaUrl()))
			viewHolder.ivIcon.setImageResource(R.drawable.icon_video);
		else
			viewHolder.ivIcon.setImageResource(R.drawable.icon_music);
		
		return view;

	}
	
	final static class ViewHolder 
	{
		TextView tvName;
		TextView tvArtist;
		ImageView ivIcon = null;
		ImageView ivEdit = null;
		View viewOperation = null;
	}
	
	private void showOperationDialog(final MediaInfor infor)
	{
		final MusicOperationDialog dialog = new MusicOperationDialog(mContext, infor, baseActivity.isAdminDevice());
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
	
	private void showAlbumSelectDialog(Activity activity, MediaInfor infor) 
	{
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		selectedList.clear();
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

}