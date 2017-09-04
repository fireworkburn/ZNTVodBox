
package com.znt.vodbox.dialog; 

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.znt.diange.mina.entity.MediaInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AlbumSelectAdapter;
import com.znt.vodbox.entity.MusicAlbumInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;

/** 
 * @ClassName: PlayListDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-9-19 下午3:49:41  
 */
public class AlbumListDialog extends Dialog implements OnItemClickListener
{

	private Activity context = null;
	private View parentView = null;
	private ProgressBar pbLoading = null;
	
	private ListView lvPlayList = null;
	private TextView tvCancel = null;
	private TextView tvConfirm = null;
	
	private List<MusicAlbumInfor> albumList = new ArrayList<MusicAlbumInfor>();
	private List<MediaInfor> mediaList = new ArrayList<MediaInfor>();
	private AlbumSelectAdapter adapter = null;
	private HttpFactory httpFactory = null;
	private String oldAlbumId = "";
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.GET_MUSIC_ALBUM_START)
			{
				pbLoading.setVisibility(View.VISIBLE);
			}
			else if(msg.what == HttpMsg.GET_MUSIC_ALBUM_SUCCESS)
			{
				albumList.clear();
				List<MusicAlbumInfor> tempList = (List<MusicAlbumInfor>)msg.obj;
				albumList.addAll(tempList);
				removeOldAlbum();
				adapter.notifyDataSetChanged();
				pbLoading.setVisibility(View.GONE);
				
			}
			else if(msg.what == HttpMsg.GET_MUSIC_ALBUM_FAIL)
			{
				pbLoading.setVisibility(View.VISIBLE);
				dismiss();
				String error = (String) msg.obj;
				if(TextUtils.isEmpty(error))
					error = "操作失败";
				Toast.makeText(context, error, 0).show();
				//showToast("获取数据失败，请下拉刷新");
			}
			else if(msg.what == HttpMsg.ADD_MUSIC_TO_ALBUM_START)
			{
				pbLoading.setVisibility(View.VISIBLE);
				//showToast("获取数据失败，请下拉刷新");
			}
			else if(msg.what == HttpMsg.ADD_MUSIC_TO_ALBUM_SUCCESS)
			{
				pbLoading.setVisibility(View.GONE);
				dismiss();
				Toast.makeText(context, "操作成功", 0).show();
				//showToast("获取数据失败，请下拉刷新");
			}
			else if(msg.what == HttpMsg.ADD_MUSIC_TO_ALBUM_FAIL)
			{
				pbLoading.setVisibility(View.GONE);
				String error = (String) msg.obj;
				if(TextUtils.isEmpty(error))
					error = "操作失败";
				Toast.makeText(context, error, 0).show();
				//showToast("获取数据失败，请下拉刷新");
			}
		};
	};
	
	public AlbumListDialog(Activity context) 
	{
		super(context, R.style.MMTheme_DataSheet);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	public AlbumListDialog(Activity context, List<MediaInfor> mediaList, String oldAlbumId) 
	{
		super(context, R.style.MMTheme_DataSheet);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.oldAlbumId = oldAlbumId;
		this.mediaList = mediaList;
	}
	
	private void removeOldAlbum()
	{
		if(!TextUtils.isEmpty(oldAlbumId))
		{
			for(int i=0;i<albumList.size();i++)
			{
				MusicAlbumInfor infor = albumList.get(i);
				if(infor.getAlbumId().equals(oldAlbumId) || infor.getAlbumId().equals("0"))
				{
					albumList.remove(i);
				}
			}
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_album_list);
		
		parentView = findViewById(R.id.view_album_list_dialog_bg);
		
		lvPlayList = (ListView)findViewById(R.id.lv_album_list);
		pbLoading = (ProgressBar)findViewById(R.id.pb_dialog_album_list);
		tvCancel = (TextView)findViewById(R.id.tv_album_cancel);
		tvConfirm = (TextView)findViewById(R.id.tv_album_confirm);
		
		lvPlayList.setOnItemClickListener(this);
		
		adapter = new AlbumSelectAdapter(context, albumList);
		lvPlayList.setAdapter(adapter);
		
		httpFactory = new HttpFactory(context, handler);
		httpFactory.getCreateAlbums();
		
		adapter.setAlbumListDialog(this);
		
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
		tvConfirm.setOnClickListener(new android.view.View.OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				
			}
		});
		this.setOnDismissListener(new OnDismissListener()
		{
			
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				// TODO Auto-generated method stub
				if(httpFactory != null)
					httpFactory.stopHttp();
			}
		});
		
	}
	
	public void addMusic(MusicAlbumInfor infor)
	{
		if(!TextUtils.isEmpty(oldAlbumId))
			addSysMusicToAlbum(infor.getAlbumId());
		else
			addMusicsToAlbum(infor.getAlbumId());
	}
	
	private void addSysMusicToAlbum(String categoryId)
	{
		if(mediaList.size() > 0)
			httpFactory.addSysMusicToAlbum(categoryId, mediaList);
		else
			Toast.makeText(context, "请选择歌单", 0).show();
	}
	private void addMusicsToAlbum(String categoryId)
	{
		if(mediaList.size() > 0)
			httpFactory.addMusicToAlbum(categoryId, mediaList.get(0));
		else
			Toast.makeText(context, "请选择歌单", 0).show();
	}

	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		MusicAlbumInfor infor = albumList.get(arg2);
		addMusic(infor);
		
	}

}