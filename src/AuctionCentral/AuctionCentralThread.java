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
import java.net.SocketException;

public class AuctionCentralThread extends Thread
{
  private Socket socket;
  
  /**
   * Default constructor.
   *
   * @param socket
   */
  public AuctionCentralThread(Socket socket)
  {
    super("[AuctionCentralThread]");
    this.socket = socket;
    
    System.out.println("[AuctionCentral]: " + socket.toString() + " connected!");
    try
    {
      System.out.println("[AuctionCentral]: This connection will timeout after 5 minutes of inactivity.");
      socket.setSoTimeout(5*60*1000);
    }
    catch(SocketException e)
    {
      e.printStackTrace();
    }
  }
  
  /**
   * Run method for auction central thread.
   */
  public void run()
  {
    try (ObjectInputStream object = new ObjectInputStream(socket.getInputStream());
         DataOutputStream out = new DataOutputStream(socket.getOutputStream());
         DataInputStream in = new DataInputStream(socket.getInputStream()))
    {
      String input, output;
      Object socketClass = null;
      
      try
      {
        socketClass = object.readObject();
      }
      catch(ClassNotFoundException e)
      {
        System.out.println(e.getMessage());
      }
      
      AuctionCentralProtocol auctionCentralProtocol = new AuctionCentralProtocol(socket, socketClass);
//      output = auctionCentralProtocol.handleRequest("START");
//      out.writeUTF(output);
      
      while (!(input = in.readUTF()).equals("EXIT"))
      {
        System.out.println(input);
        
        output = auctionCentralProtocol.handleRequest(input);
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
