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
import java.net.SocketException;

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
  }

  /**
   * Run method for bank thread.
   */
  public void run()
  {
    try
    {
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      
      ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
      try
      {
        Message input, output;
        input = ((Message) in.readObject());

        BankProtocol bankProtocol = new BankProtocol();

        while (true)
        {
          if (input != null)
          {
            System.out.println(input.getSignature() + input.getMessage());

            output = bankProtocol.handleRequest(input);
            
            if(!output.getMessage().equals("ignore"))
            {
              out.writeObject(output);
              out.flush();
            }
          }
          try
          {
            input = ((Message) in.readObject());
          } catch (SocketException e)
          {
            System.out.println(this.getName() + "Gracefully Closed from bank");
            socket.close();
            return;
            //this.isAlive();
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
