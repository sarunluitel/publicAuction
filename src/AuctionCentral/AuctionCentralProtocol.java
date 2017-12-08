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
import java.net.Socket;
import java.util.*;

class AuctionCentralProtocol {
  private static Map<Integer, Integer> agentKeys = Collections.synchronizedMap(new HashMap<>());
  
  private static Map<Integer, AuctionHouse> auctionRepository = Collections.synchronizedMap(new HashMap<Integer, AuctionHouse>());
  private static List<LinkedList> inventories = Collections.synchronizedList(new LinkedList<>());
  
  private String inventory = "";
  private static ArrayList<AuctionHouse> auctionList = new ArrayList<>();
  
  private Socket socket;
  private Object object;
  
  private Agent agent;
  private static int agentCount;
  
  private AuctionHouse auctionHouse;
  private static int houseCount;
  
  private String current;
  private Message pending;
  private boolean hasPending;
  
  /**
   * Default constructor.
   * <p>
   * Takes a socket and an object to identify who it is speaking with.
   *
   * @param socket
   * @throws IOException
   */
  AuctionCentralProtocol(Socket socket) throws IOException {
    this.socket = socket;
    if(object instanceof Agent) {
      this.agent = ((Agent) object);
      agentCount++;
  
      System.out.println(agent.getAgentName() + ": Connected to AuctionCentral.");
      System.out.println("[AuctionCentral]: " + agentCount + " agent(s) are connected!");
    }
  }
  
  /**
   * Default constructor.
   * <p>
   * Takes a socket and an object to identify who it is speaking with.
   *
   * @param socket
   * @param message
   * @throws IOException
   */
  AuctionCentralProtocol(Socket socket, Message message) throws IOException {
    this.socket = socket;
    this.object = message.getSender();
    if(object instanceof Agent) {
      this.agent = ((Agent) object);
      agentCount++;
  
      System.out.println(agent.getAgentName() + ": Connected to AuctionCentral.");
      System.out.println("[AuctionCentral]: " + agentCount + " agent(s) are connected!");
    }
    
    if(message.getSender() instanceof Agent) current = ((Agent) message.getSender()).getAgentName();
    else if(message.getSender() instanceof AuctionHouse) current = ((AuctionHouse) message.getSender()).getName();
    else current = "[Unknown-" + ((int) (Math.random() * 1000)) + "]";
  }
  
  public String getCurrent()
  {
    return current;
  }
  
  public void setMessage(Message message)
  {
    this.object = message.getSender();
  }
  
//  public boolean hasPending() {
//    return hasPending;
//  }
//
//  public Message getPending()
//  {
//    Message temp = pending;
//    pending = null;
//    hasPending = false;
//    return temp;
//  }
  
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
        agentKeys.put(request.getKey(), request.getAmount());
        
        message = "Welcome!";
        response = new Message(null, "[AuctionCentral]: ", message, "Initialized", request.getKey(), 0);
        System.out.println("[AuctionCentral] " + socket.toString()+" : " + message);
        break;
      case "register":
        int ID = (int)(Math.random() * 1000000);
        current = "[House-" + houseCount + "]: ";
        
        auctionHouse = ((AuctionHouse)request.getSender());
        auctionHouse.setName(current);
        
        message = "registered";
        auctionRepository.put(ID, auctionHouse);
        auctionList.add(auctionHouse);
        
        for(AuctionHouse a : auctionList)
        {
          inventories.add(houseCount, a.getList());
        }
        
        response = new Message(null, "[AuctionCentral]: ", message, auctionHouse.getName(), ID, houseCount);
        System.out.println("[AuctionCentral]: " + message);
        houseCount++;
        break;
      case "inventory":
        auctionHouse = auctionRepository.get(request.getKey());
        System.out.println(auctionHouse);
        System.out.println(auctionHouse.getInventory());

        auctionRepository.replace(request.getKey(), auctionHouse, ((AuctionHouse)request.getSender()));

        auctionHouse = auctionRepository.get(request.getKey());
        System.out.println(auctionHouse);
        System.out.println(auctionHouse.getInventory());

//        System.out.println(houseCount);
        inventory = auctionHouse.getInventory();
//        inventories.add(houseCount-1, inventory);
        
        message = "ignore";
        response = new Message(auctionHouse, "[AuctionCentral]: ", message, auctionHouse.getName(), request.getKey(), -1);
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "de-register":
        auctionHouse = auctionRepository.remove(request.getKey());
        auctionList.remove(auctionHouse);
        message = "de-registered";
  
        response = new Message(null, "[AuctionCentral]: ", message, auctionHouse.getName(), request.getKey(), auctionRepository.size());
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "repository":
        message = "";
//        System.out.println(auctionList.size());
        for(AuctionHouse auctionHouse : auctionRepository.values())
        {
          System.out.println(auctionHouse.getInventory());
          if(auctionHouse.getInventory().length() < 10) auctionRepository.remove(auctionHouse);
          message += auctionHouse.getInventory();
        }
        System.out.println(message);
  
        response = new Message(null, "[AuctionCentral]: \n", message, "", request.getKey(), auctionRepository.size());
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "bid":
        message = "bid";
        response = new Message(request.getSender(), "[AuctionCentral]: ", message, request.getItem(), request.getKey(), request.getAmount());
        break;
      case "accepted":
        message = "block";
        System.out.println(auctionList.size());
  //"[Agent-" + request.getKey() + "] is the highest bidder on " + request.getSignature() + request.getItem() + " for an amount of " + request.getAmount()
        System.out.println(message);
  
        response = new Message(null, "[AuctionCentral]: \n", message, request.getItem(), agentKeys.get(request.getKey()), request.getAmount());
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "declined":
        message =  request.getSignature() + "declined your bid on " + request.getItem() + " for an amount of " + request.getAmount() + ".";
        System.out.println(auctionList.size());
  
        System.out.println(message);
  
        response = new Message(null, "[AuctionCentral]: \n", message, request.getItem(), request.getKey(), request.getAmount());
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "unblock":
        response = new Message(null, "[AuctionCentral]: \n", request.getMessage(), request.getItem(), agentKeys.get(request.getKey()), request.getAmount());
        break;
      case "winner":
        message = "remove";

        response = new Message(null, "[AuctionCentral]: ", message, request.getItem(), agentKeys.get(request.getKey()), request.getAmount());
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
