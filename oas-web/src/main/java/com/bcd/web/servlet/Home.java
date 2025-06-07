package com.bcd.web.servlet;


import com.bcd.auction.remote.AuctionManager;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.NamingException;
import java.io.IOException;

@WebServlet("/home")
public class Home extends HttpServlet {

    @EJB
    AuctionManager auctionManager;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            auctionManager.openAuction(123,"Vintage clock",300000);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
