/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentral.java - Registers & de-registers auction houses,
 * accepts client requests to participate in auctions, mitigates financial
 * transactions between bank and auction house.
 */

package AuctionCentral;

import java.io.IOException;
import java.net.ServerSocket;

public class AuctionCentral
{
  public static void main(String[] args) throws IOException
  {
    int portNumber = 1111;
    boolean open = true;
    
    try (ServerSocket serverSocket = new ServerSocket(portNumber))
    {
      while (open) new AuctionCentralThread(serverSocket.accept()).start();
    }
    catch (IOException e)
    {
      System.err.println("Port " + portNumber + " may be in use.");
      System.exit(-1);
    }
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
