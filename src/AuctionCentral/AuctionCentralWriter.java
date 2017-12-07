package AuctionCentral;

import Message.Message;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AuctionCentralWriter
{
  private Socket socket;
  private ObjectOutputStream out;
  
  private String name;
  
  public AuctionCentralWriter(Socket socket) throws IOException
  {
    this.socket = socket;

    out = new ObjectOutputStream(socket.getOutputStream());
    out.flush();
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public Socket getSocket() {
    return socket;
  }
  
  public String getName() {
    return name;
  }
  
  public void sendMessage(Message message) throws IOException
  {
      out.writeObject(message);
      out.flush();
  }
}
