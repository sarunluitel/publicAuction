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

class Bank
{
  private static ArrayList<BankAccount> accounts = new ArrayList<>();

  /**
   * Adds bank account to the list of accounts.
   *
   * @param account
   */
  static void addAccounts(BankAccount account)
  {
    accounts.add(account);
  }
  
  /**
   * @return number of accounts.
   */
  static int getNumAccounts()
  {
    return accounts.size();
  }
  
  /**
   * Main method for bank.
   *
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException
  {
    System.out.println("[Bank]: IP = " + InetAddress.getLocalHost());
    try (ServerSocket serverSocket = new ServerSocket(2222, 100,InetAddress.getLocalHost()))
    {
      System.out.println("[Bank]: " + serverSocket.toString() + ".");
      while (true) new BankThread(serverSocket.accept()).start();
    }
    catch (IOException e)
    {
      System.err.println("[Bank]: Error connecting...");
      System.exit(-1);
    }
  }// to update bank code
}
