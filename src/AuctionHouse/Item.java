/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/28/17
 *
 * Item.java - Enum of items to sell at houses.
 */

package AuctionHouse;

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

  public void setCurrentBid(int bid)
  {
    bidAmount = bid;
  }
}
