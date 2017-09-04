
package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qhkj.weishi.view.drag.DragGrid;
import com.qhkj.weishi.view.drag.OtherGridView;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.DragAdapter;
import com.znt.vodbox.adapter.OtherAdapter;
import com.znt.vodbox.entity.MusicAlbumInfor;
import com.znt.vodbox.entity.MyAlbumInfor;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.entity.SubPlanInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;

/** 
 * @ClassName: ChannelManagerActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-11-24 下午3:39:49  
 */
public class ChannelActivity extends BaseActivity implements OnItemClickListener, OnClickListener
{

	/** 用户栏目的GRIDVIEW */
	private DragGrid userGridView;
	/** 其它栏目的GRIDVIEW */
	private OtherGridView otherGridView;
	
	/** 用户栏目对应的适配器，可以拖动 */
	DragAdapter userAdapter;
	/** 其它栏目对应的适配器 */
	OtherAdapter otherAdapter;
	
	private HttpFactory httpfactory = null;
	
	private List<TextView> categoryViews = new ArrayList<TextView>();
	
	private SubPlanInfor subPlanInfor = null;
	private boolean isEdit = false;
	private String planId = "";
	/** 其它栏目列表 */
	List<MusicAlbumInfor> otherChannelList = new ArrayList<MusicAlbumInfor>();
	/** 用户栏目列表 */
	List<MusicAlbumInfor> userChannelList = new ArrayList<MusicAlbumInfor>();
	/** 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。 */	
	boolean isMove = false;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.GET_MUSIC_ALBUM_START)
			{
				showLoadingView(true);
				hideHintView();
			}
			else if(msg.what == HttpMsg.GET_MUSIC_ALBUM_SUCCESS)
			{
				
				MyAlbumInfor myAlbumInfor = (MyAlbumInfor)msg.obj;
				
				otherChannelList.clear();
				otherChannelList.addAll(myAlbumInfor.getCreateAlbums());
				otherChannelList.addAll(myAlbumInfor.getCollectAlbums());
				
				filterAlbums();
				
				otherAdapter.notifyDataSetChanged();
				showLoadingView(false);
				hideHintView();
			}
			else if(msg.what == HttpMsg.GET_MUSIC_ALBUM_FAIL)
			{
				showLoadingView(false);
				//showRefreshView(onHintListener);
			}
			else if(msg.what == HttpMsg.EDIT_PLAN_START)
			{
				
			}
			else if(msg.what == HttpMsg.EDIT_PLAN_SUCCESS)
			{
				showToast("操作成功");
				finishAndFeedBack();
			}
			else if(msg.what == HttpMsg.EDIT_PLAN_FAIL)
			{
				showToast("操作失败");
			}
		};
	};
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_channel);
		
		setCenterString("歌单定制");
		
		setRightText("完成");
		
		subPlanInfor = (SubPlanInfor)getIntent().getSerializableExtra("SubPlanInfor");
		isEdit = getIntent().getBooleanExtra("IS_EDIT", false);
		planId = getIntent().getStringExtra("PLAN_ID");
		if(subPlanInfor != null)
		{
			userChannelList.addAll(subPlanInfor.getAlbumList());
		}
		else
			subPlanInfor = new SubPlanInfor();
		
		initView();
		initData();
		
	}

	private void filterAlbums()
	{
		int size = userChannelList.size();
		if(size > 0)
		{
			for(int i=0;i<size;i++)
			{
				MusicAlbumInfor infor = userChannelList.get(i);
				//otherChannelList.remove(infor);
				for(int j=0;j<otherChannelList.size();j++)
				{
					if(otherChannelList.get(j).getAlbumId().equals(infor.getAlbumId()))
						otherChannelList.remove(j);
				}
			}
		}
			
	}
	
	/** 初始化数据*/
	private void initData() 
	{
	    /*userChannelList = DBManager.newInstance(getActivity()).getMusicAlbumInfors(AlbumType.User);
	    otherChannelList = DBManager.newInstance(getActivity()).getMusicAlbumInfors(AlbumType.System);*/
	    userAdapter = new DragAdapter(this, userChannelList);
	    userGridView.setAdapter(userAdapter);
	    
	    otherAdapter = new OtherAdapter(this, otherChannelList);
	    otherGridView.setAdapter(this.otherAdapter);
	    //设置GRIDVIEW的ITEM的点击监听
	    otherGridView.setOnItemClickListener(this);
	    userGridView.setOnItemClickListener(this);
	    
	    httpfactory = new HttpFactory(getActivity(), handler);
	    httpfactory.getMusicAlbums();
	}
	
	/** 初始化布局*/
	private void initView() 
	{
		userGridView = (DragGrid) findViewById(R.id.dg_channel_user);
		otherGridView = (OtherGridView) findViewById(R.id.dg_channel_other);
		
		getRightView().setOnClickListener(this);
		showRightImageView(false);
	}

	private void clickChannelView(View view)
	{
		int size = categoryViews.size();
		for(int i=0;i<size;i++)
		{
			TextView tvChannel = categoryViews.get(i);
			if((Integer)view.getTag() == (Integer)tvChannel.getTag())
			{
				//显示当前频道的内容
				tvChannel.setBackgroundResource(R.drawable.style_channel_bg);
				tvChannel.setTextColor(getResources().getColor(R.color.white));
				
				refreshChannels(i);
			}
			else
			{
				tvChannel.setBackgroundResource(R.color.transparent);
				tvChannel.setTextColor(getResources().getColor(R.color.text_black_on));
			}
		}
	}
	
	private void refreshChannels(int index)
	{
		
		 otherAdapter.notifyDataSetChanged();
	}
	
	/** GRIDVIEW对应的ITEM点击监听接口  */
	@Override
	public void onItemClick(AdapterView<?> parent, final View view, final int position,long id) {
		//如果点击的时候，之前动画还没结束，那么就让点击事件无效
		if(isMove)
		{
			return;
		}
		switch (parent.getId())
		{
		case R.id.dg_channel_user:
			//position为 0，1 的不可以进行任何操作
			//if (position != 0) 
			{
				/*if(userChannelList.size() == 1)
				{
					showToast("请至少保留一个频道哦~");
					return ;
				}*/
				final ImageView moveImageView = getView(view);
				if (moveImageView != null) 
				{
					TextView newTextView = (TextView) view.findViewById(R.id.tv_channel_name);
					final int[] startLocation = new int[2];
					newTextView.getLocationInWindow(startLocation);
					final MusicAlbumInfor channel = ((DragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
					otherAdapter.setVisible(false);
					//添加到最后一个
					otherAdapter.addItem(channel);
					new Handler().postDelayed(new Runnable() 
					{
						public void run()
						{
							try 
							{
								int[] endLocation = new int[2];
								//获取终点的坐标
								otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
								MoveAnim(moveImageView, startLocation , endLocation, channel,userGridView);
								userAdapter.setRemove(position);
							} 
							catch (Exception localException)
							{
								
							}
						}
					}, 50L);
				}
			}
			break;
		case R.id.dg_channel_other:
			final ImageView moveImageView = getView(view);
			if (moveImageView != null)
			{
				TextView newTextView = (TextView) view.findViewById(R.id.tv_channel_name);
				final int[] startLocation = new int[2];
				newTextView.getLocationInWindow(startLocation);
				final MusicAlbumInfor channel = ((OtherAdapter) parent.getAdapter()).getItem(position);
				userAdapter.setVisible(false);
				//添加到最后一个
				userAdapter.addItem(channel);
				new Handler().postDelayed(new Runnable() 
				{
					public void run() 
					{
						try 
						{
							int[] endLocation = new int[2];
							//获取终点的坐标
							userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
							MoveAnim(moveImageView, startLocation , endLocation, channel,otherGridView);
							otherAdapter.setRemove(position);
						} 
						catch (Exception localException) 
						{
							
						}
					}
				}, 50L);
			}
			break;
		default:
			break;
		}
	}
	/**
	 * 点击ITEM移动动画
	 * @param moveView
	 * @param startLocation
	 * @param endLocation
	 * @param moveChannel
	 * @param clickGridView
	 */
	private void MoveAnim(View moveView, int[] startLocation,int[] endLocation, final MusicAlbumInfor moveChannel,
			final GridView clickGridView) 
	{
		int[] initLocation = new int[2];
		//获取传递过来的VIEW的坐标
		moveView.getLocationInWindow(initLocation);
		//得到要移动的VIEW,并放入对应的容器中
		final ViewGroup moveViewGroup = getMoveViewGroup();
		final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
		//创建移动动画
		TranslateAnimation moveAnimation = new TranslateAnimation(
				startLocation[0], endLocation[0], startLocation[1],
				endLocation[1]);
		moveAnimation.setDuration(300L);//动画时间
		//动画配置
		AnimationSet moveAnimationSet = new AnimationSet(true);
		moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
		moveAnimationSet.addAnimation(moveAnimation);
		mMoveView.startAnimation(moveAnimationSet);
		moveAnimationSet.setAnimationListener(new AnimationListener() 
		{
			
			@Override
			public void onAnimationStart(Animation animation) 
			{
				isMove = true;
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) 
			{
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) 
			{
				moveViewGroup.removeView(mMoveView);
				// instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
				if (clickGridView instanceof DragGrid) 
				{
					otherAdapter.setVisible(true);
					otherAdapter.notifyDataSetChanged();
					userAdapter.remove();
				}
				else
				{
					userAdapter.setVisible(true);
					userAdapter.notifyDataSetChanged();
					otherAdapter.remove();
				}
				isMove = false;
			}
		});
	}
	
	/**
	 * 获取移动的VIEW，放入对应ViewGroup布局容器
	 * @param viewGroup
	 * @param view
	 * @param initLocation
	 * @return
	 */
	private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) 
	{
		int x = initLocation[0];
		int y = initLocation[1];
		viewGroup.addView(view);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = x;
		mLayoutParams.topMargin = y;
		view.setLayoutParams(mLayoutParams);
		return view;
	}
	
	/**
	 * 创建移动的ITEM对应的ViewGroup布局容器
	 */
	private ViewGroup getMoveViewGroup() 
	{
		ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
		LinearLayout moveLinearLayout = new LinearLayout(this);
		moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		moveViewGroup.addView(moveLinearLayout);
		return moveLinearLayout;
	}
	
	/**
	 * 获取点击的Item的对应View，
	 * @param view
	 * @return
	 */
	private ImageView getView(View view) 
	{
		view.destroyDrawingCache();
		view.setDrawingCacheEnabled(true);
		Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		ImageView iv = new ImageView(this);
		iv.setImageBitmap(cache);
		return iv;
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == getRightView())
		{
			if(subPlanInfor == null)
				subPlanInfor = new SubPlanInfor();
			subPlanInfor.setAlbumList(userChannelList);
			if(isEdit && !TextUtils.isEmpty(planId))
			{
				/*httpfactory.editPlan(subPlanInfor.getStartTime(), subPlanInfor.getEndTime()
						, subPlanInfor.getPlanAlbumIds(), planId, subPlanInfor.getId());*/
/*				httpfactory.editPlan(planInfor.getAllStartTimes(), planInfor.getAllEndTimes()
						, planInfor.getAllCategoryIds(), planInfor.getPlanId(), planInfor.getAllScheduleIds());
*/			}
			else
				finishAndFeedBack();
		}
		else
			clickChannelView(v);
	}
	
	private void finishAndFeedBack()
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("SubPlanInfor", subPlanInfor);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
	}
}
 
