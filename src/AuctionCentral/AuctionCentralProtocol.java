/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralProtocol.java - The protocol to follow.
 */

package AuctionCentral;

import Agent.Agent;
import AuctionHouse.AuctionHouse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class AuctionCentralProtocol {
  private Socket bankSocket;
  private DataInputStream bankI;
  private DataOutputStream bankO;
  
  private Socket socket = null;
  private Object object = null;
  
  private static Map<String, AuctionHouse> auctionRepository = Collections.synchronizedMap(new HashMap<String, AuctionHouse>());
  private static int clientCount = 0;
  private static final int WAITING = 0;
  
  private int state = WAITING;
  private String[] requests = {"START", "register", "de-register", "repository", "transaction"};
  private Agent agent;
  
  AuctionCentralProtocol(Socket socket, Object object) throws IOException
  {
    this.socket = socket;
    if(object == null) System.out.println("Cannot identify socket endpoint.");
    this.object = object;
    if(object instanceof Agent) agent = ((Agent)object);
    System.out.println(object);
    if(agent != null) System.out.println(agent.getName());
    clientCount++;
    
    for(int i = 0; i < 5; i++) registerAuctionHouse();
    
    System.out.println("[AuctionCentral]: Protocol-Constructor");
    System.out.println(clientCount + " clients connected!");
  
    bankSocket = new Socket(InetAddress.getLocalHost(),2222);
    bankI = new DataInputStream(bankSocket.getInputStream());
    bankO = new DataOutputStream(bankSocket.getOutputStream());
  }
  
  String handleRequest(String request) {
    String result = "[AuctionCentral-" + this + "]: echo request = NOT RECOGNIZED";
    for(int i = 0; i < requests.length; i++)
    {
      if(request.equals(requests[i])) result = "[AuctionCentral-" + this + "]: echo request = " + request;
    }
    result += "[From socket: " + this.socket + "]";
    System.out.println(result);
    if(request.equals(requests[3])) System.out.println(auctionRepository);
    String response = "[BANK]:";
    try
    {
      if(request.equals(requests[4])) response+=handleTransaction("$100.00", "Dummy Agent", "Dummy House");
    }
    catch(IOException e) {e.printStackTrace();}
    System.out.println(response);
    return result;
  }
  
  //tell bank to find agent account with ID & perform action if possible then respond according to bank confirmation
  //to de-register auction houses, get public ID and de-register there.
  private String handleTransaction(String agentBid, String agentID, String houseID) throws IOException
  {
    bankO.writeUTF("block:"+agentBid+":"+agentID);
    bankO.writeUTF("unblock:"+agentBid+":"+agentID);
    bankO.writeUTF("move:"+agentBid+":"+agentID+":"+houseID);
    return bankI.readUTF();
  }
  
  private void registerAuctionHouse()
  {
    int publicID = (int)(Math.random()*100000);
    AuctionHouse auctionHouse = new AuctionHouse(publicID);
    auctionRepository.put(auctionHouse.getName(), auctionHouse);
  }
  
  private void deregisterAuctionHouse(int publicID)
  {
    //not sure if anything extra should be done on auction house - could just be left as remove
     AuctionHouse auctionHouse = auctionRepository.remove("[HOUSE:" + publicID + "]");
    
    try
    {
      socket.close();
    }
    catch(IOException e)
    {
      System.err.println("Socket already closed.");
    }
  }
}
