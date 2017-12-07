/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralThread.java - Threading to handle multiple client requests.
 */

package AuctionCentral;

import Agent.Agent;
import AuctionHouse.AuctionHouse;
import Bank.*;
import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class AuctionCentralThread extends Thread
{
  private String name;
  private Socket current;
  private static Map<String, Socket> sockets = Collections.synchronizedMap(new HashMap<>());
  
  /**
   * Default constructor.
   *
   * @param socket
   */
  public AuctionCentralThread(Socket socket)
  {
    super("[AuctionCentralThread]");
    current = socket;
  }

  /**
   * Run method for auction central thread.
   */
  public void run()
  {
    System.out.println("AC connected");
    try
    {
      ObjectOutputStream out = new ObjectOutputStream(current.getOutputStream());
      out.flush();
      
      ObjectInputStream in = new ObjectInputStream(current.getInputStream());
      try
      {
        System.out.println("AC streams opened");
        Message input, output;
        input = ((Message) in.readObject());

        AuctionCentralProtocol auctionCentralProtocol = new AuctionCentralProtocol(current, input);
        System.out.println("AC protocol made");
  
        Object object = input.getSender();
        name = "object";
        if(object instanceof Agent) name = ((Agent)object).getAgentName();
        else if(object instanceof AuctionHouse) name = ((AuctionHouse)object).getName();
        else name += ((int)(Math.random())*1000);
        
        sockets.put(name, current);
        
        System.out.println("CURRENT CONNECTIONS:");
        System.out.println(sockets);
        
        while (true)
        {
          if (input != null)
          {
            System.out.println(input.getSignature() + input.getMessage());

//            if(in.available()!=0)input = ((Message) in.readObject());
            output = auctionCentralProtocol.handleRequest(input);

            System.out.println("[AuctionCentral]: Sending " + output.getMessage() + " to " + current.toString());
            out.writeObject(output);
            out.flush();
            out.reset();
            System.out.println("AC sent");
            
            input = null;
          }
//          if(in.available() != 0)
          System.out.println("AC reading");
          input = ((Message) in.readObject());
          System.out.println("AC done reading");
        }
      }
      catch (ClassNotFoundException e)
      {
        System.err.println(e.getMessage());
      }

      in.close();
      out.close();
      sockets.remove(name);
      current.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
