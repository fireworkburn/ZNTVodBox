/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-21 下午5:56:05 
* @Version V1.1   
*/ 

package com.znt.vodbox.adapter; 

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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.znt.vodbox.R;
import com.znt.vodbox.activity.AlbumMusicActivity;
import com.znt.vodbox.dialog.EditNameDialog;
import com.znt.vodbox.dialog.MyAlertDialog;
import com.znt.vodbox.entity.MusicAlbumInfor;
import com.znt.vodbox.entity.MusicEditType;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.utils.ViewUtils;

/** 
 * @ClassName: MyAlbumAdapter 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-21 下午5:56:05  
 */
public class MyAlbumAdapter extends BaseAdapter
{

	private Activity activity = null;
	private List<MusicAlbumInfor> albumList = new ArrayList<MusicAlbumInfor>();
	
	private ListView listView = null;
	
	private int selectIndex = 0;
	private String editAlbumName = "";
	private MusicEditType musicEditType = null;
	private boolean isAlbumEditAble = true;
	private HttpFactory httpFactory = null;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.CREATE_ALBUM_START)
			{
				
			}
			else if(msg.what == HttpMsg.CREATE_ALBUM_SUCCESS)
			{
				/*Toast.makeText(context, "创建成功", 0).show();
				updateHeaderView();*/
			}
			else if(msg.what == HttpMsg.CREATE_ALBUM_FAIL)
			{
				//Toast.makeText(baseActivity.getActivity(), "创建失败", 0).show();
			}
			else if(msg.what == HttpMsg.DELETE_ALBUM_START)
			{
				
			}
			else if(msg.what == HttpMsg.DELETE_ALBUM_SUCCESS)
			{
				albumList.remove(selectIndex);
				notifyDataSetChanged();
				Toast.makeText(activity, "删除成功", 0).show();
				dismissDialog();
				setListViewHeight();
			}
			else if(msg.what == HttpMsg.DELETE_ALBUM_FAIL)
			{
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "删除失败";
				Toast.makeText(activity, error, 0).show();
			}
			else if(msg.what == HttpMsg.EDIT_ALBUM_START)
			{
				
			}
			else if(msg.what == HttpMsg.EDIT_ALBUM_SUCCESS)
			{
				MusicAlbumInfor tempInfor = albumList.get(selectIndex);
				tempInfor.setAlbumName(editAlbumName);
				albumList.set(selectIndex, tempInfor);
				notifyDataSetChanged();
				Toast.makeText(activity, "修改成功", 0).show();
				dismissDialog();
			}
			else if(msg.what == HttpMsg.EDIT_ALBUM_FAIL)
			{
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "修改失败";
				Toast.makeText(activity, error, 0).show();
			}
		};
	};
	
	public MyAlbumAdapter(Activity activity, List<MusicAlbumInfor> albumList, MusicEditType musicEditType)
	{
		this.activity = activity;
		this.musicEditType = musicEditType;
		this.albumList = albumList;
		httpFactory = new HttpFactory(activity, handler);
	}
	public void setEditAble(boolean isAlbumEditAble)
	{
		this.isAlbumEditAble = isAlbumEditAble;
	}
	public void setListVeiw(ListView listView)
	{
		this.listView = listView;
	}
	private void setListViewHeight()
	{
		if(listView != null)
		{
			int size = albumList.size();
			int h = size * (64);
			ViewUtils.setViewParams(activity, listView, 0, h);
		}
	}
	/**
	*callbacks
	*/
	@Override
	public int getCount() 
	{
		// TODO Auto-generated method stub
		return albumList.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0) 
	{
		// TODO Auto-generated method stub
		return albumList.get(arg0);
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
		
		ViewHolder vh = null;
		if(convertView == null)
		{
			vh = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.view_my_music_item, null);
			vh.ivCover = (ImageView)convertView.findViewById(R.id.iv_my_music_item_cover);
			vh.tvName = (TextView)convertView.findViewById(R.id.tv_my_music_item_name);
			vh.tvDesc = (TextView)convertView.findViewById(R.id.tv_my_music_item_desc);
			vh.bgView = convertView.findViewById(R.id.view_my_music_item_bg);
			vh.viewOperation = convertView.findViewById(R.id.view_my_music_operation);
			vh.viewEdit = convertView.findViewById(R.id.view_my_music_edit);
			vh.viewDelete = convertView.findViewById(R.id.view_my_music_delete);
			
			vh.viewDelete.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					selectIndex = (Integer) v.getTag();
					showAlertDialog(activity, new OnClickListener()
					{
						@Override
						public void onClick(View arg0) 
						{
							MusicAlbumInfor infor = albumList.get(selectIndex);
							// TODO Auto-generated method stub
							/*if(!isAlbumEditAble)
							{
								httpfactory.deleteAlbum(albumInfor.getAlbumId());
							}
							else
							{
								httpFactory.deleteAlbum(infor.getAlbumId());
							}*/
							httpFactory.deleteAlbum(infor.getAlbumId());
							
						}
					}, null, "确定删除该歌单吗？");
				}
			});
			vh.viewEdit.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					selectIndex = (Integer) v.getTag();
					MusicAlbumInfor infor = albumList.get(selectIndex);
					showCreateAlbumDialog(infor);
				}
			});
			
			vh.bgView.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					selectIndex = (Integer) v.getTag();
					MusicAlbumInfor infor = albumList.get(selectIndex);
					Bundle bundle = new Bundle();
					bundle.putSerializable("MusicAlbumInfor", infor);
					/*if(infor.getAlbumId().equals("0"))
						bundle.putSerializable("MusicEditType", MusicEditType.Add);
					else
						bundle.putSerializable("MusicEditType", musicEditType);*/
					bundle.putSerializable("MusicEditType", musicEditType);
					if(musicEditType == MusicEditType.Add)
						bundle.putBoolean("IS_COLLECT", true);
					ViewUtils.startActivity(activity, AlbumMusicActivity.class, bundle);
				}
			});
			vh.bgView.setOnLongClickListener(new OnLongClickListener() 
			{
				@Override
				public boolean onLongClick(View v) 
				{
					if(musicEditType == MusicEditType.Add)
						return false;
					selectIndex = (Integer) v.getTag();
					showAlertDialog(activity, new OnClickListener()
					{
						
						@Override
						public void onClick(View arg0) 
						{
							// TODO Auto-generated method stub
							MusicAlbumInfor infor = albumList.get(selectIndex);
							httpFactory.deleteAlbum(infor.getAlbumId());
						}
					}, null, "确定删除该歌单吗？");
					// TODO Auto-generated method stub
					return true;
				}
			});
			convertView.setTag(vh);
			
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		vh.viewOperation.setTag(pos);
		vh.bgView.setTag(pos);
		vh.viewDelete.setTag(pos);
		vh.viewEdit.setTag(pos);
		
		MusicAlbumInfor infor = albumList.get(pos);
		
		if(infor.getAlbumId().equals("0") || !isAlbumEditAble)
		{
			if(!isAlbumEditAble)
			{
				vh.viewOperation.setVisibility(View.VISIBLE);
				vh.viewEdit.setVisibility(View.GONE);
				vh.viewDelete.setVisibility(View.VISIBLE);
			}
			else
				vh.viewOperation.setVisibility(View.GONE);
			vh.ivCover.setImageResource(R.drawable.icon_upload);
			if(infor.getAlbumId().equals("0"))
				vh.tvName.setText(infor.getAlbumName());
			else 
				vh.tvName.setText(infor.getAlbumName() + "(" + infor.getMusicCount() + ")");
		}
		else
		{
			vh.viewOperation.setVisibility(View.VISIBLE);
			vh.tvName.setText(infor.getAlbumName() + "(" + infor.getMusicCount() + ")");
		}
		vh.tvDesc.setText(infor.getDescription());
		if(!TextUtils.isEmpty(infor.getCover()))
			Picasso.with(activity).load(infor.getCover()).into(vh.ivCover);
		
		return convertView;
	}

	private class ViewHolder
	{
		ImageView ivCover = null;
		TextView tvName = null;
		TextView tvDesc = null;
		View bgView = null;
		View viewOperation = null;
		View viewEdit = null;
		View viewDelete = null;
	}
	
	private void dismissDialog()
	{
		if(myAlertDialog != null && myAlertDialog.isShowing())
			myAlertDialog.dismiss();
		if(dialog != null && dialog.isShowing())
			dialog.dismiss();
	}
	private MyAlertDialog myAlertDialog = null;
	public final void showAlertDialog(Activity activity, OnClickListener listener, String title,
			String message) 
	{
		if (TextUtils.isEmpty(title)) 
		{
			title = "提示";
		}
		
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		if(myAlertDialog == null || myAlertDialog.isDismissed())
			myAlertDialog = new MyAlertDialog(activity, R.style.Theme_CustomDialog);
		myAlertDialog.setInfor(title, message);
		if(myAlertDialog.isShowing())
			myAlertDialog.dismiss();
		myAlertDialog.show();
		myAlertDialog.setOnClickListener(listener);
		
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = myAlertDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		myAlertDialog.getWindow().setAttributes(lp);
	}
	
	private EditNameDialog dialog = null;
	private void showCreateAlbumDialog(final MusicAlbumInfor infor)
	{
		if(dialog == null || dialog.isDismissed())
			dialog = new EditNameDialog(activity);
		dialog.setInfor(infor.getAlbumName());
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(dialog.isShowing())
			dialog.dismiss();
		dialog.show();
		dialog.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				if(TextUtils.isEmpty(dialog.getContent()))
				{
					Toast.makeText(activity, "请输入歌单名称", 0).show();
					return;
				}
				if(dialog.getContent().equals(infor.getAlbumName()))
				{
					Toast.makeText(activity, "歌单名称未更改", 0).show();
					return;
				}
				editAlbumName = dialog.getContent();
				httpFactory.editAlbum(editAlbumName, infor.getAlbumId(), infor.getDescription());
			}
		});
		
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		dialog.getWindow().setAttributes(lp);
	}
}
 
