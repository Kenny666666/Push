package org.androidpn.server.xmpp.handler;

import org.androidpn.server.service.NotificationService;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.dom4j.Element;
import org.xmpp.packet.IQ;
import org.xmpp.packet.PacketError;
/**
 * 处理客户端发送过来的设置别名的IQ handler
 * @author hugs
 */
public class IQSetAliasHandler extends IQHandler {
	
	private static final String NAMESPACE = "androidpn:iq:setalias";
	
	private SessionManager sessionManager;
	
	public IQSetAliasHandler(){
		sessionManager = SessionManager.getInstance();
	}
	
	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {

        ClientSession session = sessionManager.getSession(packet.getFrom());
        IQ reply = null;
        if (session == null) {
            log.error("Session not found for key " + packet.getFrom());
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.internal_server_error);
            return reply;
        }
        //判断客户端是否已经连接上(登录成功)
        if (session.getStatus() == Session.STATUS_AUTHENTICATED) {
			if (IQ.Type.set.equals(packet.getType())) {
				Element element = packet.getChildElement();
				//获取客户端传递过来的内容
				String username = element.elementText("username");
				String alias = element.elementText("alias");
				//处理此IQ
				if (username!=null && !username.equals("") && alias != null && !alias.equals("")) {
					
					sessionManager.setUserAlias(username, alias);
					System.out.println("设置客户端别名程序开始执行，成功");
				}
			}
		}
		return null;
	}

	@Override
	public String getNamespace() {
		return NAMESPACE;
	}

}
