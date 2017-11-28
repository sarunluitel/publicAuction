/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionHouse.java - Registers with auction central, contains fixed list of items
 * and auctions at most three items at a time, receives and acknowledges bids, requests
 * auction central to release/place holds on bank accounts associated with bids, requests
 * transfer when bid is successful, bids are successful after 30s of inactivity.
 */

package AuctionHouse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class AuctionHouse
{
  private String name;
  
  public AuctionHouse(int publicID)
  {
    name = "[HOUSE:" + publicID + "]";
  }
 
  public String getName()
  {
    return this.name;
  }
  
  
  //Not sure if auction houses need to be ran independently anymore since they are created by auction central
  //May just need to have a thread and protocols for each auction house
  public static void main(String args[]) throws IOException
  {
    Socket socket = new Socket(InetAddress.getLocalHost(), 1111);
    DataInputStream in = new DataInputStream(socket.getInputStream());
    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
    Scanner scan = new Scanner(System.in);
    String message;
    while (!(message = scan.nextLine()).equals("EXIT"))
    {
      out.writeUTF(message);
      System.out.println(in.readUTF());
    }

    out.writeUTF("EXIT");
    in.close();
    out.close();
    socket.close();
  }
}
