
package com.znt.diange.dialog; 

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.activity.SongPrepareActivity;
import com.znt.diange.db.DBManager;
import com.znt.diange.fragment.BaseFragment;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.SongInfor;
import com.znt.diange.utils.ViewUtils;

/** 
 * @ClassName: PlayListDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-9-19 下午3:49:41  
 */
public class PlayListDialog extends Dialog implements OnItemClickListener
{

	private BaseFragment context = null;
	private View parentView = null;
	
	private ListView lvPlayList = null;
	private TextView tvCancel = null;
	
	private List<SongInfor> songList = new ArrayList<SongInfor>();
	private PlayListAdapter adapter = null;
	
	public PlayListDialog(BaseFragment context) 
	{
		super(context.getActivity(), R.style.MMTheme_DataSheet);
		// TODO Auto-generated constructor stub
		this.context = context;
		
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_play_list);
		
		parentView = findViewById(R.id.view_play_list_dialog_bg);
		
		lvPlayList = (ListView)findViewById(R.id.lv_play_list);
		tvCancel = (TextView)findViewById(R.id.tv_image_cancel);
		
		lvPlayList.setOnItemClickListener(this);
		
		adapter = new PlayListAdapter();
		lvPlayList.setAdapter(adapter);
		
		parentView.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) 
			{
				// TODO Auto-generated method stub
				dismiss();
				return false;
			}
		});
		tvCancel.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				songList = DBManager.newInstance(context.getActivity()).getSongList(0, 1000);
				context.getActivity().runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						adapter.notifyDataSetChanged();
					}
				});
			}
		}).start();
	}

	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		
		MediaInfor musicInfor = songList.get(arg2);
		Bundle bundle = new Bundle();
		if(musicInfor.getMediaType().equals(MediaInfor.MEDIA_TYPE_NET))
			bundle.putBoolean("IS_CHECK", true);
		else
			bundle.putBoolean("IS_CHECK", false);
		bundle.putSerializable("MediaInfor", musicInfor);
		ViewUtils.startActivity(context.getActivity(), SongPrepareActivity.class, bundle);
		
	}

	class PlayListAdapter extends BaseAdapter
	{
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
			// TODO Auto-generated method stub
			
			ViewHoler vh = null;
			if(convertView == null)
			{
				vh = new ViewHoler();
				
				convertView = LayoutInflater.from(context.getActivity()).inflate(R.layout.view_play_list_item, null);
				vh.tvName = (TextView)convertView.findViewById(R.id.tv_play_list_item_name);
				vh.tvArtist = (TextView)convertView.findViewById(R.id.tv_play_list_item_artist);
				vh.viewDelete = convertView.findViewById(R.id.view_play_list_item_delete);
				vh.viewDelete.setOnClickListener(new android.view.View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// TODO Auto-generated method stub
						int index = (Integer)v.getTag();
						showAlertDialog(index);
					}
				});
				
				convertView.setTag(vh);
			}
			else
				vh = (ViewHoler)convertView.getTag();
			
			vh.viewDelete.setTag(pos);
			
			SongInfor songInfor = songList.get(pos);
			vh.tvName.setText(songInfor.getMediaName());
			if(!TextUtils.isEmpty(songInfor.getArtist()))
				vh.tvArtist.setText(songInfor.getArtist());
			else
				vh.tvArtist.setText("未知");
			
			return convertView;
		}
		
		private class ViewHoler
		{
			TextView tvName = null;
			TextView tvArtist = null;
			View viewDelete = null;
		}
	}
	
	private void showAlertDialog(final int index)
	{
		final SongInfor songInfor = songList.get(index);
		context.showAlertDialog(context.getActivity(), new android.view.View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				int result = DBManager.newInstance(context.getActivity()).deleteSongByUrl(songInfor.getMediaUrl());
				if(result > 0)
				{
					songList.remove(index);
					adapter.notifyDataSetChanged();
					context.dismissDialog();
					if(songList.size() == 0)
						dismiss();
				}
				else
					context.showToast("删除失败");
			}
		}, null, "确定删除该歌曲吗?");
	}
}