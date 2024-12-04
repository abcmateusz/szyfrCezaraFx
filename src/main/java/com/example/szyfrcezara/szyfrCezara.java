package com.example.szyfrcezara;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class szyfrCezara extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Szyfr Cezara");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        VBox inputSection = new VBox(10);
        inputSection.setPadding(new Insets(10));
        inputSection.setStyle("-fx-background-color: #34495e; -fx-background-radius: 10;");

        Label inputLabel = new Label("Tekst:");
        TextArea inputText = new TextArea();
        inputText.setWrapText(true);

        Label keyLabel = new Label("Klucz:");
        TextField keyField = new TextField();
        keyField.setPromptText("Podaj liczbę całkowitą");

        inputSection.getChildren().addAll(inputLabel, inputText, keyLabel, keyField);

        VBox outputSection = new VBox(10);
        outputSection.setPadding(new Insets(10));
        outputSection.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 10;");

        Label outputLabel = new Label("Wynik:");
        TextArea outputText = new TextArea();
        outputText.setEditable(false);
        outputText.setWrapText(true);

        outputSection.getChildren().addAll(outputLabel, outputText);

        HBox buttonSection = new HBox(10);
        buttonSection.setPadding(new Insets(10));
        buttonSection.setAlignment(Pos.CENTER);

        Button encryptButton = new Button("Zaszyfruj");
        Button decryptButton = new Button("Deszyfruj");
        Button saveButton = new Button("Zapisz");
        Button openButton = new Button("Otwórz");
        Button closeButton = new Button("Zamknij");

        buttonSection.getChildren().addAll(encryptButton, decryptButton, saveButton, openButton, closeButton);

        encryptButton.setOnAction(e -> {
            String text = inputText.getText();
            String keyStr = keyField.getText();
            try {
                int key = Integer.parseInt(keyStr);
                outputText.setText(caesarCipher(text, key));
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Klucz musi być liczbą całkowitą.");
            }
        });

        decryptButton.setOnAction(e -> {
            String text = inputText.getText();
            String keyStr = keyField.getText();
            try {
                int key = Integer.parseInt(keyStr);
                outputText.setText(caesarCipher(text, -key));
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Klucz musi być liczbą całkowitą.");
            }
        });

        saveButton.setOnAction(e -> {
            try {
                Path dataDirectory = Path.of("data");
                if (!Files.exists(dataDirectory)) {
                    Files.createDirectory(dataDirectory);
                }
                File file = new File(dataDirectory.toFile(), "output.txt");
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                    writer.write(outputText.getText());
                }
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Plik zapisany w folderze data.");
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się zapisać pliku.");
            }
        });

        openButton.setOnAction(e -> {
            try {
                Path dataDirectory = Path.of("data");
                File file = new File(dataDirectory.toFile(), "output.txt");
                if (!file.exists()) {
                    showAlert(Alert.AlertType.ERROR, "Błąd", "Plik nie istnieje w folderze data.");
                    return;
                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    inputText.setText(content.toString().trim());
                }
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Nie udało się otworzyć pliku.");
            }
        });

        closeButton.setOnAction(e -> primaryStage.close());

        root.setTop(inputSection);
        root.setCenter(outputSection);
        root.setBottom(buttonSection);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String caesarCipher(String text, int key) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c) || "ąćęłńóśźż".indexOf(c) >= 0 || "ĄĆĘŁŃÓŚŹŻ".indexOf(c) >= 0) {
                result.append(shiftCharacter(c, key));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private char shiftCharacter(char c, int key) {
        String alphabet = "aąbcćdeęfghijklłmnńoóprsśtuwyzźż";
        String upperAlphabet = alphabet.toUpperCase();
        if (Character.isLowerCase(c)) {
            int index = (alphabet.indexOf(c) + key) % alphabet.length();
            return alphabet.charAt((index + alphabet.length()) % alphabet.length());
        } else if (Character.isUpperCase(c)) {
            int index = (upperAlphabet.indexOf(c) + key) % upperAlphabet.length();
            return upperAlphabet.charAt((index + upperAlphabet.length()) % upperAlphabet.length());
        } else {
            return c;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
