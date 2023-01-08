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
        parser.mostPopularCrypto();


        listOfCommands.add(new BotCommand("/start", "Get a welcome message"));
        listOfCommands.add(new BotCommand("/top100", "Show statistics on popular crypto"));
        listOfCommands.add(new BotCommand("/deletedata", "delete your data"));
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

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    log.info("[/help] Replied to user " + update.getMessage().getChat().getFirstName());
                    break;

                case "/top100":
                    sendMessage(chatId, "Enter quantity of cryptocurrencies: ");
                    //onUpdateReceivedEmbedded()
                    sendMessage(chatId, parser.mostPopularCrypto());
                    log.info("[/top100] Replied to user " + update.getMessage().getChat().getFirstName());
                    break;

                default:
                    sendMessage(chatId, "Sorry, there is no such command! ");
                    log.info("[no command] Replied to user " + update.getMessage().getChat().getFirstName());

            }
        }
    }

    public String onUpdateReceivedEmbedded(Update update) {
        String embeddedText = "";
        if (update.hasMessage() && update.getMessage().hasText()) {
            embeddedText = update.getMessage().getText();
        }
        return embeddedText;
    }

    private void startCommandReceived(long chatId, String name)  {
        String answer = "Hi, " + name + " , nice to meet you!";
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
