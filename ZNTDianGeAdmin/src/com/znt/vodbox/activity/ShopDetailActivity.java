/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-1 下午4:47:57 
* @Version V1.1   
*/ 

package com.znt.vodbox.activity; 

import java.util.ArrayList;
import java.util.List;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.MusicAdapter;
import com.znt.vodbox.dialog.EditNameDialog;
import com.znt.vodbox.dialog.MusicPlayDialog;
import com.znt.vodbox.dialog.VideoDirectionDialog;
import com.znt.vodbox.dialog.VolumeSetDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.LocationInfor;
import com.znt.vodbox.entity.MusicEditType;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.http.HttpResult;
import com.znt.vodbox.utils.DateUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
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
import android.widget.TextView;

/** 
 * @ClassName: ShopDetailActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-1 下午4:47:57  
 */
public class ShopDetailActivity extends BaseActivity implements IXListViewListener, OnItemClickListener, OnClickListener
{

	private LJListView listView = null;
	private TextView tvApplication = null;
	private TextView tvClear = null;
	private TextView tvAdd = null;
	
	private View viewHeader = null;
	private TextView tvShopName = null;
	private TextView tvShopAddr = null;
	private TextView tvPlaySong = null;
	private TextView tvSongCount = null;
	private TextView tvSongPush = null;
	private TextView tvRecommand = null;
	private TextView tvVolume = null;
	private TextView viewVideoWhirl = null;
	private TextView tvEndTime = null;
	private TextView tvIp = null;
	private View viewAddr = null;
	
	private HttpFactory httpFactory = null;
	private MusicAdapter adapter = null;
	private DeviceInfor deviceInfor = null;
	private List<MediaInfor> mediaList = new ArrayList<MediaInfor>();
	
	private String shopNewName = null;
	private int pageNum = 1;
	private int total = 0;
	private boolean isRunning = false;
	private boolean isVolumeUpdated = false;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.GET_SPEAKER_MUSIC_START)
			{
				//showLoadingView(true);
				isRunning = true;
			}
			else if(msg.what == HttpMsg.GET_SPEAKER_MUSIC_SUCCESS)
			{
				HttpResult result = (HttpResult)msg.obj;
				total = result.getTotal();
				List<MediaInfor> tempList = (List<MediaInfor>)result.getReuslt();
				if(tempList.size() == 0)
				{
					if(pageNum == 1)
						showNoDataView("当前计划没有歌曲");
					else
						showToast("没有更多歌曲了");
				}
				else
				{
					if(pageNum == 1)
						mediaList.clear();
					mediaList.addAll(tempList);
					adapter.notifyDataSetChanged();
					hideHintView();
				}
				tvSongCount.setText("店铺歌曲(" + total + ")");
				//showLoadingView(false);
				isRunning = false;
				pageNum ++;
				onLoad(0);
			}
			else if(msg.what == HttpMsg.GET_SPEAKER_MUSIC_FAIL)
			{
				isRunning = false;
				onLoad(0);
			}
			else if(msg.what == HttpMsg.UPDATE_SPEAKER_INFOR_START)
			{
				showProgressDialog(getActivity(), "正在更新设备信息...");
			}
			else if(msg.what == HttpMsg.UPDATE_SPEAKER_INFOR_SUCCESS)
			{
				dismissDialog();
				showToast("设备信息更新成功");
				tvShopAddr.setText(deviceInfor.getAddr());
				if(!TextUtils.isEmpty(shopNewName))
					tvShopName.setText(shopNewName);
			}
			else if(msg.what == HttpMsg.UPDATE_SPEAKER_INFOR_FAIL)
			{
				showToast("设备信息更新失败");
				dismissDialog();
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
		
		setCenterString("店铺详情");
		
		setContentView(R.layout.activity_shop_detail);
		
		showTopView(true);
		setRightText("门店计划");
		setRightTopIcon(R.drawable.icon_plan_item);
		
		viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.view_shop_detail_header, null);
		tvShopName = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_name);
		tvShopAddr = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_addr);
		tvPlaySong = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_song);
		tvSongCount = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_song_count);
		tvSongPush = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_song_push);
		tvRecommand = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_song_recommand);
		tvVolume = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_volume);
		tvEndTime = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_end_time);
		tvIp = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_ip);
		viewAddr = viewHeader.findViewById(R.id.view_shop_detail_header_addr);
		viewVideoWhirl = (TextView)viewHeader.findViewById(R.id.tv_shop_detail_header_videowhirl);
		
		tvApplication = (TextView)findViewById(R.id.tv_shop_music_bottom_application);
		tvClear = (TextView)findViewById(R.id.tv_shop_music_bottom_clear);
		tvAdd = (TextView)findViewById(R.id.tv_shop_music_bottom_add);
		listView = (LJListView)findViewById(R.id.ptrl_shop_music);
		
		tvApplication.setOnClickListener(this);
		tvClear.setOnClickListener(this);
		tvAdd.setOnClickListener(this);
		tvSongPush.setOnClickListener(this);
		tvRecommand.setOnClickListener(this);
		tvVolume.setOnClickListener(this);
		viewVideoWhirl.setOnClickListener(this);
		viewAddr.setOnClickListener(this);
		tvShopName.setOnClickListener(this);
		
		listView.addHeader(viewHeader);
		listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		listView.getListView().setDividerHeight(1);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();
		listView.setOnItemClickListener(this);
		
		adapter = new MusicAdapter(this, mediaList, handler);
		adapter.setMusicEditType(MusicEditType.None);
		adapter.setCurPlayView(tvPlaySong);
		listView.setAdapter(adapter);
		
		httpFactory = new HttpFactory(getActivity(), handler);
		
		deviceInfor = (DeviceInfor)getIntent().getSerializableExtra("DeviceInfor");
		
		initHeaderData();
		
		listView.onFresh();
		
		if(!getLocalData().isNormalUser())
		{
			tvSongPush.setVisibility(View.VISIBLE);
			tvVolume.setVisibility(View.VISIBLE);
		}
		else
		{
			tvSongPush.setVisibility(View.GONE);
			tvVolume.setVisibility(View.GONE);
		}
		
		getRightView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Bundle bundle = new Bundle();
				bundle.putString("terminalId", deviceInfor.getCode());
				bundle.putString("terminalName", deviceInfor.getName());
				ViewUtils.startActivity(getActivity(), PlanListActivity.class, bundle);
			}
		});
		
	}
	
	private String getVideoWhirl(String videoWhirl)
	{
		if(videoWhirl.equals("0"))
		{
			return "横屏";
		}
		else if(videoWhirl.equals("1"))
		{
			return "竖屏";
		}
		else if(videoWhirl.equals("2"))
		{
			return "横屏倒置";
		}
		else if(videoWhirl.equals("3"))
		{
			return "竖屏倒置";
		}
		return "未知"+videoWhirl;
	}
	
	
	private void initHeaderData()
	{
		
		tvVolume.setText("音量(" + deviceInfor.getVolume() + " / 15)");
		viewVideoWhirl.setText("方向(" + getVideoWhirl(deviceInfor.getVideoWhirl()) + ")");
		tvShopName.setText(deviceInfor.getName());
		if(!Constant.isInnerVersion)
			tvIp.setVisibility(View.GONE);
		else
		{
			tvIp.setVisibility(View.VISIBLE);
			tvIp.setText(deviceInfor.getNetInfo() + "  " + deviceInfor.getWifiName() + "/" + deviceInfor.getWifiPwd());
		}
			
		tvShopAddr.setText(deviceInfor.getAddr());
		if(!TextUtils.isEmpty(deviceInfor.getCurPlaySong()))
		{
			if(deviceInfor.getPlayingSongType().equals("0"))
				tvPlaySong.setText("当前播放歌曲:" + deviceInfor.getCurPlaySong());
			else if(deviceInfor.getPlayingSongType().equals("1"))
				tvPlaySong.setText("当前插播歌曲:" + deviceInfor.getCurPlaySong());
		}
		else
			tvPlaySong.setText("当前播放歌曲: 未播放");
		tvSongCount.setText("当前计划歌曲(" + deviceInfor.getMusicCount() + ")");
		
		if(!TextUtils.isEmpty(deviceInfor.getEndTime()))
		{
			long endTime = Long.parseLong(deviceInfor.getEndTime());
			if(endTime > 0 && endTime < System.currentTimeMillis())
			{
				tvEndTime.setText("已到期，请尽快续费");
				tvEndTime.setTextColor(getResources().getColor(R.color.red));
			}
			else
			{
				tvEndTime.setText("到期时间：" + DateUtils.getDateFromLong(endTime));
				tvEndTime.setTextColor(getResources().getColor(R.color.text_black_mid));
			}
		}
		
	}

	private void onLoad(int updateCount) 
	{
		listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();
		if(mediaList.size() >= total)
			listView.showFootView(false);
		else
			listView.showFootView(true);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
	{
		// TODO Auto-generated method stub
		if(pos >= 2)
			pos = pos - 2;
		MediaInfor infor = mediaList.get(pos);
		infor.setFromAlbum(true);
		showPlayDialog(infor);
	}
	
	private void getPlanMusics()
	{
		httpFactory.getSpeakerMusic(deviceInfor.getCode(), pageNum);
	}

	/**
	*callbacks
	*/
	@Override
	protected void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if(httpFactory != null)
			httpFactory.stopHttp();
	}

	/**
	*callbacks
	*/
	@Override
	public void onRefresh() 
	{
		// TODO Auto-generated method stub
		if(isRunning)
			return;
		pageNum = 1;
		getPlanMusics();
	}

	/**
	*callbacks
	*/
	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		if(total > mediaList.size())
			getPlanMusics();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//LogFactory.createLog().e("resultCode-->" + resultCode);
		if(resultCode != RESULT_OK)
			return;
		LocationInfor locationInfor = (LocationInfor) data.getSerializableExtra("LocationInfor");
		
		deviceInfor.setLat(locationInfor.getLat());
		deviceInfor.setLon(locationInfor.getLon());
		deviceInfor.setAddr(locationInfor.getAddr());
		httpFactory.updateSpeakerInfor(deviceInfor);
		//LogFactory.createLog().e("get cur addr-->" + deviceInfor.getAddr());
		
	}

	/**
	*callbacks
	*/
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		if(v == tvApplication)
		{
			
		}
		else if(v == tvClear)
		{
			
		}
		else if(v == tvRecommand)
		{
			Bundle bundle = new Bundle();
			bundle.putString("terminalId", deviceInfor.getCode());
			bundle.putSerializable("MusicEditType", MusicEditType.DeleteAdd);
			ViewUtils.startActivity(getActivity(), AlbumMusicActivity.class, bundle);
		}
		else if(v == tvSongPush)
		{
			Bundle bundle = new Bundle();
			bundle.putString("terminalId", deviceInfor.getCode());
			ViewUtils.startActivity(getActivity(), SearchMusicActivity.class, bundle);
		}
		else if(v == tvVolume)
		{
			showVolumeDialog(deviceInfor);
		}
		else if(v == viewVideoWhirl)
		{
			showVideoWhirlDialog(deviceInfor);
		}
		else if(v == viewAddr)
		{
			ViewUtils.startActivity(getActivity(), LocationActivity.class, null, 1);
		}
		else if(v == tvAdd)
		{
			Bundle bundle = new Bundle();
			ViewUtils.startActivity(getActivity(), SearchMusicActivity.class, bundle);
		}
		else if(v == tvShopName)
		{
			showEditNameDialog();
		}
	}
	private void showPlayDialog(final MediaInfor infor)
	{
		final MusicPlayDialog playDialog = new MusicPlayDialog(getActivity(), R.style.Theme_CustomDialog);
		
		playDialog.setInfor(infor);
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(playDialog.isShowing())
			playDialog.dismiss();
		playDialog.show();
		playDialog.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
				if(infor.getMediaType().equals(MediaInfor.MEDIA_TYPE_PHONE))
				{
					Bundle bundle = new Bundle();
					bundle.putString("KEY_WORD", infor.getMediaName());
					ViewUtils.startActivity(getActivity(), SearchMusicActivity.class, bundle);
				}
				else
				{
					
				}
				playDialog.dismiss();
			}
		});
		
		WindowManager windowManager = ((Activity) getActivity()).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = playDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		playDialog.getWindow().setAttributes(lp);
	}

	private void showVolumeDialog(final DeviceInfor devInfor)
	{
		final VolumeSetDialog playDialog = new VolumeSetDialog(getActivity(), devInfor);
	
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(playDialog.isShowing())
			playDialog.dismiss();
		playDialog.show();
		playDialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface arg0) 
			{
				// TODO Auto-generated method stub
				isVolumeUpdated = playDialog.isVolumeUpdated();
				tvVolume.setText("音量(" + playDialog.getCurVolume() + " / 15)");
				deviceInfor.setVolume(playDialog.getCurVolume() + "");
			}
		});
		WindowManager windowManager = ((Activity) getActivity()).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = playDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		playDialog.getWindow().setAttributes(lp);
	}
	private void showVideoWhirlDialog(final DeviceInfor devInfor)
	{
		final VideoDirectionDialog videoDirectionDialog = new VideoDirectionDialog(getActivity());
	
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(videoDirectionDialog.isShowing())
			videoDirectionDialog.dismiss();
		videoDirectionDialog.showDialog(deviceInfor.getVideoWhirl(), deviceInfor.getCode());
		videoDirectionDialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface arg0) 
			{
				// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(videoDirectionDialog.getCurDerection()))
				{
					deviceInfor.setVideoWhirl(videoDirectionDialog.getCurDerection());
					viewVideoWhirl.setText("方向(" + getVideoWhirl(deviceInfor.getVideoWhirl()) + ")");
				}
				//
			}
		});
		WindowManager windowManager = ((Activity) getActivity()).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = videoDirectionDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		videoDirectionDialog.getWindow().setAttributes(lp);
	}
	
	private EditNameDialog dialog = null;
	private void showEditNameDialog()
	{
		if(dialog == null || dialog.isDismissed())
			dialog = new EditNameDialog(getActivity());
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(dialog.isShowing())
			dialog.dismiss();
		dialog.show();
		final String oldName = tvShopName.getText().toString();
		dialog.setInfor(oldName);
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
				if(dialog.getContent().equals(oldName))
				{
					showToast("设备名称未更改");
					return;
				}
				shopNewName = dialog.getContent();
				httpFactory.updateSpeakerName(dialog.getContent(), deviceInfor.getCode());
				dialog.dismiss();
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
 
