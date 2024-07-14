package se233.chapter2.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import se233.chapter2.controller.AllEventHandlers;
import se233.chapter2.controller.DrawGraphTask;
import se233.chapter2.model.Currency;

import java.util.concurrent.*;

public class CurrencyPane extends BorderPane {
    private Currency currency;
    private Button watch;
    private Button delete;
    private Button unwatch;

    public CurrencyPane(Currency currency) {
        this.watch = new Button("Watch");
        this.unwatch = new Button("Unwatch");
        this.delete = new Button("Delete");
        this.watch.setOnAction(event -> {
            try {
                AllEventHandlers.onWatch(currency.getShortCode());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        this.unwatch.setOnAction(event -> {
            try {
                AllEventHandlers.onUnwatch(currency.getShortCode());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        this.delete.setOnAction(event -> {
            try {
                AllEventHandlers.onDelete(currency.getShortCode());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        this.setPadding(new Insets(0));
        this.setPrefSize(640, 300);
        this.setStyle("-fx-background-color: black;");
        this.refreshPane(currency);
    }

    public void refreshPane(Currency currency) {
        this.currency = currency;
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<Pane> currencyInfoFuture = executor.submit(new CurrencyInfoTask(currency));
        Future<Pane> currencyTopAreaFuture = executor.submit(new CurrencyTopAreaTask(currency, watch, unwatch, delete));

        try {
            Pane currencyInfo = currencyInfoFuture.get();
            Pane currencyTopArea = currencyTopAreaFuture.get();
            FutureTask<VBox> futureTask = new FutureTask<>(new DrawGraphTask(currency));
            executor.execute(futureTask);
            VBox currencyGraph = futureTask.get();
            this.setTop(currencyTopArea);
            this.setLeft(currencyInfo);
            this.setCenter(currencyGraph);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }
}

class CurrencyInfoTask implements Callable<Pane> {
    private final Currency currency;

    public CurrencyInfoTask(Currency currency) {
        this.currency = currency;
    }

    @Override
    public Pane call() {
        VBox currencyInfoPane = new VBox(10);
        currencyInfoPane.setPadding(new Insets(5, 25, 5, 25));
        currencyInfoPane.setAlignment(Pos.CENTER);

        Label exchangeString = new Label("");
        Label watchString = new Label("");
        exchangeString.setStyle("-fx-font-size: 20;");
        watchString.setStyle("-fx-font-size: 14;");

        if (this.currency != null) {
            exchangeString.setText(String.format("%s: %.4f", this.currency.getShortCode(), this.currency.getCurrent().getRate()));

            if (this.currency.getWatch()) {
                watchString.setText(String.format("(Watch @%.4f)", this.currency.getWatchRate()));
            }
        }

        currencyInfoPane.getChildren().addAll(exchangeString, watchString);
        return currencyInfoPane;
    }
}

class CurrencyTopAreaTask implements Callable<Pane> {
    private final Currency currency;
    private final Button watch;
    private final Button unwatch;
    private final Button delete;

    public CurrencyTopAreaTask(Currency currency, Button watch, Button unwatch, Button delete) {
        this.currency = currency;
        this.watch = watch;
        this.unwatch = unwatch;
        this.delete = delete;
    }

    @Override
    public Pane call() {
        HBox topArea = new HBox(10);
        topArea.setPadding(new Insets(5));
        topArea.getChildren().addAll(watch, unwatch, delete);
        topArea.setAlignment(Pos.CENTER_RIGHT);
        return topArea;
    }
}
