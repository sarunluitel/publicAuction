package Bank;

import java.io.*;
import java.net.Socket;

/**
 * Created by jh on 11/27/17.
 */
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
