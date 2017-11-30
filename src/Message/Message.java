package Message;

import java.io.Serializable;

public class Message implements Serializable
{
  private Object sender;
  private String message;
  private int key;
  private String item;
  private int amount;
  
  public Message(Object sender, String message, String item, int key, int amount)
  {
    this.sender = sender;
    this.message = message;
    this.item = item;
    this.key = key;
    this.amount = amount;
  }
  
  public Object getSender()
  {
    return sender;
  }
  
  public String getMessage()
  {
    return message;
  }
  
  public String getItem()
  {
    return item;
  }
  
  public int getKey()
  {
    return key;
  }
  
  public int getAmount()
  {
    return amount;
  }
}
