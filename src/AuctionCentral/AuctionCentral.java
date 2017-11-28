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
  private static InetAddress address;
  private static int port = 0;
  private static boolean open = false;
  
  public static void main(String[] args) throws IOException
  {
    System.out.println("This is my IP -"+ InetAddress.getLocalHost());
    if(args.length != 2) System.exit(-1);

    
    try (ServerSocket serverSocket = new ServerSocket(1111,50,InetAddress.getLocalHost()))
    {
      System.out.println("[AuctionCentral]:" + serverSocket.toString());
      while (open) new AuctionCentralThread(serverSocket.accept()).start();
    }
    catch (IOException e)
    {
      System.err.println("[AuctionCentral]: Port " + port + " may be busy.");
      System.exit(-1);
    }
  }
}
