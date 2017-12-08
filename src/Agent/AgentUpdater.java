package Agent;

import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

public class AgentUpdater extends Thread implements Serializable
{
  private Message auctionMessage, bankMessage;
  private ObjectOutputStream auctionOut, bankOut;
  private ObjectInputStream auctionIn, bankIn;
  private Agent agent;
  
  private int balance;
  private String inventory;
  
  public AgentUpdater(InetAddress auctionAddress, InetAddress bankAddress, Agent agent)
  {
    this.agent = agent;
    
    try
    {
      Socket auction = new Socket(auctionAddress, 1111);
      Socket bank = new Socket(bankAddress, 2222);
      
      auctionOut = new ObjectOutputStream(auction.getOutputStream());
      auctionOut.flush();
      
      bankOut = new ObjectOutputStream(bank.getOutputStream());
      bankOut.flush();
      
      auctionIn = new ObjectInputStream(auction.getInputStream());
      bankIn = new ObjectInputStream(bank.getInputStream());
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public String getInventory() {
//    System.out.println(inventory);
    return inventory;
  }
  
  public int getBalance() {
//    System.out.println(balance);
    return balance;
  }
  
  @Override
  public void run()
  {
    while(true)
    {
      try { sleep(1000); } catch(InterruptedException ignored) {}
      try {
        auctionOut.writeObject(new Message(agent, agent.getAgentName(), "repository", "", agent.getAgentCentralKey(), agent.getAgentBankKey()));
        auctionOut.flush();
        bankOut.writeObject(new Message(agent, agent.getAgentName(), "balance", "", agent.getAgentBankKey(), -1));
        bankOut.flush();
  
        try
        {
          auctionMessage = ((Message) auctionIn.readObject());
          bankMessage = ((Message) bankIn.readObject());
          
          inventory = auctionMessage.getMessage();
          balance = bankMessage.getAmount();
        }
        catch(ClassNotFoundException e)
        {
          e.printStackTrace();
        }
      }
      catch(IOException e)
      {
        e.printStackTrace();
      }
    }
  }
}
