/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/27/17
 *
 * BankProtocol.java - Protocol for the bank to follow.
 */

package Bank;

import Message.Message;

import java.net.Socket;

class BankProtocol
{
  private Socket socket;
  private Message message;
  
  /**
   * Default constructor.
   *
   * @param socket
   * @param message
   */
  public BankProtocol(Socket socket, Message message)
  {
    this.socket = socket;
    this.message = message;
  }
  
  /**
   * Handles requests as they are received from socket.
   *
   * @param request
   * @return
   */
  public Message handleRequest(Message request)
  {
    String result = "[Bank]: Request = error.";
  
    if(request.equals("")) return null;
    else if(request.getMessage().length() < 5) return null;
    else if(request.getMessage().substring(0, 3).equals("new"))
    {
      System.out.println("[Bank]: Creating new account for " + request.getMessage().substring(4) + ".");
      BankAccount account = new BankAccount(request.getMessage().substring(12, request.getMessage().length()-1),
                                     Integer.parseInt(request.getMessage().substring(request.getMessage().length()-2, request.getMessage().length()-1))*1000);
      Bank.addAccounts(account);
      
      System.out.println("[Bank]: New account = [ID=" + account.getName() + ", BAL=$" + account.getBalance() + ".00].");
      System.out.println("[Bank]: " + Bank.getNumAccounts() + " account(s) are opened!");
    }
    return null;
  }
}
