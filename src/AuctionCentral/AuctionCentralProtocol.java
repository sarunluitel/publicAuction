/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralProtocol.java - The protocol to follow.
 */

package AuctionCentral;

import Agent.Agent;
import AuctionHouse.AuctionHouse;
import Bank.Bank;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AuctionCentralProtocol {
  private Socket socket = null;
  private String name = null;
  
  private static Map<String, AuctionHouse> auctionRepository = Collections.synchronizedMap(new HashMap<String, AuctionHouse>());
  private static int clientCount = 0;
  private static final int WAITING = 0;
  
  private int state = WAITING;
  private String[] requests = {"register", "de-register", "repository", "transaction"};
  
  public AuctionCentralProtocol(Socket socket, String name)
  {
    this.socket = socket;
    this.name = name;
    
    clientCount++;
    
    for(int i = 0; i < 5; i++) registerAuctionHouse();
    
    System.out.println("[AuctionCentral]: Protocol-Constructor");
    System.out.println(clientCount + " clients connected!");
  }
  
  public String handleRequest(String request) {
    String result = "[AuctionCentral-" + this + "]: echo request = NOT RECOGNIZED";
    for(int i = 0; i < requests.length; i++)
    {
      if(request.equals(requests[i])) result = "[AuctionCentral-" + this + "]: echo request = " + request;
    }
    System.out.println(result);
    return result;
  }
  
  public void handleTransaction(Agent agent, AuctionHouse auctionHouse, Bank bank)
  {
//    bank.handleRequest("block:"+agent.getBid(), agent);
//    bank.handleRequest("unblock:"+agent.getBid(), agent);
//    bank.handleRequest("move:"+agent.getBid()+":"+auctionHouse.getName());
  }
  
  public void registerAuctionHouse()
  {
    int publicID = (int)(Math.random()*100000);
    AuctionHouse auctionHouse = new AuctionHouse(publicID);
    auctionRepository.put(auctionHouse.getName(), auctionHouse);
  }
  
  private void deregisterAuctionHouse(AuctionHouse auctionHouse)
  {
    auctionRepository.remove(auctionHouse.getName());
    
    try
    {
      socket.close();
    }
    catch(IOException e)
    {
      System.err.println("Socket already closed.");
    }
  }
}
