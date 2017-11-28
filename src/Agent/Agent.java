/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * Agent.java - Opens a bank account, registers with auction central,
 * requests lists of auctions from auction central, requests list of items to bid
 * on from auction houses, places bids using bidding key.
 */

package Agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Agent
{
  public static void main(String args[]) throws IOException
  {
    Socket socket = new Socket(InetAddress.getLocalHost(),2222);
    DataInputStream input = new DataInputStream(socket.getInputStream());
    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
    Scanner scan = new Scanner(System.in);
    String message;
    while(!(message = scan.nextLine()).equals("EXIT"))
    {
      output.writeUTF(message);
    }
    socket.close();
  }
}
