package com.bcd.bid.remote;

import com.bcd.core.model.AuctionData;
import com.google.gson.JsonObject;
import jakarta.ejb.Remote;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;

import java.util.Map;

@Remote
public interface BidManager {
    void queueBid(int auctionId, String bidderName,double bidAmount) throws JMSException;
    void placeBid(int auctionId, String bidderName,double bidAmount) throws JMSException;
    void placeBid(MapMessage message) throws JMSException;
    boolean validateBid(int auctionId, double bidAmount) ;
}
