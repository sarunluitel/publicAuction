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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class AuctionCentralProtocol implements Serializable
{
  private static Map<String, AuctionHouse> auctionRepository = Collections.synchronizedMap(new HashMap<String, AuctionHouse>());
  
  private Socket bankSocket = null;
  private ObjectInputStream bankI;
  private ObjectOutputStream bankO;
  
  private Socket socket;
  private Object object;
  private Message message;
  public Message setup;
  
  private Agent agent;
  private static int agentCount;
  
  /**
   * Default constructor.
   *
   * Takes a socket and an object to identify who it is speaking with.
   * @param socket
   * @param message
   * @throws IOException
   */
  AuctionCentralProtocol(Socket socket, Message message) throws IOException
  {
    this.socket = socket;
    this.message = message;
    if(message.getSender() instanceof Agent)
    {
      agent = ((Agent)message.getSender());
      agentCount++;
  
      System.out.println(agent.getAgentName() + ": Connected to AuctionCentral.");
      System.out.println("[AuctionCentral]: " + agentCount + " agent(s) are connected!");
    }
    else this.object = message.getSender();
    
    /* for now, registering auction houses within auction central. */
    for(int i = 0; i < 5; i++) registerAuctionHouse();
    
    if(bankSocket == null)
    {
      System.out.println("[AuctionCentral]: Connected to bank.");
      /* update this to take an address for the bank server - diff. from LocalHost. */
      bankSocket = new Socket(InetAddress.getByName(message.getItem().substring(1)),2222);
      bankO = new ObjectOutputStream(bankSocket.getOutputStream());
      bankI = new ObjectInputStream(bankSocket.getInputStream());
      
      bankO.writeObject(new Message(null, "[AuctionCentral]: ", "Hello, friend.", "", 0, 0));
    }
    setup = handleRequest(message);
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
    switch(request.getMessage())
    {
      case "new":
        message = "Initializing...";
        response = new Message(null, "[AuctionCentral]: ",  message, "Initialized", request.getKey(), 0);
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "register":
        message = "Registering...";
        registerAuctionHouse();//with param request.getSender() casted to auction house
        response = new Message(this, "[AuctionCentral]: ",  message, "Action performed", request.getKey(), 0);
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "de-register":
        message = "De-registering...";
        deregisterAuctionHouse(request.getKey());
        response = new Message(this, "[AuctionCentral]: ",  message, "Action performed", request.getKey(), 0);
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "repository":
        message = auctionRepository.toString();
        response = new Message(this, "[AuctionCentral]: ",  message, "House list", request.getKey(), auctionRepository.size());
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "transaction":
        message = "Mitigating transaction...";
        response = new Message(this, "[AuctionCentral]: ", message, "Mitigated transaction", request.getKey(), 0);
        //handleTransaction(message.get)
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "EXIT":
        message = "Goodbye!";
        response = new Message(this,  "[AuctionCentral]: ", message, "Goodbye!", request.getKey(), 0);
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
  
  /* tell bank to find agent account with ID & perform action if possible
     then respond according to bank confirmation to de-register auction houses,
     get public ID and de-register there.                                       */
  /**
   * Mitigates transaction requests between agents and houses.
   *
   * @param agentBid
   * @param agentID
   * @param houseID
   * @return response to transaction request.
   * @throws IOException
   */
  private String handleTransaction(String agentBid, String agentID, String houseID) throws IOException
  {
    //don't allow bid if it has not yet been accepted by bank
    bankO.writeUTF("[AuctionCentral]: block:"+agentBid+":"+agentID);
    bankO.writeUTF("[AuctionCentral]: unblock:"+agentBid+":"+agentID);
    bankO.writeUTF("[AuctionCentral]: move:"+agentBid+":"+agentID+":"+houseID);
    //if item is sold check if house is empty de-register house if so.
    bankO.flush();

    return bankI.readUTF();
  }
  
  /**
   * Registers auction houses and adds them to repository.
   */
  private void registerAuctionHouse()
  {
//    AuctionHouse auctionHouse = new AuctionHouse();
//    auctionRepository.put(auctionHouse.getName(), auctionHouse);
  }
  
  /**
   * De-registers auction houses from repository & closes their socket.
   *
   * @param publicID
   */
  private void deregisterAuctionHouse(int publicID)
  {
    //not sure if anything extra should be done on auction house - could just be left as remove
    //AuctionHouse auctionHouse = auctionRepository.remove("[House-" + publicID + "]");
    //auctionHouse.exit();
  }
}
