package AuctionHouse;

import Message.Message;

import java.net.Socket;

public class AuctionHouseProtocol
{
  private AuctionHouse house;
  private Message message;
  private Socket socket;
  
  public AuctionHouseProtocol(AuctionHouse house, Socket socket, Message message)
  {
    this.house = house;
    this.message = message;
    this.socket = socket;
  }
  
  public Message handleRequest(Message request)
  {
    Message response;
    String message;
    System.out.println("AH handling -> " + request.getMessage());
    switch(request.getMessage())
    {
      case "registered":
        house.setIndex(request.getAmount());
        house.setPublicID(request.getKey());
        
        message = "inventory";
        
        response = new Message(house.getInventory(), house.getName() + ": ", message, "", house.getPublicID(), -1);
        System.out.println(house.getName() + ": " + message);
        break;
      case "de-registered":
        message = "";
        response = new Message(this, house.getName() + ": ", message, "", -1, -1);
        System.out.println(house.getName() + ": " + message);
        break;
      case "bid":
        message = "accepted.";
        response = new Message(this, house.getName() + ": ", message, /*house.getItem()*/"", request.getKey(), 100);
        System.out.println(house.getName() + ": " + message);
        break;
      default:
        message = "Error - request not recognized.";
        response = new Message(null, house.getName() + ": ", message, "", -1, -1);
        System.out.println(house.getName() + ": " + message);
        break;
    }
    return response;
  }
}
