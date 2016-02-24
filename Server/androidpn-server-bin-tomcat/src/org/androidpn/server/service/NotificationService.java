package org.androidpn.server.service;

import java.util.List;

import org.androidpn.server.model.Notification;

public interface NotificationService {
	void saveNotifacation(Notification notification);
	
	List<Notification> findNotificationByUsername(String username);
	
	void deleteNotification(Notification notification);
	
	void deleteNotificationByUUID(String uuid);
}
