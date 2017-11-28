/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralThread.java - Threading to handle multiple client requests.
 */

package AuctionCentral;

import java.io.*;
import java.net.Socket;

public class AuctionCentralThread extends Thread
{
  private Socket socket = null;
  
  public AuctionCentralThread(Socket socket) {
    super("AuctionCentralThread");
    this.socket = socket;
    
    System.out.println("[AuctionCentral]: " + socket.toString() + " connected!");
  }
  
  public void run()
  {
    try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());
         DataInputStream in = new DataInputStream(socket.getInputStream()))
    {
      String input, output;
      AuctionCentralProtocol auctionCentralProtocol = new AuctionCentralProtocol(socket, "Socket");
      output = auctionCentralProtocol.handleRequest("");
      out.writeUTF(output);
      
      while (!(input = in.readUTF()).equals("EXIT"))
      {
        System.out.println(input);
        output = auctionCentralProtocol.handleRequest(input);
        out.writeUTF(output);
        if(output.equals("EXIT"))
          break;
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
