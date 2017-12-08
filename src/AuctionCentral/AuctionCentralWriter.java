/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 12/03/17
 *
 * AuctionCentralWriter.java - Writer used in broadcasting to all clients.
 */

package AuctionCentral;

import Message.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

class AuctionCentralWriter
{
  private Socket socket;
  private ObjectOutputStream out;
  
  private Object object = "";
  private String name;
  
  /**
   * Default constructor.
   *
   * @param socket
   * @throws IOException
   */
  public AuctionCentralWriter(Socket socket) throws IOException
  {
    this.socket = socket;

    out = new ObjectOutputStream(socket.getOutputStream());
    out.flush();
  }
  
  /**
   * Sets the name of this client.
   *
   * @param name
   */
  public void setName(String name)
  {
    this.name = name;
  }
  
  /**
   * Set's the client object for checking instanceof.
   * @param object
   */
  public void setObject(Object object) {
    this.object = object;
  }
  
  /**
   * @return this socket.
   */
  public Socket getSocket() {
    return socket;
  }
  
  /**
   * @return this name.
   */
  public String getName() {
    return name;
  }
  
  /**
   * @return this object - for checking instanceof.
   */
  public Object getObject() {
    return object;
  }
  
  /**
   * Sends message to the client associated with this writer.
   *
   * @param message
   * @throws IOException
   */
  public void sendMessage(Message message) throws IOException
  {
      out.writeObject(message);
      out.flush();
  }
}
