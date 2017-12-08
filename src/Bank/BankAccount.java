/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/27/17
 *
 * BankAccount.java - Bank accounts to associate with agents.
 */

package Bank;

import java.util.ArrayList;

class BankAccount
{
  private final String name;
  private final int publicID;
  private int balance;
  private ArrayList<Integer> holds;
  
  /**
   * Default constructor.
   *
   * Creates an account linked the name with given balance.
   * @param name
   * @param balance
   */
  public BankAccount(String name, int publicID, int balance)
  {
    this.name = name;
    this.publicID = publicID;
    this.balance = balance;
    holds = new ArrayList<>();
  }

  /**
   * @return name associated with account.
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * @return publicID associated with account.
   */
  public int getPublicID()
  {
    return this.publicID;
  }

  /**
   * @return balance associated with account.
   */
  public int getBalance()
  {
    return this.balance;
  }
  
  /**
   * Adds holds on this account as needed.
   */
  public void addHold(int amount)
  {
    this.balance -= amount;
    holds.add(amount);
  }
  
  public void removeHold(int amount)
  {
    this.balance += amount;
    if(holds.size() > 0) holds.remove(amount);
  }
  
  public void remove(int amount)
  {
    System.out.println(balance + "????????????????????????????????????????????????????????????????2");
    this.balance -= amount;
    System.out.println(balance + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
    if(holds.size() > 0) this.balance += holds.remove(amount);
    if(this.balance < 0) this.balance = 0;
  }
}
