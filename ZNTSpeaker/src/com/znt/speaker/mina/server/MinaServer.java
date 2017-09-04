package com.znt.speaker.mina.server;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.json.JSONObject;

import android.text.TextUtils;

import com.znt.speaker.entity.Constant;
import com.znt.speaker.mina.coder.MinaCoderFactory;
import com.znt.speaker.util.LogFactory;

/** 
 * @ClassName: MainFrame 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-8-11 涓婂崍10:03:19  
 */
public class MinaServer
{
	
	private SocketAcceptor acceptor = null;
	private ServerHandler serverHandler = null;
	private DefaultIoFilterChainBuilder chain = null;
	private ProtocolCodecFilter filter = null;
	private InetSocketAddress inetSocketAddress = null;
	
	private static MinaServer INSTANCE = null;
	
	private boolean isChange = false;
	private boolean isStopServer = false;
	
	public static MinaServer getInstance()
	{
		if(INSTANCE == null)
		{
			synchronized (MinaServer.class)
			{
				if(INSTANCE == null)
					INSTANCE = new MinaServer();
			}
		}
		return INSTANCE;
	}
	
	private int getPort()
	{
		/*if(isChange)
			return 9997;
		else
			return 9998;*/
		return 9998;
	}
	
	public void startServer()
	{
		if(acceptor != null && acceptor.isActive())
			LogFactory.createLog().e("*********************鏈嶅姟鍣ㄥ凡缁忓湪杩愯浜�");
		else
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					boolean result = false;
					isStopServer = false;
					
					//鍒涘缓涓�涓潪闃诲鐨剆erver绔疭ocket 锛岀敤NIO
					if(acceptor == null)
					{
						acceptor = new NioSocketAcceptor();
						
				        acceptor.getSessionConfig().setReceiveBufferSize(10240);   // 璁剧疆鎺ユ敹缂撳啿鍖虹殑澶у皬  
				        acceptor.getSessionConfig().setSendBufferSize(10240);// 璁剧疆杈撳嚭缂撳啿鍖虹殑澶у皬  
				        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 3000);  //璇诲啓閮界┖闂叉椂闂�:30绉�  
				        acceptor.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, 4000);//璇�(鎺ユ敹閫氶亾)绌洪棽鏃堕棿:40绉�  
				        acceptor.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 5000);//鍐�(鍙戦�侀�氶亾)绌洪棽鏃堕棿:50绉�  
				        acceptor.getSessionConfig().setTcpNoDelay(true);  
					}
					if(serverHandler == null)
						serverHandler = new ServerHandler();
					
					//filter = new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
					
					if(chain == null)
					{
						filter = new ProtocolCodecFilter(new MinaCoderFactory());
						//鍒涘缓鎺ユ敹鏁版嵁鐨勮繃婊ゅ櫒
						chain = acceptor.getFilterChain();
						//chain.addLast("codec",new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));  
						//璁惧畾杩欎釜杩囨护鍣ㄥ皢浠ュ璞′负鍗曚綅璇诲彇鏁版嵁
						chain.addLast("objectFilter",filter);
						//chain.addLast("myChin", new ProtocolCodecFilter(new TextLineCodecFactory()));//杩欎釜鏄紶閫掑瓧绗︿覆鐢ㄧ殑瑙ｇ爜鍣紝灞忚斀鎺�
				        //chain.addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));//杩欎釜鎵嶆槸mina浼犻�掑璞＄殑瑙ｇ爜鍣�
					}
					
					while(!result)
					{
						if(isStopServer)
							break;
						result = bindServer();
						if(result)
						{
							LogFactory.createLog().e("鏈嶅姟鍣ㄥ紑鍚畬鎴�");
							break;
						}
						else
						{
							isChange = !isChange;
							LogFactory.createLog().e("寮�鍚湇鍔″櫒澶辫触锛屾鍦ㄩ噸鏂板紑鍚�......");
							try
							{
								Thread.sleep(3000);
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
         * mina鑷韩甯︽湁涓�浜涘父鐢ㄧ殑杩囨护鍣紝渚嬪LoggingFilter锛堟棩蹇楄褰曪級銆丅lackListFilter锛堥粦鍚嶅崟杩囨护锛夈�丆ompressionFilter锛堝帇缂╋級銆丼SLFilter锛圫SL鍔犲瘑锛夌瓑*/
        /*---------鎺ユ敹瀛楃涓�---------*/
        //鍒涘缓涓�涓帴鏀舵暟鎹繃婊ゅ櫒
        //DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        //璁惧畾杩囨护鍣ㄤ竴琛岃(/r/n)鐨勮鍙栨暟鎹�
        // chain.addLast("mychin", new ProtocolCodecFilter(new TextLineCodecFactory()   ));
        
		//鏈嶅姟鍣ㄧ粦瀹氱殑绔彛
        int bindPort = getPort();
        try 
        {
        	if(inetSocketAddress == null)
        		inetSocketAddress = new InetSocketAddress(bindPort);
        	/*---------鎺ユ敹瀵硅薄---------*/
            if(acceptor != null && !acceptor.isActive())
            {
            	//璁惧畾鏈嶅姟鍣ㄦ秷鎭鐞嗗櫒
                acceptor.setHandler(serverHandler);
                //缁戝畾绔彛锛屽惎鍔ㄦ湇鍔″櫒
                acceptor.bind(inetSocketAddress);
            }
        } 
        catch (IOException e)
        {
            //System.out.println("Mina Server start for error!"+bindPort);
            LogFactory.createLog().e("start server error-->"+e.getMessage());
            result = false;
            if(acceptor != null)
            	acceptor.unbind(inetSocketAddress);
            e.printStackTrace();
        }
        return result;
	}
	
	public void restartServer()
	{
		//closeServer();
		startServer();
	}
	
	/** 
     * 鑾峰緱瀹㈡埛绔繛鎺ユ�绘暟 
     * @return 
     */  
    public int getConNum()
    {  
          
        int num = acceptor.getManagedSessionCount();  
        System.out.println("num:" + num);  
          
        return num;  
    }  
	
    /** 
     * 鍚戞瘡涓鎴风鍙戦�佹秷鎭� 
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
            
            writeString(session, json.toString());  
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
    			writeString(session, json.toString());
    	}
    }  
    
    private void writeString(IoSession session, String str)
    {
    	session.write(str + Constant.PKG_END);
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
			if(inetSocketAddress != null)
				acceptor.unbind(inetSocketAddress);
			//acceptor.unbind();
			acceptor.getFilterChain().clear();
			acceptor.dispose();
			acceptor = null;
			serverHandler = null;
			chain = null;
			filter = null;
			isStopServer = true;
		}
	}
}
 
