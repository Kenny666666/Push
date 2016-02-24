package org.androidpn.server.dao.hibernate;

import java.util.List;

import org.androidpn.server.dao.NotificationDao;
import org.androidpn.server.model.Notification;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class NotificationDaoHibernate extends HibernateDaoSupport implements
		NotificationDao {

	public void saveNotifacation(Notification notification) {
		getHibernateTemplate().saveOrUpdate(notification);
		getHibernateTemplate().flush();
	}


	public void deleteNotification(Notification notification) {
		getHibernateTemplate().delete(notification);
	}


	@SuppressWarnings("unchecked")
	public List<Notification> findNotificationByUsername(String username) {
		//虽然数据库表名为小写notification，但这里的HQL语句一定要大写，不然会找不到表
		List<Notification> notifications = getHibernateTemplate().find("from Notification where username=?",username);
		if (notifications !=null && notifications.size()>0) {
			return notifications;
		}
		return null;
	}

	
	@SuppressWarnings("unchecked")
	public void deleteNotificationByUUID(String uuid) {
		List<Notification> notifications = getHibernateTemplate().find("from Notification where uuid=?",uuid);
		if (notifications !=null && notifications.size()>0) {
			Notification notification = notifications.get(0);
			
			deleteNotification(notification);
		}
	}

}
