package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        TabPane root = FXMLLoader.load(getClass().getResource("player.fxml"));
        primaryStage.setTitle("Media Player");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(400);
        primaryStage.show();



        // got the method from playerController class
        playerController playerController = new playerController();
        playerController.doubleClickFullScreen(root, primaryStage);



    }
    public static void main(String[] args) {
        launch(args);
    }
}
