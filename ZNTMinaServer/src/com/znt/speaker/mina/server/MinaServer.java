package com.znt.speaker.mina.server;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

/** 
 * @ClassName: MainFrame 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-8-11 上午10:03:19  
 */
public class MinaServer
{
	
	private SocketAcceptor acceptor = null;
	private ServerHandler serverHandler = null;
	private DefaultIoFilterChainBuilder chain = null;
	private ProtocolCodecFilter filter = null;
	
	private static MinaServer INSTANCE = null;
	
	private boolean isChange = false;
	
	public static final int SERVER_OPENING = 0;
	public static final int SERVER_OPENED = 1;
	public static final int SERVER_OPEN_FAIL = 2;
	public static final int SERVER_CLOSED = 3;
	public Handler handler = null;
	
	public void setHandler(Handler handler)
	{
		this.handler = handler;
	}
	
	public static MinaServer getInstance()
	{
		if(INSTANCE == null)
			INSTANCE = new MinaServer();
		return INSTANCE;
	}
	
	private int getPort()
	{
		return 8888;
	}
	
	public void startServer()
	{
		if(acceptor != null && acceptor.isActive())
			Log.e("", "*********************服务器已经在运行了");
		else
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					boolean result = false;
					
					handler.obtainMessage(SERVER_OPENING).sendToTarget();
					
					//创建一个非阻塞的server端Socket ，用NIO
					if(acceptor == null)
					{
						acceptor = new NioSocketAcceptor();
						
				        acceptor.getSessionConfig().setReceiveBufferSize(10240);   // 设置接收缓冲区的大小  
				        acceptor.getSessionConfig().setSendBufferSize(10240);// 设置输出缓冲区的大小  
				        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 3000);  //读写都空闲时间:30秒  
				        acceptor.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, 4000);//读(接收通道)空闲时间:40秒  
				        acceptor.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 5000);//写(发送通道)空闲时间:50秒  
				        acceptor.getSessionConfig().setTcpNoDelay(true);  
					}
					if(serverHandler == null)
						serverHandler = new ServerHandler();
					if(filter == null)
						filter = new ProtocolCodecFilter(new TextLineCodecFactory());
					
					if(chain == null)
					{
						//创建接收数据的过滤器
						chain = acceptor.getFilterChain();
						
						//设定这个过滤器将以对象为单位读取数据
						chain.addLast("objectFilter",filter);
					}
					
					while(!result)
					{
						result = bindServer();
						if(result)
						{
							Log.e("", "服务器开启完成");
							handler.obtainMessage(SERVER_OPENED).sendToTarget();
							break;
						}
						else
						{
							handler.obtainMessage(SERVER_OPEN_FAIL).sendToTarget();
							
							isChange = !isChange;
							Log.e("", "开启服务器失败，正在重新开启......");
							try
							{
								Thread.sleep(1000);
							} catch (InterruptedException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}).start();
    }
	
	private boolean bindServer()
	{
		boolean result = true;
		
        /*
         * mina自身带有一些常用的过滤器，例如LoggingFilter（日志记录）、BlackListFilter（黑名单过滤）、CompressionFilter（压缩）、SSLFilter（SSL加密）等*/
        /*---------接收字符串---------*/
        //创建一个接收数据过滤器
        //DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        //设定过滤器一行行(/r/n)的读取数据
        // chain.addLast("mychin", new ProtocolCodecFilter(new TextLineCodecFactory()   ));
        
		//服务器绑定的端口
        int bindPort = getPort();
        try 
        {
        	/*---------接收对象---------*/
            if(acceptor != null && !acceptor.isActive())
            {
            	//设定服务器消息处理器
                acceptor.setHandler(serverHandler);
                //绑定端口，启动服务器
                acceptor.bind(new InetSocketAddress(bindPort));
            }
        } 
        catch (IOException e)
        {
            System.out.println("Mina Server start for error!"+bindPort);
            Log.e("", "start server error-->"+e.getMessage());
            result = false;
            if(acceptor != null)
            	acceptor.unbind();
            e.printStackTrace();
        }
        return result;
	}
	
	public void restartServer()
	{
		closeServer();
		startServer();
	}
	
	public boolean isServerOpen()
	{
		return acceptor != null && acceptor.isActive();
	}
	
	/** 
     * 获得客户端连接总数 
     * @return 
     */  
    public int getConNum()
    {  
          
        int num = acceptor.getManagedSessionCount();  
        System.out.println("num:" + num);  
          
        return num;  
    }  
	
    /** 
     * 向每个客户端发送消息 
     * @return 
     */  
    public void sendConMessage(JSONObject json)
    {  
          
    	if(acceptor == null)
    		return;
    	
        IoSession session;  
          
        Map conMap = acceptor.getManagedSessions();  
          
        Iterator iter = conMap.keySet().iterator();  
        while (iter.hasNext()) 
        {  
            Object key = iter.next();  
            session = (IoSession)conMap.get(key);  
            
            /*String ip = session.getRemoteAddress().toString();
            Log.e("", "*************ip-->" + ip);*/
            
            session.write(json.toString());  
        }
    }  
    
    public void sendMessageToOne(JSONObject json, String userIp)
    {  
    	
    	if(acceptor == null || TextUtils.isEmpty(userIp))
    		return;
    	
    	IoSession session;  
    	
    	Map conMap = acceptor.getManagedSessions();  
    	
    	Iterator iter = conMap.keySet().iterator();  
    	while (iter.hasNext()) 
    	{  
    		Object key = iter.next();  
    		session = (IoSession)conMap.get(key);  
    		
    		String ip = session.getRemoteAddress().toString();
    		if(!TextUtils.isEmpty(ip) && ip.contains(userIp))
    			session.write(json.toString());  
    	}
    }  
    
    public IoSession getSessionById(String userId)
    {
    	Map<Long, IoSession> sessions = acceptor.getManagedSessions();
        //session.setAttribute(session.getId(), userId);
        return sessions.get(userId);
    }
    
	public void closeServer()
	{
		if(acceptor != null && acceptor.isActive())
		{
			acceptor.unbind();
			acceptor.getFilterChain().clear();
			acceptor.dispose();
			acceptor = null;
			serverHandler = null;
			chain = null;
			filter = null;
			
			handler.obtainMessage(SERVER_CLOSED).sendToTarget();
		}
	}
}
 
