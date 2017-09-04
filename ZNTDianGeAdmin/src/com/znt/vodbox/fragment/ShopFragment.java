/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-1 下午3:01:27 
* @Version V1.1   
*/ 

package com.znt.vodbox.fragment; 

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.PlanListActivity;
import com.znt.vodbox.activity.SearchShopActivity;
import com.znt.vodbox.adapter.MyFragmentPagerAdapter;
import com.znt.vodbox.entity.PlanInfor;
import com.znt.vodbox.utils.ViewUtils;

/** 
 * @ClassName: ShopFragment 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-1 下午3:01:27  
 */
public class ShopFragment extends BaseFragment 
{

	private View parentView = null;
	Resources resources;
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ImageView ivBottomLine;
    private TextView tvTabNew, tvTabHot;

    private int currIndex = 0;
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;
    public final static int num = 2 ; 
    private PlanInfor planInfor = null;
    LeveShopFragment leveShopFragment;
    AllShopFragment allShopFragment;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(parentView == null)
		{
			parentView = getContentView(R.layout.fragment_shop);
			
			setBackViewIcon(R.drawable.icon_search_shop);
			setCenterString("我的店铺");
			showTopView(true);
			setRightText("总部计划");
			setRightTopIcon(R.drawable.icon_plan_item);
			
			resources = getResources();
	        InitWidth(parentView);
	        InitTextView(parentView);
	        InitViewPager(parentView);
	        /*TranslateAnimation animation = new TranslateAnimation(position_one, offset, 0, 0);
	        animation.setFillAfter(true);
	        animation.setDuration(300);
	        ivBottomLine.startAnimation(animation);*/
	        
	        setCenterString("我的店铺");
	        showTopView(true);
	        
	        getRightView().setOnClickListener(new OnClickListener() 
			{
				
				@Override
				public void onClick(View arg0)
				{
					// TODO Auto-generated method stub
					/*if(deviceList.size() == 0)
					{
						showToast("您没有绑定设备，不能创建计划");
						return;
					}*/
					Bundle bundle = new Bundle();
					bundle.putSerializable("PlanInfor", planInfor);
					ViewUtils.startActivity(getActivity(), PlanListActivity.class, bundle);
				}
			});
			getBackView().setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					// TODO Auto-generated method stub
					ViewUtils.startActivity(getActivity(), SearchShopActivity.class,null);
				}
			});
		}
		else
		{
			ViewGroup parent = (ViewGroup) parentView.getParent();
            if(parent != null) 
            {
                parent.removeView(parentView);
            }
		}
        
		return parentView;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onResume() 
	{
		// TODO Auto-generated method stub
		if(currIndex != 0)
			updateIndicator(currIndex);
		super.onResume();
	}
	
	  private void InitTextView(View parentView) 
	  {
	        tvTabNew = (TextView) parentView.findViewById(R.id.tv_tab_1);
	        tvTabHot = (TextView) parentView.findViewById(R.id.tv_tab_2);

	        tvTabNew.setOnClickListener(new MyOnClickListener(0));
	        tvTabHot.setOnClickListener(new MyOnClickListener(1));
	    }

	    private void InitViewPager(View parentView) 
	    {
	        mPager = (ViewPager) parentView.findViewById(R.id.vPager);
	        fragmentsList = new ArrayList<Fragment>();

	        leveShopFragment = new LeveShopFragment();
	        allShopFragment = new AllShopFragment();

	        fragmentsList.add(allShopFragment);
	        fragmentsList.add(leveShopFragment);
	        
	        mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentsList));
	        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	        mPager.setCurrentItem(0);
	    }

	    private void InitWidth(View parentView) 
	    {
	        ivBottomLine = (ImageView) parentView.findViewById(R.id.iv_bottom_line);
	        
	        DisplayMetrics dm = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			
			bottomLineWidth = dm.widthPixels / 3;
			
			LinearLayout.LayoutParams cursor_bg_Params = (LinearLayout.LayoutParams) ivBottomLine.getLayoutParams();
			cursor_bg_Params.width = bottomLineWidth;
			ivBottomLine.setLayoutParams(cursor_bg_Params);
	        
	        int screenW = dm.widthPixels;
	        offset = (int) ((screenW / num - bottomLineWidth) / 2);
	        int avg = (int) (screenW / num);
	        position_one = avg + offset;
	        
	        
	    }

	    private void updateIndicator(int arg0)
	    {
	    	Animation animation = null;
	    	if (arg0 == 1) 
            {
	    		animation = new TranslateAnimation(offset, position_one, 0, 0);
                tvTabHot.setTextColor(resources.getColor(R.color.text_blue_off));
                tvTabNew.setTextColor(resources.getColor(R.color.text_black_mid));
            } 
	    	else if (arg0 == 0) 
            {
	    		animation = new TranslateAnimation(position_one, offset, 0, 0);
                tvTabNew.setTextColor(resources.getColor(R.color.text_blue_off));
                tvTabHot.setTextColor(resources.getColor(R.color.text_black_mid));
                 
            } 
	    	if(animation != null)
            {
            	animation.setFillAfter(true);
	            animation.setDuration(300);
	            ivBottomLine.startAnimation(animation);
            }
	    }
	    
	    public class MyOnClickListener implements View.OnClickListener 
	    {
	        private int index = 0;

	        public MyOnClickListener(int i) 
	        {
	            index = i;
	        }

	        @Override
	        public void onClick(View v) 
	        {
	            mPager.setCurrentItem(index);
	        }
	    };
	    
	    public class MyOnPageChangeListener implements OnPageChangeListener
	    {
	        @Override
	        public void onPageSelected(int arg0)
	        {
	        	/*switch (arg0) 
	            {
		            case 0:
		            	currIndex = 1;
		            case 1:
		            	currIndex = 0;
	            }*/
	        	updateIndicator(arg0);
	            currIndex = arg0;
	            /*if(arg0 == 0)
					allShopFragment.lazyLoad();
				else */if(arg0 == 1)
					leveShopFragment.loadData();
	        }

	        @Override
	        public void onPageScrolled(int arg0, float arg1, int arg2)
	        {
	        	
	        }

	        @Override
	        public void onPageScrollStateChanged(int arg0) 
	        {
	        	
	        }
	    }

		/**
		*callbacks
		*/
		@Override
		protected void lazyLoad()
		{
			// TODO Auto-generated method stub
			
		}

}
 
