package com.znt.vodbox.activity;

import java.io.File;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.diange.mina.entity.CoinInfor;
import com.znt.diange.mina.entity.MediaInfor;
import com.znt.diange.mina.entity.UpdateInfor;
import com.znt.vodbox.R;
import com.znt.vodbox.dialog.UpdateHintDialog;
import com.znt.vodbox.entity.Constant;
import com.znt.vodbox.entity.SubPlanInfor;
import com.znt.vodbox.factory.DiangeManger;
import com.znt.vodbox.factory.HttpFactory;
import com.znt.vodbox.fragment.MusicFragment;
import com.znt.vodbox.fragment.MusicLibraryFragment;
import com.znt.vodbox.fragment.MyMusicFragment;
import com.znt.vodbox.fragment.SetFragment;
import com.znt.vodbox.fragment.ShopFragment;
import com.znt.vodbox.fragment.SongFragment;
import com.znt.vodbox.http.HttpMsg;
import com.znt.vodbox.mina.client.MinaClient;
import com.znt.vodbox.netset.WifiFactory;
import com.znt.vodbox.timer.CheckSsidTimer;
import com.znt.vodbox.utils.ApkTools;
import com.znt.vodbox.utils.DownHelper;
import com.znt.vodbox.utils.DownHelper.MyDownloadListener;
import com.znt.vodbox.utils.SystemUtils;
import com.znt.vodbox.utils.ViewUtils;

public class MainActivity extends MyFragmentActivity implements OnClickListener,MyDownloadListener
{

	private View viewShop = null;
	private View viewSong = null;
	private View viewMyMusic = null;
	private View viewPersonal = null;
	private TextView tvSong = null;
	private TextView tvShop = null;
	private TextView tvMyMusic = null;
	private TextView tvPersonal = null;
	
	private FragmentTransaction transaction;
	private SongFragment songFragment = null;
	private MusicLibraryFragment musicLibraryFragment = null;
	private SetFragment setFragment = null;
	private ShopFragment shopFragment = null;
	private MusicFragment musicFragment = null;
	private MyMusicFragment myMusicFragment = null;
	
	private HttpFactory httpFactory = null;
	private DownHelper downHelper = null;
	private DiangeManger mDiangeManger = null;
	
	private CheckSsidTimer checkSsidTimer = null;
	
	private UpdateInfor updateInfor = null;
	private File apkFile = null;
	private final int DOWNLOAD_FILE = 3;
	private final int DOWNLOAD_FILE_SUCCESS = 4;
	private final int DOWNLOAD_FILE_FAIL = 5;
	private final int CHECK_WIFI_AP = 6;
	
	//private ResolveInfo homeInfo = null;
	
	public static MediaInfor musicInfor = new MediaInfor();
	
	private boolean isInit = true;
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.CHECK_UPDATE_SUCCESS)
			{
				updateInfor = (UpdateInfor)msg.obj;
				chekVersion();
				
				Constant.isCheckUpdate = false;
			}
			else if(msg.what == HttpMsg.CHECK_UPDATE_FAIL)
			{
				
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
			else if(msg.what == CHECK_WIFI_AP)
			{
				if(getCurrentSsid().endsWith(Constant.UUID_TAG))
				{
					//当前连接的是wifi热点就取消热点的扫描
					stopCheckAp();
					return;
				}
				String wifiApSsid = WifiFactory.newInstance(getActivity()).getWifiAdmin().ifHasWifiAp();
				if(!TextUtils.isEmpty(wifiApSsid))
				{
					stopCheckAp();//停止扫描音响热点
					startConnectHot(wifiApSsid);
				}
				if(checkSsidTimer.isOver())
					checkSsidTimer.stopTimer();
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
				Constant.isCoinUpdate = false;
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.activity_main);
        
		//底部控制视图
		viewShop = findViewById(R.id.view_main_bottom_shop);
		viewSong = findViewById(R.id.view_main_bottom_dianbo);
		viewPersonal = findViewById(R.id.view_main_bottom_personal);
		viewMyMusic = findViewById(R.id.view_main_bottom_my_music);
		tvSong = (TextView)findViewById(R.id.tv_main_bottom_dianbo);
		tvShop = (TextView)findViewById(R.id.tv_main_bottom_shop);
		tvMyMusic = (TextView)findViewById(R.id.tv_main_bottom_my_music);
		tvPersonal = (TextView)findViewById(R.id.tv_main_bottom_personal);
		
		viewShop.setOnClickListener(this);
		viewSong.setOnClickListener(this);
		viewMyMusic.setOnClickListener(this);
		viewPersonal.setOnClickListener(this);
		
		mDiangeManger = new DiangeManger(getActivity());
		
		checkSsidTimer = new CheckSsidTimer(getActivity());
		checkSsidTimer.setHandler(handler, CHECK_WIFI_AP);
		checkSsidTimer.setTimeInterval(3000);
		checkSsidTimer.setMaxTime(15);
		WifiFactory.newInstance(getActivity()).getWifiAdmin().startScan();
		startCheckAp();
		
		downHelper = new DownHelper();
		downHelper.setDownLoadListener(this);
		File dir = SystemUtils.getAvailableDir(getActivity(), Constant.WORK_DIR + "/update/");
		if (!dir.exists()) 
		{
			dir.mkdirs();
		}
		apkFile = SystemUtils.getAvailableDir(getActivity(), Constant.WORK_DIR + "/update/ZntDiangeGe.apk");
		
		httpFactory = new HttpFactory(getActivity(), handler);
		
		updateIndicator(0);
		
		firstInit();
		
		//startDMS();
		loadShopPage();
        
        if(getLocalData().isFirstInit())
		{
			//第一次运行删除本地的工作目录
			String worDir = SystemUtils.getAvailableDir(getActivity(), Constant.WORK_DIR).getAbsolutePath();
			File worDirFile = new File(worDir);
			if(worDirFile != null && worDirFile.exists())
				worDirFile.delete();
		}
        
        //DnsContainer.getInstance(getActivity()).restartDns();
        
        if(isOnline(false) && Constant.isCheckUpdate)
        	httpFactory.checkUpdate();
        
        /*if(MyApplication.isLogin)
        	httpFactory.getCoin();*/
        
    }
    
    private void startCheckAp()
	{
		if(!getCurrentSsid().endsWith(Constant.UUID_TAG))
			checkSsidTimer.startTimer();
	}
	private void stopCheckAp()
	{
		checkSsidTimer.stopTimer();
	}
	private void startConnectHot(final String wifiApSsid)
	{
		//当前连接的是热点就不提示
		if(getCurrentSsid().endsWith(Constant.UUID_TAG))
			return;
		showAlertDialog(getActivity(), new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				DeviceInfor tempInfor = new DeviceInfor();
				tempInfor.setAvailable(true);
				tempInfor.setId(wifiApSsid);
				tempInfor.setWifiName(wifiApSsid);
				tempInfor.setWifiPwd("00000000");
				Bundle bundle = new Bundle();
				bundle.putSerializable("DEVICE_INFOR", tempInfor);
				ViewUtils.startActivity(getActivity(), NetWorkChangeActivity.class, bundle);
				dismissDialog();
			}
		}, "设备网络发现提示", "发现设备网络，确认连接该设备吗？");
	}
    
    private void firstInit()
    {
    	if(getLocalData().isFirstInit())
    	{
    		getLocalData().setFirstInit(false);
    		//getLocalData().setCoin(9999);
    		/*String worDir = SystemUtils.getAvailableDir(getActivity(), Constant.WORK_DIR).getAbsolutePath();
    		File worDirFile = new File(worDir);
    		if(worDirFile != null && worDirFile.exists())
    			worDirFile.delete();*/
    	}
    	/*else //清除本地列表记录
    		DBManager.newInstance(getActivity()).deleteLocalSong();*/
    }
    
    /**
    * @Description: 显示主页
    * @param    
    * @return void 
    * @throws
     */
    public void loadMainPage()
    {
    	loadFragment("MusicFragment");
    }
    public void loadMyMusicPage()
    {
    	loadFragment("MyMusicFragment");
    }
    public void loadMusicLibraryPage()
    {
    	loadFragment("MusicLibraryFragment");
    }
    public void loadShopPage()
    {
    	loadFragment("ShopFragment");
    }
    public void loadHelpPage()
    {
    	ViewUtils.startActivity(getActivity(), HelpActivity.class, null);
    }
    public void loadAboutPage()
    {
    	ViewUtils.startActivity(getActivity(), AboutActivity.class, null);
    }
    public void loadSetPage()
    {
    	if(setFragment == null)
    		setFragment = SetFragment.getInstance();
    	loadFragment("SetFragment");
    }
    public void loadKuwoCategory(String url, String title, boolean hasNextCategory)
    {
    	Bundle bundle = new Bundle();
    	bundle.putString("HTTP_URL", url);
    	bundle.putString("TITLE", title);
    	bundle.putBoolean("hasNextCategory", hasNextCategory);
    	ViewUtils.startActivity(getActivity(), KuwoCategoryActivity.class, bundle);
    }
    
    public void loadFragment(String tag)
    {
    	transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, 0);
    	if(tag.equals("MusicFragment"))
    	{
    		updateIndicator(2);
    		
    		if(musicFragment == null)
    			musicFragment = new MusicFragment();
			transaction.replace(R.id.view_main_container, musicFragment, tag);
			transaction.addToBackStack(tag);// 添加到Activity管理的回退栈中。
    	}
    	else if(tag.equals("MyMusicFragment"))
    	{
    		updateIndicator(1);
    		
    		if(myMusicFragment == null)
    			myMusicFragment = new MyMusicFragment();
    		transaction.replace(R.id.view_main_container, myMusicFragment, tag);
    		transaction.addToBackStack(tag);// 添加到Activity管理的回退栈中。
    	}
    	else if(tag.equals("ShopFragment"))
    	{
    		updateIndicator(0);
    		
    		if(shopFragment == null)
    			shopFragment = new ShopFragment();
    		transaction.replace(R.id.view_main_container, shopFragment, tag);
    		transaction.addToBackStack(tag);// 添加到Activity管理的回退栈中。
    	}
    	else if(tag.equals("SetFragment"))
    	{
    		updateIndicator(3);
    		
    		transaction.replace(R.id.view_main_container, setFragment, tag);
    		transaction.addToBackStack(tag);// 添加到Activity管理的回退栈中。
    	}
    	transaction.commit();
    }
    
    /**
    *callbacks
    */
    @Override
    protected void onResume()
    {
    	MyActivityManager.getScreenManager().popAllActivityExceptionOne(MainActivity.class);
    	
    	if(Constant.isSongSended)
    	{
    		if(songFragment == null || !songFragment.isVisible())
    			loadMainPage();
    		Constant.isSongSended = false;
    	}
    	startMinaConnect();
    	
    	/*if(isWifiApConnect())
			ViewUtils.startActivity(getActivity(), DeviceEditActivity.class, null);*/
    	
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
    private void startMinaConnect()
	{
		if(mDiangeManger.isDeviceFind(false) && !MinaClient.getInstance().isConnected())
		{
			MinaClient.getInstance().startClient();
		}
	}
    
    public boolean isServiceRunning(Context mContext,String className) 
    {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
        		mContext.getSystemService(Context.ACTIVITY_SERVICE); 
        List<ActivityManager.RunningServiceInfo> serviceList 
        = activityManager.getRunningServices(30);
       if (!(serviceList.size()>0)) 
       {
            return false;
       }
       for (int i=0; i<serviceList.size(); i++) 
       {
           if (serviceList.get(i).service.getClassName().equals(className) == true) 
           {
               isRunning = true;
               break;
           }
       }
        return isRunning;
    }
    
    /**
    *callbacks
    */
    @Override
    protected void onPause()
    {
    	WifiFactory.newInstance(getActivity()).setOnConnectResultListener(null);
    	// TODO Auto-generated method stub
    	super.onPause();
    }
    
    /**
    *callbacks
    */
    @Override
    protected void onStop()
    {
    	// TODO Auto-generated method stub
    	super.onStop();
    }
    
    /**
	*callbacks
	*/
	@Override
	protected void onDestroy()
	{
		if(httpFactory != null)
			httpFactory.stopHttp();
		// TODO Auto-generated method stub
		super.onDestroy();
	}
    
    /**
    * @Description: 显示音乐信息
    * @param @param infor   
    * @return void 
    * @throws
     */
    private void showMediaInfor(MediaInfor infor)
    {
    	if(!TextUtils.isEmpty(infor.getMediaName()))
    	{
    		
    	}
    	else
    	{
    		
    	}
    }
    
    /**
    *callbacks
    */
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent data)
    {
    	
    	if(arg1 != RESULT_OK)
    		return;
    	if(arg0 == 1)
    	{
    		//扫描到了设备才注册
    		/*if(DLNAContainer.getInstance().getSelectedDevice() != null)
    			registerPlayServece();*/
    	}
    	else if(arg0 == 2)
    	{
    		musicInfor = (MediaInfor)data.getSerializableExtra("MediaInfor");
    		showMediaInfor(musicInfor);
    	}
    	else if(arg0 == 3)
    	{
    		//SongInfor tempInfor = (SongInfor)data.getSerializableExtra("SongInfor");
    		songFragment.updateSongList();
    		//songFragment.getCurPlaySong();
    	}
    	else if(arg0 == 4)
    	{
    		//restartDLNAService();
    		if(songFragment != null)
    		{
    			songFragment.showDeviceName();
        		setFragment.showSpeakerName();
    		}
    	}
    	/*else if(arg0 == 5)
    	{
    		//restartDLNAService();
    		SubPlanInfor subPlanInfor = (SubPlanInfor)data.getSerializableExtra("SubPlanInfor");
    		if(shopFragment != null)
    		{
    			shopFragment.showAlbumNames(subPlanInfor.getPlanAlbumName());
    		}
    	}
    	else if(arg0 == 6)
    	{
    		setFragment.showUserInfor();
    		shopFragment.reloadShops();
    	}*/
    	// TODO Auto-generated method stub
    	super.onActivityResult(arg0, arg1, data);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	private long touchTime = 0;
	@Override
	public void onBackPressed()
	{
		if(!isInit)
		{
			finish();
		}
		else
		{
			if((System.currentTimeMillis() - touchTime) < 2000)
			{
				 closeApp();
				 super.onBackPressed();
				// TODO Auto-generated method stub
			}
		    else
			{
				showToast("再按一次退出 店音管家");
				touchTime = System.currentTimeMillis();
			}
		}
		
		//moveTaskToBack(true); 
	}
	private void closeApp()
	{
		if(MinaClient.getInstance().isConnected())
			MinaClient.getInstance().close();
		
		closeAllActivity();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}
	
	private void updateIndicator(int index)
	{
		resetBottomView();
		
		if(index == 0)
		{
			tvShop.setTextColor(getResources().getColor(R.color.text_blue_on));
			viewShop.setSelected(true);
		}
		else if(index == 1)
		{
			tvMyMusic.setTextColor(getResources().getColor(R.color.text_blue_on));
			viewMyMusic.setSelected(true);
		}
		else if(index == 2)
		{
			tvSong.setTextColor(getResources().getColor(R.color.text_blue_on));
			viewSong.setSelected(true);
		}
		else if(index == 3)
		{
			tvPersonal.setTextColor(getResources().getColor(R.color.text_blue_on));
			viewPersonal.setSelected(true);
		}
		
	}
	
	private void resetBottomView()
	{
		tvSong.setTextColor(getResources().getColor(R.color.text_black_mid));
		tvShop.setTextColor(getResources().getColor(R.color.text_black_mid));
		tvPersonal.setTextColor(getResources().getColor(R.color.text_black_mid));
		tvMyMusic.setTextColor(getResources().getColor(R.color.text_black_mid));
		
		viewSong.setSelected(false);
		viewShop.setSelected(false);
		viewPersonal.setSelected(false);
		viewMyMusic.setSelected(false);
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == viewMyMusic)
		{
			//updateIndicator(1);
			loadMyMusicPage();
		}
		else if(v == viewSong)
		{
			//updateIndicator(2);
			loadMainPage();
		}
		else if(v == viewShop)
		{
			//updateIndicator(0);
			loadShopPage();
		}
		else if(v == viewPersonal)
		{
			//updateIndicator(3);
			loadSetPage();
		}
		
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
					String type = updateInfor.getUpdateType();
					if(type.equals("1"))//强制升级
					{
						showHintDialog();
					}
				}
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
		
		hintDialog.setInfor("检测到了新版本：" + updateInfor.getVersionName() + " 请升级到该版本");
		
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
	
	private void doApkInstall(String url)
	{
		if(apkFile.exists() && isSignatureMatch())//检测到有新的版本就先删除本地的apk
			startInstallApk();
		else
			downloadApkFile(url);
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
}
