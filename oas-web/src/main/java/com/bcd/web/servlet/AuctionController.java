package com.bcd.web.servlet;

import com.bcd.auction.remote.AuctionManager;
import com.bcd.core.model.AuctionData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.ejb.EJB;
import jakarta.jms.JMSException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.NamingException;
import java.io.IOException;

@WebServlet("/auction")
public class AuctionController extends HttpServlet {

    @EJB
    private AuctionManager auctionManager;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        try {
            response.getWriter().write(new Gson().toJson(auctionManager.getAllAuctions()));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject requestData = new Gson().fromJson(request.getReader(), JsonObject.class);
        if(requestData != null){
            String itemName = requestData.get("itemName").getAsString();
            double price = requestData.get("price").getAsDouble();
            int duration = requestData.get("duration").getAsInt();
            try {
                if(auctionManager.validateAuctionData(itemName,price,duration)){
                    auctionManager.openAuction(itemName,price,duration);
                }else{
                    System.out.println("Invalid Auction");
                }
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }else{
            System.out.println("Invalid request");
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String auctionId = req.getParameter("auctionId");
        if(auctionId != null && !auctionId.isEmpty()){
            try {
                auctionManager.closeAuction(Integer.parseInt(auctionId));
            } catch (NamingException | JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
