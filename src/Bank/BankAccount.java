/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/27/17
 *
 * BankAccount.java - Bank accounts to associate with agents.
 */

package Bank;

class BankAccount
{
  private final String name;
  private int balance, holds[];
  
  /**
   * Default constructor.
   *
   * Creates an account linked the name with given balance.
   * @param name
   * @param balance
   */
  public BankAccount(String name, int balance)
  {
    this.name = name;
    this.balance = balance;
    this.holds = null;
  }
  
  /**
   * @return name associated with account.
   */
  public String getName()
  {
    return name;
  }
  
  /**
   * @return balance associated with account.
   */
  public int getBalance()
  {
    return balance;
  }
}
