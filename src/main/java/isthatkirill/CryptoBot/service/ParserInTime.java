package isthatkirill.CryptoBot.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class ParserInTime {

    private final String topCryptoURL = "https://www.coingecko.com/";
    private final String gainersAndLosersURL = "https://www.coingecko.com/en/crypto-gainers-losers";
    private final String newsURL = "https://cryptonews.net/ru/";
    private final String eachCryptoURL = "https://www.coingecko.com/";


    private Document connectTo(String link) {
        try {
            Document document = Jsoup.connect(link).get();
            return document;

        } catch (Exception e) {
            log.error("Error while parsing: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<String> getCryptoNames(Boolean isGainer) {
        if (isGainer) {
            return new ArrayList<>(Arrays.asList((connectTo(gainersAndLosersURL).getElementsByAttributeValue("class",
                    "d-lg-inline font-normal text-3xs tw-ml-0 md:tw-ml-2 md:tw-self-center tw-text-gray-500 " +
                            "dark:tw-text-white dark:tw-text-opacity-60").text()).split(" ")));
        }
        else {
            return new ArrayList<>(Arrays.asList((connectTo(topCryptoURL).getElementsByAttributeValue("class",
                    "d-lg-inline font-normal text-3xs tw-ml-0 md:tw-ml-2 md:tw-self-center tw-text-gray-500 " +
                            "dark:tw-text-white dark:tw-text-opacity-60").text()).split(" ")));
        }
    }

    public ArrayList<String> getCryptoPrices(Boolean isGainer) {
        if (isGainer) {
            return new ArrayList<>(Arrays.asList((connectTo(gainersAndLosersURL).getElementsByAttribute("data" +
                    "-coin-symbol").text()).split("  ")));
        } else {
            return new ArrayList<>(Arrays.asList((connectTo(topCryptoURL).getElementsByAttribute("data" +
                    "-coin-symbol").text()).split("  ")));
        }
    }

    public ArrayList<String> getCrypto24hChange(Boolean isGainer) {
        if (isGainer) {
            return new ArrayList<>(Arrays.asList((connectTo(gainersAndLosersURL).getElementsByAttributeValue("data-" +
                    "24h", "true").text()).split(" ")));
        } else {
            return new ArrayList<>(Arrays.asList((connectTo(topCryptoURL).getElementsByAttributeValue("data-" +
                    "24h", "true").text()).split(" ")));
        }
    }

    public Elements getNewsInfo() {
        return connectTo(newsURL).select("body > main > div.container > div.content.row > " +
                "section.col-xs-12.col-sm > div.row.news-item.start-xs > div.desc.col-xs > a.title");
    }

    public Elements getHref() {
        return connectTo(topCryptoURL).getElementsByAttributeValue("class",
                "tw-flex tw-items-start md:tw-flex-row tw-flex-col");
    }

    public String getAvailableList() {
        String textToSend = "";
        int newLine = 0;
        ArrayList<String> tokenList = getCryptoNames(false);
        for (String tokenName : tokenList) {
            textToSend = textToSend + tokenName + " ";
            newLine++;
            if (newLine == 5) {
                textToSend = textToSend + "\n";
                newLine = 0;
            }
        }
        return textToSend;
    }


}
