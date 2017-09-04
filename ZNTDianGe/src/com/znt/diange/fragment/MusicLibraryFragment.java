
package com.znt.diange.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.znt.diange.R;
import com.znt.diange.activity.KuwoMusicActivity;
import com.znt.diange.activity.LocalMusicActivity1;
import com.znt.diange.activity.MainActivity;
import com.znt.diange.activity.SearchMusicActivity;
import com.znt.diange.activity.SongBookActivity;
import com.znt.diange.entity.AdverInfor;
import com.znt.diange.factory.DiangeManger;
import com.znt.diange.utils.StringUtils;
import com.znt.diange.utils.ViewUtils;
import com.znt.diange.view.ChildViewPager;
import com.znt.diange.view.FixedSpeedScroller;
import com.znt.diange.view.ItemTextView;

/** 
* @ClassName: MusicLibraryFragment 
* @Description: TODO
* @author yan.yu 
* @date 2015年7月15日 下午11:11:43  
*/
public class MusicLibraryFragment extends BaseFragment implements OnClickListener, OnPageChangeListener
{
	
	private View parentView = null;
	
	private ChildViewPager viewPager = null;
	private LinearLayout viewIndicator = null;
	private EditText etSearch = null;
	private ScrollView scrollView = null;
	
	private ItemTextView itvLocalPhone = null;
	private ItemTextView itvLocalSpeaker = null;
	private ItemTextView itvQingyl = null;
	private ItemTextView itvXiaoqx = null;
	private ItemTextView itvWangluo = null;
	private ItemTextView itvXiaoyuan = null;
	private ItemTextView itvXinqing = null;
	private ItemTextView itvJieri = null;
	private ItemTextView itvChangjing = null;
	private ItemTextView itvHuanjing = null;
	
	
	private ItemTextView itv70 = null;
	private ItemTextView itv80 = null;
	private ItemTextView itv90 = null;
	
	private AdverAdapter adverAdapter = null;
	private DiangeManger mDiangeManger = null;
	
	private List<View> adverItemViews = new ArrayList<View>();
	private List<AdverViewHolder> adverViews = new ArrayList<AdverViewHolder>();
	private List<AdverInfor> adverList = new ArrayList<AdverInfor>();
	private List<ImageView> indicatorItemViews = new ArrayList<ImageView>();
	
	private boolean isPrepared = false;
	
	public MusicLibraryFragment()
	{
		
	}
	
	public MusicLibraryFragment newInstance()
	{
		return new MusicLibraryFragment();
	}
	
	
	/**
	*callbacks
	*/
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
			parentView = getContentView(R.layout.fragment_music_library);
			
			scrollView = (ScrollView)parentView.findViewById(R.id.sv_music_lib);
			viewPager = (ChildViewPager)parentView.findViewById(R.id.vp_music_library_adver);
			viewIndicator = (LinearLayout)parentView.findViewById(R.id.view_music_library_indicator);
			etSearch = (EditText)parentView.findViewById(R.id.et_library_music_search);
			
			itvLocalPhone = (ItemTextView)parentView.findViewById(R.id.itv_category_phone);
			itvLocalSpeaker = (ItemTextView)parentView.findViewById(R.id.itv_category_speaker);
			itvQingyl = (ItemTextView)parentView.findViewById(R.id.itv_category_qingyl);
			itvXiaoqx = (ItemTextView)parentView.findViewById(R.id.itv_category_xiaoqx);
			itvWangluo = (ItemTextView)parentView.findViewById(R.id.itv_category_wangluo);
			itvXiaoyuan = (ItemTextView)parentView.findViewById(R.id.itv_category_xiaoyuan);
			itvXinqing = (ItemTextView)parentView.findViewById(R.id.itv_category_xinqing);
			itvJieri = (ItemTextView)parentView.findViewById(R.id.itv_category_jieri);
			itvChangjing = (ItemTextView)parentView.findViewById(R.id.itv_category_changjing);
			itvHuanjing = (ItemTextView)parentView.findViewById(R.id.itv_category_huanjing);
			
			
			itv70 = (ItemTextView)parentView.findViewById(R.id.itv_category_70);
			itv80 = (ItemTextView)parentView.findViewById(R.id.itv_category_80);
			itv90 = (ItemTextView)parentView.findViewById(R.id.itv_category_90);
			
			etSearch.setOnClickListener(this);
			
			viewPager.setOffscreenPageLimit(1);
			viewPager.setOnPageChangeListener(this);
			adverAdapter = new AdverAdapter();
			viewPager.setAdapter(adverAdapter);
			
			mDiangeManger = new DiangeManger(getActivity());
			
			initViews();
			
			showReturnView(false);
			setCenterString("我的曲库");
			
			setScrollSpeed(300);
			
			isPrepared = true;
			lazyLoad();
        }
		else
        {
            ViewGroup parent = (ViewGroup) parentView.getParent();
            if(parent != null) 
            {
                parent.removeView(parentView);
            }
        }
		
		// TODO Auto-generated method stub
		return parentView;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	private void setScrollSpeed(int time)
	{
		try 
		{
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(),
                    new AccelerateInterpolator());
            field.set(viewPager, scroller);
            scroller.setmDuration(time);
        }
		catch (Exception e) 
        {
            
        }
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onResume()
	{
		
		scrollView.scrollTo(0, 0);
		startAdver();
		
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onPause()
	{
		stopAdver();
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	private void initViews()
	{
		itvLocalPhone.getIconView().setImageResource(R.drawable.icon_music_phone);
		itvLocalPhone.getFirstView().setText("手机本地");
		itvLocalSpeaker.getIconView().setImageResource(R.drawable.icon_music_speaker);
		itvLocalSpeaker.getFirstView().setText("店铺音乐");
		itvLocalPhone.showBottomLine(false);
		itvLocalSpeaker.showBottomLine(false);
		itvLocalPhone.showMoreButton(true);
		itvLocalSpeaker.showMoreButton(true);
		
		itvQingyl.getIconView().setImageResource(R.drawable.icon_music_category);
		itvQingyl.getFirstView().setText("轻音乐");
		itvQingyl.showMoreButton(true);
		
		itvXiaoqx.getIconView().setImageResource(R.drawable.icon_music_category);
		itvXiaoqx.getFirstView().setText("小清新");
		itvXiaoqx.showMoreButton(true);
		
		itvWangluo.getIconView().setImageResource(R.drawable.icon_music_category);
		itvWangluo.getFirstView().setText("网络");
		itvWangluo.showMoreButton(true);
		
		itvXiaoyuan.getIconView().setImageResource(R.drawable.icon_music_category);
		itvXiaoyuan.getFirstView().setText("校园");
		itvXiaoyuan.showBottomLine(false);
		itvXiaoyuan.showMoreButton(true);
		
		itvXinqing.getIconView().setImageResource(R.drawable.icon_music_category);
		itvXinqing.getFirstView().setText("心情");
		itvXinqing.showMoreButton(true);
		
		itvJieri.getIconView().setImageResource(R.drawable.icon_music_category);
		itvJieri.getFirstView().setText("节日");
		itvJieri.showMoreButton(true);
		
		itvChangjing.getIconView().setImageResource(R.drawable.icon_music_category);
		itvChangjing.getFirstView().setText("场景");
		itvChangjing.showMoreButton(true);
		
		itvHuanjing.getIconView().setImageResource(R.drawable.icon_music_category);
		itvHuanjing.getFirstView().setText("环境");
		itvHuanjing.showBottomLine(false);
		itvHuanjing.showMoreButton(true);
		
		itv70.getIconView().setImageResource(R.drawable.icon_music_category);
		itv70.getFirstView().setText("70后");
		itv70.showMoreButton(true);
		
		itv80.getIconView().setImageResource(R.drawable.icon_music_category);
		itv80.getFirstView().setText("80后");
		itv80.showMoreButton(true);
		
		itv90.getIconView().setImageResource(R.drawable.icon_music_category);
		itv90.getFirstView().setText("90后");
		itv90.showMoreButton(true);
		
		int iconSize = 26;
		itvLocalPhone.setIconSize(iconSize);
		itvLocalSpeaker.setIconSize(iconSize);
		itvQingyl.setIconSize(iconSize);
		itvXiaoqx.setIconSize(iconSize);
		itvWangluo.setIconSize(iconSize);
		itvXiaoyuan.setIconSize(iconSize);
		itvXinqing.setIconSize(iconSize);
		itvJieri.setIconSize(iconSize);
		itvChangjing.setIconSize(iconSize);
		itvHuanjing.setIconSize(iconSize);
		itv70.setIconSize(iconSize);
		itv80.setIconSize(iconSize);
		itv90.setIconSize(iconSize);
		
		itvLocalPhone.setOnClick(this);
		itvLocalSpeaker.setOnClick(this);
		itvQingyl.setOnClick(this);
		itvXiaoqx.setOnClick(this);
		itvWangluo.setOnClick(this);
		itvXiaoyuan.setOnClick(this);
		itvXinqing.setOnClick(this);
		itvJieri.setOnClick(this);
		itvChangjing.setOnClick(this);
		itvHuanjing.setOnClick(this);
		itv70.setOnClick(this);
		itv80.setOnClick(this);
		itv90.setOnClick(this);
		
		viewPager.setOnPageChangeListener(this);
		adverAdapter = new AdverAdapter();
		viewPager.setAdapter(adverAdapter);
	}
	
	private void updateAdverViews()
	{
		adverItemViews.clear();
		adverList.clear();
		indicatorItemViews.clear();
		
		initAdverSoure();
		
		for(int i=0;i<adverList.size();i++)
		{
			View adverItemView = LayoutInflater.from(getActivity()).inflate(R.layout.view_adver_item, null);
			adverItemViews.add(adverItemView);
			
			AdverViewHolder avh = new AdverViewHolder();
			avh.imageView = (ImageView)adverItemView.findViewById(R.id.iv_adver_bg);
			avh.tvAdverTitle = (TextView)adverItemView.findViewById(R.id.tv_adver_infor_title);
			avh.tvAdverContent = (TextView)adverItemView.findViewById(R.id.tv_adver_infor_content);
			adverViews.add(avh);
			
			ImageView iv = new ImageView(getActivity());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);  
			int m = StringUtils.dip2px(getActivity(), 7);
			lp.setMargins(m, 0, m, 0);  
			iv.setLayoutParams(lp);  
			if(i == 0)
				iv.setBackgroundResource(R.drawable.style_indicator_on);
			else
				iv.setBackgroundResource(R.drawable.style_indicator_off);
			viewIndicator.addView(iv);
			ViewUtils.setViewParams(getActivity(), iv, 6, 6);
			indicatorItemViews.add(iv);
		}
		viewPager.setTotalPageNum(adverList.size());
		adverAdapter.notifyDataSetChanged();
	}
	
	private void initAdverSoure()
	{
		AdverInfor tempInfor1 = new AdverInfor();
		tempInfor1.setImageRes(R.drawable.guide_one);
		tempInfor1.setTile("困了累了，来一首咖啡音乐");
		tempInfor1.setContent("适合下午茶的音乐，繁忙的工作需要适当的解压，来这里点播一首温柔的曲子休息一下");
		tempInfor1.setUrl("http://yinyue.kuwo.cn/yy/cate_14.htm/yy/cate_75469.htm/yy/cinfo_107475.htm");
		
		AdverInfor tempInfor2 = new AdverInfor();
		tempInfor2.setImageRes(R.drawable.guide_two);
		tempInfor2.setTile("那些年，我们一起听过的校园音乐");
		tempInfor2.setContent("上大学了，就不能还当自己是个孩子。再也不会有那样一个你每次上课都看得见的同桌；再也不会有那样一个只要盯着你就让你心惊肉跳的老师；再也不会有那样一群朝夕相处，意气相投的同学。");
		tempInfor2.setUrl("http://yinyue.kuwo.cn/yy/cate_61.htm/yy/cinfo_17188.htm");
		
		AdverInfor tempInfor3 = new AdverInfor();
		tempInfor3.setImageRes(R.drawable.guide_three);
		tempInfor3.setTile("安静的午后,一个人在这里听歌");
		tempInfor3.setContent("如果我们相逢，我将以何来面汝，以沉默以眼泪，忧伤的琴键中，我却觉得自己被安慰，泪珠在阳光下凝结成了完美的樱花形状，纵然枯萎仍有暖意。");
		tempInfor3.setUrl("http://yinyue.kuwo.cn/yy/cate_194.htm/yy/cinfo_77639.htm");
		
		AdverInfor tempInfor4 = new AdverInfor();
		tempInfor4.setImageRes(R.drawable.guide_four);
		tempInfor4.setTile("恋爱，永远是最美好的回忆");
		tempInfor4.setContent("我爱你，没有什么目的。只是爱你。一辈子，就做一次自己。这一次，我想给你全世界。这一次，遍体鳞伤也没关系。这一次，用尽所有的勇敢。 这一次，可以什么都不在乎。但只是这一次就够了。");
		tempInfor4.setUrl("http://yinyue.kuwo.cn/yy/cate_14.htm/yy/cate_75469.htm/yy/cinfo_75492.htm");
		
		adverList.add(tempInfor1);
		adverList.add(tempInfor2);
		adverList.add(tempInfor3);
		adverList.add(tempInfor4);
	}
	
	private void updateIndicator(int pos)
	{
		resetIndicator();
		indicatorItemViews.get(pos).setBackgroundResource(R.drawable.style_indicator_on);
	}
	private void resetIndicator()
	{
		int size = adverList.size();
		for(int i=0;i<size;i++)
		{
			indicatorItemViews.get(i).setBackgroundResource(R.drawable.style_indicator_off);
		}
	}
	
	public void stopAdver()
	{
		viewPager.stopAutoScroll();
	}
	public void startAdver()
	{
		viewPager.startAutoScroll();
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == etSearch)
		{
			if(isOnline())
				ViewUtils.startActivity(getActivity(), SearchMusicActivity.class, null);
			//ViewUtils.startActivity(getActivity(), WYMusicActivity.class, null);
		}
		else if(v == itvLocalPhone.getBgView())
		{
			Bundle bundle = new Bundle();
			bundle.putBoolean("IS_LOCAL", true);
			ViewUtils.startActivity(getActivity(), LocalMusicActivity1.class, bundle);
		}
		else if(v == itvLocalSpeaker.getBgView())
		{
			if(mDiangeManger.isDeviceFind(true))
			{
				/*Bundle bundle = new Bundle();
				bundle.putBoolean("IS_LOCAL", false);
				bundle.putSerializable("DeviceInfor", mDiangeManger.getDeviceInfor());
				getLocalData().setDeviceInfor(mDiangeManger.getDeviceInfor());
				ViewUtils.startActivity(getActivity(), LocalMusicActivity1.class, bundle);*/
				Bundle bundle = new Bundle();
				bundle.putSerializable("DEVICE_INFOR", mDiangeManger.getDeviceInfor());
				ViewUtils.startActivity(getActivity(), SongBookActivity.class, bundle);
			}
		}
		else if(v == itv90.getBgView())//90
		{
			if(isOnline())
				((MainActivity)getActivity()).loadKuwoCategory("http://yinyue.kuwo.cn/yy/cate_47.htm", "90后", false);
		}
		else if(v == itv80.getBgView())//80
		{
			if(isOnline())
				((MainActivity)getActivity()).loadKuwoCategory("http://yinyue.kuwo.cn/yy/cate_46.htm", "80后", false);
		}
		else if(v == itv70.getBgView())//70
		{
			if(isOnline())
				((MainActivity)getActivity()).loadKuwoCategory("http://yinyue.kuwo.cn/yy/cate_45.htm", "70后", false);
		}
		else if(v == itvQingyl.getBgView())//轻音乐
		{
			if(isOnline())
				((MainActivity)getActivity()).loadKuwoCategory("http://yinyue.kuwo.cn/yy/cate_60.htm", "轻音乐", false);
		}
		else if(v == itvXiaoqx.getBgView())//小清新
		{
			if(isOnline())
				((MainActivity)getActivity()).loadKuwoCategory("http://yinyue.kuwo.cn/yy/cate_58.htm", "小清新", false);
		}
		else if(v == itvWangluo.getBgView())//网络
		{
			if(isOnline())
				((MainActivity)getActivity()).loadKuwoCategory("http://yinyue.kuwo.cn/yy/cate_27.htm", "网络", false);
		}
		else if(v == itvXiaoyuan.getBgView())//校园
		{
			if(isOnline())
				((MainActivity)getActivity()).loadKuwoCategory("http://yinyue.kuwo.cn/yy/cate_61.htm", "校园", false);
		}
		else if(v == itvXinqing.getBgView())//心情
		{
			if(isOnline())
				((MainActivity)getActivity()).loadKuwoCategory("http://yinyue.kuwo.cn/yy/cate_13.htm", "心情", true);
		}
		else if(v == itvJieri.getBgView())//节日
		{
			if(isOnline())
				((MainActivity)getActivity()).loadKuwoCategory("http://yinyue.kuwo.cn/yy/cate_12.htm", "节日", false);
		}
		else if(v == itvChangjing.getBgView())//场景
		{
			if(isOnline())
				((MainActivity)getActivity()).loadKuwoCategory("http://yinyue.kuwo.cn/yy/cate_14.htm", "场景", true);
		}
		else if(v == itvHuanjing.getBgView())//环境
		{
			if(isOnline())
				((MainActivity)getActivity()).loadKuwoCategory("http://yinyue.kuwo.cn/yy/cate_194.htm", "环境", false);
		}
		
	}
	/**
	*callbacks
	*/
	@Override
	public void onPageScrollStateChanged(int arg0)
	{
		// TODO Auto-generated method stub
		
	}
	/**
	*callbacks
	*/
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		// TODO Auto-generated method stub
		
	}
	/**
	*callbacks
	*/
	@Override
	public void onPageSelected(int arg0)
	{
		// TODO Auto-generated method stub
		updateIndicator(arg0);
	}
	
	public class AdverAdapter extends PagerAdapter
	{

		@Override
		public int getCount() 
		{
			// TODO Auto-generated method stub
			return adverList.size();
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) 
		{
			View adverView = adverItemViews.get(position);
			
			AdverViewHolder avh = adverViews.get(position);
			
			final AdverInfor tempInfor = adverList.get(position);
			avh.tvAdverTitle.setText(tempInfor.getTitle());
			avh.tvAdverContent.setText(tempInfor.getContent());
			
			Picasso.with(getActivity()).load(tempInfor.getImageRes()).into(avh.imageView);
			
			avh.imageView.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0)
				{
					// TODO Auto-generated method stub
					// TODO Auto-generated method stub
					if(isOnline())
					{
						Bundle bundle = new Bundle();
				    	bundle.putSerializable("ADVER_INFOR", tempInfor);
				    	ViewUtils.startActivity(getActivity(), KuwoMusicActivity.class, bundle);
					}
				}
			});
			
			/*ImageCacheView imageCacheView = new ImageCacheView(imageView, DecodeType.XXXhdpi);
			MyApplication.imageWorker.loadBitmap(tempInfor.getImageRes(), imageCacheView);*/
			
			container.addView(adverView);
			
			// TODO Auto-generated method stub
			return adverView;
		}
		
		@Override
        public void destroyItem(ViewGroup view, int position, Object obj) 
		{
			position = position % adverList.size();
			view.removeView(adverItemViews.get(position));
        }
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) 
		{
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
	}
	
	private class AdverViewHolder
	{
		ImageView imageView = null;
		TextView tvAdverTitle = null;
		TextView tvAdverContent = null;
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
			updateAdverViews();
		}
	}
	
}
 
