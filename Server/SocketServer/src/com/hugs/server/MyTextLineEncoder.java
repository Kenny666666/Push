package com.hugs.server;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * 自定义：对字符数据进行加密的类
 * @author hugs
 * 理解：就是我们要发出一条消息会先经过这个加密类的encode方法进行编码,
 * 然后写出二进制的网络操作由Mina给我们完成
 */
public class MyTextLineEncoder implements ProtocolEncoder{

	public void dispose(IoSession arg0) throws Exception {
		// TODO 
		
	}

	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		// TODO 编码加密
		String s = null;
		if (message instanceof String) {
			s = (String) message;
		}
		//转码操作
		if (s!=null) {
			//获取编码
			CharsetEncoder charsetEncoder =  (CharsetEncoder)session.getAttribute("encoder");
			//如果为空，则获取系统默认的编码
			if (null==charsetEncoder) {
				charsetEncoder = Charset.defaultCharset().newEncoder();
				session.setAttribute("encoder",charsetEncoder);
			}
					
			//根据字符串的长度给ioBuffer开辟内存
			IoBuffer ioBuffer = IoBuffer.allocate(s.length());
			//设置可自动扩展
			ioBuffer.setAutoExpand(true);
			ioBuffer.putString(s, charsetEncoder);//如果对象中有多个String字符，就要重复调用putString();
			ioBuffer.flip();
			//写出二进制的网络操作由Mina给我们完成
			out.write(ioBuffer);
		}
	}

}
