package com.bcd.web.socket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import javax.naming.NamingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.bcd.web.util.EJBManager.getAuctionManager;

@ServerEndpoint("/single-auction")
public class SingleAuctionLoader {

    private static final Map<Integer, Set<Session>> auctionSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) throws NamingException {
        System.out.println("onOpen"+session.getId());
        int auctionId = Integer.parseInt(session.getPathParameters().get("auctionId"));
        auctionSessions.computeIfAbsent(auctionId, k -> new HashSet<>()).add(session);
        session.getAsyncRemote().sendText(
                getAuctionManager().getAuctionById(auctionId).toString(true));
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("onError"+t.getMessage());
    }

    @OnClose
    public void onClose() {
        System.out.println("onClose");
    }

    public static void notifyActiveSessions(Integer auctionId) {
        for (Session session : auctionSessions.get(auctionId)) {
            if (session.isOpen()) {
                try {
                    session.getAsyncRemote().sendText(
                            getAuctionManager().getAuctionById(auctionId).toString(true)
                    );
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
