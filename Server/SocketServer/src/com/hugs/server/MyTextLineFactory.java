package com.hugs.server;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
/**
 * 自定义编解码的类
 * @author hugs
 *
 */
public class MyTextLineFactory implements ProtocolCodecFactory {
	/**加密的类*/
	private MyTextLineEncoder mEncoder;
	/**解密的类*/
	private MyTextLineDecoder mDecoder;
	/**此类用来解决在服务端收到的数据结尾不包含\n时的处理类（避免数据丢失）,比它要更好MyTextLineDecoder*/
	private MyTextLineCumulativeDecoder mCumulativeDecoder;
	public MyTextLineFactory(){
		mEncoder = new MyTextLineEncoder();
		mDecoder = new MyTextLineDecoder();
		mCumulativeDecoder = new MyTextLineCumulativeDecoder();
	}
	/**解密*/
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return mCumulativeDecoder;
	}

	/**加密*/
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return mEncoder;
	}

}
