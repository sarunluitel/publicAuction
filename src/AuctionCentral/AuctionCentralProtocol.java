/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralProtocol.java - Protocol for auction central to follow.
 */

package AuctionCentral;

import Agent.Agent;
import AuctionHouse.AuctionHouse;
import Message.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

class AuctionCentralProtocol implements Serializable
{
  private static Map<Integer, AuctionHouse> auctionRepository = Collections.synchronizedMap(new HashMap<Integer, AuctionHouse>());
  private static List<LinkedList> inventories = Collections.synchronizedList(new LinkedList<>());
  
  private ObjectInputStream bankI;
  private ObjectOutputStream bankO;
  
  private Socket socket;
  private Object object;
  
  private Agent agent;
  private static int agentCount;


  /**
   * Default constructor.
   *
   * Takes a socket and an object to identify who it is speaking with.
   *
   * @param socket
   * @throws IOException
   */
  AuctionCentralProtocol(Socket socket) throws IOException
  {
    this.socket = socket;
    if(object instanceof Agent)
    {
      this.agent = ((Agent)object);
      agentCount++;

      System.out.println(agent.getAgentName() + ": Connected to AuctionCentral.");
      System.out.println("[AuctionCentral]: " + agentCount + " agent(s) are connected!");
    }
  }
  /**
   * Default constructor.
   *
   * Takes a socket and an object to identify who it is speaking with.
   *
   * @param socket
   * @param message
   * @throws IOException
   */
  AuctionCentralProtocol(Socket socket, Message message) throws IOException
  {
    this.socket = socket;
    this.object = message.getSender();
    if(object instanceof Agent)
    {
      this.agent = ((Agent)object);
      agentCount++;

      System.out.println(agent.getAgentName() + ": Connected to AuctionCentral.");
      System.out.println("[AuctionCentral]: " + agentCount + " agent(s) are connected!");
    }
  }

  public void setMessage(Message message)
  {
    this.object = message.getSender();
  }

  /**
   * Handles requests as they are received from socket.
   *
   * @param request
   * @return response to request.
   */
  public Message handleRequest(Message request)
  {
    Message response;
    String message;
    System.out.println("AC handling -> " + request.getMessage());
    switch (request.getMessage())
    {
      case "new":
        message = "";
        response = new Message(null, "[AuctionCentral]: ", message, "Initialized", request.getKey(), 0);
        System.out.println("[AuctionCentral] " + socket.toString()+" : " + message);
        break;
      case "register":
        int ID = (int)(Math.random() * 1000000);
        AuctionHouse auctionHouse = ((AuctionHouse)request.getSender());
        
        message = "registered";
        auctionRepository.put(ID, auctionHouse);
        
        response = new Message(null, "[AuctionCentral]: ", message, auctionHouse.getName(), ID, auctionRepository.size());
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "inventory":
        auctionHouse = auctionRepository.get(request.getKey());
        LinkedList inventory = ((LinkedList)request.getSender());
        inventories.add(auctionRepository.size()-1, inventory);
        
        message = "";
        response = new Message(null, "[AuctionCentral]: ", message, auctionHouse.getName(), request.getKey(), -1);
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "de-register":
        auctionHouse = auctionRepository.remove(request.getKey());
        message = "de-registered";
  
        response = new Message(null, "[AuctionCentral]: ", message, auctionHouse.getName(), request.getKey(), auctionRepository.size());
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "repository":
        message = "inventory";
        response = new Message(inventories, "[AuctionCentral]: ", message, "House list", request.getKey(), auctionRepository.size());
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "transaction":
        message = "Mitigating transaction...";
        response = new Message(null, "[AuctionCentral]: ", message, "Mitigated transaction", request.getKey(), 0);
        //handleTransaction()
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "EXIT":
        message = "Goodbye!";
        response = new Message(null, "[AuctionCentral]: ", message, "Goodbye!", request.getKey(), 0);
        System.out.println("[AuctionCentral]: " + message);
        break;
      default:
        message = "Error - request not recognized.";
        response = new Message(null, "[AuctionCentral]: ", message, "", -1, -1);
        System.out.println("[AuctionCentral]: " + message);
        break;
    }
    return response;
  }
  
//  /* tell bank to find agent account with ID & perform action if possible
//     then respond according to bank confirmation to de-register auction houses,
//     get public ID and de-register there.                                       */
// --Commented out by Inspection START (12/4/2017 9:01 PM):
//  /**
//   * Mitigates transaction requests between agents and houses.
//   *
//   * @param agentBid
//   * @param agentID
//   * @param houseID
//   * @return response to transaction request.
//   * @throws IOException
//   */
//  private String handleTransaction(String agentBid, String agentID, String houseID) throws IOException
//  {
//    //don't allow bid if it has not yet been accepted by bank
//    bankO.writeUTF("[AuctionCentral]: block:" + agentBid + ":" + agentID);
//    bankO.writeUTF("[AuctionCentral]: unblock:" + agentBid + ":" + agentID);
//    bankO.writeUTF("[AuctionCentral]: move:" + agentBid + ":" + agentID + ":" + houseID);
//    //if item is sold check if house is empty de-register house if so.
//    bankO.flush();
//
//    return bankI.readUTF();
//  }
// --Commented out by Inspection STOP (12/4/2017 9:01 PM)
}
