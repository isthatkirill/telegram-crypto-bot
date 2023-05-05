package isthatkirill.cryptoBot.util;

import com.vdurmont.emoji.EmojiParser;
import isthatkirill.cryptoBot.service.ParserInTime;

public final class Constant {
    public static final String AUTHOR_INFO = "This bot is created by \n@isthatkirill.";
    public static final String COMMAND_TEXT = """
            /top10 -  get statistics on 10 most popular crypto (price, 24h-changes)

            /gainers - get top gainers (based on price movements in the last 24 hours)

            /losers - get top losers (based on price movements in the last 24 hours)

            /favourite - get statistics on your favourite tokens

            /settings - set your favourite tokens list

            /news - last news in crypto industry""";
    public static final String AVAILABLE_CRYPTO = new ParserInTime().getAvailableList();
    public static final String SHOW_AVAILABLE = "SHOW_AVAILABLE";
    public static final String SHOW_AVAILABLE_TEXT = "Show available cryptocurrency list";
    public static final String SHOW_AUTHOR = "SHOW_AUTHOR";
    public static final String SHOW_AUTHOR_TEXT = "Show author";
    public static final String MORE_GAINERS = "MORE_GAINERS";
    public static final String MORE_GAINERS_TEXT = "Show more gainers";
    public static final String MORE_LOSERS = "MORE_LOSERS";
    public static final String MORE_LOSERS_TEXT = "Show more losers";
    public static final String MORE_CRYPTO = "MORE_CRYPTO";
    public static final String MORE_CRYPTO_TEXT = "Show more";
    public static final String START_COMMAND_TEXT = "Start";
    public static final String TOP10_COMMAND_TEXT = "Show statistics on 10 most popular crypto";
    public static final String FAVOURITE_COMMAND_TEXT = "Show cryptocurrencies added in favourites";
    public static final String GAINERS_COMMAND_TEXT = "Gainers, based on price movements in the last 24 hours.";
    public static final String LOSERS_COMMAND_TEXT = "Losers, based on price movements in the last 24 hours.";
    public static final String NEWS_COMMAND_TEXT = "News in crypto industry";
    public static final String HELP_COMMAND_TEXT = "Information about available commands";
    public static final String SETTINGS_COMMAND_TEXT = "Set favourite token list";
    public static final String RECOMMEND_PRESSING_BUTTON = "Type command or press button -->";
    public static final String COMMAND_FAVOURITE_LIST_EMPTY = "Your favourite list is empty. Please use /settings to set " +
            "your favourite tokens list.";
    public static final String LIST_CLEARED = "Favourite list have been cleared.";
    public static final String LIST_EMPTY = "Favourite list is empty.";
    public static final String LIST_SAVED = " saved in your favourite list.";
    public static final String ALREADY_IN_LIST = " already in your favourite list.";
    public static final String NO_COMMAND = "Sorry, there is no such command! ";
    public static final String REPLYED_TO_USER = "Replied to user";
    public static final String HI_MESSAGE = EmojiParser.parseToUnicode("i'm a CryptoStatistics bot!" + " :relaxed:" +
            "\nType /help to see available commands. ");
    public static final String ERROR = "Error occurred: ";
    public static final String TYPE_TOKEN_CODE = "Type cryptocurrency code (BTC, ETH, etc).";

    public static final class ParserConstants {
        public static final String TOP_CRYPTO_URL = "https://www.coingecko.com/";
        public static final String GAINERS_AND_LOSERS_URL = "https://www.coingecko.com/en/crypto-gainers-losers";
        public static final String NEWS_URL = "https://cryptonews.net/ru/";
        public static final String NAMES_PATH = "d-lg-inline font-normal text-3xs tw-ml-0 md:tw-ml-2 md:tw-self-center " +
                "tw-text-gray-500 dark:tw-text-white dark:tw-text-opacity-60";
        public static final String PRICE_PATH = "data-coin-symbol";
        public static final String NEWS_PATH = "body > main > div.container > div.content.row > section.col-xs-12.col-sm > " +
                "div.row.news-item.start-xs > div.desc.col-xs > a.title";
        public static final String CHANGES_PER_DAY_PATH = "data-24h";
        public static final String TOKEN_URL =  "tw-flex tw-items-start md:tw-flex-row tw-flex-col";
    }
}
