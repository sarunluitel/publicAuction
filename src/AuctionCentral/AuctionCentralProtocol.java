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
  private static Map<Integer, Integer> agentKeys = Collections.synchronizedMap(new HashMap<Integer, Integer>());
  private static Map<Integer, AuctionHouse> auctionRepository = Collections.synchronizedMap(new HashMap<Integer, AuctionHouse>());
  
  private Socket socket;
  private Object object;
  
  private Agent agent;
  private static int agentCount;
  
  private AuctionHouse auctionHouse;
  private static int houseCount;
  
  private String current;
  
  /**
   * Default constructor.
   * <p>
   * Takes a socket and an object to identify who it is speaking with.
   *
   * @param socket
   * @param message
   * @throws IOException
   */
  AuctionCentralProtocol(Socket socket, Message message)
  {
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
  
  /**
   * Handles requests as they are received from socket.
   *
   * @param request
   * @return response to request.
   */
  public Message handleRequest(Message request)
  {
    Message response;
    StringBuilder message;
    switch (request.getMessage())
    {
      case "new":
        agentKeys.put(request.getKey(), request.getAmount());
        
        message = new StringBuilder("Welcome!");
        response = new Message(null, "[AuctionCentral]: ", message.toString(), "Initialized", request.getKey(), 0);
        break;
      case "register":
        int ID = (int)(Math.random() * 1000000);
        current = "[House-" + houseCount + "]: ";
        
        auctionHouse = ((AuctionHouse)request.getSender());
        auctionHouse.setName(current);
        
        message = new StringBuilder("registered");
        auctionRepository.put(ID, auctionHouse);
        
        response = new Message(null, "[AuctionCentral]: ", message.toString(), auctionHouse.getName(), ID, houseCount);
        houseCount++;
        break;
      case "inventory":
        auctionHouse = auctionRepository.get(request.getKey());
        auctionRepository.replace(request.getKey(), auctionHouse, ((AuctionHouse)request.getSender()));
        auctionHouse = auctionRepository.get(request.getKey());
        message = new StringBuilder("ignore");
        response = new Message(auctionHouse, "[AuctionCentral]: ", message.toString(), auctionHouse.getName(), request.getKey(), -1);
        break;
      case "de-register":
        auctionHouse = auctionRepository.remove(request.getKey());
        message = new StringBuilder("de-registered");
  
        response = new Message(null, "[AuctionCentral]: ", message.toString(), auctionHouse.getName(), request.getKey(), auctionRepository.size());
        break;
      case "repository":
        message = new StringBuilder();
        for(AuctionHouse auctionHouse : auctionRepository.values())
        {
          if(auctionHouse.getListings().length() < 10) auctionRepository.remove(auctionHouse);
          message.append(auctionHouse.getListings());
        }
  
        response = new Message(request.getSender(), "[AuctionCentral]: \n", message.toString(), "", request.getKey(), auctionRepository.size());
        break;
      case "bid":
        message = new StringBuilder("bid");
        response = new Message(request.getSender(), "[AuctionCentral]: ", message.toString(), request.getItem(), request.getKey(), request.getAmount());
        break;
      case "accepted":
        message = new StringBuilder("block");
  
        response = new Message(null, "[AuctionCentral]: ", message.toString(), request.getItem(), agentKeys.get(request.getKey()), request.getAmount());
        break;
      case "declined":
        message = new StringBuilder(request.getSignature() + "declined bid on " + request.getItem() + " for an amount of " + request.getAmount() + ".");
        response = new Message(null, "[AuctionCentral]: ", message.toString(), request.getItem(), request.getKey(), request.getAmount());
        break;
      case "unblock":
        response = new Message(null, "[AuctionCentral]: ", request.getMessage(), request.getItem(), agentKeys.get(request.getKey()), request.getAmount());
        break;
      case "winner":
        message = new StringBuilder("remove");
        response = new Message(null, "[AuctionCentral]: ", message.toString(), request.getItem(), agentKeys.get(request.getKey()), request.getAmount());
        break;
      case "EXIT":
        message = new StringBuilder("Goodbye!");
        response = new Message(null, "[AuctionCentral]: ", message.toString(), "Goodbye!", request.getKey(), 0);
        break;
      default:
        message = new StringBuilder("Error - request not recognized.");
        response = new Message(null, "[AuctionCentral]: ", message.toString(), "", -1, -1);
        break;
    }
    return response;
  }
}
