package com.bcd.auction.bean;

import com.bcd.auction.remote.AuctionManager;
import com.bcd.core.model.AuctionData;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.Queue;
import jakarta.servlet.ServletContext;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
public class AuctionManagerBean implements AuctionManager {

    @Resource(lookup = "bidQueue")
    private Queue bidQueue;

    @Override
    public void openAuction(int auctionId, String itemName, long durationMillis) throws NamingException {
        AuctionData auctionData = new AuctionData(
                auctionId,itemName,0,null,System.currentTimeMillis()+durationMillis
        );
        getAuctionMap().put(auctionId,auctionData);
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
    public void closeAuction(int auctionId) throws NamingException {
        AuctionData auctionData = getAuctionMap().remove(auctionId);
    }


    private Map<Integer,AuctionData> getAuctionMap() throws NamingException {
        ServletContext context = (ServletContext) new InitialContext()
                .lookup("auctionServletContext");
        return (Map<Integer,AuctionData>) context.getAttribute("auctionMap");
    }
}
