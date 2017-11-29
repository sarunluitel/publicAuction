package Bank;

import java.net.Socket;

/**
 * Created by jh on 11/27/17.
 */
public class BankProtocol
{
  private Socket socket;
  private String name;

  public BankProtocol(Socket socket, String name)
  {
    this.socket = socket;
    this.name = name;
  }

  public String handleRequest(String request)
  {
    String result = "[Bank]: Request = error.";

    if(request.equals("")) return result;
    else if(request.length() < 5) return result;
    else if(request.substring(0, 5).equals("name:"))
    {
      System.out.println("[Bank]: Creating new account for " + request.substring(6) + ".");
      BankAccount account = new BankAccount(request.substring(12, request.length() - 1),
                                     Integer.parseInt(request.substring(request.length() - 2,
                                            request.length() - 1)) * 1000);
      Bank.addAccounts(account);
      
      System.out.println("[Bank]: New account = [" + account.getName() + ", " + account.getBalance() + "].");
      System.out.println("[Bank]: " + Bank.getNumAccounts() + " account(s) are opened!");
    }
    return result;
  }
}
