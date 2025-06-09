package com.bcd.bid.bean;

import com.bcd.auction.remote.AuctionManager;
import com.bcd.bid.remote.BidManager;
import com.bcd.core.factory.AMQConnectionFactory;
import com.bcd.core.model.AuctionData;
import com.google.gson.JsonObject;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.jms.*;
import jakarta.servlet.ServletContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;

// BidManagerBean (Singleton - centralizes shared auction data access)
@Singleton
public class BidManagerBean implements BidManager {

    // EJB container ensures only one instance exists across the application
    // Shared state like the auctionMap (accessed via getAuctionMap()) can be managed here

    @Inject
    private AMQConnectionFactory connectionFactory;

    @Resource(lookup = "bidQueue")
    private Queue queue;

    @Resource(lookup = "auctionTopic")
    private Topic topic;

    @EJB
    private AuctionManager auctionManager;

    @Override
    public void queueBid(int auctionId, String bidderName, double bidAmount) {
        if (bidAmount <= 0) {
            throw new IllegalArgumentException("Bid must be positive");
        }

        try(Connection connection = connectionFactory.createConnection()){
            connection.setClientID("BID-" + auctionId);
            connection.start();

            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            MapMessage message = session.createMapMessage();
            message.setInt("auctionId",auctionId);
            message.setString("bidder",bidderName);
            message.setDouble("amount",bidAmount);
            MessageProducer producer = session.createProducer(queue);
            producer.send(message);

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    @Lock(LockType.WRITE) // Ensures thread-safe access to shared data
    public void placeBid(int auctionId, String bidderName,double bidAmount) {
        // Accesses and modifies the shared auction state
        try {
            AuctionData data = getAuctionMap().get(auctionId);
            if(System.currentTimeMillis() > data.getEndTimestamp()) return;

            if(bidAmount > data.getHighestBid()){
                System.out.println("Highest bid is " + bidAmount);
                data.setHighestBid(bidAmount);
                data.setHighestBidder(bidderName);
                auctionManager.broadcastUpdate(auctionId);
            }
        } catch (NamingException | JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void placeBid(MapMessage msg) throws JMSException {
        int auctionId = msg.getInt("auctionId");
        String bidder = msg.getString("bidder");
        double amount = msg.getDouble("amount");

        placeBid(auctionId, bidder, amount);
    }

    @Override
    public boolean validateBid(int auctionId, double bidAmount){
        try {
            AuctionData data = getAuctionMap().get(auctionId);
            if(data != null && System.currentTimeMillis() < data.getEndTimestamp() && bidAmount > data.getHighestBid()){
                return true;
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private Map<Integer,AuctionData> getAuctionMap() throws NamingException {
        // Accessing a shared map (e.g., from ServletContext)
        // This shared map could itself be managed by the Singleton
        ServletContext context = (ServletContext) new InitialContext()
                .lookup("auctionServletContext");
        return (Map<Integer,AuctionData>) context.getAttribute("auctionMap");
    }

}
