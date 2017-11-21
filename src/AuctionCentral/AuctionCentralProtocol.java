/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralProtocol.java - The protocol to follow.
 */

package AuctionCentral;

import AuctionHouse.AuctionHouse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuctionCentralProtocol {
  private static Map<String, AuctionHouse> auctionHouses = Collections.synchronizedMap(new HashMap<String, AuctionHouse>());
  
  private static final int WAITING = 0;
  private int state = WAITING;
  
  public String handleRequest(String request) {
    return "AuctionCentralProtocol";
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
