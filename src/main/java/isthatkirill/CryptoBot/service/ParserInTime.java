package isthatkirill.CryptoBot.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static isthatkirill.CryptoBot.service.Constant.ERROR;
import static isthatkirill.CryptoBot.service.Constant.ParserConstants.*;

@Slf4j
public class ParserInTime {

    private Document connectTo(String link) {
        try {
            return Jsoup.connect(link).get();

        } catch (Exception e) {
            log.error(ERROR + e.getMessage());
            return null;
        }
    }

    public ArrayList<String> getCryptoNames(Boolean isGainer) {
        if (isGainer) {
            return new ArrayList<>(Arrays.asList((Objects.requireNonNull(connectTo(gainersAndLosersURL))
                    .getElementsByAttributeValue("class", NAMES_PATH).text()).split(" ")));
        } else {
            return new ArrayList<>(Arrays.asList((Objects.requireNonNull(connectTo(topCryptoURL))
                    .getElementsByAttributeValue("class", NAMES_PATH).text()).split(" ")));
        }
    }

    public ArrayList<String> getCryptoPrices(Boolean isGainer) {
        if (isGainer) {
            return new ArrayList<>(Arrays.asList((Objects.requireNonNull(connectTo(gainersAndLosersURL)).
                    getElementsByAttribute(PRICE_PATH).text()).split("  ")));
        } else {
            return new ArrayList<>(Arrays.asList((Objects.requireNonNull(connectTo(topCryptoURL)).
                    getElementsByAttribute(PRICE_PATH).text()).split("  ")));
        }
    }

    public ArrayList<String> getCrypto24hChange(Boolean isGainer) {
        if (isGainer) {
            return new ArrayList<>(Arrays.asList((Objects.requireNonNull(connectTo(gainersAndLosersURL))
                    .getElementsByAttributeValue(CHANGES_PER_DAY_PATH, "true").text()).split(" ")));
        } else {
            return new ArrayList<>(Arrays.asList((Objects.requireNonNull(connectTo(topCryptoURL))
                    .getElementsByAttributeValue(CHANGES_PER_DAY_PATH, "true").text()).split(" ")));
        }
    }

    public Elements getNewsInfo() {
        return Objects.requireNonNull(connectTo(newsURL)).select(NEWS_PATH);
    }

    public Elements getHref() {
        return Objects.requireNonNull(connectTo(topCryptoURL)).getElementsByAttributeValue("class", TOKEN_URL);
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
