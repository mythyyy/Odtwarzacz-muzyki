package Client.view;

import Client.ClientApp;
import Client.WritableGUI;
import Client.files.MessageListener;
import Client.files.Protocol;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.DataOutputStream;
import java.io.IOException;

public class ClientViewController extends ClientApp implements WritableGUI {
    @FXML
    private Pane rootPane;
    @FXML
    private Button sendButton;
    @FXML
    private Button connectButton;
    @FXML
    private TextField messageTextField;
    @FXML
    private TextArea chatTextArea;
    @FXML
    private Button disconnectButton;
    @FXML
    private TextField ipTextField;
    @FXML
    private TextField nicknameTextField;

    MessageListener listener;

    public String nicknameC;

    @Override
    public void write(String s) {
        chatTextArea.appendText(s + System.getProperty("line.separator"));
    }

    public void connectButtonOnClicked(MouseEvent mouseEvent) {
        System.out.println(nicknameC);
        if(!ipTextField.getText().isEmpty()){

            listener = new MessageListener(this,ipTextField.getText());
            listener.start();

            if(listener.isAlive()){
                connectButton.setText("Connected");
                connectButton.setDisable(true);
                ipTextField.setDisable(true);
                disconnectButton.setDisable(false);
                sendButton.setDisable(false);
            }
            else connectButton.setText("Connect");
        }
    }

    public void sendButtonOnClicked(MouseEvent mouseEvent) {
        System.out.println("-"+nicknameC);
        if(!messageTextField.getText().isEmpty() )
        {
            String message = nicknameC + " : " + messageTextField.getText();
            DataOutputStream out = listener.getOutputStream();
            try {
                out.writeInt(Protocol.CLIENT_DATA_TEXT);
                out.writeUTF(message);}
            catch(IOException e)
            {
                e.printStackTrace();
            }
            messageTextField.clear();
        }
    }

    public void disconnectButtonOnClicked(MouseEvent mouseEvent) {
        try{listener.stopPlayer();}
        catch (Exception e){
        }
        listener.interrupt();
        sendButton.setDisable(false);
        sendButton.setDisable(true);
        connectButton.setDisable(false);
        disconnectButton.setDisable(true);
    }

    public void goToNextScene(MouseEvent mouseEvent) throws IOException {
        if (!nicknameTextField.getText().isEmpty()) {
            nicknameC = nicknameTextField.getText();
            System.out.println(nicknameC);
            setNicknameAndPort(nicknameTextField.getText());
            Pane pane = FXMLLoader.load(getClass().getResource("/Client/view/ClientView.fxml"));
            rootPane.getChildren().setAll(pane);
            System.out.println(nicknameC);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("  ERROR !!!");
            alert.setContentText("Nie wprowadzono nicku");
            alert.showAndWait();
        }
    }
}
