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

import Message.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Agent extends Thread implements Serializable
{
  private final int publicID;
  private final int agentBankKey;
  private final int agentCentralKey;
  private final String name;
  private String message = "";

  private static InetAddress bankAddress, auctionAddress;

  public static void setBankAddress(InetAddress bankAddress)
  {
    Agent.bankAddress = bankAddress;
  }

  public static void setAuctionAddress(InetAddress auctionAddress)
  {
    Agent.auctionAddress = auctionAddress;
  }

  public Agent()
  {
    publicID = (int) (Math.random() * 1000000);
    name = "[Agent-" + publicID + "]";
    agentBankKey = (int) (Math.random() * 1000000);
    agentCentralKey = (int) (Math.random() * 1000000);
  }

  // getName is a built in method after extending thread class. renamed to getAgentName.
  public String getAgentName()
  {
    return name;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  @Override
  public void run()
  {
    try
    {
      Agent agent = new Agent();
      Socket bankSocket = new Socket(bankAddress, 2222);
      Socket auctionCentralSocket = new Socket(auctionAddress, 1111);
      
      try (ObjectInputStream bankIn = new ObjectInputStream(bankSocket.getInputStream());
           ObjectOutputStream bankOut = new ObjectOutputStream(bankSocket.getOutputStream());
           ObjectInputStream auctionIn = new ObjectInputStream(bankSocket.getInputStream());
           ObjectOutputStream auctionOut = new ObjectOutputStream(bankSocket.getOutputStream()))
      {
        try
        {
          Message bankInput, bankOutput, auctionInput, auctionOutput;
          bankInput = ((Message)bankIn.readObject());
          auctionInput = ((Message)auctionIn.readObject());
  
          System.out.println(agent.name + ": Log in successful!");
          bankOut.writeObject(new Message(agent, "new", "", agent.agentBankKey, -1));
          auctionOut.writeObject(new Message(agent, "new", "", agent.agentCentralKey, -1));
          boolean flag = true;
          while (flag)
          {
            if(bankInput != null)
            {
              System.out.println(bankInput.getMessage());
    
              bankInput = ((Message)bankIn.readObject());
              bankOutput = null;//send your messages here
    
              bankOut.writeObject(bankOutput);
    
              bankInput = null;
            }
            if(auctionInput != null)
            {
              System.out.println(auctionInput.getMessage());
    
              auctionInput = ((Message)auctionIn.readObject());
              auctionOutput = null;//send your messages here
    
              auctionOut.writeObject(auctionOutput);
    
              auctionInput = null;
            }
          }
          bankIn.close();
          bankOut.close();
          bankSocket.close();

          auctionIn.close();
          auctionOut.close();
          auctionCentralSocket.close();
        }
        catch(ClassNotFoundException e)
        {
          System.err.println(e.getMessage());
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}