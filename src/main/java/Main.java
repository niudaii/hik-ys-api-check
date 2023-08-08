import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {
    private static TrayIcon trayIcon;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = (Parent) FXMLLoader.load(getClass().getResource("/main.fxml"));
        primaryStage.setTitle("海康威视/萤石 API 泄露利用工具");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
