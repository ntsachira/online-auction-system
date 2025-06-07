package com.bcd.web.socket;

import com.bcd.core.model.AuctionData;
import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import javax.naming.NamingException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.bcd.web.util.EJBManager.getAuctionManager;


@ServerEndpoint("/auction")
public class AuctionLoader {

    private static Set<Session> auctionSessions = new CopyOnWriteArraySet<>();

    private static Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("onOpen");
        auctionSessions.add(session);
        sendAuctionData(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("onMessage");
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("onClose");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println(throwable.getMessage());
    }

    private void sendAuctionData(Session session) {
        try {
            List<AuctionData> allAuctions = getAuctionManager().getAllAuctions();
            session.getAsyncRemote().sendText(gson.toJson(allAuctions));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void notifyAuctionListUpdates(){
        auctionSessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    List<AuctionData> allAuctions = getAuctionManager().getAllAuctions();
                    session.getAsyncRemote().sendText(gson.toJson(allAuctions));
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


}
