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
      
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
      try
      {
        System.out.println("B streams opened");
        Message input, output;
        System.out.println("B reading init");
        input = ((Message) in.readObject());

        BankProtocol bankProtocol = new BankProtocol(socket, input);
//        out.writeObject(bankProtocol.setup);
//        out.flush();

        while (true)
        {
          if (input != null)
          {
            System.out.println(input.getSignature() + input.getMessage());

//            if(in.available() != 0) input = ((Message) in.readObject());
            output = bankProtocol.handleRequest(input);
  
            System.out.println("[Bank]: Sending " + output.getMessage() + " to " + socket.toString());
            
            if(output.getAmount() != -1)
            {
              System.out.println("B sending");
              out.writeObject(output);
              out.flush();
              out.reset();
              System.out.println("B sent");
            }
            //input = null;
          }
          System.out.println("B reading");
          input = ((Message) in.readObject());
          System.out.println("B done reading");
          // ????? : if(in.available() != 0)
          //if(in.available() != 0) input = ((Message) in.readObject());
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
