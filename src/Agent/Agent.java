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

  private InetAddress bankAddress, auctionAddress;

  public void setBankAddress(InetAddress bankAddress)
  {
    System.out.println(auctionAddress.toString());
    this.bankAddress = bankAddress;
  }

  public  void setAuctionAddress(InetAddress auctionAddress)
  {
    System.out.println(auctionAddress.toString());
    this.auctionAddress = auctionAddress;
  }

  public Agent()
  {
    System.out.println("new agent");
    publicID = (int) (Math.random() * 1000000);
    name = "[Agent-" + publicID + "]";
    agentBankKey = (int) (Math.random() * 1000000);
    agentCentralKey = (int) (Math.random() * 1000000);
    System.out.println(this);
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
    System.out.println("agent run");
    try
    {
      Socket bankSocket = new Socket(bankAddress, 2222);
      Socket auctionCentralSocket = new Socket(auctionAddress, 1111);
      System.out.println("connected");
      try
      {
        System.out.println(bankSocket.getInputStream().available());
        System.out.println(bankSocket.getInputStream());
        System.out.println(bankSocket.isConnected());
        System.out.println(this);
        ObjectOutputStream bankOut = new ObjectOutputStream(bankSocket.getOutputStream());
        System.out.println("wtf3");
        ObjectOutputStream auctionOut = new ObjectOutputStream(auctionCentralSocket.getOutputStream());
        System.out.println("wtf5");
        ObjectInputStream bankIn = new ObjectInputStream(bankSocket.getInputStream());
        System.out.println("wtf2");
        ObjectInputStream auctionIn = new ObjectInputStream(auctionCentralSocket.getInputStream());
        System.out.println("wtf4");
        try
        {
          System.out.println("streams opened");
          Message bankInput, bankOutput, auctionInput, auctionOutput;
  
          System.out.println(this.name + ": Log in successful!");
  
          System.out.println("writing to bank");
          bankOut.writeObject(new Message(this, "new", "", this.agentBankKey, -1));
          System.out.println("writing to auction");
          auctionOut.writeObject(new Message(this, "new", auctionAddress.toString(), this.agentCentralKey, -1));
  
          System.out.println("listening to bank");
          bankInput = ((Message)bankIn.readObject());
          System.out.println("listening to auction");
          auctionInput = ((Message)auctionIn.readObject());
  
          boolean flag = true;
          while (flag)
          {
            System.out.println("loop");
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
        catch (ClassNotFoundException e)
        {
          e.printStackTrace();
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      finally {
        System.out.println("uhhh");
      }
      System.out.println("HELLO");
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}