
package com.znt.speaker.mina.coder; 

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/** 
 * @ClassName: MinaEncoder 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-8-11 下午1:42:29  
 */
public class MinaEncoder extends ProtocolEncoderAdapter 
{

	private final Charset charset;
	 
    public MinaEncoder(Charset charset) 
    {
        this.charset = charset;
 
    }
 
    @Override
    public void encode(IoSession arg0, Object arg1, ProtocolEncoderOutput arg2)
            throws Exception {
 
        CharsetEncoder ce = charset.newEncoder();
 
        String paEntity = (String) arg1;
        //String paEntity = System.currentTimeMillis() + "";
 
        IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
        buffer.putString(paEntity, ce); 
        buffer.flip();
        arg2.write(buffer);
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
 
