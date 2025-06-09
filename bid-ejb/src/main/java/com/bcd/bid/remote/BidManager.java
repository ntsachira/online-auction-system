package com.bcd.bid.remote;

import jakarta.ejb.Remote;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;

@Remote
public interface BidManager {
    void queueBid(int auctionId, String bidderName,double bidAmount) throws JMSException;
    void placeBid(int auctionId, String bidderName,double bidAmount) throws JMSException;
    void placeBid(MapMessage message) throws JMSException;
    boolean validateBid(int auctionId, double bidAmount) ;
}
