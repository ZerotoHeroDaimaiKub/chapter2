package se233.chapter2.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import se233.chapter2.Launcher;
import se233.chapter2.model.Currency;

import java.util.List;

public class RefreshTask extends Task<Void> {
    @Override
    protected Void call() throws InterruptedException {
        for (;;) {
            try {
                Thread.sleep((long) (60 * 1e3));
            } catch (InterruptedException e) {
                System.out.println("Encountered an interrupted exception");
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Launcher.refreshPane();
                        List<Currency> allCurrency = Launcher.getCurrencyList();
                        String found = "";
                        for (int i = 0; i < allCurrency.size(); i++) {
                            if (allCurrency.get(i).getWatchRate() != 0 && allCurrency.get(i).getWatchRate() > allCurrency.get(i).getCurrent().getRate()) {
                                if (found.equals("")) {
                                    found = allCurrency.get(i).getShortCode();
                                } else {
                                    found = found + " and " + allCurrency.get(i).getShortCode();
                                }
                            }
                        }
                        if (!found.equals("")) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle(null);
                            alert.setHeaderText(null);
                            if (found.length() > 3) {
                                alert.setContentText(String.format("%s have become lower than the watch rate!", found));
                            } else {
                                alert.setContentText(String.format("%s has become lower than the watch rates!", found));
                            }
                            alert.showAndWait();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}

