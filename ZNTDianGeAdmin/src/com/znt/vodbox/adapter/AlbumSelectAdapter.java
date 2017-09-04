/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-23 下午10:47:40 
* @Version V1.1   
*/ 

package com.znt.vodbox.adapter; 

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.znt.vodbox.R;
import com.znt.vodbox.dialog.AlbumListDialog;
import com.znt.vodbox.entity.MusicAlbumInfor;
import com.znt.vodbox.factory.HttpFactory;

/** 
 * @ClassName: AlbumSelectDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-23 下午10:47:40  
 */
public class AlbumSelectAdapter extends BaseAdapter
{

	private Activity baseActivity = null;
	private List<MusicAlbumInfor> list = new ArrayList<MusicAlbumInfor>();
	private List<MusicAlbumInfor> selectedList = new ArrayList<MusicAlbumInfor>();
	private boolean isEditAble = false;
	private AlbumListDialog albumListDialog = null;
	public AlbumSelectAdapter(Activity activity, List<MusicAlbumInfor> list)
	{
		this.baseActivity = activity;
		this.list = list;
	}
	
	public void setAlbumListDialog(AlbumListDialog albumListDialog)
	{
		this.albumListDialog = albumListDialog;
	}
	
	public void setSelectedList(List<MusicAlbumInfor> selectedList)
	{
		this.selectedList = selectedList;
		isEditAble = true;
		int size = selectedList.size();
		for(int i=0;i<size;i++)
		{
			MusicAlbumInfor infor = selectedList.get(i);
			int len = list.size();
			for(int j=0;j<len;j++)
			{
				MusicAlbumInfor tempInfor = list.get(j);
				if(infor.getAlbumId().equals(tempInfor.getAlbumId()))
				{
					tempInfor.setSelected(true);
					list.set(j, tempInfor);
				}
			}
		}
		notifyDataSetChanged();
	}
	public List<MusicAlbumInfor> getSelectedList()
	{
		return selectedList;
	}
	
	/**
	*callbacks
	*/
	@Override
	public int getCount() 
	{
		// TODO Auto-generated method stub
		return list.size();
	}

	/**
	*callbacks
	*/
	@Override
	public Object getItem(int arg0) 
	{
		// TODO Auto-generated method stub
		return list.get(arg0);
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

	private ViewHolder vh = null;
	/**
	*callbacks
	*/
	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) 
	{
		// TODO Auto-generated method stub
		if(convertView == null)
		{
			vh = new ViewHolder();
			convertView = LayoutInflater.from(baseActivity).inflate(R.layout.view_album_select_item, null);
			
			vh.tvName = (TextView)convertView.findViewById(R.id.tv_album_sellect_name);
			vh.ivCover = (ImageView)convertView.findViewById(R.id.iv_album_select_cover);
			vh.viewOperation = convertView.findViewById(R.id.view_album_select_item_operation);
			vh.viewBg = convertView.findViewById(R.id.view_album_select_item_bg);
			vh.ivEdit = (ImageView)convertView.findViewById(R.id.iv_album_select_item_operation);
			
			if(isEditAble)
			{
				vh.viewOperation.setVisibility(View.VISIBLE);
			}
			else
			{
				vh.viewOperation.setVisibility(View.GONE);
			}
			
			vh.viewBg.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					int index = (Integer) v.getTag();
					MusicAlbumInfor infor = list.get(index);
					if(isEditAble)
					{
						infor.setSelected(!infor.isSelected());
						if(infor.isSelected())
						{
							selectedList.add(infor);
						}
						else 
						{
							for(int i=0;i<selectedList.size();i++)
							{
								MusicAlbumInfor tempInfor = selectedList.get(i);
								if(infor.getAlbumId().equals(tempInfor.getAlbumId()))
								{
									selectedList.remove(i);
								}
							}
							if(selectedList.size() > 0)
								selectedList.remove(infor);
						}
						list.set(index, infor);
						notifyDataSetChanged();
					}
					else
					{
						albumListDialog.addMusic(infor);
					}
				}
			});
			
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		vh.viewBg.setTag(pos);
		
		MusicAlbumInfor infor = list.get(pos);
		
		if(isEditAble)
		{
			if(infor.isSelected())
				vh.ivEdit.setImageResource(R.drawable.icon_selected_on);
			else
				vh.ivEdit.setImageResource(R.drawable.icon_selected_off);
		}
		
		vh.tvName.setText(infor.getAlbumName() + "(" + infor.getMusicCount() + ")");
		if(!TextUtils.isEmpty(infor.getCover()))
			Picasso.with(baseActivity).load(infor.getCover()).into(vh.ivCover);
		/*else 
			Picasso.with(baseActivity).load("http://img5.imgtn.bdimg.com/it/u=698076066,2006876975&fm=21&gp=0.jpg").into(vh.ivCover);*/
		
		// TODO Auto-generated method stub
		return convertView;
	}
	

	private class ViewHolder
	{
		ImageView ivCover = null;
		TextView tvName = null;
		View viewOperation = null;
		View viewBg = null;
		ImageView ivEdit = null;
	}

}
 
