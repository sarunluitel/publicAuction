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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class BankProtocol implements Serializable
{
  private static Map<Integer, BankAccount> accounts = Collections.synchronizedMap(new HashMap<>());
  private BankAccount account;
  
  /**
   * Default constructor.
   */
  public BankProtocol() {}

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
    switch (request.getMessage())
    {
      case "auction central":
        message = "ignore";
        response = new Message(null, "[Bank]: ", message, "", request.getKey(), -1);
        break;
      case "new":
        String name = ((Agent) request.getSender()).getAgentName();
        
        System.out.println("[Bank]: Creating new account for " + name + ".");

        account = new BankAccount(name, request.getKey(), 500);
        Bank.addAccounts(account);
        
        accounts.put(request.getKey(), account);
        
        message = "New account = [NAME=" + account.getName() + ", ID=" + account.getPublicID() + ", BAL=$" + account.getBalance() + ".00].";
        System.out.println("[Bank]: " + message);
        System.out.println("[Bank]: " + Bank.getNumAccounts() + " account(s) are opened!");

        response = new Message(null, "[Bank]: ", message, "Account created", request.getKey(), account.getBalance());
        break;
      case "balance":
        message = "updated";
        account = accounts.get(request.getKey());
        response = new Message(null, "[Bank]: ", message, "Balance provided", request.getKey(), account.getBalance());
        break;
      case "block":
        message = "ignore";
        account = accounts.get(request.getKey());
        account.addHold(request.getAmount());
        response = new Message(null, "[Bank]: ", message, "Blocked an amount", request.getKey(), account.getBalance());
        break;
      case "unblock":
        message = "ignore";
        account = accounts.get(request.getKey());
        account.removeHold(request.getAmount());
        response = new Message(null, "[Bank]: ", message, "Blocked an amount", request.getKey(), account.getBalance());
        break;
      case "remove":
        message = "ignore";
        account = accounts.get(request.getKey());
        account.remove(request.getAmount());
        response = new Message(null, "[Bank]: ", message, "Funds removed", request.getKey(), account.getBalance());
        break;
      case "EXIT": //git
        message = "Goodbye!";
        response = new Message(null, "[Bank]: ", message, "Goodbye!", request.getKey(), account.getBalance());
        break;
      case "update":
        message = "ignore";
        response = new Message(null, "[Bank]: ", message, "Goodbye!", request.getKey(), account.getBalance());
        break;
      default:
        message = "Error - request not recognized.";
        int balance = (account != null) ? account.getBalance() : -1;
        response = new Message(null, "[Bank]: ", message, "", -1, balance);
        break;
    }
    return response;
  }
}
