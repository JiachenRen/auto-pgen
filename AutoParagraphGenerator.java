/**
 * Created by Jiachen on 2/28/18.
 */

import components.Generator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AutoParagraphGenerator extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    public static Generator generator;
    public static APGController controller;

    @Override
    public void start(Stage primaryStage) throws IOException {
        generator = new Generator();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("apg.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.initListeners(generator);

        primaryStage.setTitle("Automatic Paragraph Generator");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();



    }
}
