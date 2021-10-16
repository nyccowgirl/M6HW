package com.m6;

import java.io.*;
import java.util.*;
import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.*;

public class CustomerInput extends Application {

    private Stage primaryStage;
    private Text statusText, resultText;
    private Button uploadButton;

    private final static Font RESULT_FONT = Font.font("Helvetica", 24);
    private final static Font INPUT_FONT = Font.font("Helvetica", 20);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        VBox primaryBox = new VBox();
        primaryBox.setAlignment(Pos.CENTER);
        primaryBox.setSpacing(20);
        primaryBox.setStyle("-fx-background-color: white");

        VBox uploadBox = new VBox();
        uploadBox.setAlignment(Pos.CENTER);
        uploadBox.setSpacing(20);
        Text uploadLabel = new Text("Upload a comma-separated file with customer data.");
        uploadLabel.setFont(INPUT_FONT);
        uploadButton = new Button("Upload data");
        uploadButton.setOnAction(this::processDataUpload);

        uploadBox.getChildren().add(uploadLabel);
        uploadBox.getChildren().add(uploadButton);
        primaryBox.getChildren().add(uploadBox);

        VBox resultsBox = new VBox();
        resultsBox.setAlignment(Pos.CENTER);
        resultsBox.setSpacing(20);
        statusText = new Text("");
        statusText.setVisible(false);
        statusText.setFont(RESULT_FONT);
        statusText.setFill(Color.RED);
        resultText = new Text("");
        resultText.setVisible(false);
        resultText.setFont(RESULT_FONT);
        resultsBox.getChildren().add(statusText);
        resultsBox.getChildren().add(resultText);
        primaryBox.getChildren().add(resultsBox);

        Scene scene = new Scene(primaryBox, 475, 200, Color.TRANSPARENT);
        primaryStage.setTitle("Customer Data Upload");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void processDataUpload(ActionEvent event) {
        statusText.setVisible(false);
        resultText.setVisible(false);
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);
        parseFile(file);

    }

    private void parseFile(File file) {
        ArrayList<Customer> customerList = new ArrayList<Customer>();
        String id = null;
        String quantityString = null;

        try {
            Scanner fileScan = new Scanner(new FileInputStream(file));
            int totalCustomers = 0;
            int totalOrders = 0;

            while (fileScan.hasNext()) {
                String line = fileScan.nextLine();

                Scanner lineScan = new Scanner(line);
                lineScan.useDelimiter(",");

                id = lineScan.next();

                if (!id.matches("[A-Za-z0-9 ]*")) {
                    throw new IllegalArgumentException();
                }

                quantityString = lineScan.next();
                int quantity = Integer.parseInt(quantityString);
                Customer customer = new Customer(id, quantity);
                customerList.add(customer);
            }

            fileScan.close();

            for (Customer cust : customerList) {
                totalCustomers++;
                totalOrders += cust.getNumberOfOrders();
            }

            uploadButton.setDisable(true);
            statusText.setText("Upload Successful: " + totalCustomers + " customer(s).");
            statusText.setVisible(true);
            resultText.setText("Total Number of Orders: " + totalOrders);
            resultText.setVisible(true);
            
        } catch(FileNotFoundException | NullPointerException e){
            statusText.setText("Upload Failed: Select a file.");
            statusText.setVisible(true);
        } catch(NumberFormatException e){
            statusText.setText("Upload Failed: Non-integer quantity.");
            statusText.setVisible(true);
            resultText.setText("Quantity: " + quantityString);
            resultText.setVisible(true);
        } catch(IllegalArgumentException e) {
            statusText.setText("Upload Failed: ID contains invalid character.");
            statusText.setVisible(true);
            resultText.setText("ID: " + id);
            resultText.setVisible(true);
        } finally {
            customerList.clear();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}