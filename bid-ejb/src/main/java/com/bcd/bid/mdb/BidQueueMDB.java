package com.bcd.bid.mdb;

import com.bcd.bid.remote.BidManager;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.*;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType",propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destinationLookup",propertyValue = "bidQueue"),
        @ActivationConfigProperty(propertyName = "destination",propertyValue = "bidQueue"),
        @ActivationConfigProperty(propertyName = "resourceAdapter",propertyValue = "activemq-rar-6.1.6"),
})
public class BidQueueMDB implements MessageListener {

    // ... MDB logic for processing bids asynchronously ...

    @EJB
    private BidManager bidManager;

    @Override
    public void onMessage(Message message) {
        MapMessage msg = (MapMessage) message;

        try {
            int auctionId = msg.getInt("auctionId");
            String bidder = msg.getString("bidder");
            double amount = msg.getDouble("amount");

            bidManager.placeBid(auctionId, bidder, amount);
            message.acknowledge();
            System.out.println("Auction " + auctionId + " placed bid for " + bidder);

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }
}
