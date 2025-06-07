package com.bcd.web.util;

import com.bcd.auction.remote.AuctionManager;
import com.bcd.bid.remote.BidManager;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class EJBManager {
    private EJBManager() {}
    public static AuctionManager getAuctionManager() {
        try {
            InitialContext ic = new InitialContext();
            return((AuctionManager)ic.lookup("java:global/oas-ear/auction-ejb/AuctionManagerBean!com.bcd.auction.remote.AuctionManager"));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public static BidManager getBidManager() {
        try {
            InitialContext ic = new InitialContext();
            return((BidManager)ic.lookup("java:global/oas-ear/bid-ejb/BidManagerBean!com.bcd.bid.remote.BidManager"));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
