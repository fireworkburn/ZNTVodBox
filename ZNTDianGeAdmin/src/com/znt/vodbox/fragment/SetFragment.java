
package com.znt.vodbox.fragment; 

import java.io.File;
import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.znt.diange.mina.entity.CoinInfor;
import com.znt.diange.mina.entity.UpdateInfor;
import com.znt.diange.mina.entity.UserInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.activity.AccountActivity;
import com.znt.vodbox.activity.DeviceControllActivity;
import com.znt.vodbox.activity.DeviceEditActivity;
import com.znt.vodbox.activity.MainActivity;
import com.znt.vodbox.activity.MyApplication;
import com.znt.vodbox.activity.UserRecordActivity;
import com.znt.vodbox.activity.WebViewActivity;
import com.znt.vodbox.dialog.PlayControllDialog;
import com.znt.vodbox.dialog.UpdateHintDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.factory.DiangeManger;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.mina.client.ClientHandler;
import com.znt.vodbox.mina.client.MinaClient;
import com.znt.vodbox.utils.ApkTools;
import com.znt.vodbox.utils.DownHelper;
import com.znt.vodbox.utils.DownHelper.MyDownloadListener;
import com.znt.vodbox.utils.StringUtils;
import com.znt.vodbox.utils.SystemUtils;
import com.znt.vodbox.utils.ViewUtils;
import com.znt.vodbox.view.CircleImageView;
import com.znt.vodbox.view.ItemTextView;

/** 
 * @ClassName: SetFragment 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-3-19 下午4:59:54  
 */
public class SetFragment extends BaseFragment implements OnClickListener, MyDownloadListener
{

	private View parentView = null;
	private ItemTextView itvJinbi = null;
	private ItemTextView itvMySpeaker = null;
	private ItemTextView itvDeviceSelect = null;
	private ItemTextView itvSongBook = null;
	private ItemTextView itvSpeakerManager = null;
	private ItemTextView itvSpeakerCtroll = null;
	private ItemTextView itvSpeakerRes = null;
	private ItemTextView itvUpdate = null;
	private ItemTextView itvMark = null;
	private ItemTextView itvFeedBack = null;
	private ItemTextView itvHelp = null;
	private ItemTextView itvAbout = null;
	private ItemTextView itvPcCode = null;
	private ItemTextView itvPermission = null;
	private TextView tvPcCode = null;
	private View viewPcCode = null;
	private TextView tvDot = null;
	private TextView tvUserName = null;
	private TextView tvUserType = null;
	private CircleImageView ivHead = null;
	private View accountView = null;
	private View viewAdmin = null;
	
	private UpdateInfor updateInfor = new UpdateInfor();
	
	private DownHelper downHelper = null;
	
	private HttpFactory httpFactory = null;
	private DiangeManger mDiangeManger = null;
	
	private File apkFile = null;
	
	private boolean isAutoCheck = false;
	private boolean isPrepared = false;
	
	private final int DOWNLOAD_FILE = 3;
	private final int DOWNLOAD_FILE_SUCCESS = 4;
	private final int DOWNLOAD_FILE_FAIL = 5;
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.CHECK_UPDATE_START)
			{
				if(!isAutoCheck)
				{
					showProgressDialog(getActivity(), "正在检测升级信息...", true);
				}
			}
			else if(msg.what == HttpMsg.CHECK_UPDATE_SUCCESS)
			{
				if(!isAutoCheck)
					dismissDialog();
				updateInfor = (UpdateInfor)msg.obj;
				chekVersion();
				
				Constant.isCheckUpdate = false;
			}
			else if(msg.what == HttpMsg.CHECK_UPDATE_FAIL)
			{
				if(!isAutoCheck)
				{
					showToast("检测失败~");
					dismissDialog();
				}
				tvDot.setVisibility(View.GONE);
			}
			else if(msg.what == DOWNLOAD_FILE)
			{
				showProgressDialog(getActivity(), "正在下载升级文件...");
			}
			else if(msg.what == DOWNLOAD_FILE_SUCCESS)
			{
				dismissDialog();
				
				startInstallApk();
			}
			else if(msg.what == DOWNLOAD_FILE_FAIL)
			{
				dismissDialog();
				
				String error = (String)msg.obj;
				if(TextUtils.isEmpty(error))
					error = "下载文件失败";
				showToast(error);
			}
			else if(msg.what == ClientHandler.RECV_ERROR)
			{
				dismissDialog();
				String error = (String)msg.obj;
				if(error != null)
					showToast(error);
			}
			else if(msg.what == HttpMsg.CONIN_GET_START)
			{
				//开始获取金币
			}
			else if(msg.what == HttpMsg.CONIN_GET_SUCCESS)
			{
				CoinInfor coin = (CoinInfor)msg.obj;
				if(!TextUtils.isEmpty(coin.getBalance()))
					getLocalData().setCoin(Integer.parseInt(coin.getBalance()));
				itvJinbi.getSecondView().setText(coin.getBalance());
			}
			else if(msg.what == HttpMsg.CONIN_GET_FAIL)
			{
				/*String error = (String)msg.obj;
				if(TextUtils.isEmpty(error));
					error = "获取金币失败";
				showToast(error);*/
				
				//itvJinbi.getSecondView().setText("获取失败");
				Constant.isCoinUpdate = true;
			}
		};
	};
	
	public SetFragment()
	{
		
	}
	public static SetFragment getInstance()
	{
		return new SetFragment();
	}
	
	/**
	*callbacks
	*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		
		if(parentView == null)
		{
			parentView = getContentView(R.layout.fragment_set);
			itvJinbi = (ItemTextView)parentView.findViewById(R.id.itv_set_jinbi);
			itvMySpeaker = (ItemTextView)parentView.findViewById(R.id.itv_set_my_speaker);
			itvDeviceSelect = (ItemTextView)parentView.findViewById(R.id.itv_set_select_device);
			itvSongBook = (ItemTextView)parentView.findViewById(R.id.itv_set_speaker_song_book);
			itvSpeakerManager = (ItemTextView)parentView.findViewById(R.id.itv_set_speaker_manager);
			itvSpeakerCtroll = (ItemTextView)parentView.findViewById(R.id.itv_set_speaker_controll);
			itvSpeakerRes = (ItemTextView)parentView.findViewById(R.id.itv_set_speaker_play_res);
			itvUpdate = (ItemTextView)parentView.findViewById(R.id.itv_set_update);
			itvMark = (ItemTextView)parentView.findViewById(R.id.itv_set_mark);
			itvFeedBack = (ItemTextView)parentView.findViewById(R.id.itv_set_feed_back);
			itvHelp = (ItemTextView)parentView.findViewById(R.id.itv_set_help);
			itvAbout = (ItemTextView)parentView.findViewById(R.id.itv_set_about);
			itvPcCode = (ItemTextView)parentView.findViewById(R.id.itv_set_pc_code);
			itvPermission = (ItemTextView)parentView.findViewById(R.id.itv_set_speaker_permission);
			tvPcCode = (TextView)parentView.findViewById(R.id.tv_set_pc_code);
			viewPcCode = parentView.findViewById(R.id.view_set_pc_code);
			tvDot = (TextView)parentView.findViewById(R.id.tv_set_update_dot);
			tvUserName = (TextView)parentView.findViewById(R.id.tv_personal_login);
			tvUserType = (TextView)parentView.findViewById(R.id.tv_personal_user_type);
			ivHead = (CircleImageView)parentView.findViewById(R.id.civ_personal_head);
			viewAdmin = parentView.findViewById(R.id.view_set_admin);
			accountView = parentView.findViewById(R.id.view_personal_account);
			
			initViews();
			
			downHelper = new DownHelper();
			downHelper.setDownLoadListener(this);
			
			mDiangeManger = new DiangeManger(getActivity());
			
			File dir = SystemUtils.getAvailableDir(getActivity(), Constant.WORK_DIR + "/update/");
			if (!dir.exists()) 
			{
				dir.mkdirs();
			}
			apkFile = SystemUtils.getAvailableDir(getActivity(), Constant.WORK_DIR + "/update/ZntDiangeGe.apk");
		
			httpFactory = new HttpFactory(getActivity(), handler);
			
			isPrepared = true;
			lazyLoad();
			
		}
		else
		{
			ViewGroup parent = (ViewGroup) parentView.getParent();
			if(parent != null)
				parent.removeView(parentView);
		}
		
		// TODO Auto-generated method stub
		return parentView;
	}
	
	private void autoCheckUpdate()
	{
		isAutoCheck = true;
		if(isOnline(false))
			httpFactory.checkUpdate();
	}
	private void clickCheckUpdate()
	{
		isAutoCheck = false;
		httpFactory.checkUpdate();
	}
	
	private void showPcCode()
	{
		if(MyApplication.isLogin)
		{
			viewPcCode.setVisibility(View.VISIBLE);
			tvPcCode.setText(getLocalData().getPcCode());
		}
		else
			viewPcCode.setVisibility(View.GONE);
	}
	
	private void chekVersion()
	{
		try 
		{
			int localVersionNum = SystemUtils.getPkgInfo(getActivity()).versionCode;
			int updateNum = 0;
			if(!TextUtils.isEmpty(updateInfor.getVersionNum()))
			{
				updateNum = Integer.parseInt(updateInfor.getVersionNum());
				if(updateNum > localVersionNum)//有升级
				{
					//itvUpdate.getSecondView().setText("" + updateInfor.getVersionName());
					if(!isAutoCheck)
					{
						//普通手动升级
						showAlertDialog(getActivity(), new OnClickListener() 
						{
							@Override
							public void onClick(View arg0) 
							{
								dismissDialog();
								// TODO Auto-generated method stub
								doApkInstall(updateInfor.getApkUrl());
							};
						}, "升级提示", "检测到了新版本 :" + updateInfor.getVersionName()+ "，确认升级吗？");
					}
					else
					{
						tvDot.setVisibility(View.VISIBLE);
						//自动检查的判断是否为强制升级
						String type = updateInfor.getUpdateType();
						if(type.equals("0"))//强制升级
						{
							showHintDialog();
						}
					}
				}
				else
				{
					tvDot.setVisibility(View.GONE);
					if(!isAutoCheck)
						showToast("未检测到新版本~");
				}
			}
			else
			{
				tvDot.setVisibility(View.GONE);
				if(!isAutoCheck)
					showToast("未检测到新版本~");
			}
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void showHintDialog()
	{
		UpdateHintDialog hintDialog = new UpdateHintDialog(getActivity());
		
		hintDialog.show();
		
		WindowManager windowManager = getActivity().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = hintDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		hintDialog.getWindow().setAttributes(lp);
		
		hintDialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				// TODO Auto-generated method stub
				doApkInstall(updateInfor.getApkUrl());
			}
		});
	}
	
	private void showPlayControllDialog(boolean isPermissionSet)
	{
		PlayControllDialog hintDialog = new PlayControllDialog(getActivity(), isPermissionSet);
		
		hintDialog.show();
		
		WindowManager windowManager = getActivity().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = hintDialog.getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		lp.height = (int)(display.getHeight()); //设置高度
		hintDialog.getWindow().setAttributes(lp);
		
		hintDialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface arg0)
			{
				// TODO Auto-generated method stub
				MinaClient.getInstance().setOnConnectListener(getActivity(), handler);
			}
		});
	}
	
	
	/**
	*callbacks
	*/
	@Override
	public void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		
		if(MyApplication.isLogin)
		{
			showUserInfor();
			if(Constant.isCoinUpdate)
				httpFactory.getCoin();
			showPcCode();
		}
		else
		{
			showUnlogin();
			itvJinbi.getSecondView().setText("");
		}
		
		showAdminView();
		
		MinaClient.getInstance().setOnConnectListener(getActivity(), handler);
		
		showSpeakerName();
		
	}
	
	public void showUserInfor()
	{
		UserInfor infor = getLocalData().getUserInfor();
		tvUserName.setText(infor.getUserName());
		tvUserType.setVisibility(View.VISIBLE);
		if(getLocalData().isAdminUser())
			tvUserType.setText("管理员");
		else
			tvUserType.setText("店长");
		if(!TextUtils.isEmpty(infor.getHead()))
			Picasso.with(getActivity()).load(infor.getHead()).into(ivHead);
		else
			ivHead.setImageResource(R.drawable.logo);
		
	}
	private void showUnlogin()
	{
		tvUserName.setText("请登录");
		tvUserType.setVisibility(View.GONE);
	}
	
	
	/**
	*callbacks
	*/
	@Override
	public void onPause() 
	{
		// TODO Auto-generated method stub
		super.onPause();
		MinaClient.getInstance().setConnectStop();
	}
	
	private void showAdminView()
	{
		
		/*if(isWifiApConnect() 
				|| (mDiangeManger.isDeviceFind(false) && isAdminDevice()))
			viewAdmin.setVisibility(View.VISIBLE);
		else*/
			viewAdmin.setVisibility(View.GONE);
		//viewAdmin.setVisibility(View.VISIBLE);
	}
	
	public void showSpeakerName()
	{
		if(mDiangeManger.isDeviceFind(false))
			itvMySpeaker.getSecondView().setText(getLocalData().getDeviceInfor().getName());
		else
			itvMySpeaker.getSecondView().setText("未连接设备");
	}
	
	private void initViews()
	{
		showReturnView(false);
		showRightView(true);
		setRightText("切换");
		setRightTopIcon(R.drawable.icon_user_change);
		setCenterString("个人中心");
		
		itvJinbi.getFirstView().setText("我的金币");
		itvMySpeaker.getFirstView().setText("我的音响");
		itvDeviceSelect.getFirstView().setText("添加店铺");
		itvSongBook.getFirstView().setText("音响曲库");
		itvSpeakerManager.getFirstView().setText("音响配置");
		itvSpeakerCtroll.getFirstView().setText("音响控制");
		itvSpeakerRes.getFirstView().setText("曲库切换");
		itvUpdate.getFirstView().setText("检测升级");
		itvMark.getFirstView().setText("系统介绍");
		itvFeedBack.getFirstView().setText("申请试用");
		itvHelp.getFirstView().setText(getResources().getString(R.string.help_doc));
		itvAbout.getFirstView().setText("联系我们");
		itvPcCode.getFirstView().setText("激活码");
		itvPcCode.getSecondView().setText("PC端软件激活码  (点击复制)");
		itvPermission.getFirstView().setText("点播权限");
		
		itvSongBook.getSecondView().setText("音响曲库管理");
		itvSpeakerManager.getSecondView().setText("配置音响网络和名称");
		itvSpeakerCtroll.getSecondView().setText("音量、切歌、播放控制");
		itvSpeakerRes.getSecondView().setText("播放存储设备或者在线歌曲");
		itvPermission.getSecondView().setText("控制用户点播的曲库");
		
		itvJinbi.showMoreButton(true);
		itvMySpeaker.showMoreButton(true);
		itvDeviceSelect.showMoreButton(true);
		itvSongBook.showMoreButton(true);
		itvSpeakerManager.showMoreButton(true);
		itvSpeakerCtroll.showMoreButton(true);
		itvSpeakerRes.showMoreButton(true);
		itvUpdate.showMoreButton(true);
		itvMark.showMoreButton(true);
		itvFeedBack.showMoreButton(true);
		itvHelp.showMoreButton(true);
		itvAbout.showMoreButton(true);
		itvPcCode.showMoreButton(false);
		itvPermission.showMoreButton(true);
		itvMySpeaker.showBottomLine(true);
		
		int jinbi = getLocalData().getCoin();
		itvJinbi.getSecondView().setText(jinbi + "");
		itvJinbi.getSecondView().setTextColor(getResources().getColor(R.color.text_blue_on));
		itvAbout.showBottomLine(true);
		itvPcCode.showBottomLine(false);
		itvPermission.showBottomLine(false);
		itvMySpeaker.getSecondView().setText(getLocalData().getDeviceInfor().getName());
		
		try
		{
			itvUpdate.getSecondView().setText("v" + SystemUtils.getPkgInfo(getActivity()).versionName);
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		itvUpdate.getSecondView().setTextColor(getResources().getColor(R.color.text_black_off));
		
		itvJinbi.getIconView().setImageResource(R.drawable.icon_jinbi);
		itvMySpeaker.getIconView().setImageResource(R.drawable.icon_my_device);
		itvDeviceSelect.getIconView().setImageResource(R.drawable.icon_set_ad_device);
		itvSongBook.getIconView().setImageResource(R.drawable.icon_song_manager);
		itvSpeakerManager.getIconView().setImageResource(R.drawable.icon_speaker_manager);
		itvSpeakerCtroll.getIconView().setImageResource(R.drawable.icon_speaker_controll);
		itvSpeakerRes.getIconView().setImageResource(R.drawable.icon_set_mode);
		itvUpdate.getIconView().setImageResource(R.drawable.icon_set_update);
		itvMark.getIconView().setImageResource(R.drawable.icon_set_mark);
		itvFeedBack.getIconView().setImageResource(R.drawable.icon_set_feed_back);
		itvHelp.getIconView().setImageResource(R.drawable.icon_set_help);
		itvAbout.getIconView().setImageResource(R.drawable.icon_set_about);
		itvPcCode.getIconView().setImageResource(R.drawable.icon_set_pc_code);
		itvPermission.getIconView().setImageResource(R.drawable.icon_set_permission);
		
		int iconSize = 22;
		itvJinbi.setIconSize(iconSize);
		itvMySpeaker.setIconSize(iconSize);
		itvDeviceSelect.setIconSize(iconSize);
		itvSongBook.setIconSize(iconSize);
		itvSpeakerManager.setIconSize(iconSize);
		itvSpeakerCtroll.setIconSize(iconSize);
		itvSpeakerRes.setIconSize(iconSize);
		itvUpdate.setIconSize(iconSize);
		itvMark.setIconSize(iconSize);
		itvFeedBack.setIconSize(iconSize);
		itvHelp.setIconSize(iconSize);
		itvAbout.setIconSize(iconSize);
		itvPcCode.setIconSize(iconSize);
		itvPermission.setIconSize(iconSize);
		
		itvJinbi.setOnClick(this);
		itvSpeakerManager.setOnClick(this);
		itvSongBook.setOnClick(this);
		itvSpeakerCtroll.setOnClick(this);
		itvSpeakerRes.setOnClick(this);
		itvMySpeaker.setOnClick(this);
		itvDeviceSelect.setOnClick(this);
		itvUpdate.setOnClick(this);
		itvMark.setOnClick(this);
		itvFeedBack.setOnClick(this);
		itvHelp.setOnClick(this);
		itvAbout.setOnClick(this);
		itvPcCode.setOnClick(this);
		itvPermission.setOnClick(this);
		accountView.setOnClickListener(this);
		
		getRightView().setOnClickListener(this);
		
	}
	
	private void doApkInstall(String url)
	{
		if(apkFile.exists() && isSignatureMatch())//检测到有新的版本就先删除本地的apk
			startInstallApk();
		else
			downloadApkFile(url);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == itvUpdate.getBgView())
		{
			if(isOnline())
			{
				/*if(updateInfor != null && !TextUtils.isEmpty(updateInfor.getVersionName()))
				{
					//已经获取了升级信息
					isAutoCheck = false;
					chekVersion();
				}
				else
					clickCheckUpdate();*/
				//com.tencent.android.qqdownloader
				ViewUtils.launchAppDetail(getActivity(), "com.znt.vodbox", "");
			}
			else
				showToast("请先连接网络");
			
		}
		else if(v == itvJinbi.getBgView())
		{
			
			
		}
		else if(v == itvFeedBack.getBgView())
		{
			//((MainActivity)getActivity()).loadFeedBackPage();
			String url = getString(R.string.get_box);
			Bundle bundle = new Bundle();
			bundle.putString("TITLE", "申请试用");
			bundle.putString("URL", url);
			ViewUtils.startActivity(getActivity(), WebViewActivity.class, bundle);
		}
		else if(v == itvMark.getBgView())
		{
			String url = getString(R.string.system_detail);
			Bundle bundle = new Bundle();
			bundle.putString("TITLE", "店音管家系统介绍");
			bundle.putString("URL", url);
			ViewUtils.startActivity(getActivity(), WebViewActivity.class, bundle);
			//MarketUtils.launchAppDetail(getActivity(), "com.znt.diange", "");
		}
		else if(v == itvHelp.getBgView())
		{
			((MainActivity)getActivity()).loadHelpPage();
		}
		else if(v == itvAbout.getBgView())
		{
			((MainActivity)getActivity()).loadAboutPage();
		}
		else if(v == itvPcCode.getBgView())
		{
			String content = tvPcCode.getText().toString();
			if(!TextUtils.isEmpty(content))
			{
				StringUtils.copy(content, getActivity());
				showToast("激活码已复制到剪贴板");
			}
			else
				showToast("请先登录获取激活码");
		}
		else if(v == itvPermission.getBgView())
		{
			showPlayControllDialog(true);
		}
		/*else if(v == itvMySpeaker.getBgView())
		{
			if(mDiangeManger.isDeviceFind(true))
				ViewUtils.startActivity(getActivity(), SpeakerInforActivity.class, null);
		}*/
		else if(v == itvDeviceSelect.getBgView())
		{
			ViewUtils.startActivity(getActivity(), DeviceEditActivity.class, null);
		}
		else if(v == itvSpeakerManager.getBgView())
		{
			if(isWifiApConnect() || mDiangeManger.isDeviceFind(true))
				ViewUtils.startActivity(getActivity(), DeviceEditActivity.class, null, 4);
		}
		else if(v == itvSpeakerCtroll.getBgView())
		{
			if(isWifiApConnect() || mDiangeManger.isDeviceFind(true))
				ViewUtils.startActivity(getActivity(), DeviceControllActivity.class, null);
		}
		else if(v == itvSpeakerRes.getBgView())
		{
			showPlayControllDialog(false);
		}
		else if(v == accountView)//账户信息
		{
			Bundle bundle = new Bundle();
			bundle.putBoolean("INIT", false);
			ViewUtils.startActivity(getActivity(), AccountActivity.class, bundle, 1);
		}
		else if(v == getRightView())
		{
			Bundle bundle = new Bundle();
			ViewUtils.startActivity(getActivity(), UserRecordActivity.class, bundle, 6);
		}
	}
	private void downloadApkFile(final String downUrl)
	{
		downHelper.downloadFile(downUrl, apkFile.getAbsolutePath());
	}
	
	private void startInstallApk()
	{
		if(isFileValid())
		{
			Intent intent = new Intent(Intent.ACTION_VIEW); 
			intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive"); 
			startActivity(intent);
		}
	}
	
	private boolean isFileValid()
	{
		if(!apkFile.exists())
		{
			showToast("升级文件不存在,请重新下载~");
			return false;
		}
		if(apkFile.length() == 0)
		{
			showToast("升级文件无效,请重新下载~");
			apkFile.delete();
			return false;
		}
		
		
		return isSignatureMatch();
	}
	
	private boolean isSignatureMatch()
	{
		String curSign = ApkTools.getSignature(getActivity());
		List<String> signs = ApkTools.getSignaturesFromApk(apkFile);
		
		if(curSign == null || signs.size() == 0 
				|| signs.get(0) == null || !curSign.equals(signs.get(0)))
		{
			showToast("升级文件签名不一致,请重新下载~");
			apkFile.delete();
			return false;
		}
		
		return true;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onDownloadStart(int total)
	{
		// TODO Auto-generated method stub
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				showProgressDialog(getActivity(), "正在下载升级文件...", new OnDismissListener()
				{
					@Override
					public void onDismiss(DialogInterface arg0)
					{
						// TODO Auto-generated method stub
						downHelper.stop();
						//showToast("下载终止...");
					}
				});
			}
		});
	}

	/**
	*callbacks
	*/
	@Override
	public void onFileExist(String fileName)
	{
		// TODO Auto-generated method stub
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				showToast("升级文件已存在");
				dismissDialog();
			}
		});
	}

	/**
	*callbacks
	*/
	@Override
	public void onDownloadProgress(final int progress, final int size)
	{
		// TODO Auto-generated method stub
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				showProgressDialog(getActivity(), "正在下载升级文件..." + (int)((float)progress / size * 100) + "%");
			}
		});
		
	}

	/**
	*callbacks
	*/
	@Override
	public void onDownloadError(final String error)
	{
		// TODO Auto-generated method stub
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				dismissDialog();
				showToast(error);
			}
		});
	}

	/**
	*callbacks
	*/
	@Override
	public void onDownloadFinish()
	{
		// TODO Auto-generated method stub
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				dismissDialog();
				
				startInstallApk();
			}
		});
	}

	/**
	*callbacks
	*/
	@Override
	public void onDownloadExit()
	{
		// TODO Auto-generated method stub
		dismissDialog();
	}
	/**
	*callbacks
	*/
	@Override
	protected void lazyLoad()
	{
		// TODO Auto-generated method stub
		if(isPrepared)
		{
			autoCheckUpdate();
		}
	}
}
 
