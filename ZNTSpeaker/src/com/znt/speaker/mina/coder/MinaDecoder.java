
package com.znt.speaker.mina.coder; 

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/** 
 * @ClassName: MinaDecoder 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-8-11 下午1:44:49  
 */
public class MinaDecoder extends CumulativeProtocolDecoder 
{

	private final Charset charset;
	 
    public MinaDecoder(Charset charset) {
        this.charset = charset;
 
    }
 
    @Override
    protected boolean doDecode(IoSession arg0, IoBuffer in,
            ProtocolDecoderOutput out) throws Exception 
            {
    	if(in.remaining() > 0)
    	{
    		//int len = in.remaining();
    		CharsetDecoder cd = charset.newDecoder();
            in.mark();//标记当前位置，以便reset
            String name = in.getString(cd); 
            in.reset();
            if(!name.endsWith("znt_pkg_end"))
            {
            	//如果消息内容不够，则重置，相当于不读取size 
                return false;//父类接收新数据，以拼凑成完整数据 
            } 
            else
            { 
            	String name1 = in.getString(cd); 
                out.write(name1);
                if(in.remaining() > 0)
                {
                	//如果读取内容后还粘了包，就让父类再重读  一次，进行下一次解析 
                    return true; 
                }
            } 
        } 
        return false;//处理成功，让父类进行接收下个包 
    }
    /**
    *用于处理在 IoSession 关闭时剩余的 读取数据,一般这个方法并不会被使用到,除非协议中未定义任何标识数据什么时候截止 的约定,譬如:Http 响应的 Content-Length 未设定,那么在你认为读取完数据后,关闭 TCP 连接(IoSession 的关闭)后,就可以调用这个方法处理剩余的数据,当然你也可以忽略调 剩余的数据
    */
    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out)
    		throws Exception
    {
    	// TODO Auto-generated method stub
    	super.finishDecode(session, out);
    }
    
    /**
    *在销毁编码器时释放关联的资源
    */
    @Override
    public void dispose(IoSession session) throws Exception
    {
    	// TODO Auto-generated method stub
    	super.dispose(session);
    }

}
 
