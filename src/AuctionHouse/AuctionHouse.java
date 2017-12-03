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

public class AuctionHouse
{
  public class Item
  {
    private String itemName;
    private int agentKey;
    private int bidAmount;


    public Item(int num)
    {
      itemName = "Item" + num;
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

  private final String name;
  private final int publicID;
  private static int totalHouses = 0;
  private LinkedList<String> itemList = new LinkedList<>();
  private LinkedList<Item> itemsForSale = new LinkedList<>();




  /**
   * Default constructor.
   * <p>
   * Generates a random public ID.
   */
  public AuctionHouse()
  {
    totalHouses++;
    publicID = totalHouses;
    name = "[House-" + publicID + "]";
    setItems();
  }

  /**
   * @return name of this auction house.
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * Method to set items available to sell
   * <p>
   * No parameters
   */
  private void setItems()
  {
    for (int i = 1; i < 4; i++)
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
    Socket socket = new Socket(InetAddress.getLocalHost(), 1111);
    try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
         ObjectInputStream in = new ObjectInputStream(socket.getInputStream()))
    {
      try
      {
        Message input, output;
        input = ((Message) in.readObject());

        while (true)
        {
          if (input != null)
          {
            System.out.println(input.getMessage());

            input = ((Message) in.readObject());

            output = null;
            out.writeObject(output);

            input = null;
          }
        }
      } catch (ClassNotFoundException e)
      {
        System.err.println(e.getMessage());
      }
      in.close();
      out.close();
      socket.close();
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
