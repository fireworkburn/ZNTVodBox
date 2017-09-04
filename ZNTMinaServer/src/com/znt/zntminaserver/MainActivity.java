package com.znt.zntminaserver;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.mina.core.session.IoSession;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.znt.speaker.mina.server.MinaServer;
import com.znt.speaker.mina.server.ServerHandler;
import com.znt.speaker.mina.server.ServerHandler.OnMessageReceiveListener;

public class MainActivity extends Activity implements OnMessageReceiveListener, OnClickListener
{

	private Button btnStart = null;
	private Button btnQuit = null;
	private TextView tvStatus = null;
	private TextView tvContent = null;
	
	private int serverState = MinaServer.SERVER_CLOSED;
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			if(msg.what == MinaServer.SERVER_OPENING)
			{
				btnStart.setText("正在开启服务端");
				btnStart.setEnabled(false);
				
				serverState = msg.what;
				
			}
			else if(msg.what == MinaServer.SERVER_OPENED)
			{
				btnStart.setText("停止服务");
				btnStart.setEnabled(true);
				ServerHandler.setOnMessageReceiveListener(MainActivity.this);
				
				serverState = msg.what;
				
				tvStatus.setText("ip：" + getIP() + "端口号：8888");
				
			}
			else if(msg.what == MinaServer.SERVER_OPEN_FAIL)
			{
				btnStart.setText("开启失败，正在重试");
				btnStart.setEnabled(false);
				
				serverState = msg.what;
				
			}
			else if(msg.what == MinaServer.SERVER_CLOSED)
			{
				btnStart.setText("开启服务");
				btnStart.setEnabled(true);
				
				serverState = msg.what;
				
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnStart = (Button)findViewById(R.id.btn_start);
		btnQuit = (Button)findViewById(R.id.btn_quit);
		tvStatus = (TextView)findViewById(R.id.tv_status);
		tvContent = (TextView)findViewById(R.id.tv_recv_infor);
		
		btnStart.setOnClickListener(this);
		btnQuit.setOnClickListener(this);
		
		MinaServer.getInstance().setHandler(handler);
		
	}

	@Override
	public void onMsgRecv(IoSession session, Object message) 
	{
		// TODO Auto-generated method stub
		if(message != null && message instanceof String)
		{
			tvContent.setText((String)message);
		}
	}
	
	/**
	* @Description: 获取设备ip地址
	* @param @return   
	* @return String 
	* @throws
	 */
	public static String getIP() 
	{
	    String IP = null;
	    StringBuilder IPStringBuilder = new StringBuilder();
	    try 
	    {
	      Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
	      while (networkInterfaceEnumeration.hasMoreElements()) 
	      {
	    	  NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
	    	  Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
	    	  while (inetAddressEnumeration.hasMoreElements())
	    	  {
	    		  InetAddress inetAddress = inetAddressEnumeration.nextElement();
	    		  if (!inetAddress.isLoopbackAddress()&& 
	    				  !inetAddress.isLinkLocalAddress()&& 
	    				  inetAddress.isSiteLocalAddress()) 
	    		  {
	    			  IPStringBuilder.append(inetAddress.getHostAddress().toString()+"\n");
	    		  }
	    	  }
	      }
	    } 
	    catch (SocketException ex)
	    {

	    }
	    IP = IPStringBuilder.toString();
	    return IP;
	}

	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		if(v == btnStart)
		{
			if(serverState == MinaServer.SERVER_OPENING)
			{
				return;
			}
			else if(serverState == MinaServer.SERVER_OPENED)
			{
				MinaServer.getInstance().closeServer();
			}
			else if(serverState == MinaServer.SERVER_OPEN_FAIL)
			{
				return;
			}
			else if(serverState == MinaServer.SERVER_CLOSED)
			{
				MinaServer.getInstance().startServer();
				tvContent.setText("");
			}
		}
		else if(v == btnQuit)
		{
			MinaServer.getInstance().closeServer();
			finish();
		}
	}
}
