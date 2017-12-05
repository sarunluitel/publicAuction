# PublicAuction
<b>***Course: CS351L - Design of Large Programs<br>
Author: Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber<br>
Project: PublicAuction***</b><br>

# Overview:
<details>
  <summary>Public Auction is a socket-based simulation of a real-time auction!</summary>
  <p>  
* Agents are created/cleared dynamically as users connect/disconnect to the servers (Bank & AuctionCentral).<br>
* Upon connecting, agent's are given a bank account with a private key & an initial deposit. From there, the Agent's connection to Auction Central is made & the agent may begin bidding when Auction Houses are opened. <br>
* Auction Houses are created dynamically & exit when they no longer have items to sell. Auction Houses are registered & accessed via Auction Central. <br>
* Auction Central (static @ known address) acts as a middle-man between the agent, the agent's bank, & the auction house by mitigating transactions & providing updates from houses. <br>
* The Bank (static @ known address) opens agent bank accounts & accepts fund requests from auction central. <br>
  </p>
</details>

# Versions:
<b>Version 1: Initial GUI </b><br>
Version 1 contains the initial GUI. In this version, all connections are made and Agents & Auction Houses can be created dynamically. Messages need some work as they occasionally cause hold-ups. <br>

# How to use:
<b>Version 1:</b><br>
1) Extract *PublicAuction_V1.zip*. <br>
2) Run *AuctionCentral_V1.jar* & *Bank_V1.jar* first & note your IP for each (this is provided via command-line when AuctionCentral & Bank are running). <br>
3) Run *Agent_V1.jar* & *AuctionHouse_V1.jar*. <br>
4) AuctionHouse will request the IP address via command-line & will be automated thereafter. <br>
Agent will display a GUI - in this GUI, there will be two textfields to input your IP addresses for the Bank & Auction Central - provide the IP addresses in their respective textfields and then click "Connect" to connect to the servers. <br>
5) Once the connections have been made, you may begin submitting messages to Auction Central (updates are provided automatically from Bank) & view Auction Central's responses. <br>
6) As updates from Bank are automatic & Auction Central relays messages to Auction Houses & the Bank, the only commands that the agent needs to worry about are the commands it sends the Auction Central. <br><br>
* See the list of Auction Central commands below for more details. <br>

# Auction Central's list of commands:
* "repository"  Provides latest listings of auction houses. <br>
* "connect" - Connects agent to an auction house (NOT SUPPORTED IN V1). <br>
* Note: additional internal commands passed automatically between houses & bank. <br>

# Auction House list of commands:
* "current" - Provides current items being sold at auction house. <br>
* "bid" - Requests bid on selected item & is returned whether the bid was accepted or declined. <br>
* Note: additional internal commands passed automatically between auction central. <br>

# Bank list of commands:
* Note: all internal commands passed & received.

# Known Bugs:
<br>
