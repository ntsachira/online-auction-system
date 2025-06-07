<%--
  Created by IntelliJ IDEA.
  User: Sachira Jayawardana
  Date: 2025-06-02
  Time: 9:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Auction Page</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .auction-box {
            background-color: #fff;
            border-radius: 10px;
            padding: 30px;
            max-width: 500px;
            margin: auto;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }

        h1 {
            font-size: 24px;
            color: #2c3e50;
            margin-bottom: 20px;
        }

        .back-btn {
            position: absolute;
            top: 20px;
            right: 20px;
            padding: 8px 12px;
            background-color: #3498db;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
        }

        .field {
            margin-bottom: 15px;
            font-size: 16px;
            color: #34495e;
        }

        .bid-container {
            display: flex;
            gap: 10px;
            align-items: center;
            margin-top: 20px;
        }

        .bid-input {
            flex: 1;
            padding: 10px;
            font-size: 16px;
            border-radius: 5px;
            border: 1px solid #ccc;
        }

        .bid-btn {
            padding: 12px 20px;
            background-color: #27ae60;
            color: white;
            font-size: 16px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        .bid-btn:hover {
            background-color: #1e8449;
        }

        .status-open {
            color: green;
            font-weight: bold;
        }

        .status-closed {
            color: red;
            font-weight: bold;
        }
    </style>

</head>
<body>
<button class="back-btn" onclick="window.location='home.jsp'">Back to home</button>
<div class="auction-wrapper">
    <div class="auction-box">
        <h1>Auction: <span id="itemName">Loading...</span></h1>
        <div class="field">Auction ID: <span id="auctionId">#0000</span></div>
        <div class="field">Highest Bid: Rs.<span id="highestBid">0.00</span></div>
        <div class="field">Base Value: Rs.<span id="baseValue">0.00</span></div>
        <div class="field">Status: <span id="auctionStatus" class="status-open">Open</span></div>
        <div class="field">Ends In: <span id="auctionTime"></span></div>

        <div class="bid-container">
            <input type="number" id="bidAmount" class="bid-input" placeholder="Enter your bid">
            <button class="bid-btn" onclick="placeBid()">Place Bid</button>
        </div>
    </div>
</div>
<script>
    const urlParams = new URLSearchParams(window.location.search);
    const auctionId = urlParams.get('auctionId') || '0000';

    const url ="http://localhost:8080/oas/single-auction?auctionId="
    var auctionData = {};

    const ws = new WebSocket(url + auctionId);
    ws.onopen = ()=> {
        console.log('Connection established');
    }
    ws.onmessage = (event) => {
        auctionData = JSON.parse(event.data);
        loadAuctionData(auctionData);
    }

    async function loadAuctionData(auction){
            document.getElementById('itemName').innerHTML = auction.itemName;
            document.getElementById('auctionId').innerHTML = auction.auctionId;
            document.getElementById('highestBid').innerHTML = auction.highestBid;
            document.getElementById('baseValue').innerHTML = auction.floorPrice;
            let remainingTime = auction.endTimestamp-Date.now();

            if(remainingTime > 0){
                document.getElementById('auctionStatus').innerHTML = "Active";
                document.getElementById('auctionStatus').className = "status-open";
                document.getElementById('auctionTime').innerHTML = formatMilliseconds(remainingTime);
                updateTimeLeft();
            }else{
                document.getElementById('auctionStatus').innerHTML = "Auction Closed";
                document.getElementById('auctionStatus').className = "status-closed";
                document.getElementById('bid-btn').disabled = true;
                document.getElementById('bidAmount').disabled = true;
            }

    }

    function updateTimeLeft(){
        setInterval(()=>{
            if(auctionData.endTimestamp-Date.now() > 0){
                document.getElementById('auctionTime').innerHTML = formatMilliseconds(auctionData.endTimestamp-Date.now());
                document.getElementById('bid-btn').disabled = false;
                document.getElementById('bidAmount').disabled = false;
            }else{
                document.getElementById('auctionTime').innerHTML = "00:00";
                document.getElementById('auctionStatus').innerHTML = "Auction Closed";
                document.getElementById('auctionStatus').className = "status-closed";
                document.getElementById('bid-btn').disabled = true;
                document.getElementById('bidAmount').disabled = true;
            }
        },1000);
    }
    function formatMilliseconds(ms) {
        const totalSeconds = Math.floor(ms / 1000);
        const hours = String(Math.floor(totalSeconds / 3600)).padStart(2, '0');
        const minutes = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, '0');
        const seconds = String(totalSeconds % 60).padStart(2, '0');
        return hours + ':' + minutes + ':' + seconds;
    }

    async function placeBid() {
        const bidAmount = parseFloat(document.getElementById('bidAmount').value);
        if (isNaN(bidAmount) || bidAmount <= auctionData.highestBid) {
            alert('Please enter a valid bid greater than the current highest bid.');
            return;
        }

        const bidMessage = {
            type: 'placeBid',
            auctionId: auctionData.auctionId,
            amount: bidAmount
        };

        ws.send(JSON.stringify(bidMessage))
        document.getElementById('bidAmount').value = "";
    }
</script>
</body>
</html>

