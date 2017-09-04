/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-13 上午1:08:40 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import qh2.view.scroller.ArrayWheelAdapter;
import qh2.view.scroller.OnWheelChangedListener;
import qh2.view.scroller.OnWheelScrollListener;
import qh2.view.scroller.WheelView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.AlbumSelectAdapter;
import com.znt.vodbox.dialog.CountEditDialog;
import com.znt.vodbox.entity.MusicAlbumInfor;
import com.znt.vodbox.entity.MyAlbumInfor;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.entity.SubPlanInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.StringUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.ItemTextView;
import com.znt.vodbox.view.SwitchButton;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
 * @ClassName: PlanCreateActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-13 上午1:08:40  
 */
public class PlanCreateActivity extends BaseActivity implements OnClickListener
, OnItemClickListener, IXListViewListener
{

	
	private View headerView = null;
	private ListView listView = null;
	private WheelView wvHourStart = null;
	private WheelView wvMinStart = null;
	private WheelView wvHourEnd = null;
	private WheelView wvMinEnd = null;
	private TextView tvTimeHint = null;
	private TextView tvAddAlbum = null;
	private TextView tvPlanTime = null;
	private TextView tvHint = null;
	private ProgressBar pbHint = null;

	private ItemTextView itvInsetCount = null;
	private ItemTextView itvInsertMusic = null;
	private SwitchButton switchButtonBroad = null;
	
	private HttpFactory httpFactory = null;
	private AlbumSelectAdapter adapter = null;
	
	private boolean wheelScrolled = false;
	private int selectedHourStart, selectedMinStart,selectedHourEnd, selectedMinEnd;
	private final String TAG_HOUR = "TYPE_HOUR";
	private final String TAG_MIN = "TYPE_MIN";
	
	private String startTimes = "";
	private String endTimes = "";
	private String categoryIds = "";
	private String categoryNames = "";
	private String loopAddNum = "";
	private String loopMusic = "";
	private String cycleType = "";
	private PlanInfor planInfor = null;
	private boolean isEdit = false;
	private boolean isGetAlbumRunning = false;
	
	private List<MusicAlbumInfor> albumList = new ArrayList<MusicAlbumInfor>();
	private List<MusicAlbumInfor> selectAlbumList = new ArrayList<MusicAlbumInfor>();
	
	public static final String[] DAY_STRING = { "00", "01", "02", "03", "04", "05",
		"06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
		"17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
		"28", "29", "30", "31" , "32" , "33" , "34" , "35" , "36" , "37" , 
		"38" , "39" , "40" , "41" , "42" , "43" , "44" , "45" , "46" , "47" 
		, "48" , "49" , "50" , "51" , "52" , "53" , "54" , "55" , "56" , "57" 
		, "58" , "59" };
	public static final String[] MONTH_STRING = { "00", "01", "02", "03", "04", "05",
		"06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
		"17", "18", "19", "20", "21", "22", "23"};
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.EDIT_PLAN_START)
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
			else if(msg.what == HttpMsg.GET_MUSIC_ALBUM_START)
			{
				isGetAlbumRunning = true;
				showLoadingView(null);
				listView.setVisibility(View.GONE);
			}
			else if(msg.what == HttpMsg.GET_MUSIC_ALBUM_SUCCESS)
			{
				albumList.clear();
				MyAlbumInfor myAlbumInfor = (MyAlbumInfor)msg.obj;
				albumList.addAll(myAlbumInfor.getCreateAlbums());
				albumList.addAll(myAlbumInfor.getCollectAlbums());
				
				if(albumList.size() == 0)
				{
					showLoadingView("您还没有创建歌单，先去创建歌单吧");
				}
				else
				{
					hideLoadingView();
					listView.setVisibility(View.VISIBLE);
				}
				
				adapter.setSelectedList(selectAlbumList);
				isGetAlbumRunning = false;
				onLoad(0);
			}
			else if(msg.what == HttpMsg.GET_MUSIC_ALBUM_FAIL)
			{
				String error = (String) msg.obj;
				if(TextUtils.isEmpty(error))
					error = "获取歌单列表失败，下拉重试";
				showToast(error);
				isGetAlbumRunning = false;
				onLoad(0);
				hideLoadingView();
				listView.setVisibility(View.VISIBLE);
			}
			else if(msg.what == HttpMsg.GET_PARENT_MUSIC_ALBUM_SUCCESS)
			{
				albumList.clear();
				MyAlbumInfor myAlbumInfor = (MyAlbumInfor)msg.obj;
				albumList.addAll(myAlbumInfor.getCreateAlbums());
				albumList.addAll(myAlbumInfor.getCollectAlbums());
				
				if(albumList.size() == 0)
				{
					showLoadingView("您还没有创建歌单，先去创建歌单吧");
				}
				else
				{
					hideLoadingView();
					listView.setVisibility(View.VISIBLE);
				}
				
				adapter.setSelectedList(selectAlbumList);
				isGetAlbumRunning = false;
				onLoad(0);
			}
			else if(msg.what == HttpMsg.NO_NET_WORK_CONNECT)
			{
				isGetAlbumRunning = false;
				showToast("无网络连接");
				onLoad(0);
			}
		};
	};
	
	private void finishAndFeedBack()
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("PlanInfor", planInfor);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_plan_create);
		
		headerView = LayoutInflater.from(getActivity()).inflate(R.layout.view_plan_create_header_1, null);
		wvHourStart = (WheelView)findViewById(R.id.vh_time_select_hour);
		wvMinStart = (WheelView)findViewById(R.id.vh_time_select_min);
		wvHourEnd = (WheelView)findViewById(R.id.vh_time_select_hour_end);
		wvMinEnd = (WheelView)findViewById(R.id.vh_time_select_min_end);
		tvTimeHint = (TextView)findViewById(R.id.tv_plan_create_time_hint);
		tvAddAlbum = (TextView)findViewById(R.id.tv_plan_create_album);
		tvPlanTime = (TextView)findViewById(R.id.tv_plan_create_plan_time);
		tvHint = (TextView)findViewById(R.id.tv_plan_create_hint);
		pbHint = (ProgressBar)findViewById(R.id.pb_plan_create_hint);
		
		switchButtonBroad = (SwitchButton)headerView.findViewById(R.id.sb_plan_detail_broadcast);
		itvInsetCount = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_broadcast_count);
		itvInsertMusic = (ItemTextView)headerView.findViewById(R.id.itv_plan_detail_broadcast_music);
		
		itvInsetCount.getFirstView().setText("播放间隔");
		itvInsertMusic.getFirstView().setText("播放音乐");
		itvInsetCount.showMoreButton(true);
		itvInsertMusic.showMoreButton(true);
		itvInsetCount.getBgView().setOnClickListener(this);
		itvInsertMusic.getBgView().setOnClickListener(this);
		
		listView = (ListView)findViewById(R.id.ptrl_plan_create);
		listView.addHeaderView(headerView);
		
		switchButtonBroad.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) 
			{
				// TODO Auto-generated method stub
				showAdverMusicSet(arg1);
			}
		});
		
		/*listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		listView.getListView().setDividerHeight(1);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();*/
		//listView.addHeader(headerView);
		adapter = new AlbumSelectAdapter(getActivity(), albumList);
		listView.setAdapter(adapter);
		
		initWheelViews();
		
		showRightImageView(false);
		
		httpFactory = new HttpFactory(getActivity(), handler);
		planInfor = (PlanInfor)getIntent().getSerializableExtra("PlanInfor");
		isEdit = getIntent().getBooleanExtra("IS_EDIT", false);
		
		if(!isEdit)
		{
			setCenterString("创建计划");
			setRightText("确认添加");
			/*if(planInfor == null)
				planInfor = new PlanInfor();*/
			
			String curHour = DateUtils.getHour();
			String curMin = DateUtils.getTime();
			int curHourInt = 0;
			int curMinInt = 0;
			if(!TextUtils.isEmpty(curHour))
				curHourInt = Integer.parseInt(curHour);
			if(!TextUtils.isEmpty(curMin))
				curMinInt = Integer.parseInt(curMin);
			initStartData(getHourFromTime(curHourInt), getMinFromTime(curMinInt));
			initEndData(getHourFromTime(curHourInt), getMinFromTime(curMinInt));
			switchButtonBroad.setChecked(false);
		}
		else
		{
			setCenterString("编辑计划");
			setRightText("完成编辑");
			showCurPlanInfor();
		}
			
		tvAddAlbum.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				if(isEdit)
					bundle.putSerializable("SubPlanInfor", planInfor.getSelelctPlanInfor());
				else
					bundle.putSerializable("SubPlanInfor", null);
				ViewUtils.startActivity(getActivity(), ChannelActivity.class, bundle, 1);
			}
		});
		getRightView().setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				SubPlanInfor tempInfor = getSubPlanInfor();
				if(tempInfor == null)
					return;
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				if(!isEdit)
					planInfor.addSubPlanInfor(tempInfor);
				else
					planInfor.updateSelect(tempInfor);
				bundle.putSerializable("PlanInfor", planInfor);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		getMusicAlbums();
		//listView.onFresh();
	}
	
	private void showAdverMusicSet(boolean isShow)
	{
		if(isShow)
		{
			itvInsetCount.setVisibility(View.VISIBLE);
			itvInsertMusic.setVisibility(View.VISIBLE);
			if(planInfor != null && planInfor.getSelelctPlanInfor()!= null)
				planInfor.getSelelctPlanInfor().setCycleType("1");
		}
		else
		{
			itvInsetCount.setVisibility(View.GONE);
			itvInsertMusic.setVisibility(View.GONE);
			if(planInfor != null && planInfor.getSelelctPlanInfor()!= null)
				planInfor.getSelelctPlanInfor().setCycleType("0");
		}
	}
	
	private void showCurPlanInfor()
	{
		SubPlanInfor subPlanInfor = planInfor.getSelelctPlanInfor();
		if(subPlanInfor != null)
		{
			String sTime = subPlanInfor.getStartTime();
			String eTime = subPlanInfor.getEndTime();
			
			String[] sTimes = StringUtils.splitUrls(sTime, ":");
			String[] eTimes = StringUtils.splitUrls(eTime, ":");
			
			int sHour = Integer.parseInt(sTimes[0]);
			int sMin = Integer.parseInt(sTimes[1]);
			int eHour = Integer.parseInt(eTimes[0]);
			int eMin = Integer.parseInt(eTimes[1]);
			
			initStartData(sHour, sMin);
			initEndData(eHour, eMin);
			
			tvAddAlbum.setText(subPlanInfor.getPlanAlbumName());
			selectAlbumList.addAll(subPlanInfor.getAlbumList());
			
			loopAddNum = planInfor.getSelelctPlanInfor().getLoopAddNum();
			loopMusic = planInfor.getSelelctPlanInfor().getLoopMusicInfoId();
			cycleType = planInfor.getSelelctPlanInfor().getCycleType();
			switchButtonBroad.setChecked(cycleType.equals("1"));
			showAdverMusicSet(cycleType.equals("1"));
			
			if(!TextUtils.isEmpty(loopAddNum) && !TextUtils.isEmpty(planInfor.getSelelctPlanInfor().getLoopMusicName()))
			{
				switchButtonBroad.setChecked(true);
				itvInsetCount.getSecondView().setText("每隔 " + loopAddNum + " 首插播一次");
				itvInsertMusic.getSecondView().setText(planInfor.getSelelctPlanInfor().getLoopMusicName());
			}
			else
				switchButtonBroad.setChecked(false);
		}
	}
	
	private void showLoadingView(String textView)
	{
		if(TextUtils.isEmpty(textView))
		{
			pbHint.setVisibility(View.VISIBLE);
		}
		else
		{
			pbHint.setVisibility(View.GONE);
			tvHint.setText(textView);
		}
		tvHint.setVisibility(View.VISIBLE);
		
	}
	private void hideLoadingView()
	{
		tvHint.setVisibility(View.GONE);
		pbHint.setVisibility(View.GONE);
	}
	
	private void onLoad(int updateCount) 
	{
		/*listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();*/
	}
	
	private void initWheelViews()
	{
		wvHourStart.setTag(TAG_HOUR);
		wvHourStart.setAdapter(new ArrayWheelAdapter<String>(MONTH_STRING));
		//wheel_month.setCurrentItem(wheel_month.getCurrentVal("02"));
		wvHourStart.setCyclic(true);
		//wvHour.setLabel("��");
		wvHourStart.addChangingListener(startWheelChangeListener);
		wvHourStart.addScrollingListener(wheelScrolledListener, null);
		
		wvMinStart.setAdapter(new ArrayWheelAdapter<String>(DAY_STRING));
		//wvMin.setLabel("��");
		wvMinStart.setTag(TAG_MIN);
		wvMinStart.setCyclic(true);
		wvMinStart.addChangingListener(startWheelChangeListener);
		wvMinStart.addScrollingListener(wheelScrolledListener, null);
		
		wvHourEnd.setTag(TAG_HOUR);
		wvHourEnd.setAdapter(new ArrayWheelAdapter<String>(MONTH_STRING));
		//wheel_month.setCurrentItem(wheel_month.getCurrentVal("02"));
		wvHourEnd.setCyclic(true);
		//wvHour.setLabel("��");
		wvHourEnd.addChangingListener(endWheelChangeListener);
		wvHourEnd.addScrollingListener(wheelScrolledListener, null);
		
		wvMinEnd.setAdapter(new ArrayWheelAdapter<String>(DAY_STRING));
		//wvMin.setLabel("��");
		wvMinEnd.setTag(TAG_MIN);
		wvMinEnd.setCyclic(true);
		wvMinEnd.addChangingListener(endWheelChangeListener);
		wvMinEnd.addScrollingListener(wheelScrolledListener, null);
	}
	
	private SubPlanInfor getSubPlanInfor()
	{
		SubPlanInfor tempInfor = new SubPlanInfor();
		if(startTimes.equals(endTimes))
		{
			showToast("起始时间不能一样的哦");
			return null;
		}
		if(TextUtils.isEmpty(startTimes))
		{
			showToast("请设置起始时间");
			return null;
		}
		if(TextUtils.isEmpty(endTimes))
		{
			showToast("请设置结束时间");
			return null;
		}
		
		if(!planInfor.checkPlanTime(startTimes, endTimes))
		{
			showToast("当前时间与计划时间有重叠");
			return null;
		}
		
		if(adapter.getSelectedList().size() == 0)
		{
			showToast("请至少添加一个歌单");
			return null;
		}
		tempInfor.setStartTime(startTimes);
		tempInfor.setEndTime(endTimes);
		tempInfor.setAlbumList(adapter.getSelectedList());
		
		if(switchButtonBroad.isChecked())
		{
			if(!TextUtils.isEmpty(loopAddNum))
				tempInfor.setLoopAddNum(loopAddNum);
			else
			{
				showToast("请输入插播间隔数");
				return null;
			}
			tempInfor.setCycleType(cycleType);
			if(!TextUtils.isEmpty(loopMusic))
				tempInfor.setLoopMusicInfoId(loopMusic);
			else
			{
				showToast("请选择要插播的歌曲");
				return null;
			}
		}
		else
		{
			tempInfor.setLoopAddNum("0");
			tempInfor.setCycleType("0");
			tempInfor.setLoopMusicInfoId("0");
		}
		
		
		return tempInfor;
	}
	
	private void initStartData(int hour, int min)
	{
		wvHourStart.setCurrentItem(hour);
		wvMinStart.setCurrentItem(min);
		
		updateStartTime(hour, min);
		
	}
	private void initEndData(int hour, int min)
	{
		wvHourEnd.setCurrentItem(hour);
		wvMinEnd.setCurrentItem(min);
		
		updateEndTime(hour, min);
		
	}
	
	private int getHourFromTime(int time)
	{
		if(time < 24)
			return time;
		else
			return time % 24;
	}
	private int getMinFromTime(int time)
	{
		if(time < 60)
			return time;
		else
			return time % 60;
	}
	
	private void updateStartTime(int hour, int min)
	{
		if(hour > 0)
			startTimes = getStringTwo(hour) + ":" + getStringTwo(min);
		else if(min > 0)
			startTimes = "00:" + getStringTwo(min);
		if(hour == 0 && min == 0)
			startTimes = "00:00";
		
		if(!planInfor.checkPlanTime(startTimes, endTimes))
			tvTimeHint.setVisibility(View.VISIBLE);
		else
			tvTimeHint.setVisibility(View.GONE);
		
		tvPlanTime.setText(startTimes + " 到 " + endTimes);
		
	}
	private void updateEndTime(int hour, int min)
	{
		if(hour > 0)
			endTimes = getStringTwo(hour) + ":" + getStringTwo(min);
		else if(min > 0)
			endTimes = "00:" + getStringTwo(min);
		if(hour == 0 && min == 0)
			endTimes = "00:00";
		if(!planInfor.checkPlanTime(startTimes, endTimes))
			tvTimeHint.setVisibility(View.VISIBLE);
		else
			tvTimeHint.setVisibility(View.GONE);
		
		tvPlanTime.setText(startTimes + " 到 " + endTimes);
		
	}
	private String getStringTwo(int orgNum)
	{
		if(orgNum >= 0 && orgNum < 10)
			return "0" + orgNum;
		return orgNum + "";	
	}
	
	OnWheelScrollListener wheelScrolledListener = new OnWheelScrollListener() 
	{
		public void onScrollingStarted(WheelView wheel) 
		{
			wheelScrolled = true;
		}
		@Override
		public void onScrollingFinished(WheelView wheel) 
		{
			// TODO Auto-generated method stub
			//String tag = wheel.getTag().toString();
			wheelScrolled = false;
		}
	};

	// Wheel changed listener
	private OnWheelChangedListener startWheelChangeListener = new OnWheelChangedListener()
	{
		@Override
		public void onLayChanged(WheelView wheel, int oldValue, int newValue,
				LinearLayout layout) 
		{
			// TODO Auto-generated method stub
			
			if(wheel.getTag().toString().equals(TAG_HOUR))
				selectedHourStart = newValue;
			else if(wheel.getTag().toString().equals(TAG_MIN))
				selectedMinStart = newValue;
			
			updateStartTime(selectedHourStart, selectedMinStart);
			
		}
	};
	private OnWheelChangedListener endWheelChangeListener = new OnWheelChangedListener()
	{
		@Override
		public void onLayChanged(WheelView wheel, int oldValue, int newValue,
				LinearLayout layout) 
		{
			// TODO Auto-generated method stub
			
			if(wheel.getTag().toString().equals(TAG_HOUR))
				selectedHourEnd = newValue;
			else if(wheel.getTag().toString().equals(TAG_MIN))
				selectedMinEnd = newValue;
			
			updateEndTime(selectedHourEnd, selectedMinEnd);
			
		}
	};
	
	private void showCategoryName()
	{
		if(TextUtils.isEmpty(categoryNames))
			tvAddAlbum.setText("点击添加歌单");
		else
			tvAddAlbum.setText(categoryNames);
	}
	
	private CountEditDialog countDialog = null;
	private void showCountEditDialog(final String count)
	{
		if(countDialog == null || countDialog.isDismissed())
			countDialog = new CountEditDialog(getActivity());
		countDialog.setInfor(count);
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(countDialog.isShowing())
			countDialog.dismiss();
		countDialog.show();
		countDialog.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				loopAddNum = countDialog.getContent();
				if(TextUtils.isEmpty(loopAddNum))
				{
					showToast("请输入插播间隔数");
					return;
				}
				int looadNum = Integer.parseInt(loopAddNum);
				if(looadNum <= 0)
				{
					showToast("请输入大于0的数字");
					return;
				}
				//planInfor.getSelelctPlanInfor().setLoopAddNum(countDialog.getContent());
				itvInsetCount.getSecondView().setText("每隔 " + loopAddNum + " 首插播一次");
				countDialog.dismiss();
				//updatePlan();
			}
		});
		
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = countDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		countDialog.getWindow().setAttributes(lp);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data)
	{
		if(resultCode != RESULT_OK)
			return;
		
		if(requestCode == 1)
		{
			/*SubPlanInfor subPlaninfor = (SubPlanInfor)data.getSerializableExtra("SubPlanInfor");
			albumList = subPlaninfor.getAlbumList();
			int size = albumList.size();
			for(int i=0;i<size;i++)
			{
				MusicAlbumInfor infor = albumList.get(i);
				if(i<size-1)
				{
					categoryNames += infor.getAlbumName() + ",";
					categoryIds += infor.getAlbumId() + "-";
				}
				else
				{
					categoryNames += infor.getAlbumName();
					categoryIds += infor.getAlbumId();
				}
			}
			showCategoryName();*/
		}
		else if(requestCode == 3)
		{
			loopMusic = data.getStringExtra("media_id");
			String mediaName = data.getStringExtra("media_name");
			//planInfor.getSelelctPlanInfor().setLoopMusicInfoId(mediaId);
			itvInsertMusic.getSecondView().setText(mediaName);
			
		}
	}

	private void getMusicAlbums()
	{
		
		if(!isGetAlbumRunning)
		{
			httpFactory.getMusicAlbums();
			/*if(getLocalData().isAdminUser())
				httpFactory.getMusicAlbums();
			else
				httpFactory.getParentMusicAlbums();*/
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
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}

	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	/**
	*callbacks
	*/
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == itvInsertMusic.getBgView())
		{
			ViewUtils.startActivity(getActivity(), UploadMusicActivity.class, null, 3);
		}
		else if(v == itvInsetCount.getBgView())
		{
			showCountEditDialog(loopAddNum);
		}
	};
}
 
