package se233.chapter2.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import se233.chapter2.Launcher;
import se233.chapter2.model.Currency;
import se233.chapter2.model.CurrencyEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
                        refreshCurrencyData();
                        Launcher.refreshPane();
                        checkWatchRates();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void refreshCurrencyData() throws ExecutionException, InterruptedException {
        List<Currency> currencyList = Launcher.getCurrencyList();
        for (Currency currency : currencyList) {
            List<CurrencyEntity> cList = FetchData.fetchRange(currency.getShortCode(), 30);
            currency.setHistorical(cList);
            currency.setCurrent(cList.get(cList.size() - 1));
        }
        Launcher.setCurrencyList(currencyList);
    }

    private void checkWatchRates() {
        List<Currency> allCurrency = Launcher.getCurrencyList();
        String found = "";
        for (Currency currency : allCurrency) {
            if (currency.getWatchRate() != 0 && currency.getWatchRate() > currency.getCurrent().getRate()) {
                if (found.isEmpty()) {
                    found = currency.getShortCode();
                } else {
                    found = found + " and " + currency.getShortCode();
                }
            }
        }
        if (!found.isEmpty()) {
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
    }
}
