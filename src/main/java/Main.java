import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    private ResourceBundle bundle;

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/main.fxml")
        );

        Parent root = loader.load();

        Label title =
                (Label) root.lookup("#titleLabel");

        Label languageLabel =
                (Label) root.lookup("#languageLabel");

        ComboBox<String> language =
                (ComboBox<String>) root.lookup("#languageComboBox");

        TextField input =
                (TextField) root.lookup("#temperatureInput");

        Button convert =
                (Button) root.lookup("#convertButton");

        Label result =
                (Label) root.lookup("#resultLabel");

        language.getItems().addAll(
                "English",
                "Finnish",
                "Swedish",
                "Japanese",
                "Arabic"
        );

        Runnable updateLanguage = () -> {

            String code = switch (language.getValue()) {
                case "Finnish" -> "fi";
                case "Swedish" -> "sv";
                case "Japanese" -> "ja";
                case "Arabic" -> "ar";
                default -> "en";
            };

            bundle = ResourceBundle.getBundle(
                    "messages",
                    Locale.forLanguageTag(code)
            );

            title.setText(bundle.getString("title"));
            languageLabel.setText(bundle.getString("language"));
            input.setPromptText(bundle.getString("input"));
            convert.setText(bundle.getString("convert"));
            result.setText(bundle.getString("result") + ":");

            stage.setTitle(bundle.getString("title"));
        };

        language.setOnAction(e -> updateLanguage.run());

        convert.setOnAction(e -> {

            try {
                double celsius =
                        Double.parseDouble(input.getText().trim());

                double fahrenheit =
                        celsius * 9 / 5 + 32;

                result.setText(
                        bundle.getString("result") + ": "
                                + String.format("%.2f", fahrenheit)
                                + " °F"
                );

                try {
                    DatabaseManager.saveConversion(
                            celsius,
                            fahrenheit
                    );

                    System.out.println(
                            "Conversion saved to database!"
                    );

                } catch (SQLException dbError) {
                    System.err.println(
                            "Database error: "
                                    + dbError.getMessage()
                    );

                    Alert alert = new Alert(
                            Alert.AlertType.ERROR,
                            "Conversion completed, but database save failed:\n"
                                    + dbError.getMessage()
                    );
                    alert.showAndWait();
                }

            } catch (NumberFormatException ex) {
                result.setText(bundle.getString("error"));
            }
        });

        stage.setScene(new Scene(root));

        language.setValue("English");
        updateLanguage.run();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}