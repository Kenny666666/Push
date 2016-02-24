package com.hugs.server;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/**
 * 自定义：对字符数据进行解密的类
 * @author hugs
 *
 */
public class MyTextLineDecoder implements ProtocolDecoder {

	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// TODO 解密
		//记录一开始读取的位置
		int startPosition = in.position();
		while (in.hasRemaining()) {//判断当前的Iobuffer是否还有字节要读取
			//每次循环读取一个字节
			byte b = in.get();
			//读取到\n表示读取结束
			if (b=='\n') {
				//记录当前的位置
				int currentPosition = in.position();
				//记录当前的总长度
				int limit = in.limit();
				/**做截取操作(截取一行)*/
				in.position(startPosition);//从开始位置
				in.limit(currentPosition); //到结束位置
				in.slice(); 			   //截取
				/**写出*/
				IoBuffer buf = in.slice();
				byte[] dest = new byte[buf.limit()];
				buf.get(dest);
				String str = new String(dest);
				out.write(str);
				
				//截取一行后还原到原来的位置(重定向)
				in.position(currentPosition);
				in.limit(limit);
			}
		}
	}

	public void dispose(IoSession arg0) throws Exception {
		// TODO 

	}

	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1) throws Exception {
		// TODO 

	}

}
