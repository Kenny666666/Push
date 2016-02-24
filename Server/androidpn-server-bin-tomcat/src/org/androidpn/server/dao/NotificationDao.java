package org.androidpn.server.dao;

import java.util.List;

import org.androidpn.server.model.Notification;


public interface NotificationDao {
	
	void saveNotifacation(Notification notification);
	
	List<Notification> findNotificationByUsername(String username);
	
	void deleteNotification(Notification notification);
	
	void deleteNotificationByUUID(String uuid);
}
