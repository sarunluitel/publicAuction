package Agent;

import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

public class AgentUpdater extends Thread implements Serializable
{
  private Message auctionMessage, bankMessage;
//  private ObjectOutputStream auctionOut, bankOut;
//  private ObjectInputStream auctionIn, bankIn;
  private Agent agent;
  
  private InetAddress auctionAddress, bankAddress;
  
  private int balance;
  private String inventory;
  
  private boolean finished = false;
  
  public AgentUpdater(InetAddress auctionAddress, InetAddress bankAddress, Agent agent)
  {
    this.agent = agent;
    this.auctionAddress = auctionAddress;
    this.bankAddress = bankAddress;
  }
  
  public void setFinished(boolean flag)
  {
    finished = flag;
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
    try
    {
      Socket auction = new Socket(auctionAddress, 1111);
      Socket bank = new Socket(bankAddress, 2222);
    
      ObjectOutputStream auctionOut = new ObjectOutputStream(auction.getOutputStream());
      auctionOut.flush();
    
      ObjectOutputStream bankOut = new ObjectOutputStream(bank.getOutputStream());
      bankOut.flush();
    
      ObjectInputStream auctionIn = new ObjectInputStream(auction.getInputStream());
      ObjectInputStream bankIn = new ObjectInputStream(bank.getInputStream());
    while(!finished)
    {
      try { sleep(1000); } catch(InterruptedException ignored) {}
      try {
        auctionOut.writeObject(new Message(this, "Updater+"+agent.getAgentName(), "repository", "", agent.getAgentCentralKey(), agent.getAgentBankKey()));
        auctionOut.flush();
        bankOut.writeObject(new Message(this, "Updater+"+agent.getAgentName(), "balance", "", agent.getAgentBankKey(), -1));
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
    catch(IOException e)
    {
      e.printStackTrace();
    }
  }
}
