/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * BankThread.java - Threading to handle multiple client requests.
 */

package Bank;

import Message.Message;

import java.io.*;
import java.net.Socket;

class BankThread extends Thread
{
  private Socket socket = null;

  /**
   * Default constructor.
   *
   * @param socket
   */
  public BankThread(Socket socket)
  {
    super("[BankThread]");
    this.socket = socket;

    System.out.println("[Bank]: " + socket.toString() + " connected!");
  }

  /**
   * Run method for bank thread.
   */
  public void run()
  {
    System.out.println("B connecting");
    try
    {
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      
      ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
      try
      {
        System.out.println("B streams opened");
        Message input, output;
        System.out.println("B reading init");
        input = ((Message) in.readObject());

        BankProtocol bankProtocol = new BankProtocol();

        while (true)
        {
          if (input != null)
          {
            System.out.println(input.getSignature() + input.getMessage());

            output = bankProtocol.handleRequest(input);
  
            System.out.println("[Bank]: Sending " + output.getMessage() + " to " + socket.toString());
            
            if(!output.getMessage().equals("ignore"))
            {
              System.out.println("B sending");
              out.writeObject(output);
              out.flush();
              out.reset();
              System.out.println("B sent");
            }
          }
          System.out.println("B reading");
          input = ((Message) in.readObject());
          System.out.println("B done reading");
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
