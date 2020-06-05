package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {
    public Stage primaryStage;
    public String nickname;
    public String port;

    public void setNicknameAndPort(String nickname){
        this.nickname=nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPort() {
        return port;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Pane root2 = FXMLLoader.load(getClass().getResource("/Client/view/NewUser.fxml"));
        primaryStage.setTitle("Chat and Audio Streaming");
        primaryStage.setScene(new Scene(root2,430,510));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
