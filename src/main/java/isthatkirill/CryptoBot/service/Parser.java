package isthatkirill.CryptoBot.service;

import com.vdurmont.emoji.EmojiParser;
import isthatkirill.CryptoBot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static isthatkirill.CryptoBot.service.Constant.ParserConstants.NEWS_URL;

@Slf4j
public class Parser {

    private final HashMap<String, String> links = new HashMap<>();
    private final LinkedHashMap<String, ArrayList<String>> cryptoMap = new LinkedHashMap<>();
    private final ParserInTime parserInTime = new ParserInTime();
    private ArrayList<String> name;
    private ArrayList<String> price;
    private ArrayList<String> data24h;

    public ArrayList<String> getName() {
        return name;
    }

    public String getStringAvailableCryptoCurrency() {
        System.out.println(name.toString());
        return name.toString();
    }

    public Parser() {

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
        String textToSend = "";
        ArrayList<String> userCrypto = new ArrayList<>(Arrays.asList(user.getCrypto().split(",")));

        for (String each : userCrypto) {
            if (cryptoMap.get(each).get(1).startsWith("-")) {
                textToSend = textToSend + EmojiParser.parseToUnicode(":red_circle: ") + each + ": " +
                        cryptoMap.get(each).get(0) + " (" + cryptoMap.get(each).get(1) + ")\n\n";
            } else {
                textToSend = textToSend + EmojiParser.parseToUnicode("\uD83D\uDFE2 ") + each + ": " +
                        cryptoMap.get(each).get(0) + " (" + cryptoMap.get(each).get(1) + ")\n\n";
            }
        }
        return textToSend;
    }

    public String news() {
        String textToSend = "";
        Elements titles = parserInTime.getNewsInfo();

        for (Element element : titles) {
            textToSend += element.text() + "\n" +  NEWS_URL + element.attr("href") + "\n\n";
        }

        return textToSend;
    }

    public HashMap<String, String> getLinks() {
        return links;
    }

    private void parseHrefs() {
        ArrayList<String> title = parserInTime.getCryptoNames(false);
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
            ArrayList<String> temp = new ArrayList<>();
            temp.add(price.get(i));
            temp.add(data24h.get(i));
            cryptoMap.put(name.get(i), temp);
        }
    }

    private String display(int quantity) {

        String textToSend = "";

        if (quantity > 0) {
            for (int i = 0; i < quantity; i++) {
                if (data24h.get(i).startsWith("-")) {
                    textToSend = textToSend + EmojiParser.parseToUnicode(":red_circle: ") + name.get(i) + ": " +
                            price.get(i) + " (" + data24h.get(i) + ")\n\n";
                } else {
                    textToSend = textToSend + EmojiParser.parseToUnicode("\uD83D\uDFE2 ") + name.get(i) +
                            ": " + price.get(i) + " (" + data24h.get(i) + ")\n\n";
                }
            }
        } else if (quantity < 0) {
            for (int i = name.size() / 2; i < name.size() / 2 - quantity; i++) {
                textToSend = textToSend + EmojiParser.parseToUnicode(":red_circle: ") + name.get(i) + ": " +
                        price.get(i) + " (" + data24h.get(i) + ")\n\n";
            }
        }

        return textToSend;
    }


}
