package com.bcd.bid.mdb;

import com.bcd.auction.remote.AuctionManager;
import com.bcd.bid.remote.BidManager;
import com.bcd.core.model.AuctionData;
import jakarta.annotation.Resource;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.*;

import javax.naming.NamingException;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType",propertyValue = "jakarta.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destinationLookup",propertyValue = "bidQueue"),
        @ActivationConfigProperty(propertyName = "destination",propertyValue = "bidQueue"),
        @ActivationConfigProperty(propertyName = "resourceAdapter",propertyValue = "activemq-rar-6.1.6"),
})
public class BidQueueMDB implements MessageListener {


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

            System.out.println("Auction " + auctionId + " placed bid for " + bidder);

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }
}
