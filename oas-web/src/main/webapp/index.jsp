<%--
  Created by IntelliJ IDEA.
  User: Sachira Jayawardana
  Date: 2025-06-02
  Time: 3:13 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OAS | User Login</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            margin: 0;
        }
        .app-heading {
            position: absolute;
            top: 20px;
            left: 20px;
            font-size: 18px;
            font-weight: bold;
            color: #2c3e50;
        }

        h1 {
            margin-bottom: 20px;
            color: #333;
        }

        .user-container {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            justify-content: center;
        }

        .manage-btn {
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

        .manage-btn:hover {
            background-color: #2980b9;
        }
        .user-card {
            background-color: white;
            border-radius: 10px;
            padding: 40px;
            width: 150px;
            height: 100px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            cursor: pointer;
            transition: transform 0.2s, background-color 0.2s;
        }

        .user-card:hover {
            transform: scale(1.05);
            background-color: #e6f7ff;
        }
    </style>
</head>
<body>
<div class="app-heading">Online Auction System (Testing Mode)</div>
<button class="manage-btn" onclick="window.location.href='manage-auction.jsp'">Manage Auction</button>
<h1>Login as an Attendee</h1>
<div class="user-container">
    <div class="user-card" onclick="loginAsUser('Alice')">Alice</div>
    <div class="user-card" onclick="loginAsUser('Bob')">Bob</div>
    <div class="user-card" onclick="loginAsUser('Charlie')">Charlie</div>
</div>

<form id="loginForm" action="login" method="POST" style="display: none;">
    <input type="hidden" name="username" id="username">
</form>

<script>
    function loginAsUser(username) {
        document.getElementById('username').value = username;
        document.getElementById('loginForm').submit();
    }
</script>
</body>
</html>

