package com.bcd.auction.bean;

import com.bcd.auction.remote.AuctionManager;
import com.bcd.core.factory.AMQConnectionFactory;
import com.bcd.core.model.AuctionData;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.*;
import jakarta.servlet.ServletContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Stateless
public class AuctionManagerBean implements AuctionManager {

    @Inject
    private AMQConnectionFactory connectionFactory;

    @Resource(lookup = "auctionTopic")
    private Topic topic;

    @Override
    public void openAuction(String itemName, double price,long durationMillis) throws NamingException {
        Optional<Integer> max = getAuctionMap().keySet().stream().max(Integer::compareTo);
        if (max.isPresent()) {
            int auctionId = max.get()+1;
            AuctionData auctionData = new AuctionData(
                    auctionId,itemName,price,null,durationMillis
            );
            getAuctionMap().put(auctionId,auctionData);
            try {
                broadcastUpdate(auctionId);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public AuctionData getAuctionById(int auctionId) throws NamingException {
        return getAuctionMap().get(auctionId);
    }

    @Override
    public List<AuctionData> getAllAuctions() throws NamingException {
        return new ArrayList<>(getAuctionMap().values());
    }

    @Override
    public void closeAuction(int auctionId) throws NamingException, JMSException {
        AuctionData auctionData = getAuctionMap().get(auctionId);
        if(auctionData != null) {
            auctionData.setEndTimestamp(0);
            broadcastUpdate(auctionId);
        }
    }

    @Override
    public boolean validateAuctionData(String itemName, double price, int duration) throws NamingException {
        return !itemName.isBlank() && !(price < 0) && duration >= 0;
    }


    // Example from AuctionManagerBean
    private Map<Integer,AuctionData> getAuctionMap() throws NamingException {
        // JNDI Lookup for a shared application context
        ServletContext context = (ServletContext) new InitialContext()
                .lookup("auctionServletContext");
        return (Map<Integer,AuctionData>) context.getAttribute("auctionMap");
    }

    @Override
    public void broadcastUpdate(int auctionId) throws JMSException {
        try(Connection connection = connectionFactory.createConnection()){
            connection.setClientID("BID-" + auctionId);
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(topic);
            MapMessage message = session.createMapMessage();
            message.setInt("auctionId",auctionId);
            producer.send(message);
        }
    }
}
