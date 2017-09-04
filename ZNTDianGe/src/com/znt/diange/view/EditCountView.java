package com.znt.diange.view;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.znt.diange.utils.MyToast;

public class EditCountView extends EditText
{

	private Context context = null;
	private MyToast myToast = null;
	
	private int maxCount = 0;
	private int editStart;
	private int editEnd;
	
	public EditCountView(Context context) 
	{
		super(context);
		// TODO Auto-generated constructor stub
		initViews(context);
		
	}
	public EditCountView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		initViews(context);
		
	}
	public EditCountView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		
		initViews(context);
		
	}
	
	public void setMaxCount(int maxCount)
	{
		this.maxCount = maxCount;
	}
	
	private void initViews(Context context)
	{
		
		this.context = context;
	
		myToast = new MyToast(context);
		
		setOnFocusChangeListener(new OnFocusChangeListener() 
		{
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) 
			{
				// TODO Auto-generated method stub
				if(arg1)
					addTextChangeListener();
			}
		});
		
	}
	
	private void addTextChangeListener()
	{
		
		if(maxCount <= 0)
			return;
		
		setFilters(new InputFilter[] { new InputFilter()
		{
		    @Override
		    public CharSequence filter(CharSequence source, int start,
		      int end, Spanned dest, int dstart, int dend) 
		    {
		    	int sLen = source.length();
	        	int dLen = dest.length();
	        	   
	        	if(dLen < maxCount)//输入框中已经有的字符串长度小于要求的
	        	{
	        		int rLen = maxCount - dLen;
	        		if(sLen > rLen)//将要输入的字符串的长度大于可输入的字符串长度
	        		{
	        			//截取将要输入的字符串
	        			source = source.subSequence(0, rLen);
	        		}
	        	}
	        	
	             if (sLen > 0 && dLen >= maxCount) 
			     {
			    	 //这里可以写一些字数超过时弹出Toast
			    	 myToast.show("最多只能输入 " + maxCount + " 个哦");
			    	 return "";//长度超过了当前的字符就不要显示了，也就不返回了
			     } 
			     else
			    	 return source;//没超过就送给editText
		    	}
			} 
		}); 
		
	}
	
	
	
	/**
	 * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
	 * 
	 * @param c
	 * @return
	 */
	private long calculateLength(CharSequence c) 
	{
		double len = 0;
		for (int i = 0; i < c.length(); i++) 
		{
			int tmp = (int) c.charAt(i);
			if (tmp > 0 && tmp < 127) 
			{
				len += 0.5;
			} 
			else 
			{
				len++;
			}
		}
		return Math.round(len);
	}
}
