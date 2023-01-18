package isthatkirill.CryptoBot.service;

import com.vdurmont.emoji.EmojiParser;
import isthatkirill.CryptoBot.config.BotConfig;
import isthatkirill.CryptoBot.model.User;
import isthatkirill.CryptoBot.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    final BotConfig config;
    static final String HELP_TEXT = "This bot is created by \n@isthatkirill.";
    static final String COMMAND_TEXT = "/top10 -  get statistics in 10 most popular crypto (price, 24h-changes)" +
            "\n\n/gainers - get top gainers (based on price movements in the last 24 hours)" +
            "\n\n/losers - get top losers (based on price movements in the last 24 hours)" +
            "\n\n/settings - set your preferences" +
            "\n\n/news - last news in crypto industry";
    Parser parser;
    static final String AVAILABLE_CRYPTO = new ParserInTime().getCryptoNames(false).toString();

    public TelegramBot(BotConfig config) {

        parser = new Parser();

        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Get a welcome message"));
        listOfCommands.add(new BotCommand("/top10", "Show statistics on 10 most popular crypto"));
        listOfCommands.add(new BotCommand("/favourite", "Show cryptocurrencies added in favourites"));
        listOfCommands.add(new BotCommand("/gainers", "Gainers,  based on price movements in the last 24 hours."));
        listOfCommands.add(new BotCommand("/losers", "Losers,  based on price movements in the last 24 hours."));
        listOfCommands.add(new BotCommand("/news", "News in crypto industry"));
        listOfCommands.add(new BotCommand("/help", "About commands"));
        listOfCommands.add(new BotCommand("/settings", "Set your preferences"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText)) {
                registerUser(update.getMessage());
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName(), update);

            } else if ("/help".equals(messageText)) {
                sendMessage(chatId, COMMAND_TEXT, update);
                log.info("[/help] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("Show more".equals(messageText)) {
                sendMessage(chatId, parser.mostPopularCrypto(100), update);
                log.info("[Show more] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("/top10".equals(messageText)) {
                sendMessage(chatId, parser.mostPopularCrypto(10), update);
                log.info("[/top10] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("/gainers".equals(messageText)) {
                sendMessage(chatId, parser.gainersAndLosers(10), update);
                log.info("[/gainers] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("Show more gainers".equals(messageText)) {
                sendMessage(chatId, parser.gainersAndLosers(25), update);
                log.info("[Show more gainers] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("/losers".equals(messageText)) {
                sendMessage(chatId, parser.gainersAndLosers(-10), update);
                log.info("[/losers] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("Show more losers".equals(messageText)) {
                sendMessage(chatId, parser.gainersAndLosers(-25), update);
                log.info("[Show more losers] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("Show author".equals(messageText)) {
                sendMessage(chatId, HELP_TEXT, update);
                log.info("[Show author] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("Go back".equals(messageText)) {
                sendMessage(chatId, "Type command or press button -->", update);
                log.info("[Show author] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("/news".equals(messageText)) {
                sendMessage(chatId, parser.news(), update);
                log.info("[/news] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("/settings".equals(messageText)) {
                sendMessage(chatId, "Select an action -->", update);
                log.info("[/settings] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("/favourite".equals(messageText)) {
                User user = userRepository.findById(update.getMessage().getChatId()).get();
                if (user.getCrypto().length() == 0) {
                    sendMessage(chatId, "Your favourite list is empty.", update);
                } else {
                    sendMessage(chatId, parser.favCrypto(user), update);
                }
                log.info("[] Replied/favourite to user " + update.getMessage().getChat().getFirstName());

            } else if ("Add crypto in favourite list".equals(messageText)) {

                showAvailableCryptoList(chatId);
                log.info("[Add crypto in favourite list] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("Clear favourite list".equals(messageText)) {
                clearList(update);
                sendMessage(chatId, "Favourite list have been cleared.", update);
                log.info("[Clear favourite list] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("Show favourite list".equals(messageText)) {
                if (showList(update).length() == 0) {
                    sendMessage(chatId, "Favourite list is empty.", update);
                } else {
                    sendMessage(chatId, showList(update), update);
                }
                log.info("[Show favourite list] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if (parser.getLinks().containsKey(messageText)) {
                String textToSend;
                if (addInFavList(update)) {
                    textToSend = messageText + " saved in your favourite list.";
                } else {
                    textToSend = messageText + " already in your favourite list.";
                }
                sendMessage(chatId, textToSend, update);
                log.info("[BTC] Replied to user " + update.getMessage().getChat().getFirstName());

            } else {
                sendMessage(chatId, "Sorry, there is no such command! ", update);
                log.info("[no command] Replied to user " + update.getMessage().getChat().getFirstName());
            }
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callBackData.equals("SHOW_BUTTON")) {
                String text = " ok";
                EditMessageText editedMessage = new EditMessageText();
                editedMessage.setChatId(String.valueOf(chatId));
                editedMessage.setText(AVAILABLE_CRYPTO);
                editedMessage.setMessageId((int) messageId);

                try {
                    execute(editedMessage);
                } catch (TelegramApiException e) {
                    log.error("Error occurred: " + e.getMessage());
                }
            }
        }
    }

    private void showAvailableCryptoList(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Type cryptocurrency code (BTC, ETH, etc).");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText("Show available cryptocurrency list.");
        button.setCallbackData("SHOW_BUTTON");

        rowInLine.add(button);
        rowsInLine.add(rowInLine);

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }

    }

    private String showList(Update update) {
        return userRepository.findById(update.getMessage().getChatId()).get().getCrypto();
    }

    private void clearList(Update update) {
        User user = userRepository.findById(update.getMessage().getChatId()).get();
        user.setCrypto("");
        userRepository.save(user);
    }

    private boolean addInFavList(Update update) {
        boolean flag = false;
        User user = userRepository.findById(update.getMessage().getChatId()).get();

        String beforeUpdate = user.getCrypto();
        if (!beforeUpdate.contains(update.getMessage().getText())) {
            user.setCrypto(beforeUpdate + update.getMessage().getText() + ",");
            flag = true;
        } else if (beforeUpdate == null) {
            user.setCrypto(update.getMessage().getText() + ",");
        }
        userRepository.save(user);
        return flag;
    }

    private void registerUser(Message message) {

        if (userRepository.findById(message.getChatId()).isEmpty()) {
            var chatId = message.getChatId();
            var chat = message.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            user.setCrypto("");

            userRepository.save(user);
            log.info("User saved: " + user);
        }
    }

    private void startCommandReceived(long chatId, String name, Update update) {
        String answer = EmojiParser.parseToUnicode("Hello, " + name + ", i'm a CryptoStatistics bot!" + " :relaxed:" +
                "\nType /help to see available commands. ");
        log.info("[/start] Received from user " + name);
        sendMessage(chatId, answer, update);
        log.info("[/start] Replied to user " + name);
    }

    private void sendMessage(long chatId, String textToSend, Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        showButtons(update, message);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private void showButtons(Update update, SendMessage message) {

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setIsPersistent(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        if (update.getMessage().getText().equals("/top10")) {
            row.add("Show more");
            row.add("Go back");
            keyboardRows.add(row);
        } else if (update.getMessage().getText().equals("/help")) {
            row.add("Show author");
            row.add("Go back");
            keyboardRows.add(row);
        } else if (update.getMessage().getText().equals("/gainers")) {
            row.add("Show more gainers");
            row.add("Go back");
            keyboardRows.add(row);
        } else if (update.getMessage().getText().equals("/losers")) {
            row.add("Show more losers");
            row.add("Go back");
            keyboardRows.add(row);
        } else if (update.getMessage().getText().equals("/settings")) {
            row.add("Add crypto in favourite list");
            row.add("Clear favourite list");
            keyboardRows.add(row);
            row = new KeyboardRow();
            row.add("Show favourite list");
            row.add("Go back");
            keyboardRows.add(row);
        } else {
            row.add("/top10");
            row.add("/gainers");
            row.add("/losers");
            row.add("/favourite");
            keyboardRows.add(row);
            row = new KeyboardRow();
            row.add("/help");
            row.add("/settings");
            row.add("/news");

            keyboardRows.add(row);
        }
        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);
    }
}
