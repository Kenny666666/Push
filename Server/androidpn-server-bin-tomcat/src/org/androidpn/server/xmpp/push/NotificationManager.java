/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.xmpp.push;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.androidpn.server.model.Notification;
import org.androidpn.server.model.User;
import org.androidpn.server.service.NotificationService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

/** 
 * 具体推送的业务逻辑都是写在这里的  
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationManager {

    private static final String NOTIFICATION_NAMESPACE = "androidpn:iq:notification";

    private final Log log = LogFactory.getLog(getClass());

    private SessionManager sessionManager;
    
    private NotificationService notificationService;
    private UserService userService;
    /**
     * Constructor.
     */
    public NotificationManager() {
        sessionManager = SessionManager.getInstance();
        notificationService=ServiceLocator.getNotificationService();
        userService = ServiceLocator.getUserService();
    }

    /**
     * 给所有用户发送消息  
     * @param apiKey the API key
     * @param title the title
     * @param message the message details
     * @param uri the uri
     */
    public void sendBroadcast(String apiKey, String title, String message,
            String uri,String imageUrl) {
        log.debug("sendBroadcast()...");

        //获取数据库中所有用户
        List<User> allUser = userService.getUsers();
        for (User user : allUser) {
            //id生成策略：成生一个8位的16进制的随机数，范围在几十亿左右，重复概率在几十亿分之一
            //但如果业务概率比较高要成生上亿消息，可能出现重复，解决方案：增加随便数的位数，增加
            //到32位16进制数，那么就是无穷大了，不可能出现重复
            Random random = new Random();
            String id = Integer.toHexString(random.nextInt());
			//不管客户端是否在线都把消息先保存到数据库
			saveNotification(apiKey, user.getUsername(), title, message, uri, imageUrl,id);
            IQ notificationIQ = createNotificationIQ(id,apiKey, title, message, uri,imageUrl);
            
			ClientSession session = sessionManager.getSession(user.getUsername());
			
			if (session!=null && session.getPresence().isAvailable()) {
				notificationIQ.setTo(session.getAddress());
                session.deliver(notificationIQ);
                
			}
		}
    }

    /**
     * 
     * 给指定用户发消息
     * @param apiKey the API key
     * @param title the title
     * @param message the message details
     * @param uri the uri
     */
    public void sendNotifcationToUser(String apiKey, String username,
            String title, String message, String uri,String imageUrl,boolean shouldSave) {
        log.debug("sendNotifcationToUser()...");
        //id生成策略：成生一个8位的16进制的随机数，范围在几十亿左右，重复概率在几十亿分之一
        //但如果业务概率比较高要成生上亿消息，可能出现重复，解决方案：增加随便数的位数，增加
        //到32位16进制数，那么就是无穷大了，不可能出现重复
        Random random = new Random();
        String id = Integer.toHexString(random.nextInt());
        IQ notificationIQ = createNotificationIQ(id,apiKey, title, message, uri,imageUrl);
        try {
    		//不管客户端是否在线都把消息先保存到数据库
			User user = userService.getUserByUsername(username);
			if (user!=null && shouldSave) {
				saveNotification(apiKey, username, title, message, uri,imageUrl,id);	
			}
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		}
        ClientSession session = sessionManager.getSession(username);
        if (session != null) {
            if (session.getPresence().isAvailable()) {
                notificationIQ.setTo(session.getAddress());
                session.deliver(notificationIQ);
            }
        }
    }

    /**
     * 根据别名来推送
     * @param apiKey
     * @param alias 别名
     * @param title
     * @param message
     * @param uri
     * @param shouldSave
     */
    public void sendNotificationByAlias(String apiKey, String alias,
            String title, String message, String uri,String imageUrl,boolean shouldSave){
    	String username = sessionManager.getUsernameByAlias(alias);
    	if (username != null) {
    		sendNotifcationToUser(apiKey, username, title, message, uri,imageUrl, shouldSave);
		}
    }
    
    /**
     * 根据标签来推送
     * @param apiKey
     * @param tag 标签名
     * @param title
     * @param message
     * @param uri
     * @param shouldSave
     */
    public void sendNotificationByTag(String apiKey, String tag,
            String title, String message, String uri,String imageUrl,boolean shouldSave){
    	Set<String> usernamesSet = sessionManager.getUsernamesByTag(tag);
    	if (usernamesSet!=null && !usernamesSet.isEmpty()) {
			for (String username : usernamesSet) {
				sendNotifcationToUser(apiKey, username, title, message, uri,imageUrl, shouldSave);
			}
		}
    }
    
    /**
     * 如果客户端未连接服务器则把推送的消息保存到数据库
     * @param apiKey
     * @param username
     * @param title
     * @param message
     * @param uri
     */
    private void saveNotification(String apiKey, String username,
            String title, String message, String uri,String imageUrl,String uuid){
    	Notification notification = new Notification();
    	notification.setApiKey(apiKey);
    	notification.setUri(uri);
    	notification.setUsername(username);
    	notification.setTitle(title);
    	notification.setMessage(message);
    	notification.setImageUrl(imageUrl);
    	notification.setUuid(uuid);
    	notificationService.saveNotifacation(notification);
    }
    /**
     * Creates a new notification IQ and returns it.
     */
    private IQ createNotificationIQ(String id,String apiKey, String title,
            String message, String uri,String imageUrl) {
       
        // String id = String.valueOf(System.currentTimeMillis());

        Element notification = DocumentHelper.createElement(QName.get(
                "notification", NOTIFICATION_NAMESPACE));
        notification.addElement("id").setText(id);
        notification.addElement("apiKey").setText(apiKey);
        notification.addElement("title").setText(title);
        notification.addElement("message").setText(message);
        notification.addElement("uri").setText(uri);
        notification.addElement("imageUrl").setText(imageUrl);
        
        IQ iq = new IQ();
        iq.setType(IQ.Type.set);
        iq.setChildElement(notification);

        return iq;
    }
}
