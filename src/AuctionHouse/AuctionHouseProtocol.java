package AuctionHouse;

import Message.Message;
import AuctionHouse.AuctionHouse.Item;

import java.net.Socket;
import java.util.LinkedList;

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
        message = "ignore";
        response = new Message(this, house.getName() + ": ", message, "", -1, -1);
        System.out.println(house.getName() + ": " + message);
        break;
      case "bid":
        LinkedList<Item> itemList = house.getItemsForSale();
        int itemIndex = 0;
        boolean itemHere = false;
        for(int i = 0; i < itemList.size(); i++)
        {
          if(itemList.get(i).getItemName().equalsIgnoreCase(request.getItem()))
          {
            itemIndex = i;
            itemHere = true;
            break;
          }
        }
        if(house.higherBid(itemIndex, request.getAmount()) && itemHere)
        {
          message = "accepted";
          System.out.println("Bid accepted: " + request.getAmount());
          house.setItemBid(itemIndex, request.getAmount(), request.getKey());
        }
        else
        {
          message = "declined";
        }
        response = new Message(this, house.getName() + ": ", message, request.getItem(), request.getKey(), request.getAmount());
        System.out.println(house.getName() + ": " + message);
        break;
      case "confirmation":
        message = "ignore";
        response = new Message(null, house.getName() + ": ", message, "", -1, -1);
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
