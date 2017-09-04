package com.znt.vodbox.dialog;

import java.util.List;

import com.znt.vodbox.R;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class VideoDirectionDialog extends Dialog
{
	private Context context;
	private View parentView = null;
	private TextView tvCancel = null;
	private TextView tvOne = null;
	private TextView tvTwo = null;
	private TextView tvThree = null;
	private TextView tvFour = null;
	
	private String screenOri = "";
	private String devId = "";
	
	private HttpFactory httpFactory = null;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.UPDATE_SPEAKER_INFOR_START)
			{
				
			}
			else if(msg.what == HttpMsg.UPDATE_SPEAKER_INFOR_SUCCESS)
			{
				dismiss();
				Toast.makeText(getContext(), "设置成功", 0).show();
			}
			else if(msg.what == HttpMsg.UPDATE_SPEAKER_INFOR_FAIL)
			{
				Toast.makeText(getContext(), "设置屏幕失败", 0).show();
				screenOri = null;
			}
		};
	};
	public VideoDirectionDialog(Context context)
	{
		super(context, R.style.MMTheme_DataSheet);
		// TODO Auto-generated constructor stub
		this.context = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parentView = inflater.inflate(R.layout.dialog_video_direction, null);
		
		httpFactory = new HttpFactory(context, handler);
		
	    setScreenBrightness();
	    Window window = getWindow();
		window.setWindowAnimations(R.style.MMTheme_DataSheet);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	public void showDialog(String screenOri, String devId)
	{
		this.screenOri = screenOri;
		this.devId = devId;
		
        tvCancel = (TextView)parentView. findViewById(R.id.tv_dialog_wifi_cancel);
        tvOne = (TextView)parentView. findViewById(R.id.tv_video_direc_one);
        tvTwo = (TextView)parentView. findViewById(R.id.tv_video_direc_two);
        tvThree = (TextView)parentView. findViewById(R.id.tv_video_direc_three);
        tvFour = (TextView)parentView. findViewById(R.id.tv_video_direc_four);
        
        Window w = getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		
		final int cFullFillWidth = 10000;
		parentView.setMinimumWidth(cFullFillWidth);
		
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		onWindowAttributesChanged(lp);
		setCanceledOnTouchOutside(true);
		setContentView(parentView);
		show();
		
		tvCancel.setOnClickListener(new android.view.View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		tvOne.setOnClickListener(new android.view.View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("0");
			}
		});
		tvTwo.setOnClickListener(new android.view.View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("1");
			}
		});
		tvThree.setOnClickListener(new android.view.View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("2");
			}
		});
		tvFour.setOnClickListener(new android.view.View.OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				updateVideoDirection("3");
			}
		});
	}
	
	private void updateVideoDirection(String direct)
	{
		if(screenOri.equals(direct))
		{
			screenOri = null;
			dismiss();
			return;
		}
		httpFactory.updateSpeakerWhirl(direct, devId);
		screenOri = direct;
	}
	
	public String getCurDerection()
	{
		
		return screenOri ;
	}
	
	private void setScreenBrightness() 
    {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        /**
        *  此处设置亮度值。dimAmount代表黑暗数量，也就是昏暗的多少，设置为0则代表完全明亮。
        *  范围是0.0到1.0
        */
        lp.dimAmount = 0;
        window.setAttributes(lp);
    }
}
