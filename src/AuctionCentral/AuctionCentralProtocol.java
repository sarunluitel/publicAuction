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
  private static Map<Integer, AuctionHouse> auctionRepository = Collections.synchronizedMap(new HashMap<Integer, AuctionHouse>());

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
   * <p>
   * Takes a socket and an object to identify who it is speaking with.
   *
   * @param socket
   * @param message
   * @throws IOException
   */
  AuctionCentralProtocol(Socket socket, Message message) throws IOException
  {
    this.socket = socket;
    this.message = message;
    if (message.getSender() instanceof Agent)
    {
      agent = ((Agent) message.getSender());
      agentCount++;

      System.out.println(agent.getAgentName() + ": Connected to AuctionCentral.");
      System.out.println("[AuctionCentral]: " + agentCount + " agent(s) are connected!");
    } else this.object = message.getSender();

    if (bankSocket == null && !message.getMessage().equals("register"))
    {
      System.out.println("[AuctionCentral]: Connected to bank.");
      /* update this to take an address for the bank server - diff. from LocalHost. */
      bankSocket = new Socket(InetAddress.getByName(message.getItem().substring(1)), 2222);
      bankO = new ObjectOutputStream(bankSocket.getOutputStream());
      bankI = new ObjectInputStream(bankSocket.getInputStream());

      bankO.writeObject(new Message(null, "[AuctionCentral]: ", "auction central", "", 0, 0));
      bankO.flush();
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
    switch (request.getMessage())
    {
      case "new":
        message = "Initializing...";
        response = new Message(null, "[AuctionCentral]: ", message, "Initialized", request.getKey(), 0);
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "register":
        int ID = (int)(Math.random() * 1000000);
        AuctionHouse auctionHouse = ((AuctionHouse)request.getSender());
        
        message = "Registering " + auctionHouse.getName() + "...";
        auctionRepository.put(ID, auctionHouse);
        
        response = new Message(null, "[AuctionCentral]: ", message, "Registered", ID, 0);
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "de-register":
        auctionRepository.remove(request.getKey());
        message = "De-registering " + auctionRepository +  "...";
        response = new Message(null, "[AuctionCentral]: ", message, "De-registered", request.getKey(), 0);
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "repository":
        message = auctionRepository.toString();
        response = new Message(null, "[AuctionCentral]: ", message, "House list", request.getKey(), auctionRepository.size());
        System.out.println("[AuctionCentral]: " + message);
        break;
      case "transaction":
        message = "Mitigating transaction...";
        response = new Message(null, "[AuctionCentral]: ", message, "Mitigated transaction", request.getKey(), 0);
        //handleTransaction(message.get)
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
    bankO.writeUTF("[AuctionCentral]: block:" + agentBid + ":" + agentID);
    bankO.writeUTF("[AuctionCentral]: unblock:" + agentBid + ":" + agentID);
    bankO.writeUTF("[AuctionCentral]: move:" + agentBid + ":" + agentID + ":" + houseID);
    //if item is sold check if house is empty de-register house if so.
    bankO.flush();

    return bankI.readUTF();
  }
}
