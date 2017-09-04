/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-21 下午5:50:59 
* @Version V1.1   
*/ 

package com.znt.vodbox.view; 

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.adapter.MyAlbumAdapter;
import com.znt.vodbox.dialog.EditNameDialog;
import com.znt.vodbox.entity.MusicAlbumInfor;
import com.znt.vodbox.entity.MusicEditType;
import com.znt.vodbox.entity.MyAlbumInfor;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.utils.ViewUtils;

/** 
 * @ClassName: ViewMyMusicHeader 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-21 下午5:50:59  
 */
public class MyMusicHeaderView extends RelativeLayout
{
	private Activity context = null;
	private View parentView  = null;
	private View viewOne = null;
	private TextView tvHintOne = null;
	private ImageView ivOne = null;
	private View viewTwo = null;
	private TextView tvHintTwo = null;
	private ImageView ivTwo = null;
	private ListView listView = null;
	private MyAlbumAdapter adapter = null;
	private HttpFactory httpFactory = null;
	
	private int deleteAlbumIndex = 0;
	private List<MusicAlbumInfor> albumList = new ArrayList<MusicAlbumInfor>(); 
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.CREATE_ALBUM_START)
			{
				
			}
			else if(msg.what == HttpMsg.CREATE_ALBUM_SUCCESS)
			{
				/*Toast.makeText(context, "创建成功", 0).show();
				((MusicFragment)baseActivity).getMusicAlbums();*/
			}
			else if(msg.what == HttpMsg.CREATE_ALBUM_FAIL)
			{
				//Toast.makeText(baseActivity.getActivity(), "创建失败", 0).show();
			}
		};
	};
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param attrs
	* @param defStyle 
	*/
	public MyMusicHeaderView(Activity context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	public MyMusicHeaderView(Activity context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	public MyMusicHeaderView(Activity context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	
	private void initViews(final Activity context)
	{
		this.context = context;
		
		parentView = LayoutInflater.from(context).inflate(R.layout.view_my_music_header, this, true);
		
		viewOne = parentView.findViewById(R.id.view_my_music_header_one);
		viewTwo = parentView.findViewById(R.id.view_my_music_header_two);
		
		tvHintOne = (TextView)parentView.findViewById(R.id.tv_my_music_header_one);
		tvHintTwo = (TextView)parentView.findViewById(R.id.tv_my_music_header_two);
		ivOne = (ImageView)parentView.findViewById(R.id.iv_my_music_header_one);
		ivTwo = (ImageView)parentView.findViewById(R.id.iv_my_music_header_two);
		listView = (ListView)parentView.findViewById(R.id.lv_my_music_header);
		
		adapter = new MyAlbumAdapter(context, albumList, MusicEditType.DeleteAdd);
		adapter.setListVeiw(listView);
		listView.setAdapter(adapter);
		
		httpFactory = new HttpFactory(context, handler);
		
		viewOne.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				showCreateAlbums();
			}
		});
		/*viewTwo.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				
			}
		});*/
		
	}
	
	private void showCreateAlbums()
	{
		if(!listView.isShown())
		{
			listView.setVisibility(View.VISIBLE);
			ivOne.setImageResource(R.drawable.icon_pull_up_off);
		}
		else
		{
			listView.setVisibility(View.GONE);
			ivOne.setImageResource(R.drawable.icon_pull_down_off);
		}
	}
	public void showCollectAlbums()
	{
		if(!listView.isShown())
		{
			listView.setVisibility(View.VISIBLE);
			ivOne.setImageResource(R.drawable.icon_pull_up_off);
		}
		else
		{
			listView.setVisibility(View.GONE);
			ivOne.setImageResource(R.drawable.icon_pull_down_off);
		}
	}
	
	public void showAlbumSize(MyAlbumInfor infor)
	{
		tvHintOne.setText("我的歌单("+ infor.getCreateAlbumCount() + ")");
		tvHintTwo.setText("我的收藏("+ infor.getCollectAlbumCount() + ")");
		
	}
	
	public void updateData(List<MusicAlbumInfor> albumList)
	{
		this.albumList.clear();
		this.albumList.addAll(albumList);
		this.albumList.add(0, getUploadMusicAlbum());
		adapter.notifyDataSetChanged();
		/*MusicAlbumInfor infor = new MusicAlbumInfor();
		albumList.add(infor);*/
		setListViewHeight();
	}
	
	private MusicAlbumInfor getUploadMusicAlbum()
	{
		MusicAlbumInfor infor = new MusicAlbumInfor();
		infor.setAlbumId("0");
		infor.setAlbumName("我上传的");
		infor.setDescription("上传的文件都在这里");
		return infor;
	}
	
	private void setListViewHeight()
	{
		int size = albumList.size();
		int h = size * (64);
		ViewUtils.setViewParams(context, listView, 0, h);
	}
	
	private void showEditDialog(final String infor)
	{
		final EditNameDialog playDialog = new EditNameDialog(context, R.style.Theme_CustomDialog);
		playDialog.setInfor(infor);
		//playDialog.updateProgress("00:02:18 / 00:05:12");
		if(playDialog.isShowing())
			playDialog.dismiss();
		playDialog.show();
		playDialog.setOnClickListener(new android.view.View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				/*if(TextUtils.isEmpty(playDialog.getContent()))
				{
					Toast.makeText(baseActivity.getActivity(), "请输入名称", 0).show();
					return;
				}
				playDialog.dismiss();
				httpFactory.createAlbum(playDialog.getContent());*/
			}
		});
		
		WindowManager windowManager = context.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = playDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		playDialog.getWindow().setAttributes(lp);
	}
	
}
 
