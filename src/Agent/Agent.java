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
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class Agent extends Thread implements Serializable
{
  private final int publicID;
  private final int agentBankKey;
  private final int agentCentralKey;
  private final String name;

  private String messageText = "";
  
  private InetAddress bankAddress, auctionAddress;
  public Message bankInput, bankOutput, auctionInput, auctionOutput;
  
  class Listener extends Thread
  {
    private ObjectInputStream auctionIn, bankIn;
    
    Listener(ObjectInputStream ais, ObjectInputStream bis)
    {
      auctionIn = ais;
      bankIn = bis;
    }
  
    @Override
    public void run() {
      while(true) {
        try {
          try {
            if(auctionIn.available() != 0)
            {
              System.out.println(getAgentName() + "Reading from auction central...");
              auctionInput = ((Message) auctionIn.readObject());
              System.out.println(auctionInput.getMessage());
            }
      
            if(bankIn.available() != 0)
            {
              System.out.println(getAgentName() + "Reading from bank...");
              bankInput = ((Message) bankIn.readObject());
              System.out.println(bankInput.getMessage());
            }
          }catch(IOException e) {
          }
        }catch(ClassNotFoundException c) {
        }
      }
    }
  }
  
  /**
   * Sets the IP address for the bank.
   * @param bankAddress
   */
  void setBankAddress(InetAddress bankAddress)
  {
    this.bankAddress = bankAddress;
  }
  
  /**
   * Sets the IP address for auction central.
   * @param auctionAddress
   */
  void setAuctionAddress(InetAddress auctionAddress)
  {
    this.auctionAddress = auctionAddress;
  }
  
  /**
   * Default constructor.
   */
  public Agent()
  {
    publicID = new Random().nextInt(1000);
    name = randomName(new Random().nextInt(20));
    agentBankKey = (int) (Math.random() * 1000000);
    agentCentralKey = (int) (Math.random() * 1000000);
  }
  
  /**
   * Helper method for names in constructor.
   * @param input
   * @return random name
   */
  private String randomName(int input)
  {
    String name = "[Agent-" + getPublicID() + "]: ";
    if (input % 4 == 0) name = "[Jacob-" + getPublicID() + "]: ";
    else if (input % 4 == 1) name = "[Jaehee-" + getPublicID() + "]: ";
    else if (input % 4 == 2) name = "[Sarun-" + getPublicID() + "]: ";
    else if (input % 4 == 3) name = "[Vincent-" + getPublicID() + "]: ";
    return name;
  }
  
//  public String getAgentPublicID()
//  {
//    return "[Agent-" + publicID + "]";
//  }
  
  /**
   * @return public ID associated with agent.
   */
  public int getPublicID()
  {
    return publicID;
  }
  
  /**
   * @return name associated with agent.
   */
  public String getAgentName()
  {
    return name;
  }
  
  /**
   * Used by GUIController to set messages as user enters them.
   * @param messageText
   */
  void setMessageText(String messageText)
  {
    this.messageText = messageText;
  }
  
  /**
   * Run method for agent thread - handles messaging.
   */
  @Override
  public void run()
  {
    System.out.println("A connecting");
    try
    {
      Socket bankSocket = new Socket(bankAddress, 2222);
      System.out.println("A connected B");
      Socket auctionCentralSocket = new Socket(auctionAddress, 1111);
      System.out.println("A connected AC");
      try
      {
        ObjectOutputStream auctionOut = new ObjectOutputStream(auctionCentralSocket.getOutputStream());
        auctionOut.flush();
        System.out.println("A - AC out stream opened");
        ObjectOutputStream bankOut = new ObjectOutputStream(bankSocket.getOutputStream());
        bankOut.flush();
        System.out.println("A - B out stream opened");

        ObjectInputStream auctionIn = new ObjectInputStream(auctionCentralSocket.getInputStream());
        System.out.println("A - AC in stream opened");
        ObjectInputStream bankIn = new ObjectInputStream(bankSocket.getInputStream());
        System.out.println("A - B in stream opened");
        
        try
        {
          System.out.println(this.name + "Log in successful!");
          System.out.println(this.name + "BankSocket = " + bankSocket.toString());
          System.out.println(this.name + "AuctionSocket = " + auctionCentralSocket.toString());
  
          System.out.println("A writing init to B");
          
          bankOut.writeObject(new Message(this, this.getAgentName(), "new", "", this.agentBankKey, -1));
          bankOut.flush();
          
          System.out.println("A sent init to B\nA writing init to AC");
          
          auctionOut.writeObject(new Message(this, this.getAgentName(), "new", auctionAddress.toString(), this.agentCentralKey, -1));
          auctionOut.flush();
          
          System.out.println("A sent init to AC");
          
          Listener listener = new Listener(auctionIn, bankIn);
          listener.start();
          
          while (!messageText.equals("EXIT"))
          {
//            bankInput = auctionInput = null;
            if (!messageText.equals(""))
            {
              System.out.println(this.getAgentName() + "Submitting message = " + messageText + " to auction & bank.");
              
              auctionOutput = new Message(this, this.getAgentName(), messageText, "", agentCentralKey, 0);
              bankOutput = new Message(this, this.getAgentName(), messageText, "", agentBankKey, 0);
  
              System.out.println("A writing to AC");
              auctionOut.writeObject(auctionOutput);
              auctionOut.flush();
              System.out.println("A sent to AC");
  
              System.out.println("A writing to B");
              bankOut.writeObject(bankOutput);
              bankOut.flush();
              System.out.println("A sent to B");
              
              messageText = "";
            }
            else
            {
              synchronized (this)
              {
                try
                {
                  System.out.println("A waiting");
                  this.wait();
                }
                catch (InterruptedException ignored) {}
              }
            }
            System.out.println("A notified");
          }

          bankIn.close();
          bankOut.close();
          bankSocket.close();

          auctionIn.close();
          auctionOut.close();
          auctionCentralSocket.close();
        }
        catch (IOException e)
        {
          
          e.printStackTrace();
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}