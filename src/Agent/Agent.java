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
  private int bidAmount;

  private InetAddress bankAddress, auctionAddress;
  public Message bankInput;
  private Message bankOutput;
  public Message auctionInput;
  public Message auctionOutput;

  public String inventory;
  public int balance;

  /**
   * Sets the IP address for the bank.
   *
   * @param bankAddress
   */
  void setBankAddress(InetAddress bankAddress)
  {
    this.bankAddress = bankAddress;
  }

  /**
   * Sets the IP address for auction central.
   *
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

  public int getAgentBankKey()
  {
    return agentBankKey;
  }

  public int getAgentCentralKey()
  {
    return agentCentralKey;
  }

  /**
   * Helper method for names in constructor.
   *
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

  /**
   * @return public ID associated with agent.
   */
  private int getPublicID()
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
   *
   * @param messageText
   */
  void setMessageText(String messageText)
  {
    this.messageText = messageText;
  }

  void setBidAmount(int amount) { this.bidAmount = amount;}
  
  /**
   * Run method for agent thread - handles messaging.
   */
  @Override
  public void run()
  {
    System.out.println("A connecting");
    try (Socket bankSocket = new Socket(bankAddress, 2222);
         Socket auctionCentralSocket = new Socket(auctionAddress, 1111))
    {
      System.out.println("A connected");
      try
      {
        System.out.println("A auction out stream");
        ObjectOutputStream auctionOut = new ObjectOutputStream(auctionCentralSocket.getOutputStream());
        auctionOut.flush();
        System.out.println("A out opened");

        System.out.println("A bank out stream");
        ObjectOutputStream bankOut = new ObjectOutputStream(bankSocket.getOutputStream());
        bankOut.flush();
        System.out.println("A out opened");

        System.out.println("A in a stream");
        ObjectInputStream auctionIn = new ObjectInputStream(new BufferedInputStream(auctionCentralSocket.getInputStream()));
        System.out.println("A in a opened\nA in b stream");
        ObjectInputStream bankIn = new ObjectInputStream(new BufferedInputStream(bankSocket.getInputStream()));
        System.out.println("A in b opened");
        try
        {
          System.out.println(this.name + "Log in successful!");

          AgentUpdater agentUpdater = new AgentUpdater(auctionAddress, bankAddress, this);

          System.out.println("A init to B");
          bankOut.writeObject(new Message(this, this.getAgentName(), "new", "", this.agentBankKey, -1));
          bankOut.flush();
          System.out.println("A init sent to B");
          System.out.println("A init to AC");
          auctionOut.writeObject(new Message(this, this.getAgentName(), "new", auctionAddress.toString(), this.agentCentralKey, this.agentBankKey));
          auctionOut.flush();
          System.out.println("A init sent to AC");

          System.out.println(this.getAgentName() + "Reading from bank.... about to read size:" + bankIn.available());
          bankInput = ((Message) bankIn.readObject());
          System.out.println(this.getAgentName() + "Finished Reading from bank..." + bankInput.getSignature() + "Size read" + bankIn.available());

          System.out.println(this.getAgentName() + "Reading from auction central...");
          auctionInput = ((Message) auctionIn.readObject());
          System.out.println(this.getAgentName() + "Finished Reading from auction central..." + auctionInput.getSignature());

          agentUpdater.start();
          while (!messageText.equals("EXIT"))
          {
            inventory = agentUpdater.getInventory();
            balance = agentUpdater.getBalance();
            try { sleep(1); } catch(InterruptedException ignored) {}
            
            if (!messageText.equals(""))
            {
              String temp[], houseName = "", itemName = "";
              if(messageText.contains("bid"))
              {
                temp = messageText.split("\\ ");
                houseName = temp[0];
                itemName = temp[1];
              }
              System.out.println(this.getAgentName() + "Submitting message = " + messageText + " to auction & bank.");

              auctionOutput = new Message(houseName, this.getAgentName(), messageText, itemName, agentBankKey, bidAmount);
              bankOutput = new Message(houseName, this.getAgentName(), messageText, itemName, agentBankKey, bidAmount);

              System.out.println("A to AC");
              auctionOut.writeObject(auctionOutput);
              auctionOut.flush();
              System.out.println("A sent to AC");
              System.out.println("A to B");
              bankOut.writeObject(bankOutput);
              bankOut.flush();
              System.out.println("A sent to B");

              messageText = "";

              System.out.println(this.getAgentName() + "Reading from bank.... about to read size:" + bankIn.available());
              bankInput = ((Message) bankIn.readObject());
              System.out.println(this.getAgentName() + "Finished Reading from bank..." + bankInput.getSignature() + "Size read" + bankIn.available());
              filterBank(bankInput);

              System.out.println(this.getAgentName() + "Reading from auction central...");
              auctionInput = ((Message) auctionIn.readObject());
              System.out.println(this.getAgentName() + "Finished Reading from auction central..." + auctionInput.getSignature());
              filterAuction(auctionInput);
            }
          }
          
          agentUpdater.setFinished(true);

          bankIn.close();
          bankOut.close();
          bankSocket.close();

          auctionIn.close();
          auctionOut.close();
          auctionCentralSocket.close();
        } catch (ClassNotFoundException e)
        {
          e.printStackTrace();
        }
      } catch (IOException e)
      {
        e.printStackTrace();
      }
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private void filterBank(Message message)
  {
    switch (message.getMessage())
    {
      case "Error - request not recognized.":
        bankInput = null;
        break;
      case "updated":
        auctionInput = null;
        break;
      default:
        break;
    }
  }

  private void filterAuction(Message message)
  {
    switch (message.getMessage())
    {
      case "registered":
        auctionInput = null;
        break;
      case "de-registered":
        auctionInput = null;
        break;
      case "remove":
        auctionInput = null;
        break;
      default:
        break;
    }
  }
}