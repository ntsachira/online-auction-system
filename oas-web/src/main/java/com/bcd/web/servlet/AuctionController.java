package com.bcd.web.servlet;

import com.bcd.auction.remote.AuctionManager;
import com.google.gson.Gson;
import jakarta.ejb.EJB;
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
}
