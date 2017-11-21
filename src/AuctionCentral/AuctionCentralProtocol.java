/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralProtocol.java - The protocol to follow.
 */

package AuctionCentral;

import AuctionHouse.AuctionHouse;

import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuctionCentralProtocol {
  private Socket socket = null;
  private String name = null;
  
  private static Map<String, AuctionHouse> auctionHouses = Collections.synchronizedMap(new HashMap<String, AuctionHouse>());
  
  private static final int WAITING = 0;
  private int state = WAITING;
  
  public AuctionCentralProtocol(Socket socket, String name)
  {
    this.socket = socket;
    this.name = name;
    System.out.println("AuctionCentralProtocol-Constructor");
  }
  
  public String handleRequest(String request) {
    return "AuctionCentralProtocol-Request";
  }
  
  public void handleTransaction()
  {
  }
  
  public void registerAuctionHouse(AuctionHouse auctionHouse)
  {
  
  }
  
  public void deregisterAuctionHouse(AuctionHouse auctionHouse)
  {
  }
}
