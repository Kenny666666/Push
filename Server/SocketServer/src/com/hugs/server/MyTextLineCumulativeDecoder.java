package com.hugs.server;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/**
 * 此类用来解决在服务端收到的数据结尾不包含\n时的处理类（避免数据丢失）
 * @author Administrator
 *
 */
public class MyTextLineCumulativeDecoder extends CumulativeProtocolDecoder {

	protected boolean doDecode(IoSession session, IoBuffer in,ProtocolDecoderOutput out) throws Exception {
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
				
				return true;
			}
		}
		
		in.position(startPosition);
		return false;
	}

}
