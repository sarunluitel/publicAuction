package Bank;

import AuctionHouse.AuctionHouse;

import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jh on 11/27/17.
 */
public class BankProtocol
{
  private Socket socket = null;
  private String name = null;

  public BankProtocol(Socket socket, String name)
  {
    this.socket = socket;
    this.name = name;
  }


  public String handleRequest(String request) {
    String result = "[Bank-" + this + "]: echo request = NOT RECOGNIZED";
//    for(int i = 0; i < requests.length; i++)
//    {
//      if(request.equals(requests[i])) result = "[AuctionCentral-" + this + "]: echo request = " + request;
//    }
    System.out.println(result);
    return result;
  }
}
