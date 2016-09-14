package filter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view.fxml"));
        primaryStage.setTitle("ImageMiniFilters");
        primaryStage.setScene(new Scene(root, 870, 650));
        primaryStage.setMinWidth(870);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}