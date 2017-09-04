/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-21 下午5:36:27 
* @Version V1.1   
*/ 

package com.znt.vodbox.fragment; 

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.MyAlbumAdapter;
import com.znt.vodbox.dialog.EditNameDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.MusicAlbumInfor;
import com.znt.vodbox.entity.MusicEditType;
import com.znt.vodbox.entity.MyAlbumInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.view.MyMusicHeaderView;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: MyMusicFragment 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-21 下午5:36:27  
 */
public class MyMusicFragment  extends BaseFragment implements IXListViewListener
{
	private View parentView = null;
	private LJListView listView = null;
	private MyMusicHeaderView headerView = null;
	
	private HttpFactory httpfactory = null;
	private List<MusicAlbumInfor> albumList = new ArrayList<MusicAlbumInfor>();
	private MyAlbumAdapter adapter = null;
	private boolean isPrepared = false;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.GET_MUSIC_ALBUM_START)
			{
				//showLoadingView(true);
				hideHintView();
			}
			else if(msg.what == HttpMsg.GET_MUSIC_ALBUM_SUCCESS)
			{
				
				MyAlbumInfor myAlbumInfor = (MyAlbumInfor)msg.obj;
				List<MusicAlbumInfor> tempList = myAlbumInfor.getCollectAlbums();
				headerView.updateData(myAlbumInfor.getCreateAlbums());
				headerView.showAlbumSize(myAlbumInfor);
				
				albumList.clear();
				albumList.addAll(tempList);
				
				adapter.notifyDataSetChanged();
				//showLoadingView(false);
				hideHintView();
				onLoad(0);
				Constant.isAlbumUpdated = false;
			}
			else if(msg.what == HttpMsg.GET_MUSIC_ALBUM_FAIL)
			{
				//showLoadingView(false);
				onLoad(0);
				Constant.isAlbumUpdated = true;
			}
			else if(msg.what == HttpMsg.CREATE_ALBUM_START)
			{
				
			}
			else if(msg.what == HttpMsg.CREATE_ALBUM_SUCCESS)
			{
				showToast("创建成功");
				getMusicAlbums();
				dismissDialog();
				if(dialog != null && dialog.isShowing())
					dialog.dismiss();
			}
			else if(msg.what == HttpMsg.CREATE_ALBUM_FAIL)
			{
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "创建失败";
				showToast(error);
			}
		};
	};
	public MyMusicFragment()
	{
		
	}
	public static MyMusicFragment getInstance()
	{
		return new MyMusicFragment();
	}
	
	/**
	*callbacks
	*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		
		if(parentView == null)
		{
			showReturnView(false);
			setCenterString("歌曲管理");
			
			parentView = getContentView(R.layout.fragment_my_music);
			listView = (LJListView)parentView.findViewById(R.id.ptrl_my_music);
			listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
			listView.getListView().setDividerHeight(1);
			listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
			listView.setPullRefreshEnable(true);
			listView.setIsAnimation(true); 
			listView.setXListViewListener(this);
			listView.showFootView(false);
			listView.setRefreshTime();
			
			adapter = new MyAlbumAdapter(getActivity(), albumList, MusicEditType.Add);
			adapter.setEditAble(false);
			headerView = new MyMusicHeaderView(getActivity());
			listView.addHeader(headerView);
			listView.setAdapter(adapter);
			
			httpfactory = new HttpFactory(getActivity(), handler);
			
			setRightText("新建歌单");
			showRightView(true);
			getRightView().setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					// TODO Auto-generated method stub
					showCreateAlbumDialog();
				}
			});
			
			isPrepared = true;
			lazyLoad();
			
		}
		else
		{
			ViewGroup parent = (ViewGroup) parentView.getParent();
			if(parent != null)
				parent.removeView(parentView);
		}
		
		// TODO Auto-generated method stub
		return parentView;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onResume() 
	{
		// TODO Auto-generated method stub
		if(Constant.isAlbumUpdated)
		{
			listView.onFresh();
			Constant.isAlbumUpdated = false;
		}
		super.onResume();
	}
	
	public void getMusicAlbums()
	{
		httpfactory.getMusicAlbums();
		//httpfactory.getSystemAlbums("0","0");
	}
	public void createAlbum(String name)
	{
		httpfactory.createAlbum(name);
	}
	
	private void onLoad(int updateCount) 
	{
		listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void lazyLoad()
	{
		// TODO Auto-generated method stub
		if(isPrepared)
		{
			listView.onFresh();
		}
	}
	/**
	*callbacks
	*/
	@Override
	public void onRefresh() 
	{
		// TODO Auto-generated method stub
		getMusicAlbums();
	}
	/**
	*callbacks
	*/
	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		
	}
	
	private EditNameDialog dialog = null;
	private void showCreateAlbumDialog()
	{
		if(dialog == null || dialog.isDismissed())
			dialog = new EditNameDialog(getActivity());
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
					showToast("请输入歌单名称");
					return;
				}
				createAlbum(dialog.getContent());
			}
		});
		
		WindowManager windowManager = getActivity().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		dialog.getWindow().setAttributes(lp);
	}
	
}
 
