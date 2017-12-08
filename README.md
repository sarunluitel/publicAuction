# PublicAuction
<b>***Course: CS351L - Design of Large Programs<br>
Author: Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber<br>
Project: PublicAuction***</b><br>

# Overview:
<details>
<summary>Public Auction is a socket-based simulation of a real-time auction!</summary>
Agents are created/cleared dynamically as users connect/disconnect to the servers (Bank & AuctionCentral). Upon connecting, agent's are given a bank account with a private key & an initial deposit. From there, the Agent's connection to Auction Central is made & the agent may begin bidding when Auction Houses are opened. Auction Houses are created dynamically & exit when they no longer have items to sell. Auction Houses are registered & accessed via Auction Central. Auction Central (static @ known address) acts as a middle-man between the agent, the agent's bank, & the auction house by mitigating transactions & providing updates from houses. The Bank (static @ known address) opens agent bank accounts & accepts fund requests from auction central. <br>
</details>

# Versions:
<b>Version 1: Initial GUI </b><br>
Version 1 contains the initial GUI. In this version, all connections are made and Agents & Auction Houses can be created dynamically. Messages need some work as they occasionally cause hold-ups. <br>
<b>Version 2: Enhanced GUI & Auction </b><br>
Version 2 builds off of version 1 by fixing bugs, adding a major UI overhaul, and adding automated updates. Messages now send properly and are handled properly on server end, UI provides additional information such as balance, bids, timestamps, and current listings with up-to-date pricing. Automated updaters have been added in the agent class to eliminate the need for manual updates with agent. Automated updates poll for changes from server periodically and update on the agent display. <br>

# How to use:
<b>Version 1:</b><br>
1) Extract *PublicAuction_V1.zip*. <br>
2) Run *AuctionCentral_V1.jar* & *Bank_V1.jar* first & note your IP for each (this is provided via command-line when AuctionCentral & Bank are running). <br>
3) Run *Agent_V1.jar* & *AuctionHouse_V1.jar*. <br>
4) AuctionHouse will request the IP address via command-line & will be automated thereafter. <br>
Agent will display a GUI - in this GUI, there will be two textfields to input your IP addresses for the Bank & Auction Central - provide the IP addresses in their respective textfields and then click "Connect" to connect to the servers. <br>
5) Once the connections have been made, you may begin submitting messages to Auction Central & the Bank.<br><br>

<b>Version 2:</b><br>
1) Extract *PublicAuction_V2.zip*. <br>
2) Run *AuctionCentral_V2.jar* & *Bank_V2.jar* first & note your IP for each (this is provided via command-line when AuctionCentral & Bank are running). <br>
3) When prompted by Auction Central, provide the Bank IP.
4) Run *Agent_V1.jar* & *AuctionHouse_V1.jar* - as many as desired. <br>
4) Auction Houses will request the Auction Central IP via command-line & will be automated thereafter. <br>
Agents will display a GUI - in this GUI, there will be two textfields to input your IP addresses for the Bank & Auction Central - provide the IP addresses in their respective textfields and then click "Connect" to connect to the servers. <br>
5) Once the connections have been made, you may begin bidding - (updates are provided automatically from Bank & Auction Central). <br>
6) As updates from Bank are automatic & Auction Central relays messages to Auction Houses & the Bank, the only commands that the agent needs to worry about are the commands it sends the Auction Central (However, an Agent may still message Bank if desired). <br>
* See the list of Auction Central & Bank commands below for more details. <br>

# Auction Central's list of commands:
* "repository" - Refreshes latest listings of auction houses manually. <br>
* "bid <amount>" - Submits bid on item in Auction House - make sure to have item selected in combobox. <br>
* Note: additional internal commands passed automatically between houses & bank. <br>

# Auction House's list of commands:
* Note: all internal commands passed & received.

# Bank list of commands:
* "balance" - Refreshes the agents balance manually.

# Known Bugs:
* On rare occasions, messages can get stuck. Sometimes sending another message fixes this.
