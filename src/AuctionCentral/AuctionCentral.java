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

import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    //AuctionCentral auctionCentral = new AuctionCentral();
    System.out.println("[AuctionCentral]: IP = " + InetAddress.getLocalHost() + ".");
    System.out.println("Enter Auction Central's IP: ");
    Scanner scan = new Scanner(System.in);
    String address = scan.nextLine();

    //try (Socket bankSocket = new Socket(InetAddress.getByName(address), 2222))


    try (ServerSocket serverSocket = new ServerSocket(1111, 50, InetAddress.getLocalHost()))
    {
      System.out.println("[AuctionCentral]: " + serverSocket.toString() + ".");
      while (true)
      {
        new AuctionCentralThread(serverSocket.accept()).start();
      }
    }
    catch (IOException e)
    {
      System.err.println("[AuctionCentral]: Error connecting...");
      System.exit(-1);
    }

  }
}
