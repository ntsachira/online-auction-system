package com.bcd.bid.bean;

import com.bcd.auction.remote.AuctionManager;
import com.bcd.bid.remote.BidManager;
import com.bcd.core.model.AuctionData;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.jms.*;
import jakarta.servlet.ServletContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;

@Singleton
public class BidManagerBean implements BidManager {

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
    @Lock(LockType.WRITE)
    public void placeBid(int auctionId, String bidderName,double bidAmount) {
        try {
            AuctionData data = getAuctionMap().get(auctionId);
            System.out.println(System.currentTimeMillis());
            System.out.println(data.getEndTimestamp());
            if(System.currentTimeMillis() > data.getEndTimestamp()) return;

            if(bidAmount > data.getHighestBid()){
                System.out.println("Highest bid is " + bidAmount);
                data.setHighestBid(bidAmount);
                data.setHighestBidder(bidderName);
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Integer,AuctionData> getAuctionMap() throws NamingException {
        ServletContext context = (ServletContext) new InitialContext()
                .lookup("auctionServletContext");
        return (Map<Integer,AuctionData>) context.getAttribute("auctionMap");
    }

    @Override
    public void broadcastNewHighBid(int auctionId, String bidder, double amount) {
        System.out.println("broadcastNewHighBid");
    }


}
