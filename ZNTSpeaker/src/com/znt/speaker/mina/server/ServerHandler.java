package com.znt.speaker.mina.server;
import java.util.Collection;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.json.JSONObject;

import android.text.TextUtils;

import com.znt.speaker.entity.Constant;
import com.znt.speaker.util.LogFactory;


/** 
 * @ClassName: ServerHandler 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-8-11 上午10:05:33  
 */
public class ServerHandler extends IoHandlerAdapter
{
	
	private static OnMessageReceiveListener mListener = null;
	public static void setOnMessageReceiveListener(OnMessageReceiveListener listener)
	{
		mListener = listener;
	}
	
	public static interface OnMessageReceiveListener
	{
		public void onMsgRecv(IoSession session, JSONObject obj);
	}
	
	/**
	 *当客户端发送的消息到达时 
	 *session.getService().broadcast(zntObj);//发送消息给所有客户端
	 */
	@Override
	public void messageReceived(IoSession session, Object message)
	      throws Exception
	{
		if(message != null && message instanceof String)
		{
			String recvStr = (String)message;
    		if(TextUtils.isEmpty(recvStr) || !recvStr.contains(Constant.PKG_END))
			{
				return;
			}
    		recvStr = recvStr.substring(0, (recvStr.length() - Constant.PKG_END.length()));
			JSONObject recvCmd = new JSONObject((String)message);
			if(mListener != null)
			{
				mListener.onMsgRecv(session, recvCmd);
			}
		}
	}
	  
	/**
	*当接口中其他方法抛出异常未被捕获时触发此方法
	 */
	  @Override
	  public void exceptionCaught(IoSession session, Throwable cause)
	      throws Exception 
	  {
		  System.out.println("其他方法抛出异常：" +cause);
	  }
		  
	  /**
	*新建了一个客户端连接
	*/
	@Override
	public void sessionCreated(IoSession session) throws Exception
	{
		// TODO Auto-generated method stub
		super.sessionCreated(session);
		System.out.println("新客户端连接");
	}
	
	/**
	* 当一个客端端连结进入时
	*/
	@Override
	public void sessionOpened(IoSession session) throws Exception
	{
		// TODO Auto-generated method stub
		//super.sessionOpened(session);
		//count++;
		 Collection<IoSession> sessions = session.getService().getManagedSessions().values();
		 int count = sessions.size();
         System.out.println("第 " + count + " 个 client 登陆！address： : "
                + session.getRemoteAddress());
         
         //session.write("收到了来自" + session.getRemoteAddress() + "的连接请求");
	}
	
	/**
	*当信息已经传送给客户端后触发此方法
	*/
	@Override
	public void messageSent(IoSession session, Object message) throws Exception
	{
		// TODO Auto-generated method stub
		//BaseCmd cmdInfor = (BaseCmd)message;
		//LogFactory.createLog().e("信息已经传送给客户端-->"+cmdInfor.getUserInfor().getUserName());
		//zntObj.setCmd(CmdInfor.SEND_MSG_FB);//消息发送出去的反馈
		
		/*session.write(obj);
		super.messageSent(session, obj);*/
	}
	
	/**
	*当一个客户端关闭时
	*/
	@Override
	public void sessionClosed(IoSession session) throws Exception
	{
		// TODO Auto-generated method stub
		//super.sessionClosed(session);
		System.out.println("one Clinet Disconnect !");
		CloseFuture closeFuture = session.close(true);
        closeFuture.addListener(new IoFutureListener<IoFuture>() 
        {
            public void operationComplete(IoFuture future) 
            {
                if (future instanceof CloseFuture) 
                {
                    ((CloseFuture) future).setClosed();
                    LogFactory.createLog().e("***********用户断开连接 seeionId-->"+future.getSession().getId());
                    System.out.println("sessionClosed CloseFuture setClosed-->{}," + future.getSession().getId());
                }
            }
        });
	}

	/**
	*当连接空闲时触发此方法
	*/
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception
	{
		// TODO Auto-generated method stub
		//super.sessionIdle(session, status);
		System.out.println("连接空闲");
	}
	
	private String getInforFromJson(String key, JSONObject json)
	{
		String result = "";
		try
		{
			if(json.has(key))
				result = json.getString(key);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}
 
