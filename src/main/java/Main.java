import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
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

        Label title = (Label) root.lookup("#titleLabel");
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

        Label unitName =
                (Label) root.lookup("#unitNameLabel");

        Label unitDescription =
                (Label) root.lookup("#unitDescriptionLabel");

        // Conversion history UI
        Label historyTitle = new Label("Last 5 Conversions");

        ListView<String> historyList = new ListView<>();
        historyList.setPrefHeight(150);
        historyList.setMaxWidth(350);

        VBox historyBox = new VBox(8);
        historyBox.getChildren().addAll(
                historyTitle,
                historyList
        );

        historyBox.setStyle(
                "-fx-padding: 12; " +
                        "-fx-alignment: center;"
        );

        // Keep existing FXML and add history below it
        VBox mainLayout = new VBox(10);
        mainLayout.getChildren().addAll(
                root,
                historyBox
        );

        mainLayout.setStyle(
                "-fx-alignment: top-center;"
        );

        Runnable refreshHistory = () -> {

            try {
                List<String> history =
                        DatabaseManager.getLastFiveConversions();

                historyList.getItems().setAll(history);

            } catch (SQLException ex) {
                historyList.getItems().setAll(
                        "History unavailable"
                );
                ex.printStackTrace();
            }
        };

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

            historyTitle.setText(
                    switch (code) {
                        case "fi" -> "Viimeiset 5 muunnosta";
                        case "sv" -> "Senaste 5 omvandlingarna";
                        case "ja" -> "最近の5件の変換";
                        case "ar" -> "آخر 5 تحويلات";
                        default -> "Last 5 Conversions";
                    }
            );

            try {
                unitName.setText(
                        DatabaseManager.getUnitName(
                                "celsius", code
                        )
                );

                unitDescription.setText(
                        DatabaseManager.getUnitDescription(
                                "celsius", code
                        )
                );

            } catch (SQLException ex) {
                unitName.setText("Celsius");
                unitDescription.setText(
                        "Database unavailable"
                );
                ex.printStackTrace();
            }
        };

        language.setOnAction(
                e -> updateLanguage.run()
        );

        convert.setOnAction(e -> {

            try {
                double celsius = Double.parseDouble(
                        input.getText().trim()
                );

                if (!Double.isFinite(celsius)) {
                    throw new NumberFormatException(
                            "Invalid temperature"
                    );
                }

                double fahrenheit =
                        celsius * 9 / 5 + 32;

                if (!Double.isFinite(fahrenheit)) {
                    throw new NumberFormatException(
                            "Temperature out of range"
                    );
                }

                result.setText(
                        bundle.getString("result") + ": " +
                                String.format(
                                        Locale.US,
                                        "%.2f",
                                        fahrenheit
                                ) + " °F"
                );

                try {
                    DatabaseManager.saveConversion(
                            celsius,
                            fahrenheit
                    );

                    refreshHistory.run();

                    System.out.println(
                            "Conversion saved to database!"
                    );

                } catch (SQLException dbError) {

                    Alert alert = new Alert(
                            Alert.AlertType.ERROR,
                            "Conversion completed, " +
                                    "but database save failed:\n" +
                                    dbError.getMessage()
                    );

                    alert.showAndWait();
                }

            } catch (NumberFormatException ex) {
                result.setText(
                        bundle.getString("error")
                );
            }
        });

        stage.setScene(new Scene(mainLayout));

        language.setValue("English");
        updateLanguage.run();
        refreshHistory.run();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
