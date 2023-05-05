package isthatkirill.cryptoBot.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static isthatkirill.cryptoBot.util.Constant.ERROR;
import static isthatkirill.cryptoBot.util.Constant.ParserConstants.*;

@Slf4j
@Service
public class ParserInTime {

    private Document connectTo(String link) {
        try {
            return Jsoup.connect(link).get();

        } catch (Exception e) {
            log.error(ERROR + e.getMessage());
            return null;
        }
    }

    private List<String> getCryptoData(String url, String attribute, String splitBy) {
        return new ArrayList<>(Arrays.asList((Objects.requireNonNull(connectTo(url))
                .getElementsByAttributeValue("class", attribute).text()).split(splitBy)));
    }

    public List<String> getCryptoNames(boolean isGainer) {
        if (isGainer) {
            return getCryptoData(GAINERS_AND_LOSERS_URL, NAMES_PATH, " ");
        } else {
            return getCryptoData(TOP_CRYPTO_URL, NAMES_PATH, " ");
        }
    }

    public List<String> getCryptoPrices(boolean isGainer) {
        if (isGainer) {
            return getCryptoData(GAINERS_AND_LOSERS_URL, PRICE_PATH, "  ");
        } else {
            return getCryptoData(TOP_CRYPTO_URL, PRICE_PATH, "  ");
        }
    }

    public List<String> getCrypto24hChange(boolean isGainer) {
        if (isGainer) {
            return getCryptoData(GAINERS_AND_LOSERS_URL, CHANGES_PER_DAY_PATH, " ");
        } else {
            return getCryptoData(TOP_CRYPTO_URL, CHANGES_PER_DAY_PATH, " ");
        }
    }

    public Elements getNewsInfo() {
        return Objects.requireNonNull(connectTo(NEWS_URL)).select(NEWS_PATH);
    }

    public Elements getHref() {
        return Objects.requireNonNull(connectTo(TOP_CRYPTO_URL)).getElementsByAttributeValue("class", TOKEN_URL);
    }

    public String getAvailableList() {
        List<String> tokenList = getCryptoNames(false);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokenList.size(); i++) {
            sb.append(tokenList.get(i)).append(" ");
            if ((i + 1) % 5 == 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
