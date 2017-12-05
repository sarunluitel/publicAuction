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
    System.out.println("Enter the Bank's IP: ");
    Scanner scan = new Scanner(System.in);
    String address = scan.nextLine();

    try (ServerSocket serverSocket = new ServerSocket(1111, 50, InetAddress.getLocalHost());
         Socket socket = new Socket(InetAddress.getByName(address), 2222))
    {
      System.out.println("[AuctionCentral]: " + serverSocket.toString() + ".");

      /**
       * Connecting to Bank
       */
      try (ObjectOutputStream bankOut = new ObjectOutputStream(socket.getOutputStream());
           ObjectInputStream bankIn = new ObjectInputStream(socket.getInputStream()))
      {
        System.out.println("[AuctionCentral]: " + socket.toString() + " I'm connected!(to Bank)");

        Message bankInput, bankOutput;

        bankOut.writeObject(new Message(null, "[AuctionCentral]: ", "", "", 1234, -1));
        bankOut.flush();

        while (true)
        {
          try
          {
            serverSocket.setSoTimeout(5000);
            System.out.println("huh?");
            System.out.println("accept? " + serverSocket.accept());
            new AuctionCentralThread(serverSocket.accept()).start();
          }
          catch (IOException e)
          {
            //System.out.println("[AuctionCentral]: No Clients");
          }

          System.out.println("[AuctionCentral]: Reading from bank...");
          //if(bankIn.available() != 0) bankInput = ((Message) bankIn.readObject());

          bankOut.writeObject(new Message(null, "[AuctionCentral]: hmm....", "", "", 1234, -1));
          bankOut.flush();
        }
      }
      catch (IOException e)
      {
        System.err.println("[AuctionCentral]: Error connecting...2");
        System.exit(-1);
      }
    }
    catch (IOException e)
    {
      System.err.println("[AuctionCentral]: Error connecting...3");
      System.exit(-1);
    }

  }
}
