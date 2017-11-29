/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/27/17
 *
 * BankAccount.java - Bank accounts to associate with agents.
 */

package Bank;

public class BankAccount
{
  private String name;
  private int balance;
  
  public BankAccount(String name, int balance)
  {
    this.name = name;
    this.balance = balance;
  }

  public String getName()
  {
    return name;
  }

  public int getBalance()
  {
    return balance;
  }
}
