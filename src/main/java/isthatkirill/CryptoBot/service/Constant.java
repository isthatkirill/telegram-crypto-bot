package isthatkirill.CryptoBot.service;

import com.vdurmont.emoji.EmojiParser;

public final class Constant {
    static final String AUTHOR_INFO = "This bot is created by \n@isthatkirill.";
    static final String COMMAND_TEXT = "/top10 -  get statistics on 10 most popular crypto (price, 24h-changes)" +
            "\n\n/gainers - get top gainers (based on price movements in the last 24 hours)" +
            "\n\n/losers - get top losers (based on price movements in the last 24 hours)" +
            "\n\n/favourite - get statistics on your favourite tokens" +
            "\n\n/settings - set your favourite tokens list" +
            "\n\n/news - last news in crypto industry";
    static final String AVAILABLE_CRYPTO = new ParserInTime().getAvailableList();
    static final String SHOW_AVAILABLE = "SHOW_AVAILABLE";
    static final String SHOW_AVAILABLE_TEXT = "Show available cryptocurrency list";
    static final String SHOW_AUTHOR = "SHOW_AUTHOR";
    static final String SHOW_AUTHOR_TEXT = "Show author";
    static final String MORE_GAINERS = "MORE_GAINERS";
    static final String MORE_GAINERS_TEXT = "Show more gainers";
    static final String MORE_LOSERS = "MORE_LOSERS";
    static final String MORE_LOSERS_TEXT = "Show more losers";
    static final String MORE_CRYPTO = "MORE_CRYPTO";
    static final String MORE_CRYPTO_TEXT = "Show more";
    static final String START_COMMAND_TEXT = "Start";
    static final String TOP10_COMMAND_TEXT = "Show statistics on 10 most popular crypto";
    static final String FAVOURITE_COMMAND_TEXT = "Show cryptocurrencies added in favourites";
    static final String GAINERS_COMMAND_TEXT = "Gainers, based on price movements in the last 24 hours.";
    static final String LOSERS_COMMAND_TEXT = "Losers, based on price movements in the last 24 hours.";
    static final String NEWS_COMMAND_TEXT = "News in crypto industry";
    static final String HELP_COMMAND_TEXT = "Information about available commands";
    static final String SETTINGS_COMMAND_TEXT = "Set favourite token list";
    static final String RECOMMEND_PRESSING_BUTTON = "Type command or press button -->";
    static final String COMMAND_FAVOURITE_LIST_EMPTY = "Your favourite list is empty. Please use /settings to set " +
            "your favourite tokens list.";
    static final String LIST_CLEARED = "Favourite list have been cleared.";
    static final String LIST_EMPTY = "Favourite list is empty.";
    static final String LIST_SAVED = " saved in your favourite list.";
    static final String ALREADY_IN_LIST = " already in your favourite list.";
    static final String NO_COMMAND = "Sorry, there is no such command! ";
    static final String REPLYED_TO_USER = "Replied to user ";
    static final String HI_MESSAGE = EmojiParser.parseToUnicode("i'm a CryptoStatistics bot!" + " :relaxed:" +
            "\nType /help to see available commands. ");
    static final String ERROR = "Error occurred: ";
    static final String TYPE_TOKEN_CODE = "Type cryptocurrency code (BTC, ETH, etc).";

    private Constant() {
    }
}
