
package com.znt.diange.view; 

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.utils.StringUtils;
import com.znt.diange.utils.ViewUtils;

/** 
 * @ClassName: ItemTextView 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-10-30 下午1:26:01  
 */
public class ItemTextView extends RelativeLayout
{

	private Context context = null;
	
	private View parentView = null;
	private ImageView ivIcon = null;
	private TextView tvOne = null;
	private TextView tvTwo = null;
	private ImageView imageView = null;
	private View topLine = null;
	private View bottomLine = null;
	private View bgView = null;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param attrs
	* @param defStyle 
	*/
	public ItemTextView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	public ItemTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	public ItemTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initViews(context);
	}

	private void initViews(Context context)
	{
		this.context = context;
		
		parentView = LayoutInflater.from(context).inflate(R.layout.item_text_view, this, true);
		
		tvOne = (TextView)parentView.findViewById(R.id.tv_text_item_one);
		tvTwo = (TextView)parentView.findViewById(R.id.tv_text_item_two);
		imageView = (ImageView)parentView.findViewById(R.id.iv_text_item_more);
		ivIcon = (ImageView)parentView.findViewById(R.id.iv_text_item_icon);
		topLine = parentView.findViewById(R.id.view_text_item_top);
		bottomLine = parentView.findViewById(R.id.view_text_item_bottom);
		bgView = parentView.findViewById(R.id.view_item_text_view_bg);
	}
	
	public View getBgView()
	{
		return bgView;
	}
	
	public void setOnClick(OnClickListener listener)
	{
		bgView.setOnClickListener(listener);
	}
	
	public TextView getFirstView()
	{
		return tvOne;
	}
	public TextView getSecondView()
	{
		return tvTwo;
	}
	
	public void setSecondHint(String text)
	{
		tvTwo.setTextColor(context.getResources().getColor(R.color.text_black_off));
		tvTwo.setText(text);
	}
	public void setSecondText(String text)
	{
		tvTwo.setTextColor(context.getResources().getColor(R.color.text_black_mid));
		tvTwo.setText(text);
	}
	
	
	public ImageView getMoreView()
	{
		imageView.setVisibility(View.VISIBLE);
		return imageView;
	}
	
	public void hideIocn()
	{
		ivIcon.setVisibility(View.GONE);
	}
	public ImageView getIconView()
	{
		ivIcon.setVisibility(View.VISIBLE);
		return ivIcon;
	}
	public void setIconSize(int iconSize)
	{
		ViewUtils.setViewParams(context, ivIcon, iconSize, iconSize);
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
	public void showMoreButton(boolean show)
	{
		if(show)
			imageView.setVisibility(View.VISIBLE);
		else
			imageView.setVisibility(View.GONE);
	}
	
	public void setPadding(Activity activity, int left, int top, int right, int bottom)
	{
		left = StringUtils.dip2px(activity, left);
		top = StringUtils.dip2px(activity, top);
		right = StringUtils.dip2px(activity, right);
		bottom = StringUtils.dip2px(activity, bottom);
		bgView.setPadding(left, top, right, bottom);
	}
	
	public void setTextSize(int size)
	{
		tvOne.setTextSize(size);
		tvTwo.setTextSize(size);
	}
	
}