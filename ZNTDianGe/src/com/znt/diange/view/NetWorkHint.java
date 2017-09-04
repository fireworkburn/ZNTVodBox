
package com.znt.diange.view; 

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.utils.SystemUtils;
import com.znt.diange.utils.ViewUtils;

/** 
 * @ClassName: NetWorkHint 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-4-16 下午4:45:23  
 */
public class NetWorkHint extends RelativeLayout
{

	private View parentView = null;
	private TextView tvSetNet = null;
	private TextView tvVersion = null;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param attrs
	* @param defStyle 
	*/
	public NetWorkHint(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	public NetWorkHint(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initViews(context);
	}
	public NetWorkHint(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		
		initViews(context);
	}
	
	private void initViews(final Context context)
	{
		
		parentView = LayoutInflater.from(context).inflate(R.layout.view_network_error_hint, this, true);
		
		tvSetNet = (TextView)parentView.findViewById(R.id.tv_network_setting);
		tvVersion = (TextView)parentView.findViewById(R.id.tv_version);
		
		try
		{
			tvVersion.setText("V" + SystemUtils.getPkgInfo(context).versionName);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tvSetNet.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				ViewUtils.startNetWorkSet(context);
			}
		});
	}

}
 
