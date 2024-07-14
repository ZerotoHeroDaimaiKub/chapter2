package se233.chapter2.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import se233.chapter2.Launcher;
import se233.chapter2.model.Currency;
import se233.chapter2.model.CurrencyEntity;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class AllEventHandlers {
    public static void onRefresh(){
        try{
            Launcher.refreshPane();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void onAdd() {
        boolean validInput = false;
        while (!validInput) {
            try {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add Currency");
                dialog.setContentText("Currency code:");
                dialog.setHeaderText(null);
                dialog.setGraphic(null);
                Optional<String> code = dialog.showAndWait();
                if (code.isPresent()) {
                    List<Currency> currencyList = Launcher.getCurrencyList();
                    Currency c = new Currency(code.get().toUpperCase()); // Convert to uppercase
                    List<CurrencyEntity> cList = null;
                    try {
                        cList = FetchData.fetchRange(c.getShortCode(), 30);
                    } catch (org.json.JSONException e) {
                        throw new IllegalArgumentException("Invalid currency code");
                    }
                    if (cList == null || cList.isEmpty()) {
                        throw new IllegalArgumentException("Invalid currency code");
                    }
                    c.setHistorical(cList);
                    c.setCurrent(cList.get(cList.size() - 1));
                    currencyList.add(c);
                    Launcher.setCurrencyList(currencyList);
                    Launcher.refreshPane();
                    validInput = true;
                } else {
                    validInput = true; // User cancelled the dialog
                }
            } catch (IllegalArgumentException e) {
                showAlert("Invalid Currency Code", "The currency code you entered is invalid. Please try again.");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
//    public static void onAdd() throws ExecutionException, InterruptedException {
//        boolean validInput = false;
//        while(!validInput){
//            TextInputDialog dialog = new TextInputDialog();
//            dialog.setTitle("Add Currency");
//            dialog.setContentText("Currency code:");
//            dialog.setHeaderText(null);
//            dialog.setGraphic(null);
//            Optional<String> code = dialog.showAndWait();
//
//            if (code.isPresent()) {
//                List<Currency> currencyList = Launcher.getCurrencyList();
//                Currency c = new Currency(code.get().toUpperCase());
//                List<CurrencyEntity> cList = null;
//                try {
//                    cList = FetchData.fetchRange(c.getShortCode(), 30);
//                } catch (org.json.JSONException e) {
//                    throw new IllegalArgumentException("Invalid currency code");
//                }
//                if (cList == null || cList.isEmpty()) {
//                    throw new IllegalArgumentException("Invalid currency code");
//                }
//                c.setHistorical(cList);
//                c.setCurrent(cList.get(cList.size() - 1));
//                currencyList.add(c);
//                Launcher.setCurrencyList(currencyList);
//                Launcher.refreshPane();
//                validInput = true;
//            } else{
//                validInput = true; // User cancelled the dialog
//            }
//        }

    }
    public static void onDelete(String code) throws ExecutionException, InterruptedException {
        List<Currency> currencyList = Launcher.getCurrencyList();
        int index = -1;
        for (int i = 0; i < currencyList.size(); i++) {
            if (currencyList.get(i).getShortCode().equals(code)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            currencyList.remove(index);
            Launcher.setCurrencyList(currencyList);
            Launcher.refreshPane();
        }
    }
    public static void onWatch(String code) throws ExecutionException, InterruptedException {
        List<Currency> currencyList = Launcher.getCurrencyList();
        int index = -1;
        for (int i = 0; i < currencyList.size(); i++) {
            if (currencyList.get(i).getShortCode().equals(code)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Watch");
            dialog.setContentText("Rate:");
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            Optional<String> retrievedRate = dialog.showAndWait();

            if (retrievedRate.isPresent()) {
                double rate = Double.parseDouble(retrievedRate.get());
                currencyList.get(index).setWatch(true);
                currencyList.get(index).setWatchRate(rate);
                Launcher.setCurrencyList(currencyList);
                Launcher.refreshPane();
            }
        }
    }

    public static void onUnwatch(String code) throws ExecutionException, InterruptedException {
        List<Currency> currencyList = Launcher.getCurrencyList();
        int index = -1;
        for (int i = 0; i < currencyList.size(); i++) {
            if (currencyList.get(i).getShortCode().equals(code)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            currencyList.get(index).setWatch(false);
            currencyList.get(index).setWatchRate(0.0);
            Launcher.setCurrencyList(currencyList);
            Launcher.refreshPane();
        }
    }



    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
