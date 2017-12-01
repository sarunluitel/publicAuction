/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralThread.java - Threading to handle multiple client requests.
 */

package AuctionCentral;

import Message.Message;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

class AuctionCentralThread extends Thread
{
  private final Socket socket;
  
  /**
   * Default constructor.
   *
   * @param socket
   */
  public AuctionCentralThread(Socket socket)
  {
    super("[AuctionCentralThread]");
    this.socket = socket;
//    System.out.println("[AuctionCentral]: " + socket.toString() + " connected!");
//    try
//    {
//      System.out.println("[AuctionCentral]: This connection will timeout after 5 minutes of inactivity.");
//      socket.setSoTimeout(5*60*1000);
//    }
//    catch(SocketException e)
//    {
//      e.printStackTrace();
//    }
  }
  
  /**
   * Run method for auction central thread.
   */
  public void run()
  {
    System.out.println("run");
    try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
         ObjectInputStream in = new ObjectInputStream(socket.getInputStream()))
    {
      try
      {
        System.out.println("message start");
        Message input, output;
        input = ((Message)in.readObject());
      
        AuctionCentralProtocol auctionCentralProtocol = new AuctionCentralProtocol(socket, input);
      
        while (true)
        {
          input = ((Message)in.readObject());
          if(input != null)
          {
            System.out.println(input.getMessage());
          
            input = ((Message)in.readObject());
            output = auctionCentralProtocol.handleRequest(input);
          
            out.writeObject(output);
          
            input = null;
          }
        }
      }
      catch(ClassNotFoundException e)
      {
        System.err.println(e.getMessage());
      }
      in.close();
      out.close();
      socket.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
