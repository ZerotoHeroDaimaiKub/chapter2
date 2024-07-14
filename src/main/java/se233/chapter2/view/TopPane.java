package se233.chapter2.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import se233.chapter2.Launcher;
import se233.chapter2.controller.AllEventHandlers;

import java.time.LocalDateTime;


public class TopPane extends FlowPane{
    private Button refresh;
    private Button add;
    private Label update;
    private ComboBox<String> baseCurrencySelector;

    public TopPane() {
        this.setPadding(new Insets(10));
        this.setHgap(10);
        this.setPrefSize(640,20);
        add = new Button("Add");
        refresh = new Button("Refresh");
        baseCurrencySelector = new ComboBox<>();

        baseCurrencySelector.getItems().addAll("USD", "EUR", "JPY", "THB");
        baseCurrencySelector.setValue(Launcher.getBaseCurrency());

        baseCurrencySelector.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String selectedCurrency = baseCurrencySelector.getValue();
                Launcher.setBaseCurrency(selectedCurrency);
                try {
                    AllEventHandlers.onRefresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        refresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AllEventHandlers.onRefresh();
            }
        });
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AllEventHandlers.onAdd();
            }
        });
        update = new Label();
        refreshPane();
        this.getChildren().addAll(refresh, add, baseCurrencySelector, update);
    }
    public void refreshPane(){
        update.setText(String.format("Last update: %s", LocalDateTime.now().toString()));
    }

}
