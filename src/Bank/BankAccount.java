/**
 * CS351L Project #4: PublicAuction.
 *
 * @authors: Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/27/17
 * <p>
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
    this.holds = new ArrayList<>();
//    balance - amount
//    holds [] = amount
  }

  /**
   * @return name associated with account.
   */
  public String getName()
  {
    return name;
  }

  /**
   * @return publicID associated with account.
   */
  public int getPublicID()
  {
    return publicID;
  }

  /**
   * @return balance associated with account.
   */
  public int getBalance()
  {
    return balance;
  }

  public void addHolds()
  {
  }
}
