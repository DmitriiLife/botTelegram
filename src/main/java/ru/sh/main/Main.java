package ru.sh.main;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.sh.echo.EchoBot;
import ru.sh.promodo.PomodoroBot;

public class Main {

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        EchoBot echoBot = new EchoBot();
        PomodoroBot pomodoroBot = new PomodoroBot();
        telegramBotsApi.registerBot(echoBot);
//        new Thread(() -> {
//            try {
//                pomodoroBot.checkTimer();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).run();
    }
}
