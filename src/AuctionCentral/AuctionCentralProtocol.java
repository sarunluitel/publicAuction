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

import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
//    bank.send("block:"+agent.getBid(), agent);
//    bank.send("unblock:"+agent.getBid(), agent);
//    bank.send("move:"+agent.getBid()+":"+auctionHouse.getName());
  }
  
  public void registerAuctionHouse(AuctionHouse auctionHouse)
  {
//    auctionRepository.put(auctionHouse.getName(), auctionHouse);
  }
  
  public void deregisterAuctionHouse(AuctionHouse auctionHouse)
  {
//    auctionRepository.remove(auctionHouse.getName());
//    socket.close();
  }
}
