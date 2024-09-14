package com.example.tour;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;



public class SceneManager {

    public static void switchScene(Node eventSource, String resource, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(resource));
            Parent root = loader.load();
            Stage stage = (Stage) eventSource.getScene().getWindow();

            stage.setScene(new Scene(root, width, height));
            Image icon = new Image(SceneManager.class.getResource("/img.png").toExternalForm());
            stage.getIcons().add(icon);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in switching scenes.");
        }
    }
    private static  Stack<String> sceneStack = new Stack<>();
    public static void loadScene(String scene, ActionEvent event) {
        sceneStack.push(scene);
        switchScene((Node) event.getSource(), scene, "Tour Companion", 1024, 650);

    }

    public static void goBack(ActionEvent event) {
        if (!sceneStack.isEmpty()) {
            sceneStack.pop();
            String previous = "login.fxml";
            try {
                previous = sceneStack.peek();
            }catch (Exception e){
                System.err.println("Stack underflow");
            }
            System.out.println(previous);
            switchScene((Node) event.getSource(), previous, "Tour Companion", 1024, 650);
        }
    }

    public static void clearScene(ActionEvent event) {
        switchScene((Node) event.getSource(), "login.fxml", "Tour Companion", 1024, 650);
        sceneStack.clear();
    }


}
