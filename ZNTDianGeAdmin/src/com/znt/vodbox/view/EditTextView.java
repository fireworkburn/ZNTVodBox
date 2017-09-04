package com.znt.vodbox.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.utils.EmojiFilter;

public class EditTextView extends RelativeLayout
{

	private View parentView = null;
	private TextView tvLabel = null;
	private EditCountView etContent = null;
	private View topLine = null;
	private View bottomLine = null;
	
	public EditTextView(Context context) 
	{
		super(context);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	public EditTextView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	public EditTextView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initViews(context);
	}

	private void initViews(Context context)
	{
		parentView = LayoutInflater.from(context).inflate(R.layout.view_edit_text, this, true);
		
		tvLabel = (TextView)parentView.findViewById(R.id.tv_edit_text_label);
		etContent = (EditCountView)parentView.findViewById(R.id.et_edit_text_content);
		topLine = parentView.findViewById(R.id.view_edit_text_top);
		bottomLine = parentView.findViewById(R.id.view_edit_text_bottom);
	}
	
	public EditText getEditText()
	{
		return etContent;
	}
	
	public void setMinLines(int line)
	{
		etContent.setMinLines(line);
		etContent.setSingleLine(false);
	}
	
	public void setMaxCount(int maxCount)
	{
		etContent.setMaxCount(maxCount);
	}
	
	public void setInputType(int type)
	{
		//自动换行 InputType.TYPE_TEXT_FLAG_MULTI_LINE
		//InputType.TYPE_CLASS_NUMBER//电话
		//InputType.TYPE_NUMBER_FLAG_DECIMAL |InputType.TYPE_CLASS_NUMBER//数字带小数
		etContent.setInputType(type);
	}
	
	public void enableInput(boolean enable)
	{
		etContent.setEnabled(enable);
	}
	
	public void setLable(String text)
	{
		tvLabel.setText(text);
	}
	public void setLable(int text)
	{
		tvLabel.setText(text);
	}
	public TextView getLable()
	{
		return tvLabel;
	}
	
	public void setText(String text)
	{
		etContent.setText(text);
	}
	public void setText(int text)
	{
		etContent.setText(text);
	}
	
	public void setHint(String text)
	{
		etContent.setHint(text);
	}
	public void setHint(int text)
	{
		etContent.setHint(text);
	}
	
	public void setInputEnable(boolean enable)
	{
		etContent.setFocusable(enable); 
		etContent.setFocusableInTouchMode(enable); 
		etContent.setEnabled(enable);
	}
	
	public String getText()
	{
		return EmojiFilter.filterEmoji(etContent.getText().toString());
	}
	
	public void showTopLine(boolean show)
	{
		if(show)
			topLine.setVisibility(View.VISIBLE);
		else
			topLine.setVisibility(View.GONE);
	}
	public void showBottomLine(boolean show)
	{
		if(show)
			bottomLine.setVisibility(View.VISIBLE);
		else
			bottomLine.setVisibility(View.GONE);
	}
}
