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

  public Message(Object sender, String signature, String message, String item, int key, int amount)
  {
    this.sender = sender;
    this.signature = signature;
    this.message = message;
    this.item = item;
    this.amount = amount;
    this.key = key;
  }

  public String getSignature()
  {
    return signature;
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

  public int getAmount()
  {
    return amount;
  }

  public int getKey()
  {
    return key;
  }
}
