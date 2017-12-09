/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralThread.java - Threading to handle multiple client requests.
 */

package AuctionCentral;

import Agent.*;
import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.*;

class AuctionCentralThread extends Thread
{
  private final Socket socket;
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
    try
    {
      auctionCentralWriter = new AuctionCentralWriter(socket);
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
      try
      {
        Message input, output;
        input = ((Message) in.readObject());

        AuctionCentralProtocol auctionCentralProtocol = new AuctionCentralProtocol(socket, input);
        
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
          }
          input = ((Message) in.readObject());
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
    Message win = new Message(message.getSender(), message.getSignature(),"has won the bid on " + message.getItem() + " for $" + message.getAmount() + "!", "", 0, 0);
    Message accept = new Message(message.getSender(), message.getSignature(), "is the highest bidder on " + message.getItem() + " for $" + message.getAmount() + "!", "", 0, 0);
    
    boolean winner = false;
    boolean accepted = false;
    
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
        if(client.getObject() instanceof Agent && message.getMessage().contains("remove")) winner = true;
        if(client.getObject() instanceof Agent && message.getMessage().contains("block")) accepted = true;
        
        if(winner) client.sendMessage(win);
        else if(accepted) client.sendMessage(accept);
        else client.sendMessage(message);
  
        winner = false;
        accepted = false;
      }
      catch (IOException e)
      {
        deadClients.add(client);
      }
    }
    
    writers.removeAll(deadClients);
  }
  
}
