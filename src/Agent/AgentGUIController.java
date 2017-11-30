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

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AgentGUIController extends Application
{
  @FXML
  private TextField txtbankIP, txtAuctionCentalIP, txtInput;
  @FXML
  private Button btnConnectIP;
  @FXML
  private TextArea txtField;

  private Agent agent=new Agent();

  @FXML
  public void initialize()
  {
    txtField.setVisible(false);
    txtInput.setVisible(false);
    txtField.setEditable(false);
  }

  @FXML
  private void secureConnection()
  {
    try
    {
      Agent.setAuctionAddress(InetAddress.getByName(txtbankIP.getText()));
      Agent.setBankAddress(InetAddress.getByName(txtAuctionCentalIP.getText()));
      txtAuctionCentalIP.setVisible(false);
      txtbankIP.setVisible(false);
      btnConnectIP.setVisible(false);

      txtField.setVisible(true);
      txtInput.setVisible(true);

    } catch (UnknownHostException e)
    {
      e.printStackTrace();
      System.exit(-1);
    }

    // run code to setup connections after we get addresses to bank and Auction Central
    try
    {

      agent.start();
    } catch (Exception e)
    {
      e.printStackTrace();
      System.exit(-1);
    }

  }


  public static void main(String args[])
  {
    launch(args);

  }

  @Override
  public void start(Stage primaryStage) throws Exception
  {

    Parent Agent = FXMLLoader.load(getClass().getResource("AgentGUI.fxml"));
    primaryStage.setScene(new Scene(Agent));

    primaryStage.show();
  }

  @FXML
  private void submitRequest()
  {
    agent.setMessage(txtInput.getText());
    String request= txtInput.getText();
    txtInput.setText("");
    txtField.setText(request);

  }
}
