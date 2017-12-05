/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionHouse.java - Registers with auction central, contains fixed list of items
 * and auctions at most three items at a time, receives and acknowledges bids, requests
 * auction central to release/place holds on bank accounts associated with bids, requests
 * transfer when bid is successful, bids are successful after 30s of inactivity.
 */

package AuctionHouse;

import Message.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class AuctionHouse implements Serializable
{
  private String name;
  private int index;
  private int publicId;
  private LinkedList<String> itemList = new LinkedList<>();
  private LinkedList<Item> itemsForSale = new LinkedList<>();
  
  class Item implements Serializable
  {
    private final String itemName;
    private int agentKey;
    private int bidAmount;


    Item(int num)
    {
      itemName = "Items" + num;
      bidAmount = 100;
    }

    public String getItemName()
    {
      return itemName;
    }

    public int getBidAmount()
    {
      return bidAmount;
    }

    public int getAgent()
    {
      return agentKey;
    }

    private void setCurrentBid(int bid)
    {
      bidAmount = bid;
    }

    private void setAgentKey(int agent)
    {
      agentKey = agent;
    }
  }
  
  /**
   * Default constructor.
   * <p>
   * Generates a random public ID.
   */
  private AuctionHouse()
  {
    name = "[House-...] ";
    setItems();
  }
  
  private void setIndex(int index) {
    name = "[House-" + index + "] ";
    this.index = index;
  }
  
  private int getIndex() {
    return index;
  }
  
  /**
   * @return name of this auction house.
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * @return auction house public id
   */
  private int getPublicId()
  {
    return publicId;
  }

  /**
   * Method sets publicId
   * Used by AuctionCentral
   * @param id
   */
  private void setPublicId(int id)
  {
    publicId = id;
  }

  /**
   * Method to set items available to sell
   * <p>
   * No parameters
   */
  private void setItems()
  {
    for (int i = 0; i < 3; i++)
    {
      itemsForSale.add(new Item(i));
      itemsForSale.get(i).setCurrentBid(100);
    }

  }

  /**
   * Main method for auction house.
   *
   * @param args
   * @throws IOException
   */
  public static void main(String args[]) throws IOException
  {
    AuctionHouse house = new AuctionHouse();
    Scanner scan = new Scanner(System.in);
    
    System.out.println("Enter Auction Central's IP: ");
    String address = scan.nextLine();
    
    Socket socket = new Socket(InetAddress.getByName(address), 1111);
    
    try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
         ObjectInputStream in = new ObjectInputStream(socket.getInputStream()))
    {
      try
      {
        Message input, output;
        
        out.writeObject(new Message(house, house.getName(), "register", "", -1, -1));
        out.flush();
        
        input = ((Message)in.readObject());
        house.setIndex(input.getAmount());
        house.setPublicId(input.getKey());
        
        while (true)
        {
          if(in.available() != 0) input = ((Message) in.readObject());
          if (input != null)
          {
            System.out.println(input.getSignature() + input.getMessage());
            
            output = new Message(house, house.getName(), "de-register", "", house.getPublicId(), house.getIndex());
            out.writeObject(output);
            out.flush();
            
            input = null;
          }
        }
      }
      catch (ClassNotFoundException e)
      {
        System.err.println(e.getMessage());
      }
      
      in.close();
      out.close();
      socket.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
