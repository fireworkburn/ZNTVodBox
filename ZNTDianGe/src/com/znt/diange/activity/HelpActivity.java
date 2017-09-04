
package com.znt.diange.activity; 

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.entity.AdverInfor;
import com.znt.diange.fragment.HelpItemFragment;
import com.znt.diange.utils.StringUtils;

/** 
 * @ClassName: HelpActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-4-22 下午5:21:27  
 */
public class HelpActivity extends FragmentActivity implements OnClickListener, OnPageChangeListener
{
	private ViewPager viewPager = null;
	
	private View viewTop = null;
	private View viewBack = null;
	private TextView tvTitle = null;
	private LinearLayout viewIndicator = null;
	
	private List<AdverInfor> itemInfors = new ArrayList<AdverInfor>();
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private List<TextView> indicatorItemViews = new ArrayList<TextView>();
	
	private FragAdapter adapter;  
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_help);
		
		viewPager = (ViewPager)findViewById(R.id.vp_help);
		viewTop = findViewById(R.id.rlt_view_help_top_bg);
		viewBack = findViewById(R.id.view_help_title_back);
		tvTitle = (TextView)findViewById(R.id.tv_view_help_top_infor);
		viewIndicator = (LinearLayout)findViewById(R.id.view_help_indicator);
		
		viewPager.setOnPageChangeListener(this);
		
		tvTitle.setText(getResources().getString(R.string.help_doc));
		viewBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		initData();
		
		adapter = new FragAdapter(getSupportFragmentManager(), fragments);
		viewPager.setAdapter(adapter);
		
	}
	
	private void initData()
	{
		AdverInfor infor1 = new AdverInfor();
		infor1.setTile(getResources().getString(R.string.help_step_one));
		infor1.setContent(getResources().getString(R.string.help_step_one_infor));
		infor1.setImageRes(R.drawable.icon_help_one);
		itemInfors.add(infor1);
		fragments.add(new HelpItemFragment(infor1));
		addIndicator(0);
		
		AdverInfor infor2 = new AdverInfor();
		infor2.setTile(getResources().getString(R.string.help_step_two));
		infor2.setContent(getResources().getString(R.string.help_step_two_infor));
		infor2.setImageRes(R.drawable.icon_help_two);
		itemInfors.add(infor2);
		fragments.add(new HelpItemFragment(infor2));
		addIndicator(1);
		
		AdverInfor infor3 = new AdverInfor();
		infor3.setTile(getResources().getString(R.string.help_step_three));
		infor3.setContent(getResources().getString(R.string.help_step_three_infor));
		infor3.setImageRes(R.drawable.icon_help_three);
		itemInfors.add(infor3);
		fragments.add(new HelpItemFragment(infor3));
		addIndicator(2);
		
		AdverInfor infor4 = new AdverInfor();
		infor4.setTile(getResources().getString(R.string.help_step_four));
		infor4.setContent(getResources().getString(R.string.help_step_four_infor));
		infor4.setImageRes(R.drawable.icon_help_four);
		itemInfors.add(infor4);
		fragments.add(new HelpItemFragment(infor4));
		addIndicator(3);
		
		AdverInfor infor5 = new AdverInfor();
		infor5.setTile(getResources().getString(R.string.help_step_five));
		infor5.setContent(getResources().getString(R.string.help_step_five_infor));
		infor5.setImageRes(R.drawable.icon_help_five);
		itemInfors.add(infor5);
		fragments.add(new HelpItemFragment(infor5));
		addIndicator(4);
		
	}
	
	private void addIndicator(int i)
	{
		TextView tv = new TextView(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);  
		int m = StringUtils.dip2px(this, 10);
		lp.setMargins(m, 0, m, 0);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setLayoutParams(lp);  
		if(i == 0)
			setTextViewStatus(tv, true);
		else
			setTextViewStatus(tv, false);
		tv.setText("" + (i + 1));
			
		viewIndicator.addView(tv);
		indicatorItemViews.add(tv);
	}
	
	private void updateIndicator(int pos)
	{
		resetIndicator();
		
		TextView tv = indicatorItemViews.get(pos);
		setTextViewStatus(tv, true);
	}
	private void resetIndicator()
	{
		int size = itemInfors.size();
		for(int i=0;i<size;i++)
		{
			TextView tv = indicatorItemViews.get(i);
			setTextViewStatus(tv, false);
			//ViewUtils.setViewParams(this, tv, 16, 16);
			tv.setText("" + (i + 1));
		}
	}
	
	private void setTextViewStatus(TextView tv, boolean isOn)
	{
		if(isOn)
		{
			tv.setTextSize(16);
			tv.setBackgroundResource(R.drawable.style_indicator_on);
			//ViewUtils.setViewParams(this, tv, 22, 22);
		}
		else
		{
			tv.setTextSize(12);
			tv.setBackgroundResource(R.drawable.style_indicator_off);
			//ViewUtils.setViewParams(this, tv, 16, 16);
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
	
	public class FragAdapter extends FragmentPagerAdapter
	{  
	      
	    private List<Fragment> fragments;  
	      
	  
	    public FragAdapter(FragmentManager fm)
	    {  
	        super(fm);  
	    }  
	      
	    public FragAdapter(FragmentManager fm, List<Fragment> fragments) 
	    {  
	        super(fm);  
	        this.fragments = fragments;  
	    }  
	  
	    @Override  
	    public Fragment getItem(int position) 
	    {  
	        return fragments.get(position);  
	    }  
	  
	    @Override  
	    public int getCount() 
	    {  
	        return fragments.size();  
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
}
 
