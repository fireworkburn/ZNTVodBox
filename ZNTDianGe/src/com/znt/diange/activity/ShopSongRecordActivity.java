/*  
* @Project: ZNTDianGe 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-5-25 下午11:27:49 
* @Version V1.1   
*/ 

package com.znt.diange.activity; 

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.entity.SongRecordInfor;
import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.UserInfor;
import com.znt.diange.utils.ViewUtils;
import com.znt.diange.view.SongRecordAdapter;
import com.znt.diange.view.listview.LJListView;
import com.znt.diange.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: ShopSongRecordActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-5-25 下午11:27:49  
 */
public class ShopSongRecordActivity extends BaseActivity implements OnClickListener,IXListViewListener, OnItemClickListener
{
	
	private LJListView listView = null;
	private TextView tvHint = null;
	
	private SongRecordAdapter adapter = null;
	private DeviceInfor devInfor = null;
	
	private List<SongRecordInfor> recordList = new ArrayList<SongRecordInfor>();
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_shop_song_record);
		
		devInfor = (DeviceInfor)getIntent().getSerializableExtra("DeviceInfor");
		
		setCenterString(devInfor.getName());
		
		tvHint = (TextView)findViewById(R.id.tv_shop_song_record);
		listView = (LJListView)findViewById(R.id.ptrl_shop_song_record);
		listView.getListView().setDivider(getResources().getDrawable(R.color.spacebar_1));
		listView.getListView().setDividerHeight(1);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();
		listView.setOnItemClickListener(this);
		
		adapter = new SongRecordAdapter(getActivity(), recordList);
		listView.setAdapter(adapter);
		listView.onFresh();
		
	}
	
	private void getRecordDatas()
	{
		
		recordList.clear();
		
		SongRecordInfor songRecordInfor = new SongRecordInfor();
		songRecordInfor.setMediaName("我的好兄弟");
		songRecordInfor.setMediaUrl("http://other.web.ra01.sycdn.kuwo.cn/resource/n2/128/90/55/2160085839.mp3");
		songRecordInfor.setMediaCover("http://img154.ph.126.net/WbFPQCN97OQeZ9Gd3HpiXQ==/2272066012010034092.jpg");
		songRecordInfor.setCommentCount("99");
		songRecordInfor.setPlayMsg("高进与小沈阳演唱的《我的好兄弟》可以说是男声对唱的典范之作，传唱度极高。好兄弟在人生道路上互相力挺，互相支持都在感人的唱词中生动的表达出来，歌曲因诠释出兄弟间最真挚的情谊，而成为KTV好友聚会时的必点曲目");
		songRecordInfor.setPraiseCount("188");
		
		UserInfor userInfor = new UserInfor();
		userInfor.setHead("http://img5.imgtn.bdimg.com/it/u=517984490,1644954693&fm=21&gp=0.jpg");
		userInfor.setUserName("马云");
		songRecordInfor.setUserInfor(userInfor);
		
		recordList.add(songRecordInfor);
		recordList.add(songRecordInfor);
		recordList.add(songRecordInfor);
		recordList.add(songRecordInfor);
		recordList.add(songRecordInfor);
		recordList.add(songRecordInfor);
		recordList.add(songRecordInfor);
		recordList.add(songRecordInfor);
		recordList.add(songRecordInfor);
		recordList.add(songRecordInfor);
		
		adapter.notifyDataSetChanged();
		
		onLoad(0);
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) 
	{
		// TODO Auto-generated method stub
		if(pos > 0)
			pos = pos - 1;
		SongRecordInfor tempInfor = recordList.get(pos);
		Bundle bundle = new Bundle();
		bundle.putSerializable("SongRecordInfor", tempInfor);
		ViewUtils.startActivity(getActivity(), SongCommentActivity.class, bundle);
	}

	/**
	*callbacks
	*/
	@Override
	public void onRefresh() 
	{
		// TODO Auto-generated method stub
		getRecordDatas();
	}

	/**
	*callbacks
	*/
	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	*callbacks
	*/
	@Override
	public void onClick(View arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	
}
 
