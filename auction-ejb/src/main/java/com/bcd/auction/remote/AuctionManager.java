package com.bcd.auction.remote;

import com.bcd.core.model.AuctionData;
import jakarta.ejb.Remote;

import javax.naming.NamingException;
import java.util.List;

@SuppressWarnings("EjbInterfaceSignatureInspection")


@Remote
public interface AuctionManager {
    void openAuction(int auctionId,String itemName,long durationMillis) throws NamingException;
    AuctionData getAuctionById(int auctionId) throws NamingException;
    List<AuctionData> getAllAuctions() throws NamingException;
    void closeAuction(int auctionId) throws NamingException;
}
