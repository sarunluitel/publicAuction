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

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

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
      //Not too sure how we should handle the agent connecting to both the bank and the auction central socket
      //and eventually the auction houses but this seems like a start
      Agent agent = new Agent();
      Scanner scan = new Scanner(System.in);
      
      Socket bankSocket = new Socket(bankAddress, 2222);
      DataInputStream bankI = new DataInputStream(bankSocket.getInputStream());
      DataOutputStream bankO = new DataOutputStream(bankSocket.getOutputStream());

      Socket auctionCentralSocket = new Socket(auctionAddress, 1111);
      ObjectOutputStream auctionCentralObj = new ObjectOutputStream(auctionCentralSocket.getOutputStream());
      DataInputStream auctionCentralI = new DataInputStream(auctionCentralSocket.getInputStream());
      DataOutputStream auctionCentralO = new DataOutputStream(auctionCentralSocket.getOutputStream());

      System.out.println(agent.name + ": Log in successful!");
      bankO.writeUTF("new:" + agent.getAgentName());
      auctionCentralObj.writeObject(agent);

      while (!(message.equalsIgnoreCase("EXIT")))
      {
        if(!message.equals(""))
        {
          System.out.println(message);
          message = agent.name + ":" + message;

          bankO.writeUTF(message);
          auctionCentralO.writeUTF(message);

          //System.out.println(bankI.readUTF());
          //System.out.println(auctionCentralI.readUTF());
          //NEEDS TO STOP WHEN THERE IS NO MESSAGE and Wake up during message.
        }
        message = "";
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
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }


}

// Close a port manually for Mac
// sudo lsof -i :<port>
// kill -9 <PID>