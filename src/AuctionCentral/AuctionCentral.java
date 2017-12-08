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
import java.net.Socket;
import java.util.Scanner;

class AuctionCentral
{
  /**
   * Main method for AuctionCentral server.
   *
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException
  {
    System.out.println("[AuctionCentral]: IP = " + InetAddress.getLocalHost());
    System.out.println("Enter the Bank's IP: ");
    Scanner scan = new Scanner(System.in);
    String address = scan.nextLine();

    try (ServerSocket serverSocket = new ServerSocket(1111, 100, InetAddress.getLocalHost());
         Socket bankSocket = new Socket(address,2222))
    {
      System.out.println("[AuctionCentral]: serverSocket" + serverSocket.toString() + ".");
      System.out.println("[AuctionCentral]: bankSocket" + bankSocket.toString() + ".");

      AuctionCentralWriter bankWriter = new AuctionCentralWriter(bankSocket);
      
      while (true)
      {
        new AuctionCentralThread(serverSocket.accept(), bankWriter).start();
      }
    }
    catch (IOException e)
    {
      System.err.println("[AuctionCentral]: Error connecting...");
      System.exit(-1);
    }

  }
}
