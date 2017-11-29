/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * BankThread.java - Threading to handle multiple client requests.
 */

package Bank;

import java.io.*;
import java.net.Socket;

public class BankThread extends Thread
{
  private Socket socket = null;
  private Bank bank;

  public BankThread(Socket socket)
  {
    super("[BankThread]");
    this.socket = socket;

    System.out.println("[Bank]: " + socket.toString() + " connected!");
  }

  public void run()
  {
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
         DataInputStream in = new DataInputStream(socket.getInputStream()))
    {
      String input, output;
      BankProtocol bankProtocol = new BankProtocol(socket, "Socket");
//      output = bankProtocol.handleRequest("START");
//      out.writeUTF(output);

      while (!(input = in.readUTF()).equals("EXIT"))
      {
        System.out.println(input);
    
        output = bankProtocol.handleRequest(input);
        out.writeUTF(output);
    
        if(output.equals("EXIT")) break;
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
