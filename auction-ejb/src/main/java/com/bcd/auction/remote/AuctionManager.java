package com.bcd.auction.remote;

import com.bcd.core.model.AuctionData;
import jakarta.ejb.Remote;
import jakarta.jms.JMSException;

import javax.naming.NamingException;
import java.util.List;

@SuppressWarnings("EjbInterfaceSignatureInspection")


@Remote
public interface AuctionManager {
    void openAuction(String itemName,double price,long durationMillis) throws NamingException;
    AuctionData getAuctionById(int auctionId) throws NamingException;
    List<AuctionData> getAllAuctions() throws NamingException;
    void closeAuction(int auctionId) throws NamingException, JMSException;
    boolean validateAuctionData(String itemName,double price, int duration) throws NamingException;
    void broadcastUpdate(int auctionId) throws JMSException;
}
