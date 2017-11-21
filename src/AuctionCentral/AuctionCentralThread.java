/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/20/17
 *
 * AuctionCentralThread.java - Threading to handle multiple client requests.
 */

package AuctionCentral;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
    {
      String input, output;
      AuctionCentralProtocol auctionCentralProtocol = new AuctionCentralProtocol(socket, "Socket");
      output = auctionCentralProtocol.handleRequest("");
      out.println(output);
      
      while (!(input = in.readLine()).equals("EXIT"))
      {
        System.out.println(input);
        output = auctionCentralProtocol.handleRequest(input);
        out.println(output);
        if(output.equals("EXIT"))
          break;
      }
      socket.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
