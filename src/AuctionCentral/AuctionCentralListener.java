package AuctionCentral;

import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by jh on 12/5/17.
 */
public class AuctionCentralListener extends Thread
{
  private final Socket socket;
  private static int count = 0;

  public AuctionCentralListener(Socket socket)
  {
    super("[AuctionCentralThread]");
    this.socket = socket;
  }

  public static void addCount()
  {
    count++;
  }

  public static int getCount()
  {
    return count;
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

        while (true)
        {
          if (in.available() != 0)
          {
            input = ((Message) in.readObject());
          }
          if (input != null)
          {

            System.out.println(input.getSignature() + input.getMessage());

//            if(in.available()!=0)input = ((Message) in.readObject());
            output = auctionCentralProtocol.handleRequest(input);

            System.out.println("[AuctionCentral]: Sending " + output.getMessage() + " to " + socket.toString());

            out.writeObject(output);
            out.flush();

            input = null;
          }
          if(in.available() != 0) input = ((Message) in.readObject());
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
