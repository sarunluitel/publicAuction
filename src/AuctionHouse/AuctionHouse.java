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
import java.util.Timer;
import java.util.TimerTask;

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
    int getAgent()
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
   * Sets name of this house.
   * @param name
   */
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
  
  /**
   * @return String version of the current listings.
   */
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
   * Method used by AuctionHouseProtocol to check the items.
   *
   * @return items
   */
  LinkedList<Item> getInventory()
  {
    return inventory;
  }
  
  /**
   * Removes item with given name.
   * @param itemName
   */
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
    System.out.println("Removed item " + itemName);
  }
  
  /**
   * @param itemIndex
   * @param bidValue
   * @return whether a higher bid is in place.
   */
  boolean higherBid(int itemIndex, int bidValue, int agentKey)
  {
    Item item = inventory.get(itemIndex);
    if(item.getAgent() == agentKey)
    {
      return false;
    }
    return item.getBidAmount() < bidValue;
  }
  
  /**
   * Sets this bid to the highest bid.
   * @param itemIndex
   * @param bidValue
   * @param agentKey
   */
  void setItemBid(int itemIndex, int bidValue, int agentKey)
  {
    inventory.get(itemIndex).setCurrentBid(bidValue);
    inventory.get(itemIndex).setAgentKey(agentKey);
  }
  
  /**
   * Closes bids on timer finish.
   * @param house
   * @param out
   * @param itemIndex
   */
  private static synchronized void closeBid(AuctionHouse house, ObjectOutputStream out, int itemIndex)
  {
    try
    {
      Item item = house.getInventory().get(itemIndex-1);
      house.removeItem("Item-" + itemIndex);
      out.writeObject(new Message(house, house.getName(), "winner", item.itemName, item.getAgent(), item.getBidAmount()));
      out.flush();
    }
    catch(IOException e)
    {
      e.printStackTrace();
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

    Timer timer1 = new Timer();
    boolean timer1running = false;
    Timer timer2 = new Timer();
    boolean timer2running = false;
    Timer timer3 = new Timer();
    boolean timer3running = false;

    System.out.println("Enter Auction Central's IP: ");
    String address = scan.nextLine();
    
    Socket socket = new Socket(InetAddress.getByName(address), 1111);
    try
    {
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
      try
      {
        Message input, output;
  
        out.writeObject(new Message(house, house.getName(), "register", "", -1, -1));
        out.flush();
  
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
                for (int i = 0; i < itemList.size(); i++)
                {
                  if (itemList.get(i).getItemName().equalsIgnoreCase(output.getItem()))
                  {

                    itemIndex = i;
                    break;
                  }
                }
                switch(output.getItem())
                {
                  //just to push
                  case "Item-1":
                    System.out.println("Running item1");
                    if(timer1running)
                    {
                      timer1.cancel();
                      timer1 = new Timer();
                    }
                    TimerTask item1task = new TimerTask()
                    {
                      public int count =0;
                      @Override
                      public void run()
                      {
                        System.out.println("inside timer item1");
                        count++;
                        System.out.println(count);
                        if(count == 30)
                        {
                          System.out.println("Item 1 sold");
                          closeBid(house, out, 1);
                          //timer1.cancel();
                          //timer1.purge();
                          return;
                        }
                      }

                    };
                    timer1running = true;
                    timer1.schedule(item1task,0,1000);
                    //timer1.playFromStart();
                 //   timer1.setOnFinished(event -> closeBid(house, out, 1));
                    break;
                  case "Item-2":
                    System.out.println("Running item1");
                    if(timer2running)
                    {
                      timer2.cancel();
                      timer2 = new Timer();
                    }
                    TimerTask item2task = new TimerTask()
                    {
                      public int count =0;
                      @Override
                      public void run()
                      {
                        System.out.println("inside timer item2");
                        count++;
                        System.out.println(count);
                        if(count == 30)
                        {
                          System.out.println("Item 2 sold");
                          closeBid(house, out, 2);
                          //timer1.cancel();
                          //timer1.purge();
                          return;
                        }
                      }

                    };
                    timer2running = true;
                    timer2.schedule(item2task,0,1000);
                 //   timer2.playFromStart();
                 //   timer2.setOnFinished(event -> closeBid(house, out, 2));
                    break;
                  case "Item-3":
                    System.out.println("Running item3");
                    if(timer3running)
                    {
                      timer3.cancel();
                      timer3 = new Timer();
                    }
                    TimerTask item3task = new TimerTask()
                    {
                      public int count =0;
                      @Override
                      public void run()
                      {
                        System.out.println("inside timer item1");
                        count++;
                        System.out.println(count);
                        if(count == 30)
                        {
                          System.out.println("Item 3 sold");
                          closeBid(house, out, 3);
                          //timer1.cancel();
                          //timer1.purge();
                          return;
                        }
                      }

                    };
                    timer3running = true;
                    timer3.schedule(item3task,0,1000);
                //    timer3.playFromStart();
                //    timer3.setOnFinished(event -> closeBid(house, out, 3));
                    break;
                  default:
                    break;
                }
                if(itemList.get(itemIndex).getPrevious() != -1)
                {
                  out.writeObject(new Message(output.getSender(), output.getSignature(), "unblock", output.getItem(), itemList.get(itemIndex).getPrevious(), itemList.get(itemIndex).getPrevAmount()));
                  out.flush();
                }
              }
            }
            if(!output.getMessage().equals("ignore"))
            {
              out.writeObject(output);
              out.flush();
            }
          }
          input = ((Message)in.readObject());
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
