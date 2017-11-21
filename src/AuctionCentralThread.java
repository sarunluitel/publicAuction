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
  }
  
  public void run() {
    
    try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
    {
      String input, output;
      AuctionCentralProtocol auctionCentralProtocol = new AuctionCentralProtocol();
      output = auctionCentralProtocol.processInput("");
      out.println(output);
      
      while ((input = in.readLine()) != null)
      {
        output = auctionCentralProtocol.processInput(input);
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
