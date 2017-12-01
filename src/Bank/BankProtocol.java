/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/27/17
 *
 * BankProtocol.java - Protocol for the bank to follow.
 */

package Bank;

import Agent.Agent;
import Message.Message;

import java.io.Serializable;
import java.net.Socket;

class BankProtocol implements Serializable
{
  private Socket socket;
  private Message message;
  private BankAccount account;
  
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
    handleRequest(message);
  }
  
  /**
   * Handles requests as they are received from socket.
   *
   * @param request
   * @return
   */
  public Message handleRequest(Message request)
  {
    Message response;
    String message;
    switch(request.getMessage())
    {
      case "auction central":
        message = "[Bank]: Connection made with auction central.";
        response = new Message(this, message, "Connected", request.getKey(), request.getAmount());
        System.out.println(message);
        break;
      case "new":
        String agent = ((Agent)request.getSender()).getAgentName();
        
        System.out.println("[Bank]: Creating new account for " + agent + ".");
        account = new BankAccount(agent.substring(12, agent.length()-1),Integer.parseInt(agent.substring(agent.length()-2, agent.length()-1))*1000);
        Bank.addAccounts(account);
        
        message = "[Bank]: New account = [ID=" + account.getName() + ", BAL=$" + account.getBalance() + ".00].";
        System.out.println(message);
        System.out.println("[Bank]: " + Bank.getNumAccounts() + " account(s) are opened!");
        
        response = new Message(this, message, "Account created", request.getKey(), account.getBalance());
        break;
      case "balance":
        message = "[Bank]: See amount for balance.";
        response = new Message(this, message, "Balance provided", request.getKey(), account.getBalance());
        System.out.println(message);
        break;
      case "block":
        message = "[Bank]: Blocking " + account.getBalance() + " on " + account.getName() + "'s...";
        response = new Message(this, message, "Blocked an amount", request.getKey(), account.getBalance());
        System.out.println(message);
        break;
      case "unblock":
        message = "[Bank]: Unblocking " + account.getBalance() + " on " + account.getName() + "'s...";
        response = new Message(this, message, "Blocked an amount", request.getKey(), account.getBalance());
        System.out.println(message);
        break;
      case "transaction":
        message = "[Bank]: Purchase made, removing $" + account.getBalance() + ".00 from " + account.getName() + "'s account...";
        response = new Message(this, message, "Funds removed", request.getKey(), account.getBalance());
        System.out.println(message);
        break;
      case "EXIT":
        message = "[Bank]: Goodbye!";
        response = new Message(this, message, "Goodbye!", request.getKey(), account.getBalance());
        System.out.println(message);
        break;
      default:
        message = "[Bank]: Error - request not recognized.";
        response = new Message(this, message, "", request.getKey(), account.getBalance());
        System.out.println(message);
        break;
    }
    return response;
  }
}
