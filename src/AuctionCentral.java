/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentral.java - Registers & de-registers auction houses,
 * accepts client requests to participate in auctions, mitigates financial
 * transactions between bank and auction house.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuctionCentral
{
  private static Map<String, AuctionHouse> auctionHouses = Collections.synchronizedMap(new HashMap<String, AuctionHouse>());
  private static String[] houseNames;
  
  public static void main(String args[]) throws IOException
  {
    ServerSocket serverSocket = new ServerSocket(1234);
    Socket clientSocket = serverSocket.accept();
  }
  
  /*
  houseProtocol
  agentProtocol
  bankProtocol
  
  while(true)
  {
    accept a connection;
    create a thread to deal with the client;
  }
   */
}
