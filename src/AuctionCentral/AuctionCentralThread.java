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
import java.io.ObjectOutputStream;
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
    System.out.println("AC connected");
    try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
         ObjectInputStream in = new ObjectInputStream(socket.getInputStream()))
    {
      try
      {
        System.out.println("AC streams opened");
        Message input, output;
        input = ((Message) in.readObject());

        AuctionCentralProtocol auctionCentralProtocol = new AuctionCentralProtocol(socket, input);
        System.out.println("AC protocol made");
        
        while (true)
        {
          if (input != null)
          {
            System.out.println(input.getSignature() + input.getMessage());

//            if(in.available()!=0)input = ((Message) in.readObject());
            output = auctionCentralProtocol.handleRequest(input);

            System.out.println("[AuctionCentral]: Sending " + output.getMessage() + " to " + socket.toString());
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
      socket.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
