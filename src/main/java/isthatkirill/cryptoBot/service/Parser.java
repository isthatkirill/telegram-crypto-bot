package isthatkirill.cryptoBot.service;

import com.vdurmont.emoji.EmojiParser;
import isthatkirill.cryptoBot.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.*;

import static isthatkirill.cryptoBot.util.Constant.ParserConstants.NEWS_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class Parser {

    private final ParserInTime parserInTime;
    private final Map<String, String> links = new HashMap<>();
    private final Map<String, List<String>> cryptoMap = new LinkedHashMap<>();
    private List<String> name;
    private List<String> price;
    private List<String> data24h;

    public Parser() {
        this.parserInTime = new ParserInTime();
        parseHrefs();
    }

    public String mostPopularCrypto(int quantity) {
        parse(false);
        return display(quantity);
    }

    public String gainersAndLosers(int quantity) {
        parse(true);
        return display(quantity);
    }

    public String favCrypto(User user) {
        mostPopularCrypto(100);
        StringBuilder textToSend = new StringBuilder();
        List<String> userCrypto = new ArrayList<>(Arrays.asList(user.getCrypto().split(",")));

        for (String each : userCrypto) {
            String emoji = cryptoMap.get(each).get(1).startsWith("-") ? ":red_circle: " : "\uD83D\uDFE2 ";
            textToSend.append(EmojiParser.parseToUnicode(emoji)).append(each).append(": ")
                    .append(cryptoMap.get(each).get(0)).append(" (").append(cryptoMap.get(each).get(1)).append(")\n\n");
        }
        return textToSend.toString();
    }

    public String news() {
        StringBuilder textToSend = new StringBuilder();
        Elements titles = parserInTime.getNewsInfo();

        for (Element element : titles) {
            textToSend.append(element.text()).append("\n").append(NEWS_URL).append(element.attr("href")).append("\n\n");
        }

        return textToSend.toString();
    }

    public Map<String, String> getLinks() {
        return links;
    }

    private void parseHrefs() {
        List<String> title = parserInTime.getCryptoNames(false);
        Elements urlsInfo = parserInTime.getHref();

        for (int i = 0; i < title.size(); i++) {
            links.put(title.get(i), urlsInfo.get(i).attr("href"));
        }
    }

    private void parse(Boolean isGainer) {

        name = parserInTime.getCryptoNames(isGainer);
        price = parserInTime.getCryptoPrices(isGainer);
        data24h = parserInTime.getCrypto24hChange(isGainer);

        for (int i = 0; i < name.size(); i++) {
            List<String> temp = new ArrayList<>();
            temp.add(price.get(i));
            temp.add(data24h.get(i));
            cryptoMap.put(name.get(i), temp);
        }
    }

    private String display(int quantity) {

        StringBuilder textToSend = new StringBuilder();

        if (quantity > 0) {
            for (int i = 0; i < quantity; i++) {
                String emoji = data24h.get(i).startsWith("-") ? ":red_circle: " : "\uD83D\uDFE2 ";
                textToSend.append(EmojiParser.parseToUnicode(emoji)).append(name.get(i)).append(": ")
                        .append(price.get(i)).append(" (").append(data24h.get(i)).append(")\n\n");
            }
        } else if (quantity < 0) {
            for (int i = name.size() / 2; i < name.size() / 2 - quantity; i++) {
                textToSend.append(EmojiParser.parseToUnicode(":red_circle: ")).append(name.get(i)).append(": ")
                        .append(price.get(i)).append(" (").append(data24h.get(i)).append(")\n\n");
            }
        }
        return textToSend.toString();
    }

}
