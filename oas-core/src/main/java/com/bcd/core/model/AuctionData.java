package com.bcd.core.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;

public class AuctionData implements Serializable {
    private int auctionId;
    private String itemName;
    private double floorPrice;
    private double highestBid;
    private String highestBidder;
    private long endTimestamp;

    public static final String AUCTION_ID = "auctionId";
    public static final String ITEM_NAME = "itemName";
    public static final String FLOOR_PRICE = "floorPrice";
    public static final String HIGHEST_BID = "highestBid";
    public static final String HIGHEST_BIDDER = "highestBidder";
    public static final String END_TIMESTAMP = "endTimestamp";
    public static final String AMOUNT = "amount";


    public AuctionData() {
    }

    public AuctionData(int auctionId,String itemName,double floorPrice, long duration){
        this.auctionId = auctionId;
        this.itemName = itemName;
        this.floorPrice = floorPrice;
        this.highestBid = 0.0;
        this.highestBidder = "";
        this.endTimestamp = System.currentTimeMillis()+duration;
    }

    public AuctionData(int auctionId, String itemName, double highestBid, String highestBidder, long endTimestamp) {
        this.auctionId = auctionId;
        this.itemName = itemName;
        this.highestBid = highestBid;
        this.highestBidder = highestBidder;
        this.endTimestamp = endTimestamp;
    }

    public double getFloorPrice() {
        return floorPrice;
    }

    public void setFloorPrice(double floorPrice) {
        this.floorPrice = floorPrice;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(double highestBid) {
        this.highestBid = highestBid;
    }

    public String getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(String highestBidder) {
        this.highestBidder = highestBidder;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("auctionId", auctionId);
        jsonObject.addProperty("highestBid", highestBid);
        jsonObject.addProperty("highestBidder", highestBidder);
        return jsonObject.toString();
    }

    public String toString(boolean isAll) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("auctionId", auctionId);
        jsonObject.addProperty("itemName", itemName);
        jsonObject.addProperty("floorPrice", floorPrice);
        jsonObject.addProperty("highestBid", highestBid);
        jsonObject.addProperty("highestBidder", highestBidder);
        jsonObject.addProperty("endTimestamp", endTimestamp);
        return jsonObject.toString();
    }
}
