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

    @EJB
    private AuctionManager auctionManager;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int auctionId = Integer.parseInt(request.getParameter("auctionId"));
        try {
            AuctionData data = auctionManager.getAuctionById(auctionId);
            response.setContentType("application/json");

            if(data == null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("status", "closed");
                response.getWriter().write(jsonObject.toString());
            }else{
                response.getWriter().write(data.toString(true));
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject jsonObject = new Gson().fromJson(request.getReader().readLine(), JsonObject.class);
        int auctionId = jsonObject.get("auctionId").getAsInt();
        double amount = jsonObject.get("amount").getAsDouble();

        try {
            bidManager.queueBid(auctionId,"",amount);
        } catch (jakarta.jms.JMSException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getMessage());
        }
    }
}
