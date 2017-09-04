/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-1 下午4:04:34 
* @Version V1.1   
*/ 

package com.znt.vodbox.fragment; 

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.SearchMusicActivity;
import com.znt.vodbox.adapter.MusicAlbumAdapter;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.MusicAlbumInfor;
import com.znt.vodbox.entity.MyAlbumInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.HintView.OnHintListener;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: MusicFragment 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-1 下午4:04:34  
 */
public class MusicFragment extends BaseFragment implements OnHintListener, IXListViewListener, OnItemClickListener
{

	private View parentView = null;
	private LJListView listView = null;
	private EditText etSearch = null;
	private View viewHeader = null;
	
	private OnHintListener onHintListener = null;
	private HttpFactory httpfactory = null;
	private MusicAlbumAdapter adapter = null;
	private List<MusicAlbumInfor> albumList = new ArrayList<MusicAlbumInfor>();
	
	private int pageNum = 1;
	private boolean isPrepared = false;
	private boolean isLoadFinish = false;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.GET_SYSTEM_ALBUMS_START)
			{
				
			}
			else if(msg.what == HttpMsg.GET_SYSTEM_ALBUMS_SUCCESS)
			{
				
				List<MusicAlbumInfor> tempList = (List<MusicAlbumInfor>)msg.obj;
				
				if(pageNum == 1)
				{
					if(tempList.size() == 0)
					{
						listView.showFootView(false);
						showNoDataView("未找到任何歌单");
					}
						
					albumList.clear();
				}
				if(tempList.size() < 20)
				{
					listView.showFootView(false);
					isLoadFinish = true;
				}
				else
				{
					listView.showFootView(true);
					isLoadFinish = false;
				}
					
				albumList.addAll(tempList);
				/*MusicAlbumInfor infor = new MusicAlbumInfor();
				albumList.add(infor);*/
				pageNum ++;
				
				adapter.notifyDataSetChanged();
				onLoad(0);
			}
			else if(msg.what == HttpMsg.GET_SYSTEM_ALBUMS_FAIL || msg.what == HttpMsg.GET_PARENT_MUSIC_ALBUM_FAIL)
			{
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "获取数据失败";
				showToast(error);
				showLoadingView(false);
				hideHintView();
				showRefreshView(onHintListener);
				onLoad(0);
				isLoadFinish = false;
			}
			/*else if(msg.what == HttpMsg.GET_PARENT_MUSIC_ALBUM_START)
			{
				
			}*/
			else if(msg.what == HttpMsg.GET_PARENT_MUSIC_ALBUM_SUCCESS)
			{
				
				albumList.clear();
				MyAlbumInfor myAlbumInfor = (MyAlbumInfor)msg.obj;
				albumList.addAll(myAlbumInfor.getCreateAlbums());
				albumList.addAll(myAlbumInfor.getCollectAlbums());
				pageNum = 1;
				isLoadFinish = true;
				listView.showFootView(false);
					
				adapter.notifyDataSetChanged();
				onLoad(0);
			}
			/*else if(msg.what == HttpMsg.GET_PARENT_MUSIC_ALBUM_FAIL)
			{
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "获取数据失败";
				showToast(error);
				showLoadingView(false);
				showRefreshView(onHintListener);
				onLoad(0);
				isLoadFinish = false;
			}*/
		};
	};
	
	public MusicFragment()
	{
		
	}
	public static MusicFragment getInstance()
	{
		return new MusicFragment();
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
			parentView = getContentView(R.layout.fragment_music);
			
			showReturnView(false);
			
			viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.view_music_album_header, null);
			listView = (LJListView)parentView.findViewById(R.id.ptrl_music_album);
			etSearch = (EditText)viewHeader.findViewById(R.id.et_library_music_search);
			
			if(getLocalData().isAdminUser())
			{
				etSearch.setVisibility(View.VISIBLE);
				setCenterString("系统推荐");
			}
			else
			{
				etSearch.setVisibility(View.GONE);
				setCenterString("总部歌单");
			}
			
			listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
			listView.getListView().setDividerHeight(1);
			listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
			listView.setPullRefreshEnable(true);
			listView.setIsAnimation(true); 
			listView.setXListViewListener(this);
			listView.showFootView(false);
			listView.setRefreshTime();
			listView.setOnItemClickListener(this);
			listView.addHeader(viewHeader);
			
			adapter = new MusicAlbumAdapter(getActivity(), albumList);
			listView.setAdapter(adapter);
			
			httpfactory = new HttpFactory(getActivity(), handler);
			
			onHintListener = this;
			
			isPrepared = true;
			lazyLoad();
			
			etSearch.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					// TODO Auto-generated method stub
					ViewUtils.startActivity(getActivity(), SearchMusicActivity.class, null);
				}
			});
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
		if(Constant.isAlbumUpdated)
			getMusicAlbums();
		// TODO Auto-generated method stub
		super.onResume();
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

	public void getMusicAlbums()
	{
		//httpfactory.getMusicAlbums();
		if(getLocalData().isAdminUser())
			httpfactory.getSystemAlbums("" + pageNum);
		else
			httpfactory.getParentMusicAlbums();
	}
	/**
	*callbacks
	*/
	@Override
	public void onHintRefresh() 
	{
		// TODO Auto-generated method stub
		listView.onFresh();
	}
	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		// TODO Auto-generated method stub
		
	}
	/**
	*callbacks
	*/
	@Override
	public void onRefresh() 
	{
		// TODO Auto-generated method stub
		pageNum = 1;
		getMusicAlbums();
	}
	/**
	*callbacks
	*/
	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		if(!isLoadFinish)
			getMusicAlbums();
		else
		{
			onLoad(0);
			listView.showFootView(false);
		}
	}
	
}
 
