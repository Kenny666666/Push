package com.hugs.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
/**
 * Mina的使用：
 * 1、到官网http://www.apache.org/dyn/closer.cgi/mina/mina/2.0.9/apache-mina-2.0.9-bin.zip下载Mina的zip包
 * 2、将dist/mina-core-2.0.9.jar，lib/slf4j-api-1.7.7.jar 架包导入工程
 *
 */
public class MinaServer {
	public static void main(String[] args) {
		try {
			//创建
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			acceptor.setHandler(new MyServerHandler());
			//设置Mina的拦截器,收发的所有信息全都是要经过这些拦截器过滤之后才能接收和发出去
			//能在网络上传输的肯定都是字节那么怎样才能把这些字节转换成对象呢：
			//就是通过拦截器FilterChain来完成的。这里的TextLineCodecFactory表示解码类型，读取一行一行的字符串
			acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MyTextLineFactory()));
			/**
			 * 应用场景：定义session会话多久进入空闲状态。此方法一般用于检测客户端是否掉线的功能
			 * 一般就是客户端每隔一段时间给服务器发送一条信息告诉服务器我还连着，多长时间
			 * 客户端没有发送信息过来那么服务器就会认为客户端进入空闲状态。然后服务器可以
			 * 选择把客户端踢下线或者做一些其它操作
			 * 
			 * READER_IDLE：多长时间没有读取到客户端发来的信息就会进入到空闲状态
			 * WRITER_IDLE：多长时间没有向客户端写信息就会进入到空闲状态
			 * BOTH_IDLE：多长时间没有读取或者发送信息就会进入到空闲状态
			 * 5 = 5秒   
			 */
//			acceptor.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, 5);
			//启动服务器
			acceptor.bind(new InetSocketAddress(9898));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
