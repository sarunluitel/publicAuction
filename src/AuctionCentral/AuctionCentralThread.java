/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralThread.java - Threading to handle multiple client requests.
 */

package AuctionCentral;

import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.*;

class AuctionCentralThread extends Thread
{
  private String name;
  private Socket current;
  private static Map<String, Socket> sockets = Collections.synchronizedMap(new HashMap<>());
  private static ArrayList<AuctionCentralWriter> writers = new ArrayList<>();
  
  /**
   * Default constructor.
   *
   * @param socket
   */
  public AuctionCentralThread(Socket socket, AuctionCentralWriter writer)
  {
    super("[AuctionCentralThread]");
    current = socket;
    
    if(!sockets.containsValue(writer.getSocket()))
    {
      name = "[Bank]: ";
      sockets.put(name, writer.getSocket());
    }
    if(!writers.contains(writer)) writers.add(writer);
  }

  /**
   * Run method for auction central thread.
   */
  public void run()
  {
    AuctionCentralWriter auctionCentralWriter;
    System.out.println("AC connected");
    try
    {
      auctionCentralWriter = new AuctionCentralWriter(current);
      ObjectInputStream in = new ObjectInputStream(current.getInputStream());
      try
      {
        System.out.println("AC streams opened");
        Message input, output;
        input = ((Message) in.readObject());

        AuctionCentralProtocol auctionCentralProtocol = new AuctionCentralProtocol(current, input);
//        auctionCentralProtocol.handleRequest(input);
        System.out.println("AC protocol made");
  
        name = auctionCentralProtocol.getCurrent();
        
        sockets.put(name, current);
        writers.add(auctionCentralWriter);
        
        System.out.println("CURRENT CONNECTIONS:");
        System.out.println(sockets);
        
        while (true)
        {
          if (input != null)
          {
            System.out.println(input.getSignature() + input.getMessage());

//            if(in.available()!=0)input = ((Message) in.readObject());
            output = auctionCentralProtocol.handleRequest(input);
            
//            auctionCentralWriter.sendMessage(output);
//            out.writeObject(output);
//            out.flush();
//            out.reset();
            sendMessageToClients(output);
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
//      out.close();
      
      sockets.remove(name);
      
      current.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public synchronized void sendMessageToClients(Message message)
  {
    List<AuctionCentralWriter> deadClients = new ArrayList<>();
    
    for(AuctionCentralWriter client: writers)
    {
      try {
        System.out.println("[AuctionCentral]: Sending " + message.getMessage() + " to " + client.getSocket().toString());
        client.sendMessage(message);
      } catch (IOException e) {
        deadClients.add(client);
      }
    }
    
    writers.removeAll(deadClients);
  }
  
}
