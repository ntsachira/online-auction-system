package com.bcd.bid.remote;

import com.bcd.core.model.AuctionData;
import jakarta.ejb.Remote;
import jakarta.jms.JMSException;

import java.util.Map;

@Remote
public interface BidManager {
    void queueBid(int auctionId, String bidderName,double bidAmount) throws JMSException;
    void placeBid(int auctionId, String bidderName,double bidAmount) throws JMSException;
    void broadcastNewHighBid(int auctionId, String bidder, double amount);
}
