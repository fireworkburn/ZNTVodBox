
package com.znt.vodbox.dialog; 

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;

/** 
 * @ClassName: MusicPlayDialog 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-7-20 下午3:30:43  
 */
public class VolumeSetDialog extends Dialog
{

	private TextView textTitle = null;
	private TextView textInfor = null;
	private SeekBar seekBar = null;
	private Button btnLeft = null;
	private android.view.View.OnClickListener listener = null;
	
	private HttpFactory httpFactory = null;
	private DeviceInfor deviceInfor = null;
	private Activity context = null;
	private boolean isDismissed = false;
	private boolean isStop = false;
	private boolean isUpdateRunning = false;
	private boolean isVolumeUpdated = false;
	
	private final int PREPARE_FINISH = 0;
	private final int PREPARE_FAIL = 1;
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.UPDATE_SPEAKER_INFOR_START)
			{
				isUpdateRunning = true;
				showProgressDialog(context, null);
			}
			else if(msg.what == HttpMsg.UPDATE_SPEAKER_INFOR_SUCCESS)
			{
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						textInfor.setText(seekBar.getProgress() + " / " + 15);
						isUpdateRunning = false;
						isVolumeUpdated = true;
						Toast.makeText(context, "设置成功", 0).show();
						dismissDialog();
					}
				}, Constant.SUC_DELAY_TIME);
			}
			else if(msg.what == HttpMsg.UPDATE_SPEAKER_INFOR_FAIL)
			{
				dismissDialog();
				textInfor.setText(deviceInfor.getVolume() + " / " + 15);
				seekBar.setProgress(deviceInfor.getVolume());
				
				isUpdateRunning = false;
				Toast.makeText(context, "设置失败", 0).show();
			}
			else if(msg.what == HttpMsg.NO_NET_WORK_CONNECT)
			{
				textInfor.setText(deviceInfor.getVolume() + " / " + 15);
				seekBar.setProgress(deviceInfor.getVolume());
				
				isUpdateRunning = false;
				Toast.makeText(context, "无网络连接", 0).show();
			}
		};
	};
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context 
	*/
	public VolumeSetDialog(Activity context, DeviceInfor deviceInfor)
	{
		super(context, R.style.Theme_CustomDialog);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.deviceInfor = deviceInfor;
	}

    /** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param themeCustomdialog 
	*/
	public VolumeSetDialog(Activity context, int themeCustomdialog)
	{
		super(context, themeCustomdialog);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
    {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog_volume_set);
	    setScreenBrightness();
	    
	    Window window = getWindow();
		window.setWindowAnimations(R.style.AnimAlph);
	    
		seekBar = (SeekBar) VolumeSetDialog.this.findViewById(R.id.sb_dialog_volume_set_progress);
    	btnLeft = (Button) VolumeSetDialog.this.findViewById(R.id.btn_dialog_volume_set_left);
    	textTitle = (TextView) VolumeSetDialog.this.findViewById(R.id.tv_dialog_volume_set_title);
        textInfor = (TextView) VolumeSetDialog.this.findViewById(R.id.tv_dialog_volume_set_progress);
		
        httpFactory = new HttpFactory(context, handler);
        
	    this.setOnShowListener(new OnShowListener()
	    {
            @Override
            public void onShow(DialogInterface dialog)
            {
            	initViews();
            }
        });
	    
	    int cur = deviceInfor.getVolume();
	    seekBar.setMax(15);
	    seekBar.setProgress(cur);
	    
	    textInfor.setText(cur + " / " + 15);
	    
	    seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			@Override
			public void onStopTrackingTouch(SeekBar sb)
			{
				// TODO Auto-generated method stub
				isStop = false;
				updateSpeakerInfor(sb.getProgress());
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int position, boolean fromUser)
			{
				// TODO Auto-generated method stub
				if(fromUser)
				{
					//MyLog.e("onProgressChanged position-->"+position);
					
					isStop = true;
					textInfor.setText(position + " / " + 15);
				}
			}
		});
	}
	
	public boolean isVolumeUpdated()
	{
		return isVolumeUpdated;
	}
	public int getCurVolume()
	{
		return seekBar.getProgress();
	}
	
    private void initViews()
    {
        setCanceledOnTouchOutside(false);
        
        btnLeft.setOnClickListener(new android.view.View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				isDismissed = true;
				dismiss();
			}
		});
    }
    
    private void updateSpeakerInfor(int volume)
	{
    	if(!isUpdateRunning)
    		httpFactory.updateSpeakerVolume(volume + "", deviceInfor.getCode());
	}
    
    public boolean isDismissed()
    {
    	return isDismissed;
    }
    
    public void setOnClickListener(android.view.View.OnClickListener listener)
    {
    	this.listener  = listener;
    }
    
    public void setDeviceInfor(DeviceInfor deviceInfor)
    {
    	this.deviceInfor = deviceInfor;
    }
    
    public void updateProgress(String text)
    {
    	this.textInfor.setText(text);
    }
    
    public Button getLeftButton()
    {
    	return btnLeft;
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
    private MyProgressDialog mProgressDialog = null;
	private final void showProgressDialog(Activity activity,
			String message) 
	{
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		if (TextUtils.isEmpty(message)) 
		{
			message = "正在加载...";
		}
		if(mProgressDialog == null)
			mProgressDialog = new MyProgressDialog(activity, R.style.Theme_CustomDialog);
		mProgressDialog.setInfor(message);
		
		if(!mProgressDialog.isShowing())
		{
			mProgressDialog.show();
			WindowManager windowManager = (activity).getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = mProgressDialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()); //设置宽度
			lp.height = (int)(display.getHeight()); //设置高度
			mProgressDialog.getWindow().setAttributes(lp);
		}
	}
    private void dismissDialog()
	{
		if(mProgressDialog != null && mProgressDialog.isShowing())
		{
			mProgressDialog.dismiss();
		}
	}
}