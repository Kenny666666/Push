package com.hugs.server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
/**
 * Mina的服务器都是通过Handler来接收信息的
 * @author Kenny
 *
 */
public class MyServerHandler extends IoHandlerAdapter {
	/**网络连接出现异常时会进入此方法*/
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		// TODO 网络连接异常时会进入此方法
		System.out.println("exceptionCaught");
	}

	/**
	 * 收到信息的时候会进入此方法
	 * @param 管理此会话的session
	 * @param 此会话收到的消息message，支持任意对象，能在网络上传输的肯定都是字节
	 * 那么怎样才能把这些字节转换成对象呢：就是通过拦截器FilterChain来完成的
	 */
	public void messageReceived(IoSession session, Object message) throws Exception {
		// TODO 收到信息的时候会进入此方法
		String clientMessage= (String) message;
		System.out.println("clientMessage:"+clientMessage);
		//给客户端返回一条信息
		session.write("server reply:"+clientMessage);
	}

	/**发送一条消息后会进入到此方法*/
	public void messageSent(IoSession session, Object message) throws Exception {
		// TODO 发送一条消息后会进入到此方法
		System.out.println("messageSent");
	}

	/**客户端跟服务器的会话关闭的时候会进入到此方法*/
	public void sessionClosed(IoSession session) throws Exception {
		// TODO 客户端跟服务器的会话关闭的时候会进入到此方法
		System.out.println("sessionClosed");
	}

	/**会话创建的时候会调用此方法*/
	public void sessionCreated(IoSession session) throws Exception {
		// TODO 会话创建的时候会调用此方法
		System.out.println("sessionCreated");
	}

	/**
	 * 会话进入空闲状态Idle时会进入到此方法,什么时候才算空闲呢？
	 * 就是说服务器这边可以做一个定义：比如说10分钟或30分钟客户端和服务器
	 * 没有互发信息就给他进入空闲状态
	 */
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		// TODO 会话进入空闲状态Idle时会进入到此方法
		System.out.println("sessionIdle");
	}

	/**会话打开的时候会进入到此方法*/
	public void sessionOpened(IoSession session) throws Exception {
		// TODO 会话打开的时候会进入到此方法
		System.out.println("sessionOpened");
	}
	
}
