
package com.znt.diange.view; 

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.utils.ViewUtils;

/** 
 * @ClassName: HintView 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-10-11 下午3:12:34  
 */
public class HintView extends RelativeLayout implements OnClickListener
{

	private Context context = null;
	
	private View parentView  = null;
	private ImageView imageView = null;
	private TextView textView = null;
	private Button button = null;
	
	private OnHintListener onHintListener = null;
	
	private boolean isRefresh = true;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param attrs
	* @param defStyle 
	*/
	public HintView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	public HintView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	public HintView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initViews(context);
	}

	private void initViews(Context context)
	{
		this.context = context;
		
		parentView = LayoutInflater.from(context).inflate(R.layout.view_hint, this, true);
		
		imageView = (ImageView)parentView.findViewById(R.id.iv_hint_icon);
		textView = (TextView)parentView.findViewById(R.id.tv_hint_infor);
		button = (Button)parentView.findViewById(R.id.btn_hint_click);
		
		button.setOnClickListener(this);
	}
	
	public void showNetWorkSetView()
	{
		imageView.setImageResource(R.drawable.icon_net_work_error);
		textView.setText("无网络连接");
		button.setText("设置网络");
		
		isRefresh = false;
	}
	
	public void showRefreshView(OnHintListener onHintListener)
	{
		if(onHintListener != null)
		{
			this.onHintListener = onHintListener;
			button.setText("刷新");
			button.setVisibility(View.VISIBLE);
		}
		else
		{
			button.setVisibility(View.GONE);
		}
		textView.setText("获取数据失败");
		
		isRefresh = true;
	}
	public void showUnloginView(OnHintListener onHintListener)
	{
		if(onHintListener != null)
		{
			this.onHintListener = onHintListener;
			button.setText("登录");
			button.setVisibility(View.VISIBLE);
		}
		else
		{
			button.setVisibility(View.GONE);
		}
		textView.setText("请先登录");
		
		isRefresh = true;
	}
	
	public void showNoDataView(String hintInfor)
	{
		if(hintInfor == null)
			hintInfor = "";
		textView.setText(hintInfor);
		button.setVisibility(View.GONE);
	}
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == button)
		{
			if(!isRefresh)
			{
				ViewUtils.startNetWorkSet(context);
			}
			else if(onHintListener != null)
				onHintListener.onHintRefresh();
		}
	}
	
	public interface OnHintListener
	{
		public void onHintRefresh();
	}
}
 
