
package com.znt.vodbox.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.ResoureType;
import com.znt.vodbox.R;
import com.znt.vodbox.adapter.MusicSearchAdapter;
import com.znt.vodbox.db.DBManager;
import com.znt.vodbox.dialog.MusicPlayDialog;
import com.znt.vodbox.utils.DownHelper;
import com.znt.vodbox.utils.MusicResoureUtils;
import com.znt.vodbox.utils.SystemUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.RecordAdapter;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
* @ClassName: SearchMusicActivity 
* @Description: TODO
* @author yan.yu 
* @date 2015年7月19日 下午4:36:49  
*/
public class SearchMusicActivity extends BaseActivity implements OnItemClickListener, IXListViewListener
{
	private AutoCompleteTextView etSearch = null;
	private TextView tvSearch = null;
	private LJListView listView = null;
	private ListView lvRecord = null;
	private View recordView = null;
	private View recordViewClear = null;
	
	private ArrayList<MediaInfor> musicList = new ArrayList<MediaInfor>();
	
	private MusicSearchAdapter adapter = null;
	private DownHelper downHelper = null;
	private MusicResoureUtils musicResource = null;
	
	private int total = 0;
	private int pageNum = 1;
	private boolean isSave = false;
	private int playPos = 0;
	private String searchKey = "";
	private String albumId = null;
	private String terminalId = "";
	
	private List<String>  searchRecords = new ArrayList<String>();
	
	private RecordAdapter recordAdapter = null;
	
	private int resourceType = ResoureType.WANGYI;
	
	private final int SEARCH_START = 0;
	private final int SEARCH_SUCCESS = 1;
	private final int SEARCH_FAIL = 2;
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == SEARCH_START)
			{
				String key = (String)msg.obj;
				DBManager.newInstance(getActivity()).setSearchRecord(key);
				recordView.setVisibility(View.GONE);
			}
			else if(msg.what == SEARCH_SUCCESS)
			{
				if(pageNum == 1)
					musicList.clear();
				musicList.addAll((ArrayList<MediaInfor>)msg.obj);
				if(musicList.size() > 0)
					adapter.notifyDataSetChanged();
				else
				{
					showToast("没有搜索到任何内容，重试看看");
				}
					
				if(musicList.size() >= total)
					onLoad(0);
				else
					onLoad(20);
				listView.scrollTo(0, 0);
			}
			else if(msg.what == SEARCH_FAIL)
			{
				showToast("查询失败，请稍后重试");
				onLoad(0);
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
		
		setContentView(R.layout.activity_search_music);
		
		etSearch = (AutoCompleteTextView)findViewById(R.id.cet_search_music);
		tvSearch = (TextView)findViewById(R.id.tv_search_music);
		listView = (LJListView)findViewById(R.id.ptrl_music_search);
		recordView = findViewById(R.id.view_search_bottom_record);
		recordViewClear = findViewById(R.id.view_search_record_clear);
		lvRecord = (ListView)findViewById(R.id.lv_search_record);
		
		listView.setRefreshTime();
		
		musicResource = new MusicResoureUtils();
		albumId = getIntent().getStringExtra("ALBUM_ID");
		terminalId = getIntent().getStringExtra("terminalId");
		
		listView.showFootView(false);
		
		recordAdapter = new RecordAdapter(getActivity(), searchRecords);
		lvRecord.setAdapter(recordAdapter);
		lvRecord.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				// TODO Auto-generated method stub
				etSearch.setText(searchRecords.get(position));
				hideInput();
				listView.onFresh();
			}
		});
		
		etSearch.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
                if ((actionId == 0 || actionId == 3) && event != null) 
                {
                    //点击搜索要做的操作
                	hideInput();
        			listView.onFresh();
                }
                return false;
            }
        });
		
		/*etSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1)
					initRecordData();
			}
		});*/
		etSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etSearch.setFocusable(true);
				etSearch.setFocusableInTouchMode(true);
				etSearch.requestFocus();
				etSearch.findFocus();
				showRecordView();
				
				SystemUtils.showInputView(v);
			}
		});
		
		recordViewClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DBManager.newInstance(getActivity()).clearSearchRecord();
				initRecordData();
			}
		});
		
		init();
	}
	
	private void hideInput()
	{
		etSearch.setFocusable(false);
		SystemUtils.hideInputView(getActivity());
	}
	
	private void init()
	{
		downHelper = new DownHelper();
		
		listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		listView.getListView().setDividerHeight(1);
		listView.setOnItemClickListener(this);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		
		adapter = new MusicSearchAdapter(getActivity(), musicList, albumId);
		adapter.setTerminalId(terminalId);
		listView.setAdapter(adapter);
		
		
		setCenterString("搜索歌曲");
		
		initRecordData();
		
		String keyWord = getIntent().getStringExtra("KEY_WORD");
		if(!TextUtils.isEmpty(keyWord))
		{
			etSearch.setText(keyWord);
			listView.onFresh();
			hideInput();
		}
		
		tvSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				String name = etSearch.getText().toString();
				if(!TextUtils.isEmpty(name))
				{
					listView.onFresh();
					hideInput();
				}
				else
					showToast("请输入搜索内容");
			}
		});
	}
	
	private void initRecordData()
	{
		searchRecords.clear();
		
		searchRecords.addAll(DBManager.newInstance(this).getSearchRecordList());
		
		recordAdapter.notifyDataSetChanged();
		
		if(searchRecords.size() > 0)
			recordView.setVisibility(View.VISIBLE);
		else
			recordView.setVisibility(View.GONE);
		
	}
	
	private void showRecordView()
	{
		if(searchRecords.size() > 0)
			recordView.setVisibility(View.VISIBLE);
		else
			recordView.setVisibility(View.GONE);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onPause()
	{
	
		hideInput();
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onDestroy()
	{
		
		downHelper.stop();
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) 
		{
			ArrayList<String> nbest = data.getExtras().getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
	        if(nbest.size() > 0)
	        {
	        	String key = nbest.get(0);
	            if(!TextUtils.isEmpty(key))
	    		{
	            	etSearch.setText(key);
	    			listView.onFresh();
	    			hideInput();
	    		}
	    		else
	    			showToast("请输入搜索内容");
	        }
	        else
	        	showToast("识别失败");
        }
	}
	
	private void startSearchMusic(String key)
	{
		hideInput();
		if(!TextUtils.isEmpty(key))
			doSearch(key);
		else
		{
			onLoad(0);
			showToast("请输入关键字查找");
		}
	}
	
	private void doSearch(String name)
	{
		//if(resourceType == ResoureType.WANGYI)
			doSearchByKuwo(name);
		/*else if(resourceType == ResoureType.KUWO)
			doSearchByWY(name);*/
	}
	
	private void doSearchByWY(final String name)
	{
		this.searchKey = name;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				resourceType = ResoureType.WANGYI;
				adapter.setResoureType(resourceType);
				ViewUtils.sendMessage(handler, SEARCH_START, name);
				// TODO Auto-generated method stub
				//List<MediaInfor> tempList = musicResource.searchMusicByKuwo(name, pageNum, total);
				//List<MediaInfor> tempList = musicResource.searchMusicDuomi(name, pageNum, total);
				List<MediaInfor> tempList;
				try {
					tempList = musicResource.searchMusicByYun(name, pageNum, 25);
					if(tempList != null)
						ViewUtils.sendMessage(handler, SEARCH_SUCCESS, tempList);
					else
						ViewUtils.sendMessage(handler, SEARCH_FAIL);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//List<MediaInfor> tempList = musicResource.searchMusicBaidu(name, pageNum, total);
				
			}
		}).start();
	}
	private void doSearchByKuwo(final String name)
	{
		this.searchKey = name;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				
				resourceType = ResoureType.KUWO;
				adapter.setResoureType(resourceType);
				ViewUtils.sendMessage(handler, SEARCH_START, name);
				// TODO Auto-generated method stub
				List<MediaInfor> tempList = musicResource.searchMusicByKuwo(name, pageNum, total);
				//List<MediaInfor> tempList = musicResource.searchMusicDuomi(name, pageNum, total);
				//List<MediaInfor> tempList = musicResource.searchMusicByWY(name, pageNum, total);
				//List<MediaInfor> tempList = musicResource.searchMusicBaidu(name, pageNum, total);
				if(tempList != null)
					ViewUtils.sendMessage(handler, SEARCH_SUCCESS, tempList);
				else
					ViewUtils.sendMessage(handler, SEARCH_FAIL);
			}
		}).start();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
	{
		// TODO Auto-generated method stub
		if(pos >= 1)
			pos = pos - 1;
		MediaInfor infor = musicList.get(pos);
		if(infor.isAvailable())
			showPlayDialog(infor);
		else
			showToast("该文件已失效");
	}
	private void showPlayDialog(final MediaInfor infor)
	{
		final MusicPlayDialog playDialog = new MusicPlayDialog(getActivity(), R.style.Theme_CustomDialog, false);
	
		if(resourceType == ResoureType.WANGYI)
		{
			playDialog.setHasUrl(true);
		}
		else if(resourceType == ResoureType.KUWO)
		{
			playDialog.setHasUrl(false);
		}
		
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
	
	private void onLoad(int updateCount) 
	{
		listView.showFootView(updateCount > 0);
		
		listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();
	}

	/**
	*callbacks
	*/
	@Override
	public void onRefresh()
	{
		// TODO Auto-generated method stub
		pageNum = 1;
		
		startSearchMusic(etSearch.getText().toString());
	}

	/**
	*callbacks
	*/
	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		if(resourceType == ResoureType.KUWO || resourceType == ResoureType.WANGYI)
		{
			++pageNum;
			startSearchMusic(etSearch.getText().toString());
		}
		else
		{
			showToast("没有更多数据了");
			onLoad(0);
		}
	}
	/**
	*callbacks
	*/
	@Override
	public void onBackPressed() 
	{
		// TODO Auto-generated method stub
		if(recordView.isShown())
		{
			recordView.setVisibility(View.GONE);
			etSearch.setFocusable(false);
		}
		else
			super.onBackPressed();
	}
	
}
 
