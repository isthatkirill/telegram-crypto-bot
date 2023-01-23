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

import static isthatkirill.CryptoBot.service.Constant.*;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    final BotConfig config;
    Parser parser;

    public TelegramBot(BotConfig config) {

        parser = new Parser();

        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", START_COMMAND_TEXT));
        listOfCommands.add(new BotCommand("/top10", TOP10_COMMAND_TEXT));
        listOfCommands.add(new BotCommand("/favourite", FAVOURITE_COMMAND_TEXT));
        listOfCommands.add(new BotCommand("/gainers", GAINERS_COMMAND_TEXT));
        listOfCommands.add(new BotCommand("/losers", LOSERS_COMMAND_TEXT));
        listOfCommands.add(new BotCommand("/news", NEWS_COMMAND_TEXT));
        listOfCommands.add(new BotCommand("/help", HELP_COMMAND_TEXT));
        listOfCommands.add(new BotCommand("/settings", SETTINGS_COMMAND_TEXT));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(ERROR + e.getMessage());
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
                String name = update.getMessage().getChat().getFirstName();
                sendMessage(chatId, EmojiParser.parseToUnicode("Hello, " + name + ", " + HI_MESSAGE), update);
                log.info("[/start] " + REPLYED_TO_USER + name);

            } else if ("/help".equals(messageText)) {
                inlineButtonCall(chatId, SHOW_AUTHOR);
                log.info("[/help] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else if ("/top10".equals(messageText)) {
                inlineButtonCall(chatId, MORE_CRYPTO);
                log.info("[/top10] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else if ("/gainers".equals(messageText)) {
                inlineButtonCall(chatId, MORE_GAINERS);
                log.info("[/gainers] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else if ("/losers".equals(messageText)) {
                inlineButtonCall(chatId, MORE_LOSERS);
                log.info("[/losers] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else if ("Show author".equals(messageText)) {
                sendMessage(chatId, AUTHOR_INFO, update);
                log.info("[Show author] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else if ("Go back".equals(messageText)) {
                sendMessage(chatId, RECOMMEND_PRESSING_BUTTON, update);
                log.info("[Show author] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else if ("/news".equals(messageText)) {
                sendMessage(chatId, parser.news(), update);
                log.info("[/news] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else if ("/settings".equals(messageText)) {
                sendMessage(chatId, RECOMMEND_PRESSING_BUTTON, update);
                log.info("[/settings] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else if ("/favourite".equals(messageText)) {
                User user = userRepository.findById(update.getMessage().getChatId()).get();
                if (user.getCrypto().length() == 0) {
                    sendMessage(chatId, COMMAND_FAVOURITE_LIST_EMPTY, update);
                } else {
                    sendMessage(chatId, parser.favCrypto(user), update);
                }
                log.info("[/favourite] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else if ("Add crypto in favourite list".equals(messageText)) {

                inlineButtonCall(chatId, SHOW_AVAILABLE);
                log.info("[Add crypto in favourite list] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else if ("Clear favourite list".equals(messageText)) {
                clearList(update);
                sendMessage(chatId, LIST_CLEARED, update);
                log.info("[Clear favourite list] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else if ("Show favourite list".equals(messageText)) {
                if (showList(update).length() == 0) {
                    sendMessage(chatId, LIST_EMPTY, update);
                } else {
                    sendMessage(chatId, showList(update), update);
                }
                log.info("[Show favourite list] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else if (parser.getLinks().containsKey(messageText)) {
                String textToSend;
                if (addInFavList(update)) {
                    textToSend = messageText + LIST_SAVED;
                } else {
                    textToSend = messageText + ALREADY_IN_LIST;
                }
                sendMessage(chatId, textToSend, update);
                log.info("[BTC/ETH etc] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());

            } else {
                sendMessage(chatId, NO_COMMAND, update);
                log.info("[no command] " + REPLYED_TO_USER + update.getMessage().getChat().getFirstName());
            }

        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            EditMessageText editedMessage = new EditMessageText();
            editedMessage.setChatId(String.valueOf(chatId));
            editedMessage.setMessageId((int) messageId);

            switch (callBackData) {
                case SHOW_AVAILABLE -> editedMessage.setText(AVAILABLE_CRYPTO);
                case SHOW_AUTHOR -> editedMessage.setText(AUTHOR_INFO);
                case MORE_CRYPTO -> editedMessage.setText(parser.mostPopularCrypto(100));
                case MORE_GAINERS -> editedMessage.setText(parser.gainersAndLosers(25));
                case MORE_LOSERS -> editedMessage.setText(parser.gainersAndLosers(-25));
            }

            try {
                execute(editedMessage);
            } catch (TelegramApiException e) {
                log.error(ERROR + e.getMessage());
            }

        }
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

    private void sendMessage(long chatId, String textToSend, Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        showButtons(update, message);
        executeMessage(message);
    }

    private void showButtons(Update update, SendMessage message) {

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setIsPersistent(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        if (update.getMessage().getText().equals("/settings")) {
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

    private void inlineButtonCall(long chatId, String action) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        switch (action) {
            case MORE_LOSERS -> {
                message.setText(parser.gainersAndLosers(-10));
                button.setCallbackData(MORE_LOSERS);
                button.setText(MORE_LOSERS_TEXT);
            }
            case MORE_GAINERS -> {
                message.setText(parser.gainersAndLosers(10));
                button.setText(MORE_GAINERS_TEXT);
                button.setCallbackData(MORE_GAINERS);
            }
            case SHOW_AVAILABLE -> {
                message.setText(TYPE_TOKEN_CODE);
                button.setText(SHOW_AVAILABLE_TEXT);
                button.setCallbackData(SHOW_AVAILABLE);
            }
            case MORE_CRYPTO -> {
                message.setText(parser.mostPopularCrypto(10));
                button.setText(MORE_CRYPTO_TEXT);
                button.setCallbackData(MORE_CRYPTO);
            }
            case SHOW_AUTHOR -> {
                message.setText(COMMAND_TEXT);
                button.setText(SHOW_AUTHOR_TEXT);
                button.setCallbackData(SHOW_AUTHOR);
            }
        }

        rowInLine.add(button);
        rowsInLine.add(rowInLine);

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR + e.getMessage());
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
}
