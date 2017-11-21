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
      String inputLine, outputLine;
      AuctionCentralProtocol acp = new AuctionCentralProtocol();
      outputLine = acp.processInput(null);
      out.println(outputLine);
      
      while ((inputLine = in.readLine()) != null)
      {
        outputLine = acp.processInput(inputLine);
        out.println(outputLine);
        if(outputLine.equals("EXIT"))
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
