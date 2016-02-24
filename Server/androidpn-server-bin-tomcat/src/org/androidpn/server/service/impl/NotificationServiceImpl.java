package org.androidpn.server.service.impl;

import java.util.List;

import org.androidpn.server.dao.NotificationDao;
import org.androidpn.server.model.Notification;
import org.androidpn.server.service.NotificationService;

public class NotificationServiceImpl implements NotificationService {

	private NotificationDao notificationDao;
	
	public NotificationDao getNotificationDao() {
		return notificationDao;
	}
	//必须要加，不要spring映射找不到
	public void setNotificationDao(NotificationDao notificationDao) {
		this.notificationDao = notificationDao;
	}

	public void saveNotifacation(Notification notification) {
		notificationDao.saveNotifacation(notification);
	}

	public List<Notification> findNotificationByUsername(String username) {
		return notificationDao.findNotificationByUsername(username);
	}

	public void deleteNotification(Notification notification) {
		notificationDao.deleteNotification(notification);
	}
	
	public void deleteNotificationByUUID(String uuid) {
		notificationDao.deleteNotificationByUUID(uuid);
	}

}
