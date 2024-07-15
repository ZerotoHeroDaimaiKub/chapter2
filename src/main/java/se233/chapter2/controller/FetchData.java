
package se233.chapter2.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import se233.chapter2.Launcher;
import se233.chapter2.model.CurrencyEntity;

public class FetchData {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public FetchData() {
    }

    public static List<CurrencyEntity> fetchRange(String symbol, int N) {
        String baseCurrency = Launcher.getBaseCurrency();
        String dateEnd = LocalDate.now().format(formatter);
        String dateStart = LocalDate.now().minusDays((long)N).format(formatter);
        String urlStr = String.format("https://cmu.to/SE233currencyapi?base=%s&symbol=%s&start_date=%s&end_date=%s", baseCurrency, symbol, dateStart, dateEnd);
        List<CurrencyEntity> histList = new ArrayList();

        try {
            String retrievedJson = IOUtils.toString(new URL(urlStr), Charset.defaultCharset());
            System.out.println("API Response: " + retrievedJson);
            JSONObject jsonOBJ = new JSONObject(retrievedJson);
            if (!jsonOBJ.has("rates")) {
                throw new JSONException("Rates not found in the response");
            }

            JSONObject ratesOBJ = jsonOBJ.getJSONObject("rates");
            Iterator keysToCopyIterator = ratesOBJ.keys();

            while(keysToCopyIterator.hasNext()) {
                String key = (String)keysToCopyIterator.next();
                Double rate = Double.parseDouble(ratesOBJ.get(key).toString());
                histList.add(new CurrencyEntity(rate, key));
            }

            histList.sort(new Comparator<CurrencyEntity>() {
                public int compare(CurrencyEntity o1, CurrencyEntity o2) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });
        } catch (MalformedURLException var13) {
            System.err.println("Encountered a Malformed URL exception");
        } catch (IOException var14) {
            System.err.println("Encountered an IO exception");
        } catch (JSONException var15) {
            JSONException e = var15;
            System.err.println("JSON exception: " + e.getMessage());
        }

        return histList;
    }
}
