package Message;

import java.io.Serializable;

public class Message implements Serializable
{
  private final Object sender;

  private final String signature;
  private final String message;
  private final String item;

  private final int amount;
  private final int key;

  private long timestamp;
  
  /**
   * Default constructor.
   *
   * @param sender
   * @param signature
   * @param message
   * @param item
   * @param key
   * @param amount
   */
  public Message(Object sender, String signature, String message, String item, int key, int amount)
  {
    this.sender = sender;
    this.signature = signature;
    this.message = message;
    this.item = item;
    this.amount = amount;
    this.key = key;
    
    timestamp = System.currentTimeMillis();
  }
  
  /**
   * @return the name of sender.
   */
  public String getSignature()
  {
    return signature;
  }
  
  /**
   * @return the sender, if serializable.
   */
  public Object getSender()
  {
    return sender;
  }
  
  /**
   * @return the message contents.
   */
  public String getMessage()
  {
    return message;
  }
  
  /**
   * @return the item name, if any.
   */
  public String getItem()
  {
    return item;
  }
  
  /**
   * @return the amount - where amount can be a quantity of houses, items, or money.
   */
  public int getAmount()
  {
    return amount;
  }
  
  /**
   * @return the key held by sender.
   */
  public int getKey()
  {
    return key;
  }
  
  /**
   * @return the timestamp of this messages creation.
   */
  public long getTimestamp()
  {
    return timestamp;
  }
}
