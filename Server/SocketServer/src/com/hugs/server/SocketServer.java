package com.hugs.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author 作者 hugs
 * @version 创建时间：2015-3-26 
 * 类说明：使用原生的Socket来写一个即时通信服务端，   阻塞式服务器。
 * 但这种原生Socket来写的服务端不能满足当客户端出现几十万量级的并发。
 * 当java考虑到这一点后就出现了nio
 * nio 是New IO 的简称，在jdk1.4 里提供的新api 。Sun 官方标榜的特性如下：
 * 为所有的原始类型提供(Buffer)缓存支持。字符集编码解码解决方案。
 * Channel ：一个新的原始I/O 抽象。 支持锁和内存映射文件的文件访问接口。
 * 提供多路(non-bloking)   
 * 重点：nio是非阻塞式的高伸缩性网络I/O 。但用法非常复杂所以一些大牛在他的基础
 * 上再次封装如Mina(网络通讯框架)，很不错。
 */
public class SocketServer {
	/**用于向客户端写信息的writer*/
	BufferedWriter writer;
	/**用于读取客户端发送过来的信息的reader*/
	BufferedReader reader;
	
	public static void main(String[] args) {
		SocketServer server = new SocketServer();
		server.startServer();
	}
	
	public void startServer(){
		ServerSocket serverSocket=null;
		Socket socket=null;
		try {
			serverSocket  = new ServerSocket(9898);
			System.out.println("server started..");
			//如果客户端没有信息发过来程序会一直阻塞在这一步，这是socket通讯的特性
			while (true) {
				
				/**
				 * 程序一启动进入此循环就会在此被阻塞住，就运行不了了，当一个客户端接入后
				 * 通过此方法manageConnection(socket);创建子线程来管理这个客户端，且此
				 * 子线程不会影响主线程的运行。因为第一个客户端连入执行完方法后循环会再次
				 * 运行至socket = serverSocket.accept();主线程就会再次阻塞住，当再次
				 * 有客户端连入时就会又创建子线程来执行操作
				 * 总之应用的方式就是：服务器的socket只有一个，利于子线程来管理多个客户端
				 * 一个客户端接入就创建一个子线程来管理 。
				 */
				socket = serverSocket.accept();
				//此方法用于管理客户端的连接(当多个客户端连接服务器时的处理方法)
				manageConnection(socket);
			}
			//测试客户端是否能随时收到信息，开启定时器，定时向客户端发送一个信息
//			new Timer().schedule(new TimerTask() {
//				
//				public void run() {
//					try {
//						System.out.println("每隔3秒向客户端发一条信息");
//						//记得，一向客户端或服务器写信息的时候结尾一定加\n换行符
//						writer.write("每隔3秒向客户端发一条信息\n");
//						writer.flush();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}, 3000,3000);//第一个3000：首次执行任务延迟3秒钟,第二个3000：之后每隔3秒执行一次
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				socket.close();
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**此方法用于管理客户端的连接(当多个客户端连接服务器时的处理方法)*/
	public void manageConnection(final Socket socket){
		new Thread(new Runnable() {
			
			public void run() {
				try {
					System.out.println("client: "+socket.hashCode()+" connedted");
					//读取客户端发过来的信息
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					//用于向客户端写信息
					writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					String receivedMsg;
					while (null!= (receivedMsg =reader.readLine())) {
						System.out.println("客户端"+socket.hashCode()+"发来的消息："+receivedMsg);
						writer.write("服务端返回给客户端的消息："+receivedMsg+"\n");
						writer.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}finally{
					try {
						reader.close();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
