/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * Bank.java - Accepts client requests to open a new bank account
 * with a fixed initial deposit, accepts auction central requests to block/unblock
 * funds on a particular account, accepts auction central requests to move amounts to
 * auction houses when a bid is successful.
 */

package Bank;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Bank
{
  private static ArrayList<BankAccount> accounts = new ArrayList<>();

  static void addAccounts(BankAccount account)
  {
    accounts.add(account);
  }

  static boolean contains(BankAccount account)
  {
    return accounts.contains(account);
  }

  static int getNumAccounts()
  {
    return accounts.size();
  }

  static BankAccount getAccount(String name)
  {
    for (BankAccount account : accounts)
    {
      if(account.getName().equals(name)) return account;
    }

    return null;
  }


  public static void main(String[] args) throws IOException
  {
    Bank bank = new Bank();
    
    System.out.println("[Bank]: IP = " + InetAddress.getLocalHost());
    try (ServerSocket serverSocket = new ServerSocket(2222, 50, InetAddress.getLocalHost()))
    {
      boolean open = true;
      System.out.println("[Bank]: " + serverSocket.toString() + ".");
      while (open) new BankThread(serverSocket.accept()).start();
    }
    catch (IOException e)
    {
      System.err.println("[Bank]: Error connecting...");
      System.exit(-1);
    }
  }
}
