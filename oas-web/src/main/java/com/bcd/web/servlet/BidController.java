package com.bcd.web.servlet;

import com.bcd.auction.remote.AuctionManager;
import com.bcd.bid.remote.BidManager;
import com.bcd.core.model.AuctionData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.NamingException;
import java.io.IOException;

@WebServlet("/bid")
public class BidController extends HttpServlet {

    @EJB
    private BidManager bidManager;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String username = String.valueOf(request.getSession().getAttribute("username"));
//        if(username != null) {
            JsonObject jsonObject = new Gson().fromJson(request.getReader(), JsonObject.class);
            int auctionId = jsonObject.get("auctionId").getAsInt();
            double amount = jsonObject.get("amount").getAsDouble();
            if(bidManager.validateBid(auctionId,amount)){
                try {
                    bidManager.queueBid(auctionId,"user",amount);
                } catch (jakarta.jms.JMSException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage());
                }
            }else{
//                response.sendError(HttpServletResponse.SC_BAD_REQUEST,"Bid not accepted");
            }
//        }else{
//            response.sendRedirect("index.jsp");
//        }

    }
}
