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

import Message.Message;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AgentGUIController extends Application
{
  private final SimpleDateFormat time = new SimpleDateFormat("h:mm:ss");

  @FXML
  private HBox hBox1;
  @FXML
  private TextField bankIP, auctionIP, input;
  @FXML
  private Button btnBid;
  @FXML
  private TextArea textArea;
  @FXML
  private Text txtTotalBidPlaced, txtBankBalance;
  @FXML
  private ComboBox<String> itemsComboBox;

  private Agent agent;
  private String history = "";
  private int bankBalance = 0, bidCount = 0;


  /**
   * Initializes state of GUI components.
   */
  @FXML
  public void initialize()
  {
    textArea.setVisible(false);
    input.setVisible(false);
    textArea.setEditable(true);
    txtTotalBidPlaced.setVisible(false);
    txtBankBalance.setVisible(false);
    itemsComboBox.setVisible(false);
    btnBid.setVisible(false);
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
      hBox1.setVisible(false);

      txtTotalBidPlaced.setVisible(true);
      textArea.setVisible(true);

      txtBankBalance.setVisible(true);
      btnBid.setVisible(true);
      itemsComboBox.setVisible(true);
      input.setVisible(true);

      agent.setMessageText("");

    } catch (UnknownHostException e)
    {
      e.printStackTrace();
      System.exit(-1);
    }

    agent.start();

    new AnimationTimer()
    {
      @Override
      public void handle(long now)
      {
        Message bankIn = agent.bankInput, auctionIn = agent.auctionInput;
        if (bankIn != null)
        {
          history += time.format(new Date(bankIn.getTimestamp())) + " | " + bankIn.getSignature() + bankIn.getMessage() + "\n";

          agent.bankInput = null;

        }
        if (auctionIn != null)
        {
          history += time.format(new Date(auctionIn.getTimestamp())) + " | " + auctionIn.getSignature() + auctionIn.getMessage() + "\n";
          agent.auctionInput = null;
        }

        if (bankBalance != agent.balance)
        {
          System.out.println(bankBalance + " " + agent.balance + " ????");
          if (bankBalance != 0) bidCount++;
          bankBalance = agent.balance;
          txtBankBalance.setText("Balance: $" + agent.balance + ".00");
          txtTotalBidPlaced.setText("Bid : \n" + bidCount);
        }

        if (!textArea.getText().equals(history))
        {
          textArea.setText(history);
          textArea.setScrollTop(textArea.getText().length());
        }

        if (agent.inventory != null)
        {
          String[] list = agent.inventory.split("\\.");
          itemsComboBox.getItems().setAll(list);
        }


      }
    }.start();
  }

  /**
   * Main entry point for AgentGUIController.
   *
   * @param args
   */
  public static void main(String args[])
  {
    launch(args);
  }


  /**
   * Handles user input.
   */
  @FXML
  private void placeBid()
  {
    if (!textArea.getText().equals(history))
    {
      textArea.setText(history);
      textArea.setScrollTop(textArea.getText().length());
    }
    if (itemsComboBox.getValue() == null) return;
    String request = input.getText();
    input.setText("");

    int bidAmount;
    try
    {
      String temp[] = request.split("\\s+");
      if (temp.length == 2) bidAmount = Integer.parseInt(temp[1]);
      else bidAmount = 0;
    } catch (NumberFormatException e)
    {
      bidAmount = 0;
    }

    if (bidAmount > bankBalance)
    {
      history += time.format(new Date(System.currentTimeMillis())) + " | " + "Error - insufficient funds." + "\n";
      textArea.setText(history);
      textArea.setScrollTop(textArea.getText().length());
      return;
    }

    String combo = " ";
    if (itemsComboBox.getValue() != null) combo = itemsComboBox.getValue();

    if (!request.equals("") && !request.equalsIgnoreCase("new"))
    {
      System.out.println(request + " " + combo + " " + bidAmount + "???");
      agent.setMessageText(request);
      agent.setCombo(combo);
      agent.setBidAmount(bidAmount);

      history += time.format(new Date(System.currentTimeMillis())) + " | " + agent.getAgentName() + request + "\n";

      if (!textArea.getText().equals(history))
      {
        textArea.setText(history);
        textArea.setScrollTop(textArea.getText().length());
      }
    }
  }

  /**
   * Sets the primary stage.
   *
   * @param primaryStage
   * @throws Exception
   */
  @Override
  public void start(Stage primaryStage) throws Exception
  {
    Parent root = FXMLLoader.load(getClass().getResource("AgentGUI.fxml"));
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
    primaryStage.setOnCloseRequest(event ->
    {
      if (agent != null) agent.setMessageText("EXIT");
      Platform.exit();
      System.exit(0);
    });
  }
}
