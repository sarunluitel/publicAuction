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

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Agent implements Serializable
{
  private int publicID;
  private int agentBankKey;
  private int agentCentralKey;
  private String name;
  
  public Agent()
  {
    publicID = (int)(Math.random()*1000000);
    name = "Agent:" + publicID;
    agentBankKey = (int)(Math.random()*1000000);
    agentCentralKey = (int)(Math.random()*1000000);
  }
  
  public String getName() {
    return name;
  }
  
  public static void main(String args[]) throws IOException
  {
    //Not too sure how we should handle the agent connecting to both the bank and the auction central socket
    //and eventually the auction houses but this seems like a start
    
    Agent agent = new Agent();
    
    Socket bankSocket = new Socket(InetAddress.getLocalHost(),2222);
    DataInputStream bankI = new DataInputStream(bankSocket.getInputStream());
    DataOutputStream bankO = new DataOutputStream(bankSocket.getOutputStream());
    
    Socket auctionCentralSocket = new Socket(InetAddress.getLocalHost(), 1111);
    ObjectOutputStream auctionCentralObj = new ObjectOutputStream(auctionCentralSocket.getOutputStream());
    DataInputStream auctionCentralI = new DataInputStream(auctionCentralSocket.getInputStream());
    DataOutputStream auctionCentralO = new DataOutputStream(auctionCentralSocket.getOutputStream());
    
    Scanner scan = new Scanner(System.in);
    String message;
  
    System.out.println(agent);
    auctionCentralObj.writeObject(agent);
    
    while(!(message = scan.nextLine()).equals("EXIT"))
    {
      bankO.writeUTF(message);
      auctionCentralO.writeUTF(message);
      System.out.println(bankI.readUTF());
    }

    bankO.writeUTF("EXIT");
    bankI.close();
    bankO.close();
    bankSocket.close();
    
    auctionCentralO.writeUTF("EXIT");
    auctionCentralI.close();
    auctionCentralO.close();
    auctionCentralSocket.close();
  }
}

// Close a port manually for Mac
// sudo lsof -i :<port>
// kill -9 <PID>