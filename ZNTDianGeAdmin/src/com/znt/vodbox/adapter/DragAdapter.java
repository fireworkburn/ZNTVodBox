package com.znt.vodbox.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.entity.MusicAlbumInfor;

public class DragAdapter extends BaseAdapter {
	/** TAG*/
	private final static String TAG = "DragAdapter";
	private boolean isItemShow = false;
	private Context context;
	private int holdPosition;
	private boolean isChanged = false;
	boolean isVisible = true;
	public List<MusicAlbumInfor> channelList;
	private TextView item_text;
	public int remove_position = -1;

	public DragAdapter(Context context, List<MusicAlbumInfor> channelList) {
		this.context = context;
		this.channelList = channelList;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return channelList == null ? 0 : channelList.size();
	}

	@Override
	public MusicAlbumInfor getItem(int position) {
		// TODO Auto-generated method stub
		if (channelList != null && channelList.size() != 0) {
			return channelList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.view_subscribe_category_item, null);
		item_text = (TextView) view.findViewById(R.id.tv_channel_name);
		MusicAlbumInfor channel = getItem(position);
		//if(TextUtils.isEmpty(channel.getMusicCount()))
			item_text.setText(channel.getAlbumName());
		/*else
			item_text.setText(channel.getAlbumName() + "(" + channel.getMusicCount() + ")");*/
		/*if ((position == 0)){
//			item_text.setTextColor(context.getResources().getColor(R.color.black));
			item_text.setEnabled(false);
		}*/
		if (isChanged && (position == holdPosition) && !isItemShow) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
			isChanged = false;
		}
		if (!isVisible && (position == -1 + channelList.size())) {
			item_text.setText("");
			item_text.setSelected(true);
			item_text.setEnabled(true);
		}
		if(remove_position == position){
			item_text.setText("");
		}
		return view;
	}

	public void addItem(MusicAlbumInfor channel) {
		channelList.add(channel);
		notifyDataSetChanged();
	}

	public void exchange(int dragPostion, int dropPostion) {
		holdPosition = dropPostion;
		MusicAlbumInfor dragItem = getItem(dragPostion);
		Log.d(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
		if (dragPostion < dropPostion) {
			channelList.add(dropPostion + 1, dragItem);
			channelList.remove(dragPostion);
		} else {
			channelList.add(dropPostion, dragItem);
			channelList.remove(dragPostion + 1);
		}
		isChanged = true;
		notifyDataSetChanged();
	}
	
	public List<MusicAlbumInfor> getChannnelLst() {
		return channelList;
	}

	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
	}

	public void remove() {
		channelList.remove(remove_position);
		remove_position = -1;
		notifyDataSetChanged();
	}
	
	public void setListDate(List<MusicAlbumInfor> list) {
		channelList = list;
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public void setVisible(boolean visible) {
		isVisible = visible;
	}
	public void setShowDropItem(boolean show) {
		isItemShow = show;
	}
}