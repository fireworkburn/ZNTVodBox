
package com.znt.diange.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.znt.diange.R;
import com.znt.diange.entity.AdverInfor;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.utils.ViewUtils;
import com.znt.diange.view.SortAdapter;
import com.znt.diange.view.listview.LJListView;
import com.znt.diange.view.listview.LJListView.IXListViewListener;

/** 
* @ClassName: KuwoMusicActivity 
* @Description: TODO
* @author yan.yu 
* @date 2015年7月19日 下午11:32:00  
*/
public class KuwoMusicActivity extends BaseActivity implements OnClickListener
, OnItemClickListener, IXListViewListener
{

	private LJListView listView = null;
	private View viewHeader = null;
	private TextView tvHeaderDesc = null;
	private ImageView ivHeaderBg = null;
	
	private Document doc;
	private SortAdapter adapter = null;
	private AdverInfor adverInfor = null;
	
	private String httpUrl = "";
	private String httpUrlNext = "";
	private String title = "";
	private boolean isFirstLoad = true;
	
	private ArrayList<MediaInfor> musicList = new ArrayList<MediaInfor>();
	
	private final int LOAD_START = 0;
	private final int LOAD_SUCCESS = 1;
	private final int LOAD_FAIL = 2;
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == LOAD_START)
			{
				musicList.clear();
				if(isFirstLoad)
				{
					showLoadingView(true);
					listView.setVisibility(View.INVISIBLE);
				}
				listView.showFootView(false);
			}
			else if(msg.what == LOAD_SUCCESS)
			{
				
				musicList.addAll((ArrayList<MediaInfor>)msg.obj);
				
				if(isFirstLoad)
				{
					showLoadingView(false);
					isFirstLoad = false;
					listView.setVisibility(View.VISIBLE);
				}
				
				if(musicList.size() == 0)
					showNoDataView("暂时还没有相关内容哦~");
				else
				{
					hideHintView();
					adapter.notifyDataSetChanged();
				}
				
				onLoad(0);
				listView.showFootView(false);
				
			}
			else if(msg.what == LOAD_FAIL)
			{
				onLoad(0);
				showNoDataView("获取数据失败，请重新加载~");
				listView.showFootView(false);
				listView.setVisibility(View.VISIBLE);
			}
		};
	};
	
	/**
	*callbacks
	*/
	@Override
	public void onCreate(android.os.Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_kuwo_music);
		
		listView = (LJListView)findViewById(R.id.ptrl_kuwo_music);
		
		listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		listView.getListView().setDividerHeight(1);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();
		
		adapter = new SortAdapter(this, musicList);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(this);
		
		adverInfor = (AdverInfor)getIntent().getSerializableExtra("ADVER_INFOR");
		if(adverInfor == null)
		{
			httpUrl = getIntent().getStringExtra("HTTP_URL");
			title = getIntent().getStringExtra("TITLE");
		}
		else
		{
			httpUrl = adverInfor.getUrl();
			title = adverInfor.getTitle();
			
			initHeaderView();
			
		}
		
		setCenterString(title);
		
		loadFirst();
	}
	
	private void initHeaderView()
	{
		viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.view_musics_header, null);
		
		tvHeaderDesc = (TextView)viewHeader.findViewById(R.id.tv_musics_header_desc);
		ivHeaderBg = (ImageView)viewHeader.findViewById(R.id.iv_musics_header);
		
		tvHeaderDesc.setText(adverInfor.getContent());
		Picasso.with(getActivity()).load(adverInfor.getImageRes()).into(ivHeaderBg);
		
		listView.addHeader(viewHeader);
		
	}
	
	private void loadFirst()
	{
		isFirstLoad = true;
		getResource(httpUrl);
	}
	
	private void getResource(final String url)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				load(url);
			}
		}).start();
	}

	private ArrayList<MediaInfor> getMediaList()
	{
		ArrayList<MediaInfor> tempList = new ArrayList<MediaInfor>();
		
		Elements rootEles = doc.getElementsByClass("m_list");
		if(rootEles.size() == 0)
			return null;
		String title = doc.getElementsByClass("comm").get(0).getElementsByTag("h1").attr("title");//分类的标题
		Elements es = rootEles.get(0).getElementsByClass("list").get(0).getElementsByTag("ul");
		for (Element e : es) 
		{
			Elements elements = e.getElementsByTag("li");//分类列表
			int size = elements.size();
			for(int i=0;i<size;i++)
			{
				String mid = elements.get(i).getElementsByClass("number").get(0).getElementsByTag("input").attr("mid");
				String m_name = elements.get(i).getElementsByClass("m_name").get(0).getElementsByTag("a").text();
				//String title = elements.get(i).getElementsByClass("m_name").get(0).getElementsByTag("a").attr("title");
				String m_url = elements.get(i).getElementsByClass("m_name").get(0).getElementsByTag("a").attr("href");
				
				String a_name = elements.get(i).getElementsByClass("a_name").get(0).getElementsByTag("a").text();
				String a_url = elements.get(i).getElementsByClass("a_name").get(0).getElementsByTag("a").attr("href");
				
				String s_name = elements.get(i).getElementsByClass("s_name").get(0).getElementsByTag("a").text();
				/*String s_url = elements.get(i).getElementsByClass("s_name").get(0).getElementsByTag("a").attr("href");
				
				String listen_name = elements.get(i).getElementsByClass("listen").get(0).getElementsByTag("a").text();
				String listen_url = elements.get(i).getElementsByClass("listen").get(0).getElementsByTag("a").attr("href");
				
				String video_name = elements.get(i).getElementsByClass("video").get(0).getElementsByTag("a").text();
				String video_url = elements.get(i).getElementsByClass("video").get(0).getElementsByTag("a").attr("href");*/
				
				MediaInfor infor = new MediaInfor();
				infor.setMediaId(mid);
				infor.setMediaName(m_name);
				infor.setMediaUrl(m_url);
				infor.setArtist(s_name);
				infor.setAlbumName(a_name);
				infor.setAlbumUrl(a_url);
				tempList.add(infor);
			}
		}
		
		return tempList;
	}
	
	private void onLoad(int updateCount) 
	{
		listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();
	}
	
	protected void load(String url) 
	{
		ViewUtils.sendMessage(handler, LOAD_START);
		
		for(int i=0;i<3;i++)
		{
			try 
			{
				doc = Jsoup.parse(new URL(url), 5000);
				if(doc != null)
				{
					ArrayList<MediaInfor> tempList = getMediaList();
					if(tempList != null)
						ViewUtils.sendMessage(handler, LOAD_SUCCESS, tempList);
					else
						ViewUtils.sendMessage(handler, LOAD_FAIL);
				}
			} 
			catch (MalformedURLException e1) 
			{
				e1.printStackTrace();
				doc = null;
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
				doc = null;
			}
			if(doc != null)
				break;
			try
			{
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(doc == null)
			ViewUtils.sendMessage(handler, LOAD_FAIL);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		if(adverInfor == null)
		{
			if(arg2 > 0)
				arg2 = arg2 - 1;
		}
		else
		{
			if(arg2 > 1)
				arg2 = arg2 - 2;
		}
		Bundle bundle = new Bundle();
		bundle.putSerializable("MediaInfor", musicList.get(arg2));
		ViewUtils.startActivity(this, SongPrepareActivity.class, bundle);
	}

	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
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
		
		getResource(httpUrl);
	}

	/**
	*callbacks
	*/
	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		//getResource(httpUrlNext);
	}
}
 
