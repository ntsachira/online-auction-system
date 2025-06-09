<%--
  Created by IntelliJ IDEA.
  User: Sachira Jayawardana
  Date: 2025-06-09
  Time: 6:22 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Manager - Auctions</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" />
    <style>
        body {
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
        }
        header {
            background-color: #003366;
            color: white;
            padding: 20px;
            text-align: center;
            font-size: 1.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .container {
            max-width: 900px;
            margin: 30px auto;
            padding: 20px;
        }
        .card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
            padding: 20px;
            margin-bottom: 30px;
        }
        .card h2 {
            margin-top: 0;
            font-size: 1.3rem;
        }
        form input, form button {
            padding: 10px;
            margin: 10px 5px 10px 0;
            font-size: 1rem;
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
        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #003366;
            color: white;
        }
        button.close-btn {
            background-color: #cc0000;
            color: white;
            border: none;
            padding: 6px 12px;
            border-radius: 4px;
            cursor: pointer;
            margin-right: 5px;
        }
        button.close-btn:hover {
            background-color: #a30000;
        }
    </style>
    <script>

        async function startAuction() {
            const data = {
                itemName:document.getElementById("itemName").value,
                price:document.getElementById("startingPrice").value,
                duration:document.getElementById("duration").value,
            }
            const response = await fetch("http://localhost:8080/oas/auction",{
                method: 'POST',
                body: JSON.stringify(data),
                headers: { 'Content-Type': 'application/json' },
            })
            if(response.ok){
                document.getElementById("itemName").value = "";
                document.getElementById("startingPrice").value = "";
                document.getElementById("duration").value = "";
            }else{
                console.log("Auction not started");
            }
        }
    </script>
</head>
<body>
<header>
    <i class="fas fa-gavel"></i> Auction Management Panel
</header>
<button class="back-btn" onclick="window.location='index.jsp'">User Login</button>
<div class="container">
    <div class="card">
        <h2>Start New Auction</h2>
        <form id="auctionForm">
            <input type="text" id="itemName" placeholder="Item Name" required />
            <input type="number" id="startingPrice" placeholder="Starting Price" required />
            <input type="number" id="duration" placeholder="Duration (millis)" required />
            <button type="button" onclick="startAuction()">Start Auction</button>
        </form>
    </div>

    <div class="card">
        <h2>Existing Auctions</h2>
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Item</th>
                <th>Floor Price</th>
                <th>Highest Bid</th>
                <th>Remaining Time</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody id="auctionContainer">
            <tr id="auctionTemplate">
                <td class="item-id">1</td>
                <td class="item-name">Vintage Clock</td>
                <td class="floor-price">Rs. 100</td>
                <td class="current-bid">Rs. 200</td>
                <td class="time-left">12 min</td>
                <td>
                    <button  class="close-btn">Close Auction</button>
                </td>
            </tr>
            <!-- More rows can be added dynamically -->
            </tbody>
        </table>
    </div>
</div>

<script>
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

            const itemIdElem = clone.getElementsByClassName('item-id')[0];
            const itemNameElem = clone.getElementsByClassName('item-name')[0];
            const currentBidElem = clone.getElementsByClassName('current-bid')[0];
            const floorBidElem = clone.getElementsByClassName('floor-price')[0];
            const timeLeftElem = clone.getElementsByClassName('time-left')[0];
            const closeBtn = clone.getElementsByClassName('close-btn')[0];

            itemIdElem.textContent = auction.auctionId;
            itemNameElem.textContent = auction.itemName;
            currentBidElem.textContent = "Rs."+auction.highestBid;
            floorBidElem.textContent = "Rs."+auction.floorPrice;
            timeLeftElem.textContent = (auction.endTimestamp-Date.now())>0?(formatMilliseconds(auction.endTimestamp-Date.now())):"Ended";
            closeBtn.onclick = () => closeAuction(auction.auctionId);
            container.appendChild(clone);
        });
    }

    async function closeAuction(auctionId) {
        const response = await fetch("http://localhost:8080/oas/auction?auctionId=" + auctionId,{
            method: 'DELETE'})
        if(response.ok){

        }else{
            console.log("Auction could not close");
        }
    }

    setInterval(function () {
        auctionsList.forEach(function (auction) {
            const card = document.getElementById(auction.auctionId);
            const timeLeftElem = card.getElementsByClassName('time-left')[0];

            if((auction.endTimestamp-Date.now())>0){
                timeLeftElem.textContent = formatMilliseconds(auction.endTimestamp-Date.now());
            }else{
                timeLeftElem.textContent = "Ended";
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

