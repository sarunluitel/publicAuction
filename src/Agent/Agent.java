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
    try
    {
      Socket bankSocket = new Socket(bankAddress, 2222);
      Socket auctionCentralSocket = new Socket(auctionAddress, 1111);
      try
      {
        ObjectOutputStream auctionOut = new ObjectOutputStream(auctionCentralSocket.getOutputStream());
        auctionOut.flush();
        ObjectOutputStream bankOut = new ObjectOutputStream(bankSocket.getOutputStream());
        bankOut.flush();

        ObjectInputStream auctionIn = new ObjectInputStream(auctionCentralSocket.getInputStream());
        ObjectInputStream bankIn = new ObjectInputStream(bankSocket.getInputStream());

        try
        {
          System.out.println(this.name + "Log in successful!");
          System.out.println(this.name + ": Log in successful!");
          System.out.println(this.name + "'s bankSocket : " + bankSocket.toString());
          System.out.println(this.name + "'s auctionSocket : " + auctionCentralSocket.toString());

          bankOut.writeObject(new Message(this, this.getAgentName(), "new", "", this.agentBankKey, -1));
          bankOut.flush();
          auctionOut.writeObject(new Message(this, this.getAgentName(), "new", auctionAddress.toString(), this.agentCentralKey, -1));
          auctionOut.flush();

          while (!messageText.equals("EXIT"))
          {
            System.out.println(this.getAgentName() + "Reading from auction central...");
            if(auctionIn.available() != 0) auctionInput = ((Message) auctionIn.readObject());

            System.out.println(this.getAgentName() + "Reading from bank...");
            if(bankIn.available() != 0) bankInput = ((Message) bankIn.readObject());

//            bankInput = auctionInput = null;
            if (!messageText.equals(""))
            {
              System.out.println(this.getAgentName() + "Submitting message = " + messageText + " to auction & bank.");
              
              auctionOutput = new Message(this, this.getAgentName(), messageText, "", agentCentralKey, 0);
              bankOutput = new Message(this, this.getAgentName(), messageText, "", agentBankKey, 0);
              
              //auctionOut.writeObject(auctionOutput);
              //auctionOut.flush();
              bankOut.writeObject(bankOutput);
              bankOut.flush();
              
              messageText = "";
            }
            else
            {
              synchronized (this)
              {
                try
                {
                  this.wait();
                }
                catch (InterruptedException ignored) {}
              }
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
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}