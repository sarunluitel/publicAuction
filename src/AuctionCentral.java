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

public class AuctionCentral
{
  public static void main(String[] args) throws IOException
  {
    if (args.length != 1)
    {
      System.err.println("Usage: java AuctionCentral <port number>");
      System.exit(1);
    }
    
    int portNumber = Integer.parseInt(args[0]);
    boolean listening = true;
    
    try (ServerSocket serverSocket = new ServerSocket(portNumber))
    {
      while (listening) new AuctionCentralThread(serverSocket.accept()).start();
    }
    catch (IOException e)
    {
      System.err.println("Could not listen on port " + portNumber);
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
