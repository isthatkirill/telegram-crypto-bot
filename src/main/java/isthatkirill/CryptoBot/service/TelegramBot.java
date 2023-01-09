package isthatkirill.CryptoBot.service;

import isthatkirill.CryptoBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    static final String HELP_TEXT = "This bot is created by \n @isthatkirill.";
    Parser parser;


    public TelegramBot (BotConfig config) {

        parser = new Parser();
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        parser.gainers(10); //TODO

        listOfCommands.add(new BotCommand("/start", "Get a welcome message"));
        listOfCommands.add(new BotCommand("/top10", "Show statistics on 10 most popular crypto"));
        listOfCommands.add(new BotCommand("/showall", "Show statistics on top-100 crypto"));
        listOfCommands.add(new BotCommand("/gainers", "Gainers,  based on price movements in the last 24 hours."));
        listOfCommands.add(new BotCommand("/help", "about commands"));
        listOfCommands.add(new BotCommand("/settings", "set your preferences"));


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
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());

            } else if ("/help".equals(messageText)) {
                sendMessage(chatId, HELP_TEXT);
                log.info("[/help] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("/showall".equals(messageText)) {
                sendMessage(chatId, parser.mostPopularCrypto(100));
                log.info("[/showAll] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("/top10".equals(messageText)) {
                sendMessage(chatId, parser.mostPopularCrypto(10));
                log.info("[/top10] Replied to user " + update.getMessage().getChat().getFirstName());

            } else if ("/gainers".equals(messageText)) {
                sendMessage(chatId, parser.gainers(10));
            }
            else {
                sendMessage(chatId, "Sorry, there is no such command! ");
                log.info("[no command] Replied to user " + update.getMessage().getChat().getFirstName());
            }
        }
    }

    private void startCommandReceived(long chatId, String name)  {
        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("[/start] Received from user " + name);
        sendMessage(chatId, answer);
        log.info("[/start] Replied to user " + name);

    }

    private void sendMessage(long chatId, String textToSend)  {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }


}
