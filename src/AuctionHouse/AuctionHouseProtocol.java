/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 12/01/17
 *
 * AuctionHouseProtocol.java - Protocol for auction house to follow.
 */

package AuctionHouse;

import Message.Message;
import AuctionHouse.AuctionHouse.Item;

import java.util.LinkedList;

class AuctionHouseProtocol
{
  private final AuctionHouse house;
  
  /**
   * Default constructor.
   *
   * @param house
   */
  public AuctionHouseProtocol(AuctionHouse house)
  {
    this.house = house;
  }
  
  /**
   * @param request
   * @return response to request.
   */
  public Message handleRequest(Message request)
  {
    Message response;
    String message;
    switch(request.getMessage())
    {
      case "registered":
        house.setIndex(request.getAmount());
        house.setPublicID(request.getKey());
        
        message = "inventory";
        
        response = new Message(house, house.getName(), message, "", house.getPublicID(), -1);
        break;
      case "de-registered":
        message = "ignore";
        response = new Message(house, house.getName(), message, "", -1, -1);
        break;
      case "bid":
        LinkedList<Item> itemList = house.getInventory();
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
        if(house.higherBid(itemIndex, request.getAmount(), request.getKey()) && itemHere)
        {
          message = "accepted";
          house.setItemBid(itemIndex, request.getAmount(), request.getKey());
        }
        else
        {
          message = "declined";
        }
        response = new Message(house, house.getName(), message, request.getItem(), request.getKey(), request.getAmount());
        break;
      default:
        message = "ignore";
        response = new Message(house, house.getName(), message, "", -1, -1);
        break;
    }
    if(request.getMessage().contains("Item"))
    {
      message = "inventory";
      response = new Message(house, house.getName(), message, house.getListings(), house.getPublicID(), house.getInventory().size());
    }
    return response;
  }
}
