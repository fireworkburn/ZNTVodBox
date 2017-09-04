/*  
* @Project: ZNTDianGe 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-5-26 上午12:55:22 
* @Version V1.1   
*/ 

package com.znt.diange.activity; 

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.znt.diange.R;
import com.znt.diange.entity.CommentInfor;
import com.znt.diange.entity.SongRecordInfor;
import com.znt.diange.view.CommentAdapter;
import com.znt.diange.view.listview.LJListView;
import com.znt.diange.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: SongCommentActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-5-26 上午12:55:22  
 */
public class SongCommentActivity extends BaseActivity implements OnClickListener,IXListViewListener, OnItemClickListener
{
	private LJListView listView = null;
	private TextView tvHint = null;
	private View viewHeader = null;
	private ImageView ivMusicCover = null;
	private TextView tvMusicName = null;
	private TextView musicArist = null;
	
	private CommentAdapter adapter = null;
	private SongRecordInfor songRecordInfor = null;
	
	private List<CommentInfor> commentList = new ArrayList<CommentInfor>();
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_song_comment);
		
		songRecordInfor = (SongRecordInfor)getIntent().getSerializableExtra("SongRecordInfor");
		
		setCenterString("评论(" + songRecordInfor.getCommentCount() +  ")");
		
		initHeaderViews();
		
		tvHint = (TextView)findViewById(R.id.tv_song_comment_hint);
		listView = (LJListView)findViewById(R.id.ptrl_song_comment);
		listView.getListView().setDivider(getResources().getDrawable(R.color.spacebar_1));
		listView.getListView().setDividerHeight(1);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();
		listView.setOnItemClickListener(this);
		listView.addHeader(viewHeader);
		
		adapter = new CommentAdapter(getActivity(), commentList);
		listView.setAdapter(adapter);
		listView.onFresh();
		
		getComments();
		
	}
	
	private void initHeaderViews()
	{
		viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.view_comment_header, null);
		ivMusicCover = (ImageView)viewHeader.findViewById(R.id.iv_comment_header_cover);
		tvMusicName = (TextView)viewHeader.findViewById(R.id.tv_comment_header_name);
		musicArist = (TextView)viewHeader.findViewById(R.id.tv_comment_header_artist);
		
		if(!TextUtils.isEmpty(songRecordInfor.getMediaCover()))
			Picasso.with(getActivity()).load(songRecordInfor.getMediaCover()).into(ivMusicCover);
		tvMusicName.setText(songRecordInfor.getMediaName());
		musicArist.setText(songRecordInfor.getArtist());
		
	}
	
	private void getComments()
	{
		CommentInfor infor = new CommentInfor();
		infor.setUserHead("http://img5.imgtn.bdimg.com/it/u=517984490,1644954693&fm=21&gp=0.jpg");
		infor.setUserName("马云");
		infor.setCommentTime("2016年5月26日");
		infor.setContent("这首歌很好听，每次听都会想起当初的18位兄弟，想起大家一起创业的日子，这一路上感谢各位兄弟");
		
		commentList.add(infor);
		commentList.add(infor);
		commentList.add(infor);
		commentList.add(infor);
		commentList.add(infor);
		commentList.add(infor);
		commentList.add(infor);
		commentList.add(infor);
		commentList.add(infor);
		commentList.add(infor);
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
 
