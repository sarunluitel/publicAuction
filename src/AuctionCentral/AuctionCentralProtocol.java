/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralProtocol.java - The protocol to follow.
 */

package AuctionCentral;

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
  
  private static final int WAITING = 0;
  private int state = WAITING;
  private String[] requests = {"register", "de-register", "transaction"};
  
  public AuctionCentralProtocol(Socket socket, String name)
  {
    this.socket = socket;
    this.name = name;
    System.out.println("[AuctionCentral]: Protocol-Constructor");
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
  
  public void handleTransaction(Bank bank, AuctionHouse auctionHouse)
  {
  }
  
  public void registerAuctionHouse(String name, AuctionHouse auctionHouse)
  {
  
  }
  
  public void deregisterAuctionHouse(String name, AuctionHouse auctionHouse)
  {
  }
}
