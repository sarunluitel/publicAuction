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
