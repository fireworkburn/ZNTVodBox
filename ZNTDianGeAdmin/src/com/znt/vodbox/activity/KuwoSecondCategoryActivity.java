
package com.znt.vodbox.activity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.znt.vodbox.R;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.vodbox.entity.MusicCategory;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.listview.LJListView;
import com.znt.vodbox.view.listview.LJListView.IXListViewListener;

/** 
* @ClassName: KuwoSecondCategoryFragment 
* @Description: TODO
* @author yan.yu 
* @date 2015年7月19日 下午11:35:59  
*/
public class KuwoSecondCategoryActivity extends BaseActivity implements OnClickListener
, OnItemClickListener, IXListViewListener
{
	private LJListView listView = null;

	private Document doc;
	private MusicCategoryAdapter adapter = null;
	
	private String httpUrl = "http://yinyue.kuwo.cn/yy/category.htm";
	private String title = "";
	private List<MediaInfor> mediaList = new ArrayList<MediaInfor>();
	private List<String> categoryKey = new ArrayList<String>();
	private Map<String, List<MusicCategory>> categorys = new HashMap<String, List<MusicCategory>>();
	
	private boolean isFirstLoad = true;
	
	private final int LOAD_START = 0;
	private final int LOAD_SUCCESS = 1;
	private final int LOAD_FAIL = 2;
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == LOAD_START)
			{
				mediaList.clear();
				if(isFirstLoad)
				{
					listView.showFootView(false);
					showLoadingView(true);
					listView.setVisibility(View.INVISIBLE);
				}
			}
			else if(msg.what == LOAD_SUCCESS)
			{
				mediaList.addAll((ArrayList<MediaInfor>)msg.obj);
				
				if(isFirstLoad)
				{
					showLoadingView(false);
					isFirstLoad = false;
					listView.setVisibility(View.VISIBLE);
				}
				
				if(mediaList.size() == 0)
					showNoDataView("暂时还没有相关内容哦~");
				else
					adapter.notifyDataSetChanged();
				
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
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_kuwo_second_category);
	
		
		listView = (LJListView)findViewById(R.id.ptrl_kuwo_category);
		
		setCenterString(title);
		
		listView.getListView().setDivider(getResources().getDrawable(R.color.transparent));
		listView.getListView().setDividerHeight(1);
		listView.setOnItemClickListener(this);
		listView.setPullLoadEnable(true,"共5条数据"); //如果不想让脚标显示数据可以mListView.setPullLoadEnable(false,null)或者mListView.setPullLoadEnable(false,"")
		listView.setPullRefreshEnable(true);
		listView.setIsAnimation(true); 
		listView.setXListViewListener(this);
		listView.showFootView(false);
		listView.setRefreshTime();
		
		adapter = new MusicCategoryAdapter();
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(this);
		
		httpUrl = getIntent().getStringExtra("HTTP_URL");
		title = getIntent().getStringExtra("TITLE");
		boolean hasNextCategory = getIntent().getBooleanExtra("hasNextCategory", false);
		
		setCenterString(title);
		
		loadFirst();
	}
	
	private void loadFirst()
	{
		isFirstLoad = true;
		getResource();
	}
	
	private void getResource()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				load(httpUrl);
			}
		}).start();
	}

	private ArrayList<MediaInfor> getMediaList()
	{
		ArrayList<MediaInfor> tempList = new ArrayList<MediaInfor>();
		
		Elements rootEles = doc.getElementsByClass("main");
		if(rootEles.size() == 0)
		{
			return null;
		}
		//String title = rootEles.get(0).getElementsByClass("title").get(0).text();//分类的标题
		Elements es = rootEles.get(0).getElementsByClass("singer_list");
		for (Element e : es) 
		{
			Elements elements = e.getElementsByTag("li");//分类列表
			int size = elements.size();
			for(int i=0;i<size;i++)
			{
				Element element = elements.get(i).getElementsByTag("a").get(0);//单个分类元素
				Element element1 = elements.get(i).getElementsByTag("a").get(1);//单个分类元素
				Element element2 = elements.get(i).getElementsByTag("p").get(0);//单个分类元素
				MediaInfor infor = new MediaInfor();
				infor.setMediaName(element.attr("title"));//标题
				infor.setMediaUrl(httpUrl + element.attr("href"));//链接
				infor.setMediaCover(element.getElementsByTag("img").attr("lazy_src"));//封面
				infor.setchildCount(element2.getElementsByClass("m_number").get(0).text());//歌曲数量
				tempList.add(infor);
			}
		}
		return tempList;
	}
	
	private void getCategoryList()
	{
		Elements rootes = doc.getElementsByClass("sider");
		if(rootes.size() == 0)
		{
			return;
		}
		Elements es = rootes.get(0).getElementsByClass("hotlist");
		String headUrl = "http://yinyue.kuwo.cn";
		for (Element e : es) 
		{
			List<MusicCategory> categoryList = new ArrayList<MusicCategory>();
			String title = e.getElementsByTag("h1").text();
			Elements elements = e.getElementsByTag("li");//分类列表
			int size = elements.size();
			for(int i=0;i<size;i++)
			{
				Element element = elements.get(i).getElementsByTag("a").get(0);//单个分类元素
				String name = element.getElementsByTag("a").text();
				String url = headUrl + element.attr("href");
				
				MusicCategory category = new MusicCategory();
				category.setName(name);
				category.setUrl(url);
				categoryList.add(category);
			}
			if(categoryKey.size() == 0)
			{
				categoryKey.add(title);
				categorys.put(title, categoryList);
			}
		}
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
					getCategoryList();
					
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
	 * @param urlString
	 * @return
	 */
	public String getHtmlString(String urlString) 
	{
		try
		{
			URL url = null;
			url = new URL(urlString);

			URLConnection ucon = null;
			ucon = url.openConnection();

			InputStream instr = null;
			instr = ucon.getInputStream();

			BufferedInputStream bis = new BufferedInputStream(instr);

			ByteArrayBuffer baf = new ByteArrayBuffer(500);
			int current = 0;
			while ((current = bis.read()) != -1) 
			{
				baf.append((byte) current);
			}
			return EncodingUtils.getString(baf.toByteArray(), "gbk");
		} 
		catch (Exception e) 
		{
			return "";
		}
	}
	
	private void onLoad(int updateCount) 
	{
		listView.setCount(updateCount);
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime();
	}
	
	class MusicCategoryAdapter extends BaseAdapter
	{

		/**
		*callbacks
		*/
		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return mediaList.size();
		}

		/**
		*callbacks
		*/
		@Override
		public Object getItem(int arg0)
		{
			// TODO Auto-generated method stub
			return mediaList.get(arg0);
		}

		/**
		*callbacks
		*/
		@Override
		public long getItemId(int arg0)
		{
			// TODO Auto-generated method stub
			return arg0;
		}

		/**
		*callbacks
		*/
		@Override
		public View getView(int pos, View convertView, ViewGroup arg2)
		{
			ViewHolder vh = null;
			if(convertView == null)
			{
				vh = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_music_category_item, null);
				vh.imageView = (ImageView)convertView.findViewById(R.id.iv_music_category_cover);
				vh.tvTitle = (TextView)convertView.findViewById(R.id.tv_music_category_name);
				vh.tvCount = (TextView)convertView.findViewById(R.id.tv_music_category_count);
				
				convertView.setTag(vh);
			}
			else
				vh = (ViewHolder)convertView.getTag();
			
			MediaInfor infor = mediaList.get(pos);
			if(!TextUtils.isEmpty(infor.getMediaCover()))
				Picasso.with(getActivity()).load(infor.getMediaCover()).into(vh.imageView);
			vh.tvTitle.setText(infor.getMediaName());
			vh.tvCount.setText(infor.getchildCount());
			
			// TODO Auto-generated method stub
			return convertView;
		}
		
		private class ViewHolder
		{
			ImageView imageView = null;
			TextView tvTitle = null;
			TextView tvCount = null;
		}
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		if(arg2 > 0)
			arg2 = arg2 - 1;
		MediaInfor infor = mediaList.get(arg2);
		String url = infor.getMediaUrl();
		
		Bundle bundle = new Bundle();
    	bundle.putString("HTTP_URL", url);
    	bundle.putString("TITLE", infor.getMediaName());
    	ViewUtils.startActivity(getActivity(), KuwoMusicActivity.class, bundle);
		
	}

	/**
	*callbacks
	*/
	@Override
	public void onRefresh()
	{
		// TODO Auto-generated method stub
		getResource();
	}

	/**
	*callbacks
	*/
	@Override
	public void onLoadMore()
	{
		// TODO Auto-generated method stub
		
	}
}
 
