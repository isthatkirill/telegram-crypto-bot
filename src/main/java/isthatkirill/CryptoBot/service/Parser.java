package isthatkirill.CryptoBot.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;

@Slf4j
public class Parser {

    public Parser() {
    }

    public String mostPopularCrypto (int quantity) {

        try {
            Document document = Jsoup.connect("https://www.coingecko.com/").get();
            return parse(document, quantity);

        } catch (Exception e) {
            log.error("Error while parsing: " + e.getMessage());
            return null;
        }

    }

    public String gainersAndLosers (int quantity) {

        try {
            Document document = Jsoup.connect("https://www.coingecko.com/en/crypto-gainers-losers").get();
            return parse(document, quantity);

        } catch (Exception e) {
            log.error("Error while parsing: " + e.getMessage());
            return null;
        }

    }

    public void makingUrl () {

        try {
            Document document = Jsoup.connect("https://www.coingecko.com/").get();
            ArrayList<String> name = new ArrayList<>(Arrays.asList((document.getElementsByAttributeValue("class",
                    "d-lg-inline font-normal text-3xs tw-ml-0 md:tw-ml-2 md:tw-self-center tw-text-gray-500 " +
                            "dark:tw-text-white dark:tw-text-opacity-60").text()).split(" ")));

            var da = document.getElementsByAttributeValue("class",
                    "d-lg-inline font-normal text-3xs tw-ml-0 md:tw-ml-2 md:tw-self-center tw-text-gray-500 " +
                            "dark:tw-text-white dark:tw-text-opacity-60").attr("href");

            System.out.println(da);


        } catch (Exception e) {
            log.error("Error while parsing: " + e.getMessage());
        }

    }

    public String parse(Document document, int quantity) {

        String textToSend = "";

        ArrayList<String> name = new ArrayList<>(Arrays.asList((document.getElementsByAttributeValue("class",
                "d-lg-inline font-normal text-3xs tw-ml-0 md:tw-ml-2 md:tw-self-center tw-text-gray-500 " +
                        "dark:tw-text-white dark:tw-text-opacity-60").text()).split(" ")));

        ArrayList<String> price = new ArrayList<>(Arrays.asList((document.getElementsByAttribute("data" +
                "-coin-symbol").text()).split("  ")));

        ArrayList<String> data24h = new ArrayList<>(Arrays.asList((document.getElementsByAttributeValue("data-" +
                "24h", "true").text()).split(" ")));
        
        if (quantity > 0) {
            for (int i = 0; i < quantity; i++) {
                textToSend = textToSend + name.get(i) + ": " + price.get(i) + " (" + data24h.get(i) + ")\n\n";
            }
        } else if (quantity < 0) {
            for (int i = name.size() / 2; i < name.size() / 2 - quantity; i++) {
                textToSend = textToSend + name.get(i) + ": " + price.get(i) + " (" + data24h.get(i) + ")\n\n";
            }
        }

        return textToSend;
    }

}
