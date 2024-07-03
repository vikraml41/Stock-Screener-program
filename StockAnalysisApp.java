import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StockAnalysisApp extends Application {

    private TextField symbolInput;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Stock Analysis");
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label symbolLabel = new Label("Enter Stock Symbol:");
        symbolInput = new TextField();
        Button startButton = new Button("Start Analysis");

        startButton.setOnAction(e -> {
            String symbol = symbolInput.getText().trim().toUpperCase();
            if (!symbol.isEmpty()) {
                launchStockAnalysis(symbol);
            }
        });

        vbox.getChildren().addAll(symbolLabel, symbolInput, startButton);
        Scene scene = new Scene(vbox, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void launchStockAnalysis(String symbol) {
        // Start your existing stock analysis program with the specified symbol
        StockAnalysisMain.setStockSymbol(symbol);
        StockAnalysisMain.main(new String[]{});
    }

    public static void main(String[] args) {
        launch(args);
    }
}
