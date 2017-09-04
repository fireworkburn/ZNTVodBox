
package com.znt.vodbox.fragment; 

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.znt.vodbox.R;
import com.znt.vodbox.activity.MyActivityManager;
import com.znt.vodbox.db.DBManager;
import com.znt.vodbox.dialog.MyAlertDialog;
import com.znt.vodbox.dialog.MyProgressDialog;
import com.znt.vodbox.dialog.SongAlertDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.LocalDataEntity;
import com.znt.vodbox.factory.DiangeManger;
import com.znt.vodbox.netset.WifiFactory;
import com.znt.vodbox.utils.MyToast;
import com.znt.vodbox.utils.SystemUtils;
import com.znt.vodbox.view.HintView;
import com.znt.vodbox.view.HintView.OnHintListener;

/** 
 * @ClassName: BaseFragment 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-7-18 下午4:16:51  
 */
public abstract class BaseFragment extends Fragment
{
	
	private View parentView = null;
	private MyToast myToast = null;
	private LocalDataEntity localData = null;
	private static MyProgressDialog mProgressDialog;
	private static MyAlertDialog myAlertDialog = null;
	private static SongAlertDialog mSongAlertDialog = null;
	private View viewBack = null;
	private TextView tvRight = null;
	private TextView tvCenter = null;
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
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		parentView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_base, null);
		
		myToast = new MyToast(getActivity());
		
		localData = new LocalDataEntity(getActivity());
		initTitleViews();
		
	}
	
	/*public boolean isDeviceConnected()
	{
		if(DLNAContainer.getInstance().isSpeakerDeviceFind())
		{
			String localUuid = getLocalData().getDeviceId();
			if(DLNAContainer.getInstance().getSpeakerDeviceUuid().equals(localUuid))
			{
				return true;
			}
		}
		String localUuid = getLocalData().getDeviceId();
		if(DnsContainer.getInstance(getActivity()).isServcerFind())
		{
			if(getDeviceInforFromServer().getId().equals(localUuid))
			{
				return true;
			}
		}
		else if(DLNAContainer.getInstance().isSpeakerDeviceFind())
		{
			if(DLNAContainer.getInstance().getSpeakerDeviceUuid().equals(localUuid))
			{
				return true;
			}
		}
		else if(isWifiApConnect() && MinaClient.getInstance().isConnected())
			return true;
		return false;
	}*/
	
	/*public DeviceInfor getDeviceInforFromServer()
	{
		return DnsContainer.getInstance(getActivity()).getInforFromDevice();
	}*/
	
	public View getContentView(int layoutResID) 
	{
		
		((LinearLayout)parentView.findViewById(R.id.layout_content))
		.addView(LayoutInflater.from(getActivity()).inflate(layoutResID, null, false));
		
		return parentView;
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
		viewBack = parentView.findViewById(R.id.view_title_back);
		imageView = (ImageView)parentView.findViewById(R.id.view_title_back_icon);
		tvRight = (TextView)parentView.findViewById(R.id.tv_view_top_right);
		tvCenter = (TextView)parentView.findViewById(R.id.tv_view_top_infor);
		hintView = (HintView)parentView.findViewById(R.id.hintview);
		topView = parentView.findViewById(R.id.rlt_view_top_bg);
		loadingView = parentView.findViewById(R.id.view_loading);
		contentView = parentView.findViewById(R.id.layout_content);
		viewTopRight = parentView.findViewById(R.id.view_base_top_right);
		ivTopRight = (ImageView)parentView.findViewById(R.id.iv_base_top_right);
		
    	viewBack.setOnClickListener(new OnClickListener() 
    	{
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				getActivity().onBackPressed();;
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
	
	public boolean isOnline(boolean showInfor)
	{
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
	public boolean isOnline()
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
			showToast("无网络连接，请先联网!");
		}
		else if(curSSID.equals(getWifiHotName()))
		{
			result = false;
			showToast("当前网络不能访问外部数据~");
		}
		else
			result = true;
		
		return result;
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
	public void showRefreshView(OnHintListener onHintListener, String text, String btnHint)
	{
		hintView.setVisibility(View.VISIBLE);
		hintView.showRefreshView(onHintListener, text, btnHint);
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
	}
	public void hideHintView()
	{
		hintView.setVisibility(View.GONE);
	}
	
	/*****************标题操作********************/
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
	public void setRightTopIcon(int icon)
	{
		ivTopRight.setImageResource(icon);
	}
	
	public void setRightButton(int resId)
	{
		tvRight.setBackgroundResource(resId);
		tvRight.setVisibility(View.VISIBLE);
	}
	public void setRightText(int text)
	{
		tvRight.setVisibility(View.VISIBLE);
		//tvRight.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
    	tvRight.setTextColor(getResources().getColor(R.color.white));
		//tvRight.setBackgroundResource(0);
		//MyViewUtil.setViewParams(tvRight, 300, 130);
		tvRight.setText(text);
	}
	public void setRightText(String text)
	{
		viewTopRight.setVisibility(View.VISIBLE);
		//tvRight.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
    	tvRight.setTextColor(getResources().getColor(R.color.text_blue_on));
		//tvRight.setBackgroundResource(0);
		//MyViewUtil.setViewParams(tvRight, 300, 130);
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
	public static final void showProgressDialog(Activity activity,
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
		
		dismissDialog();
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
	
	public final void showSongErrorDialog(Activity activity
			, String title, String songName, String message) 
	{
		if (TextUtils.isEmpty(title)) 
		{
			title = "提示";
		}
		
		while (activity.getParent() != null) 
		{  
            activity = activity.getParent();  
        }  
		
		if(mSongAlertDialog == null || mSongAlertDialog.isDismissed())
			mSongAlertDialog = new SongAlertDialog(activity, R.style.Theme_CustomDialog);
		mSongAlertDialog.setInfor(title, songName, message);
		if(mSongAlertDialog.isShowing())
			mSongAlertDialog.dismiss();
		mSongAlertDialog.show();
		WindowManager windowManager = ((Activity) activity).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = mSongAlertDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		mSongAlertDialog.getWindow().setAttributes(lp);
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
		if (mSongAlertDialog != null && mSongAlertDialog.isShowing()) 
		{
			mSongAlertDialog.dismiss();
			mSongAlertDialog = null;
		}
	}
	
	public void closeAllActivity()
	{
		MyActivityManager.getScreenManager().popAllActivityExceptionOne(null);
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
	
	public String getCurrentSsid()
	{
		return SystemUtils.getConnectWifiSsid(getActivity());
	}
	
	public static final void showProgressDialog(Context context,
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
	public void setActivityView(View activityView)
	{
		this.activityView = activityView;
	}
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
	
	private boolean isVisible = false;
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) 
	{
		super.setUserVisibleHint(isVisibleToUser);
		
		if(getUserVisibleHint()) 
		{
			isVisible = true;
			onVisible();
		} 
		else 
		{
			isVisible = false;
			onInvisible();
		}
	}
	
	/**
	 * 可见
	 */
	protected void onVisible() 
	{
		lazyLoad();		
	}
	
	/**
	 * 不可见
	 */
	protected void onInvisible() 
	{
		
	}
	
	/** 
	 * 延迟加载
	 * 子类必须重写此方法
	 */
	protected abstract void lazyLoad();
}
 
