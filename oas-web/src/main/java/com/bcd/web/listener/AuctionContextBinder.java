package com.bcd.web.listener;

import com.bcd.auction.remote.AuctionManager;
import com.bcd.core.model.AuctionData;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebListener
public class AuctionContextBinder implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Map<Integer, AuctionData> auctions = new ConcurrentHashMap<>();
        loadAuctions(auctions);
        sce.getServletContext().setAttribute("auctionMap", auctions);
        try {
            InitialContext context = new InitialContext();
            context.rebind("auctionServletContext", sce.getServletContext());
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAuctions(Map<Integer, AuctionData> auctions) {
        auctions.put(100,new AuctionData(100,"Vintage clock",0,300000));
        auctions.put(101,new AuctionData(101,"Antique Vase",1000,600000));
        auctions.put(102,new AuctionData(102,"Gold Plated Lamp",500,900000));
    }
}
