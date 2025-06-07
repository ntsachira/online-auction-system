package com.bcd.web.socket;

import com.bcd.auction.remote.AuctionManager;
import com.bcd.bid.remote.BidManager;
import com.bcd.web.util.EJBManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.jms.JMSException;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import javax.naming.NamingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.bcd.web.util.EJBManager.getAuctionManager;
import static com.bcd.web.util.EJBManager.getBidManager;


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

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("onMessage"+message);
        JsonObject data = new Gson().fromJson(message, JsonObject.class);
        int auctionId = data.get("auctionId").getAsInt();
        String type = data.get("type").getAsString();
        try {
            if(type.equals("placeBid")){
                double amount = data.get("amount").getAsDouble();
                getBidManager().queueBid(auctionId,"",amount);
                notifyActiveSessions(auctionId);
                AuctionLoader.notifyAuctionListUpdates();
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("onError"+t.getMessage());
    }

    @OnClose
    public void onClose() {
        System.out.println("onClose");
    }

    private static void notifyActiveSessions(Integer auctionId) {
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
