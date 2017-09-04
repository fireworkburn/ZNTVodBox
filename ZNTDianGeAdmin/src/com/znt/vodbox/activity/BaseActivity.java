package com.znt.vodbox.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.db.DBManager;
import com.znt.vodbox.dialog.MyAlertDialog;
import com.znt.vodbox.dialog.MyProgressDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.factory.DiangeManger;
import com.znt.vodbox.netset.WifiFactory;
import com.znt.vodbox.utils.MyToast;
import com.znt.vodbox.utils.SystemUtils;
import com.znt.vodbox.view.HintView;
import com.znt.vodbox.view.HintView.OnHintListener;

public class BaseActivity extends Activity
{
	private MyToast myToast = null;
	private LocalDataEntity localData = null;
	private MyProgressDialog mProgressDialog;
	private MyAlertDialog myAlertDialog = null;
	private View viewBack = null;
	private TextView tvRight = null;
	private TextView tvCenter = null;
	private ProgressBar progressBar = null;
	private HintView hintView = null;
	private View topView = null;
	private View loadingView = null;
	private View contentView = null;
	private View viewTopRight = null;
	private ImageView ivTopRight = null;
	private ImageView imageView = null;
	
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		activityView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_base, null);
		super.setContentView(activityView);
		setTitle(null);
		
		myToast = new MyToast(getApplicationContext());
		
		MyActivityManager.getScreenManager().pushActivity(this);
		localData = new LocalDataEntity(getActivity());
		
		initTitleViews();
		
	}
	
	public String getCurrentSsid()
	{
		return SystemUtils.getConnectWifiSsid(getActivity());
	}
	
	public void showTopView(boolean visiable)
	{
		if(visiable)
			topView.setVisibility(View.VISIBLE);
		else
			topView.setVisibility(View.GONE);
	}
	
	private void initTitleViews()
	{
		viewBack = findViewById(R.id.view_title_back);
		imageView = (ImageView)findViewById(R.id.view_title_back_icon);
		tvRight = (TextView)findViewById(R.id.tv_view_top_right);
		tvCenter = (TextView)findViewById(R.id.tv_view_top_infor);
		progressBar = (ProgressBar)findViewById(R.id.pb_view_top_right);
		hintView = (HintView)findViewById(R.id.hintview);
		topView = findViewById(R.id.rlt_view_top_bg);
		loadingView = findViewById(R.id.view_loading);
		contentView = findViewById(R.id.layout_content);
		viewTopRight = findViewById(R.id.view_base_top_right);
		ivTopRight = (ImageView)findViewById(R.id.iv_base_top_right);
		
		viewBack.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
    	contentView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				SystemUtils.hideInputView(getActivity());
			}
		});
	}
	
	public String getWifiHotName()
	{
		return Constant.WIFI_HOT_NAME + localData.getDeviceInfor().getId();
	}
	
	public boolean isWifiApConnect()
	{
		String curSsid = getCurrentSsid();
		if(TextUtils.isEmpty(curSsid))
			return false;
		return curSsid.endsWith(Constant.UUID_TAG);
	}
	
	/*****************提示页面********************/
	public void showLoadingView(boolean isShow)
	{
		if(isShow)
		{
			loadingView.setVisibility(View.VISIBLE);
			contentView.setVisibility(View.INVISIBLE);
		}
		else
		{
			loadingView.setVisibility(View.INVISIBLE);
			contentView.setVisibility(View.VISIBLE);
		}
	}
	
	public void showReturnView(boolean isShow)
	{
		if(isShow)
			viewBack.setVisibility(View.VISIBLE);
		else
			viewBack.setVisibility(View.GONE);
	}
	public void setBackViewIcon(int resId)
	{
		imageView.setImageResource(resId);
		viewBack.setVisibility(View.VISIBLE);
	}
	public View getBackView()
	{
		return viewBack;
	}
	
	/**
	* @Description: 显示设置网络页面
	* @param    
	* @return void 
	* @throws
	 */
	public void showNetWorkSetView()
	{
		hintView.setVisibility(View.VISIBLE);
		hintView.showNetWorkSetView();
	}
	/**
	* @Description: 显示刷新页面
	* @param @param onHintListener   
	* @return void 
	* @throws
	 */
	public void showRefreshView(OnHintListener onHintListener)
	{
		hintView.setVisibility(View.VISIBLE);
		hintView.showRefreshView(onHintListener);
	}
	/**
	* @Description: 显示没有数据页面
	* @param @param hintInfor   
	* @return void 
	* @throws
	 */
	public void showNoDataView(String hintInfor)
	{
		hintView.setVisibility(View.VISIBLE);
		hintView.showNoDataView(hintInfor);
		loadingView.setVisibility(View.GONE);
	}
	public void hideHintView()
	{
		hintView.setVisibility(View.GONE);
	}
	
	/*****************标题操作********************/
	
	public void showProgressView(boolean isVisiable)
	{
		if(isVisiable)
		{
			progressBar.setVisibility(View.VISIBLE);
			tvRight.setVisibility(View.GONE);
		}
		else
		{
			progressBar.setVisibility(View.GONE);
			tvRight.setVisibility(View.VISIBLE);
		}
		
	}
	
	public View getRightView()
	{
		return viewTopRight;
	}
	public void showRightView(boolean isShow)
	{
		showRightImageView(isShow);
		if(isShow)
			viewTopRight.setVisibility(View.VISIBLE);
		else
			viewTopRight.setVisibility(View.GONE);
	}
	public void showRightImageView(boolean isShow)
	{
		if(isShow)
			ivTopRight.setVisibility(View.VISIBLE);
		else
			ivTopRight.setVisibility(View.GONE);
	}
	
	public void setRightButton(int resId)
	{
		tvRight.setBackgroundResource(resId);
		tvRight.setVisibility(View.VISIBLE);
	}
	public void setRightText(int text)
	{
		viewTopRight.setVisibility(View.VISIBLE);
		//tvRight.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
    	tvRight.setTextColor(getResources().getColor(R.color.text_blue_on));
		//tvRight.setBackgroundResource(0);
		//MyViewUtil.setViewParams(tvRight, 300, 130);
		tvRight.setText(text);
	}
	public void setRightText(String text)
	{
		viewTopRight.setVisibility(View.VISIBLE);
    	tvRight.setTextColor(getResources().getColor(R.color.text_blue_on));
		//tvRight.setBackgroundResource(0);
		tvRight.setText(text);
	}
	public void setCenterString(int text)
	{
		tvCenter.setVisibility(View.VISIBLE);
		tvCenter.setText(text);
	}
	public void setCenterString(String text)
	{
		tvCenter.setVisibility(View.VISIBLE);
		tvCenter.setText(text);
	}
	
	public void setRightTopIcon(int icon)
	{
		ivTopRight.setImageResource(icon);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDestroy()
	{
		MyActivityManager.getScreenManager().popActivity(this);
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public void setContentView(int layoutResID)
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(layoutResID, null);
		((LinearLayout)findViewById(R.id.layout_content)).addView(view);
	}
	
	
	public boolean isOnline(boolean showInfor)
	{
		if(SystemUtils.is3gConnected(getActivity()))
			return true;
		
		if(!WifiFactory.newInstance(getActivity()).getWifiAdmin().isWifiEnabled())
			return false;
		
		//0,无网络连接  1，连接的是WIFI热点 2，正常连接
		boolean result = false;
		String curSSID = getCurrentSsid();
		if(TextUtils.isEmpty(curSSID))
		{
			result = false;
			if(showInfor)
				showToast("无网络连接，请先联网!");
		}
		else if(curSSID.equals(getWifiHotName()))
		{
			result = false;
			if(showInfor)
				showToast("当前网络不能访问外部数据~");
		}
		else
			result = true;
		
		return result;
	}
	
	public final void showProgressDialog(Activity activity,
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
	public final void showProgressDialog(Activity activity, 
			String message, boolean isBackEnable) 
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
		mProgressDialog.setBackEnable(isBackEnable);
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
	
	public final void showAlertDialog(Activity activity, OnClickListener listener, String title,
			String message) 
	{
		if (TextUtils.isEmpty(title)) 
		{
			title = "提示";
		}
		
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		if(myAlertDialog == null || myAlertDialog.isDismissed())
			myAlertDialog = new MyAlertDialog(activity, R.style.Theme_CustomDialog);
		myAlertDialog.setInfor(title, message);
		if(myAlertDialog.isShowing())
			myAlertDialog.dismiss();
		myAlertDialog.show();
		myAlertDialog.setOnClickListener(listener);
		
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = myAlertDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		myAlertDialog.getWindow().setAttributes(lp);
	}
	
	public final void showAlertDialog(Activity activity, String title,
			String message) 
	{
		
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		if (TextUtils.isEmpty(title)) 
		{
			title = "提示";
		}
		
		
		if(myAlertDialog == null || myAlertDialog.isDismissed())
			myAlertDialog = new MyAlertDialog(activity, R.style.Theme_CustomDialog);
		myAlertDialog.setInfor(title, message);
		if(myAlertDialog.isShowing())
			myAlertDialog.dismiss();
		myAlertDialog.show();
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = myAlertDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		myAlertDialog.getWindow().setAttributes(lp);
	}

	public final void dismissDialog() 
	{
		if(getActivity() == null || getActivity().isFinishing())
			return;
		if (mProgressDialog != null && mProgressDialog.isShowing()) 
		{
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		if (myAlertDialog != null && myAlertDialog.isShowing()) 
		{
			myAlertDialog.dismiss();
			myAlertDialog = null;
		}
	}
	
	public void closeAllActivity()
	{
		MyActivityManager.getScreenManager().popAllActivityExceptionOne(null);
	}
	
	public Activity getActivity()
	{
		return BaseActivity.this;
	}
	
	public LocalDataEntity getLocalData()
	{
		return localData;
	}
	
	public DBManager getDBManager()
	{
		return DBManager.newInstance(getActivity());
	}
	
	public void showToast(int res)
	{
		myToast.show(res);
	}
	public void showToast(String res)
	{
		myToast.show(res);
	}
	
	public final void showProgressDialog(Context context,
			String message, OnDismissListener listener) 
	{
		if (TextUtils.isEmpty(message)) 
		{
			message = "正在加载...";
		}
		if(mProgressDialog == null)
		{
			mProgressDialog = new MyProgressDialog(context, R.style.Theme_CustomDialog);
			mProgressDialog.setOnDismissListener(listener);
		}
		mProgressDialog.setInfor(message);
		
		if(!mProgressDialog.isShowing())
		{
			mProgressDialog.show();
			WindowManager windowManager = ((Activity) context).getWindowManager();
			Display display = windowManager.getDefaultDisplay();
			WindowManager.LayoutParams lp = mProgressDialog.getWindow().getAttributes();
			lp.width = (int)(display.getWidth()); //设置宽度
			lp.height = (int)(display.getHeight()); //设置高度
			mProgressDialog.getWindow().setAttributes(lp);
		}
	}
	
	private View activityView = null;
	/*public void setActivityView(View activityView)
	{
		this.activityView = activityView;
	}*/
	public void showActivityAnim()
    {
		if(activityView != null)
		{
			final Animation mScaleAnimation = AnimationUtils.loadAnimation(getActivity(),
					R.anim.activity_back);
			mScaleAnimation.setDuration(800);
			mScaleAnimation.setFillAfter(true);
			activityView.startAnimation(mScaleAnimation);
		}
    }
    public void clearActivityAnim()
    {
    	if(activityView != null)
    	{
    		activityView.clearAnimation();
    	}
    }
    
    public boolean isAdminDevice()
    {
    	String bindDeviceId = getLocalData().getUserInfor().getBindDevices();
		String localDeviceId = getLocalData().getDeviceId();
		DiangeManger diangeManager = new DiangeManger(getActivity());
		if(diangeManager.isDeviceFind(false) &&
				!TextUtils.isEmpty(localDeviceId)
				&& !TextUtils.isEmpty(bindDeviceId)
				&& bindDeviceId.contains(localDeviceId))
		{
			return true;
		}
    	return false;
    }
    
}
