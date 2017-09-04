
package com.znt.vodbox.mina.coder; 

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/** 
 * @ClassName: MinaCoderFactory 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-8-11 下午1:50:25  
 */
public class MinaCoderFactory implements ProtocolCodecFactory
{
	private final MinaEncoder encoder;
    private final MinaDecoder decoder;
 
    public MinaCoderFactory() 
    {
        this(Charset.defaultCharset());
    }
 
    public MinaCoderFactory(Charset charSet) 
    {
        this.encoder = new MinaEncoder(charSet);
        this.decoder = new MinaDecoder(charSet);
    }
 
    @Override
    public ProtocolDecoder getDecoder(IoSession arg0) throws Exception 
    {
        // TODO Auto-generated method stub
        return decoder;
    }
 
    @Override
    public ProtocolEncoder getEncoder(IoSession arg0) throws Exception 
    {
        // TODO Auto-generated method stub
        return encoder;
    }
}
 
