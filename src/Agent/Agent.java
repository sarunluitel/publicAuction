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
  private boolean messageSubmitted = false;

  private InetAddress bankAddress, auctionAddress;
  public Message MsgFrmAuction, MsgFrmBank;

  public void setBankAddress(InetAddress bankAddress)
  {
    this.bankAddress = bankAddress;
  }

  public void setAuctionAddress(InetAddress auctionAddress)
  {
    this.auctionAddress = auctionAddress;
  }

  public Agent()
  {
    publicID = new Random().nextInt(1000);
    name = giveRandName(new Random().nextInt(20));
    agentBankKey = (int) (Math.random() * 1000000);
    agentCentralKey = (int) (Math.random() * 1000000);
  }

  private String giveRandName(int input)
  {
    if (input % 4 == 0) return "Jacob";
    else if (input % 4 == 1) return "Jaehee";
    else if (input % 4 == 2) return "Sarun";
    else if (input % 4 == 3) return "Vince";

    return "NoName";
  }
  public String getAgentPublicID()
  {
    return "[Agent-" + publicID + "]";
  }

  public int getPublicID()
  {
    return publicID;
  }

  // getName is a built in method after extending thread class. renamed to getAgentName.
  public String getAgentName()
  {
    return name;
  }


  public void setMessageText(String messageText)
  {
    this.messageText = messageText;
  }

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
          Message bankInput, bankOutput, auctionInput, auctionOutput;
          System.out.println(this.name + ": Log in successful!");

          bankOut.writeObject(new Message(this, this.getAgentName() + ": ", "new", "", this.agentBankKey, -1));
          bankOut.flush();
          auctionOut.writeObject(new Message(this, this.getAgentName() + ": ", "new", auctionAddress.toString(), this.agentCentralKey, -1));
          auctionOut.flush();

          while (true)
          {
            bankInput = auctionInput = null;
            if (!messageText.equals(""))
            {
              System.out.println(this.getAgentName() + ": Submitting message = " + messageText + " to auction & bank.");
              auctionOutput = new Message(this, this.getAgentName() + ": ", messageText, "", agentCentralKey, 0);
              bankOutput = new Message(this, this.getAgentName() + ": ", messageText, "", agentBankKey, 0);

              auctionOut.writeObject(auctionOutput);
              auctionOut.flush();
              bankOut.writeObject(bankOutput);
              bankOut.flush();


              messageText = "";
            } else
            {
              synchronized (this)
              {
                try
                {
                  this.wait();
                } catch (InterruptedException ignored)
                {
                }
              }
            }

            System.out.println(this.getAgentName() + ": Reading from auction central...");
            if(auctionIn.available()!=0)   auctionInput = ((Message) auctionIn.readObject());

            System.out.println(this.getAgentName() + ": Reading from bank...");
            if(bankIn.available()!=0) bankInput = ((Message) bankIn.readObject());
            MsgFrmAuction=auctionInput;
            MsgFrmBank= bankInput;
           // System.out.println(MsgFrmBank.getAmount());


            if (bankInput != null)
            {
              if (bankInput.getMessage().equals("EXIT")) break;
              System.out.println(bankInput.getSignature() + bankInput.getMessage());
            }
            if (auctionInput != null)
            {
              if (auctionInput.getMessage().equals("EXIT")) break;
              System.out.println(auctionInput.getSignature() + auctionInput.getMessage());
            }
          }
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
}