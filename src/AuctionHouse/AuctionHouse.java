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
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class AuctionHouse implements Serializable
{
  private String name;

  private int index;
  private int publicID;
  private LinkedList<Item> inventory = new LinkedList<>();
  
  /**
   * Inner class for item listings with house.
   */
  public class Item implements Serializable
  {
    private final String itemName;
    private int previous, current;
    private int bidAmount, prevAmount;
    
    /**
     * Default constructor
     * @param index
     */
    Item(int index)
    {
      itemName = "Item-" + index;
      bidAmount = 100;
      current = -1;
    }
  
    /**
     * @return the item's name.
     */
    public String getItemName()
    {
      return itemName;
    }
  
    /**
     * @return the current highest bid on item.
     */
    int getBidAmount()
    {
      return bidAmount;
    }

    /**
     * @return the previous highest bid on item.
     */
    int getPrevAmount()
    {
      return prevAmount;
    }
  
    /**
     * @return the current highest bidder key on item.
     */
    public int getAgent()
    {
      return current;
    }

    /**
     * @return the previous highest bidder key on item.
     */
    int getPrevious()
    {
      return previous;
    }
  
    /**
     * Sets the current bid.
     * @param bid
     */
    synchronized void setCurrentBid(int bid)
    {
      prevAmount = bidAmount;
      bidAmount = bid;
    }
  
    /**
     * Sets the current bidder.
     * @param agent
     */
    synchronized void setAgentKey(int agent)
    {
      previous = current;
      current = agent;
    }


  }
  
  /**
   * Default constructor.
   *
   * Generates a random public ID.
   */
  private AuctionHouse()
  {
    this.name = "[House-...]: ";
    
    setItems();
  }
  
  /**
   * Sets index and name of house.
   * @param index
   */
  void setIndex(int index) {
    this.name = "[House-" + index + "]: ";
    this.index = index;
  }
  
  /**
   * @return index of this house.
   */
  int getIndex() {
    return index;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  /**
   * @return name of this auction house.
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * @return auction house public ID.
   */
  int getPublicID()
  {
    return publicID;
  }

  /**
   * Sets the public ID of house.
   * @param ID
   */
  void setPublicID(int ID)
  {
    publicID = ID;
  }

  /**
   * Sets the items available for bidding.
   */
  private void setItems()
  {
    for (int i = 0; i < 3; i++)
    {
      inventory.add(new Item(i+1));
      inventory.get(i).setCurrentBid(100);
    }
  }
  
  public String getListings()
  {
    StringBuilder items = new StringBuilder();
    for(Item item : inventory)
    {
      items.append(this.name).append(item.getItemName()).append(" Bid-").append(item.getBidAmount()).append(".");
    }
    return items.toString();
  }

  /**
   * Method used by AuctionHouseProtocol to check the items
   * @return items
   */
  LinkedList<Item> getInventory()
  {
    return inventory;
  }

  void removeItem(String itemName)
  {
    int index = 0;
    for (int i = 0; i < inventory.size(); i++) {
      if (inventory.get(i).getItemName().equalsIgnoreCase(itemName)) {
        index = i;
        break;
      }
    }
    inventory.remove(index);
  }

  boolean higherBid(int itemIndex, int bidValue)
  {
    Item item = inventory.get(itemIndex);
    return item.getBidAmount() < bidValue;
  }

  void setItemBid(int itemIndex, int bidValue, int agentKey)
  {
    inventory.get(itemIndex).setCurrentBid(bidValue);
    inventory.get(itemIndex).setAgentKey(agentKey);
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

    PauseTransition timer1 = new PauseTransition(Duration.seconds(30));
    PauseTransition timer2 = new PauseTransition(Duration.seconds(30));
    PauseTransition timer3 = new PauseTransition(Duration.seconds(30));
    
    System.out.println("Enter Auction Central's IP: ");
    String address = scan.nextLine();
    
    Socket socket = new Socket(InetAddress.getByName(address), 1111);
    System.out.println("AH connected");
    try
    {
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
      try
      {
        System.out.println("AH streams opened");
        Message input, output;
  
        System.out.println("AH sending init");
        out.writeObject(new Message(house, house.getName(), "register", "", -1, -1));
        out.flush();
  
        System.out.println("AH reading init");
        input = ((Message)in.readObject());
        
        AuctionHouseProtocol auctionHouseProtocol = new AuctionHouseProtocol(house);
        
        while (true)
        {
          if (input != null)
          {
            System.out.println(input.getSignature() + input.getMessage());
            output = auctionHouseProtocol.handleRequest(input);
            if(output.getMessage().equalsIgnoreCase("accepted"))
            {
              synchronized (house)
              {
                int itemIndex = 0;
                LinkedList<Item> itemList = house.inventory;
                for (int i = 0; i < itemList.size(); i++) {
                  if (itemList.get(i).getItemName().equalsIgnoreCase(output.getItem())) {
                    itemIndex = i;
                    break;
                  }
                }
                switch(output.getItem())
                {
                  case "Item-1":
                    timer1.playFromStart();
                    timer1.setOnFinished(event -> {
                      System.out.println("Timer 1 finished");
                      //house.removeItem("Item-1");
                    });
                    break;
                  case "Item-2":
                    timer2.playFromStart();
                    timer2.setOnFinished(event -> {
                      System.out.println("Timer 2 finished");
                      //house.removeItem(output.getItem());
                    });
                    break;
                  case "Item-3":
                    timer3.playFromStart();
                    timer3.setOnFinished(event -> {
                      System.out.println("Timer 3 finished");
                      //house.removeItem(output.getItem());
                    });
                    break;
                  default:
                    System.out.println("Wrong index");
                    break;
                }
                out.writeObject(new Message(output.getSender(), output.getSignature(), "unblock",
                        output.getItem(), itemList.get(itemIndex).getPrevious(), itemList.get(itemIndex).getPrevAmount()));
                out.flush();
              }
            }
            
            if(!output.getMessage().equals("ignore"))
            {
              System.out.println(house.name + ": Sending " + output.getMessage() + " to " + socket.toString());
              out.writeObject(output);
              out.flush();
              
              System.out.println("AH sent");

//              input = ((Message) in.readObject());
            }
          }
          System.out.println("AH reading");
          input = ((Message)in.readObject());
          System.out.println("AH done reading");
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
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
