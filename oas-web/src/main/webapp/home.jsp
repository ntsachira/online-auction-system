<%--
  Created by IntelliJ IDEA.
  User: Sachira Jayawardana
  Date: 2025-06-04
  Time: 9:47 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Auction Home</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #eef2f3;
            margin: 0;
            padding: 20px;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }

        .header h1 {
            margin: 0;
            font-size: 20px;
            color: #2c3e50;
        }

        .greeting {
            font-size: 18px;
            color: #34495e;
        }

        .auction-container {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
        }

        .auction-card {
            background-color: #fff;
            border-radius: 10px;
            padding: 20px;
            width: 250px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .auction-card h2 {
            margin: 0;
            font-size: 18px;
            color: #2c3e50;
        }

        .back-btn {
            position: absolute;
            bottom: 20px;
            left: 20px;
            padding: 8px 12px;
            background-color: #3498db;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
        }

        .status-open {
            color: green;
        }

        .status-closed {
            color: red;
        }

        .view-btn {
            margin-top: auto;
            padding: 10px;
            background-color: #3498db;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        .view-btn:hover {
            background-color: #2980b9;
        }
    </style>
</head>
<body>
<div class="header">
    <h1>Online Auction System (Testing Mode)</h1>
    <div class="greeting">Welcome, <span id="usernameDisplay">${sessionScope.username}</span>!</div>
</div>

<div class="auction-container" id="auctionContainer">
    <div id="auctionTemplate" class="auction-card" hidden>
        <h2 class="item-name"></h2>
        <div class="current-bid"></div>
        <div class="status"></div>
        <div class="time-left"></div>
        <button class="view-btn">View Auction</button>
    </div>
</div>

<button class="back-btn" onclick="window.location='index.jsp'">Back to home</button>

<form id="viewAuctionForm" action="auction.jsp" method="GET" style="display: none;">
    <input type="hidden" name="auctionId" id="auctionId">
</form>

<script>

    function viewAuction(auctionId) {
        document.getElementById('auctionId').value = auctionId;
        document.getElementById('viewAuctionForm').submit();
    }

    const ws = new WebSocket('ws://localhost:8080/oas/auction');

    var auctionsList = [];
    const container = document.getElementById('auctionContainer');
    const template = document.getElementById('auctionTemplate');

    ws.onopen = ()=>{
        console.log('Auction loader opened');
    }
    ws.onclose = ()=>{
        console.log('Auction closed');
    }
    ws.onerror = ()=>{
        console.log('Auction error');
    }
    ws.onmessage = (event) => {
        auctionsList = JSON.parse(event.data);
        initAuctions()
    }

    function initAuctions() {
            container.innerHTML = '';

            auctionsList.forEach(auction => {
                const clone = template.cloneNode(true);
                clone.removeAttribute('id');
                clone.setAttribute('id', auction.auctionId);
                clone.style.display = 'flex';

                const itemNameElem = clone.getElementsByClassName('item-name')[0];
                const currentBidElem = clone.getElementsByClassName('current-bid')[0];
                const statusElem = clone.getElementsByClassName('status')[0];
                const timeLeftElem = clone.getElementsByClassName('time-left')[0];
                const viewBtn = clone.getElementsByClassName('view-btn')[0];

                itemNameElem.textContent = auction.itemName;
                currentBidElem.textContent = "Current Bid: Rs."+auction.highestBid;
                statusElem.innerHTML = "Status: "+((auction.endTimestamp-Date.now())>0?"Active":"Auction Closed");
                statusElem.style.color = ((auction.endTimestamp-Date.now())>0?"green":"red");
                timeLeftElem.textContent = (auction.endTimestamp-Date.now())>0?("Time Left: "+formatMilliseconds(auction.endTimestamp-Date.now())):"";
                viewBtn.onclick = () => viewAuction(auction.auctionId);

                container.appendChild(clone);
            });
    }


        setInterval(function () {
        auctionsList.forEach(function (auction) {
            const card = document.getElementById(auction.auctionId);
            const timeLeftElem = card.getElementsByClassName('time-left')[0];
            const statusElem = card.getElementsByClassName('status')[0];
            const viewBtn = card.getElementsByClassName('view-btn')[0];

            if((auction.endTimestamp-Date.now())>0){
                timeLeftElem.textContent = "Time Left: "+formatMilliseconds(auction.endTimestamp-Date.now());
            }else{
                timeLeftElem.textContent = "";
                statusElem.innerHTML = "Status: Auction Closed";
                statusElem.style.color = 'red';
                viewBtn.disabled = true;
            }
        })
    },1000)

    function formatMilliseconds(ms) {
        const totalSeconds = Math.floor(ms / 1000);
        const hours = String(Math.floor(totalSeconds / 3600)).padStart(2, '0');
        const minutes = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, '0');
        const seconds = String(totalSeconds % 60).padStart(2, '0');
        return hours + ':' + minutes + ':' + seconds;
    }


</script>
</body>
</html>

