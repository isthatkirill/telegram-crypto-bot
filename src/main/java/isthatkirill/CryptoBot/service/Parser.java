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

        String textToSend = "";

        try {
            Document document = Jsoup.connect("https://www.coingecko.com/").get();

            ArrayList<String> name = new ArrayList<>(Arrays.asList((document.getElementsByAttributeValue("class",
                    "d-lg-inline " + "font-normal text-3xs tw-ml-0 md:tw-ml-2 md:tw-self-center tw-text-gray-500 " +
                            "dark:tw-text-white dark:tw-text-opacity-60").text()).split(" ")));

            ArrayList<String> price = new ArrayList<String>(Arrays.asList((document.getElementsByAttribute("data" +
                    "-coin-symbol").text()).split("  ")));

            for (int i = 0; i < quantity; i++) {
                textToSend = textToSend + name.get(i) + ": " + price.get(i) + "\n";
            }

        } catch (Exception e) {
            log.error("Error while parsing: " + e.getMessage());
        }
        return textToSend;
    }

}
