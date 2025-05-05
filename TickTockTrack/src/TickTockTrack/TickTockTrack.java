package TickTockTrack;	

import gui.UserInterface;
import javafx.application.Application;
import javafx.stage.Stage;
import logic.SystemLogic;

public class TickTockTrack extends Application {
    @Override
    public void start(Stage primaryStage) {
        SystemLogic systemLogic = new SystemLogic();
        UserInterface userInterface = new UserInterface(systemLogic);
        userInterface.initPreLoginUI(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
