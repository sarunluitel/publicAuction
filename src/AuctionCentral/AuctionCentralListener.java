package AuctionCentral;

import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by jh on 12/5/17.
 */
public class AuctionCentralListener extends Thread implements Serializable
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


//        AuctionCentralProtocol auctionProtocol = new AuctionCentralProtocol(socket);
//        out.writeObject(bankProtocol.setup);
//        out.flush();

        while (true)
        {
//          System.out.println("Write : ");
//          Scanner scan = new Scanner(System.in);
//          String msg = scan.nextLine();
//          output = new Message(null, "[AuctionCentralListener]: ", msg, "Initialized", 0, 0);
//
//          System.out.println("[AuctionCentralListener]: Sending " + output.getMessage() + " to " + socket.toString());
//
//          if (!output.getMessage().isEmpty())
//          {
//            out.writeObject(output);
//            out.flush();
//          }

          if(in.available() != 0)
          {
            input = ((Message) in.readObject());
            System.out.println(input.getSignature() + input.getMessage());
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
