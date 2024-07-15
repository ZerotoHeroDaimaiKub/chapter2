//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package se233.chapter2.view;

import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import se233.chapter2.Launcher;
import se233.chapter2.controller.AllEventHandlers;

public class TopPane extends FlowPane {
    private Button refresh;
    private Button add;
    private Label update;
    private ComboBox<String> baseCurrencySelector;

    public TopPane() {
        this.setPadding(new Insets(10.0));
        this.setHgap(10.0);
        this.setPrefSize(640.0, 20.0);
        this.add = new Button("Add");
        this.refresh = new Button("Refresh");
        this.baseCurrencySelector = new ComboBox();
        this.baseCurrencySelector.getItems().addAll(new String[]{"USD", "EUR", "JPY", "THB", "AUD", "CAD", "CHF", "CNY", "GBP", "HKD", "IDR", "INR", "MYR", "NZD", "PHP", "RUB", "SAR", "SGD", "TWD", "VND"});
        this.baseCurrencySelector.setValue(Launcher.getBaseCurrency());
        this.baseCurrencySelector.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String selectedCurrency = (String)TopPane.this.baseCurrencySelector.getValue();
                Launcher.setBaseCurrency(selectedCurrency);

                try {
                    AllEventHandlers.onRefresh();
                } catch (Exception var4) {
                    Exception e = var4;
                    e.printStackTrace();
                }

            }
        });
        this.refresh.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                AllEventHandlers.onRefresh();
            }
        });
        this.add.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                AllEventHandlers.onAdd();
            }
        });
        this.update = new Label();
        this.refreshPane();
        this.getChildren().addAll(new Node[]{this.refresh, this.add, this.baseCurrencySelector, this.update});
    }

    public void refreshPane() {
        this.update.setText(String.format("Last update: %s", LocalDateTime.now().toString()));
    }
}
