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
import java.net.InetAddress;
import java.net.ServerSocket;

public class AuctionCentral
{
  public static void main(String[] args) throws IOException
  {
    System.out.println("[AuctionCentral]: IP = " + InetAddress.getLocalHost() + ".");
    try (ServerSocket serverSocket = new ServerSocket(1111, 50, InetAddress.getLocalHost()))
    {
      boolean open = true;
      System.out.println("[AuctionCentral]: " + serverSocket.toString() + ".");
      while (open) new AuctionCentralThread(serverSocket.accept()).start();
    }
    catch (IOException e)
    {
      System.err.println("[AuctionCentral]: Error connecting...");
      System.exit(-1);
    }
  }
}
