package com.bcd.web.mdb;

import com.bcd.web.socket.AuctionLoader;
import com.bcd.web.socket.SingleAuctionLoader;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType",propertyValue = "jakarta.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destinationLookup",propertyValue = "auctionTopic"),
        @ActivationConfigProperty(propertyName = "destination",propertyValue = "auctionTopic"),
        @ActivationConfigProperty(propertyName = "resourceAdapter",propertyValue = "activemq-rar-6.1.6"),
})
public class AuctionTopicMDB implements MessageListener {
    @Override
    public void onMessage(Message message) {
        MapMessage msg = (MapMessage) message;
        try {
            SingleAuctionLoader.notifyActiveSessions(msg.getInt("auctionId"));
            AuctionLoader.notifyAuctionListUpdates();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
