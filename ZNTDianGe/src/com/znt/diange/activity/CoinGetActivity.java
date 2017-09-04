
package com.znt.diange.activity; 

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.znt.diange.R;
import com.znt.diange.factory.HttpFactory;
import com.znt.diange.http.HttpMsg;
import com.znt.diange.mina.entity.CoinInfor;

/** 
 * @ClassName: GetCoinActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-3-15 下午3:24:01  
 */
public class CoinGetActivity extends BaseActivity implements OnClickListener
{

	private TextView tvCoin1 = null;
	private TextView tvCoin2 = null;
	private TextView tvCoin3 = null;
	private TextView tvCoin4 = null;
	private TextView tvCoin5 = null;
	private TextView tvCoin6 = null;
	private TextView tvConfirm = null;
	private TextView tvCoinLeft = null;
	
	private HttpFactory httpFactory = null;
	
	private List<TextView> tvConins = new ArrayList<TextView>();
	
	private int selectCoin = 100;
	private boolean isRunning = false;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == HttpMsg.CONIN_UPLOAD_START)
			{
				isRunning = true;
			}
			else if(msg.what == HttpMsg.CONIN_UPLOAD_SUCCESS)
			{
				showToast("充值成功");
				finish();
			}
			else if(msg.what == HttpMsg.CONIN_UPLOAD_FAIL)
			{
				showToast("充值失败");
				isRunning = false;
			}
			else if(msg.what == HttpMsg.CONIN_GET_START)
			{
				
			}
			else if(msg.what == HttpMsg.CONIN_GET_SUCCESS)
			{
				CoinInfor coinInfor = (CoinInfor)msg.obj;
				if(!TextUtils.isEmpty(coinInfor.getBalance()))
					getLocalData().setCoin(Integer.parseInt(coinInfor.getBalance()));
				tvCoinLeft.setText("剩余金币：" + coinInfor.getBalance()
						+ "\n" + "冻结金币：" + coinInfor.getFreeze());
			}
			else if(msg.what == HttpMsg.CONIN_GET_FAIL)
			{
				
			}
		};
	};
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_get_coin);
		
		setCenterString("我的金币");
		
		tvCoin1 = (TextView)findViewById(R.id.tv_coin_recharge_one);
		tvCoin2 = (TextView)findViewById(R.id.tv_coin_recharge_two);
		tvCoin3 = (TextView)findViewById(R.id.tv_coin_recharge_three);
		tvCoin4 = (TextView)findViewById(R.id.tv_coin_recharge_four);
		tvCoin5 = (TextView)findViewById(R.id.tv_coin_recharge_five);
		tvCoin6 = (TextView)findViewById(R.id.tv_coin_recharge_six);
		tvConfirm = (TextView)findViewById(R.id.tv_coin_recharge_confirm);
		tvCoinLeft = (TextView)findViewById(R.id.tv_get_coin_left);
		
		tvCoin1.setTag(100);
		tvCoin2.setTag(200);
		tvCoin3.setTag(300);
		tvCoin4.setTag(500);
		tvCoin5.setTag(1000);
		tvCoin6.setTag(2000);
		
		tvCoin1.setSelected(true);
		tvCoin1.setTextColor(getResources().getColor(R.color.white));
		
		tvCoin1.setOnClickListener(this);
		tvCoin2.setOnClickListener(this);
		tvCoin3.setOnClickListener(this);
		tvCoin4.setOnClickListener(this);
		tvCoin5.setOnClickListener(this);
		tvCoin6.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
		
		tvConins.add(tvCoin1);
		tvConins.add(tvCoin2);
		tvConins.add(tvCoin3);
		tvConins.add(tvCoin4);
		tvConins.add(tvCoin5);
		tvConins.add(tvCoin6);
		
		httpFactory = new HttpFactory(getActivity(), handler);
		
		httpFactory.getCoin();
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
	*callbacks
	*/
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		if(v == tvConfirm)
		{
			if(getLocalData().getCoin() >= 5000)
			{
				showToast("您的金币已达上限，不能获取更多了");
				return;
			}
			if(isRunning)
				return;
			httpFactory.uploadCoin(selectCoin + "");
		}
		else
		{
			int size = tvConins.size();
			for(int i=0;i<size;i++)
			{
				TextView textView = tvConins.get(i);
				if(v == textView)
				{
					textView.setSelected(true);
					textView.setTextColor(getResources().getColor(R.color.white));
					selectCoin = (Integer) textView.getTag();
				}
				else
				{
					textView.setTextColor(getResources().getColor(R.color.text_blue_on));
					textView.setSelected(false);
				}
			}
		}
	}
}
 
