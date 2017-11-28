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
  private static String ip = "";
  private static InetAddress address;
  private static int port = 0;
  private static boolean open = false;
  
  public static void main(String[] args) throws IOException
  {
    if(args.length != 3) System.exit(-1);
    try
    {
      ip = args[0];
      address = InetAddress.getByName(ip);
      port = 1111;
      open = true;
    }
    catch(Exception e)
    {
      e.printStackTrace();
      System.exit(-1);
    }
    
    try (ServerSocket serverSocket = new ServerSocket(port, 50, address))
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
