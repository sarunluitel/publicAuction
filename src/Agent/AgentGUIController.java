/*
 * CS351L Project #4: PublicAuction.
 * Jacob Hurst, Jaehee Shin, Sarun Luitel, Vincent Huber.
 * 11/29/17
 *
 * AgentGUIController.java - asks a prompt to connect to the Bank and Auction
 * central. secures connections and provides an GUI for Agents to place bids
 * and see the status.
 */

package Agent;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AgentGUIController extends Application
{
  @FXML
  private TextField bankIP, auctionIP, input;
  @FXML
  private Button connect;
  @FXML
  private TextArea textArea;
  @FXML
  private Text txtTotalBidPlaced, txtBankBalance;
  @FXML
  private ComboBox<String> itemsComboBox;

  private Agent agent;
  private String history = "";
  
  /**
   * Initializes state of GUI components.
   */
  @FXML
  public void initialize()
  {
    textArea.setVisible(false);
    input.setVisible(false);
    textArea.setEditable(false);
    txtTotalBidPlaced.setVisible(false);
    txtBankBalance.setVisible(false);
    itemsComboBox.setVisible(false);
  }

  /**
   * secureConnection() is invoked when btnConnect is clicked. secures connections.
   * hides old GUIElements and shows interface for agent to work on. starts the Agent Thread
   *
   * @return void.
   */
  @FXML
  private void secureConnection()
  {
    agent = new Agent();

    try
    {
      agent.setAuctionAddress(InetAddress.getByName(bankIP.getText()));
      agent.setBankAddress(InetAddress.getByName(auctionIP.getText()));
      auctionIP.setVisible(false);
      bankIP.setVisible(false);
      connect.setVisible(false);

      textArea.setVisible(true);
      input.setVisible(true);
      txtTotalBidPlaced.setVisible(true);
      txtBankBalance.setVisible(true);
      itemsComboBox.setVisible(true);

      agent.setMessageText("");
      
      synchronized (agent)
      {
        agent.notify();
      }
    }
    catch (UnknownHostException e)
    {
      e.printStackTrace();
      System.exit(-1);
    }

    agent.start();

    new AnimationTimer() {
      @Override
      public void handle(long now) {
        if(agent.MsgFrmBank!=null)
        {
          txtBankBalance.setText("$$$ - "+agent.MsgFrmBank.getAmount());
        }
      }
    }.start();

  }
  
  /**
   * Main entry point for AgentGUIController.
   * @param args
   */
  public static void main(String args[])
  {
    launch(args);
  }
  
  /**
   * Sets the primary stage.
   * @param primaryStage
   * @throws Exception
   */
  @Override
  public void start(Stage primaryStage) throws Exception
  {
    Parent root = FXMLLoader.load(getClass().getResource("AgentGUI.fxml"));
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
  }
  
  
  /**
   * Handles user input.
   */
  @FXML
  private void placeBid()
  {
    String request = input.getText();
    input.setText("");
    
    if(!request.equals(""))
    {
      history += request + "\n";
      if(agent.MsgFrmBank!=null) history += agent.MsgFrmBank.getSignature() + agent.MsgFrmBank.getMessage()+ "\n";
      if(agent.MsgFrmAuction!=null) history += agent.MsgFrmAuction.getSignature() + agent.MsgFrmAuction.getMessage()+ "\n";
      textArea.setText(history);
      
      agent.setMessageText(request);
    }
    
    synchronized (agent)
    {
      agent.notify();
    }
  }
}
