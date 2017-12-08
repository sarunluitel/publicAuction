/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralThread.java - Threading to handle multiple client requests.
 */

package AuctionCentral;

import Agent.AgentUpdater;
import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.*;

class AuctionCentralThread extends Thread
{
  private Socket socket;
  private static ArrayList<AuctionCentralWriter> writers = new ArrayList<>();
  
  /**
   * Default constructor.
   *
   * @param socket
   */
  public AuctionCentralThread(Socket socket, AuctionCentralWriter writer)
  {
    super("[AuctionCentralThread]");
    this.socket = socket;
    
    if(!writers.contains(writer))
    {
      writer.setName("[Bank]: ");
      writers.add(writer);
    }
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
      auctionCentralWriter = new AuctionCentralWriter(socket);
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
      try
      {
        System.out.println("AC streams opened");
        Message input, output;
        input = ((Message) in.readObject());

        AuctionCentralProtocol auctionCentralProtocol = new AuctionCentralProtocol(socket, input);
        System.out.println("AC protocol made");
        
        auctionCentralWriter.setName(auctionCentralProtocol.getCurrent());
        auctionCentralWriter.setObject(input.getSender());
        writers.add(auctionCentralWriter);
        
        
        while (true)
        {
          if (input != null)
          {
            System.out.println(input.getSignature() + input.getMessage());

            output = auctionCentralProtocol.handleRequest(input);
            
            broadcast(output);
            System.out.println("AC sent");
            
//            input = null;
          }
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
      
      socket.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  private synchronized void broadcast(Message message)
  {
    String content = message.getMessage();
    String name;
    
    if(content.equals("ignore")) return;
    
    List<AuctionCentralWriter> deadClients = new ArrayList<>();
    for(AuctionCentralWriter client: writers)
    {
      try
      {
        name = client.getName();
        
        if((name.contains("Bank") || name.contains("House")) && (content.contains("Error") || content.contains("Welcome"))) continue;
        if((message.getSender() instanceof AgentUpdater) && !(client.getObject() instanceof AgentUpdater)) continue;
        //        request.getSignature() + " has won the bid on " + request.getItem() + " for $" + request.getAmount() + "!";
        //        request.getSignature() + " has won the bid on " + request.getItem() + " for $" + request.getAmount() + "!";
        System.out.println("[AuctionCentral]: Sending " + message.getMessage() + " to " + client.getSocket().toString());
        client.sendMessage(message);
      }
      catch (IOException e)
      {
        deadClients.add(client);
      }
    }
    
    writers.removeAll(deadClients);
  }
  
}
