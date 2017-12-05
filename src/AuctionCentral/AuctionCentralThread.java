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
  }

  /**
   * Run method for auction central thread.
   */
  public void run()
  {
    try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
         ObjectInputStream in = new ObjectInputStream(socket.getInputStream()))
    {
      try
      {
        Message input, output;
        input = ((Message) in.readObject());

        AuctionCentralProtocol auctionCentralProtocol = new AuctionCentralProtocol(socket, input);
//        out.writeObject(auctionCentralProtocol.setup);
//        out.flush();

        while (true)
        {
          if(in.available() != 0) input = ((Message) in.readObject());
          if (input != null)
          {
            System.out.println(input.getSignature() + input.getMessage());
            
            if(in.available()!=0)input = ((Message) in.readObject());
            output = auctionCentralProtocol.handleRequest(input);
  
            System.out.println("[AuctionCentral]: Sending " + output.getMessage() + " to " + socket.toString());
            
            out.writeObject(output);
            out.flush();
  
            input = null;
          }
        }
      }
      catch (ClassNotFoundException e)
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
