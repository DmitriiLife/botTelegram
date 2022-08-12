package ru.sh.promodo;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.LocalTime.*;

public class PomodoroBot extends TelegramLongPollingBot {

    private final ConcurrentHashMap<UserTimer, Long> userTimerRepository = new ConcurrentHashMap();

    enum TimerType {
        WORK, BREAK
    }

    record UserTimer(Instant userTimer, TimerType timerType) {
    }

    @Override
    public String getBotUsername() {
        return "Pomodoro";
    }

    @Override
    public String getBotToken() {
        return "5417246939:AAFY316gLImQ1JED4NiUcBossT7STXyoHcw";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        var args = update.getMessage().getText().split(" ");

        Instant workTime = Instant.now().plus(Long.parseLong(args[0]), ChronoUnit.MINUTES);
        Instant breakTime = workTime.plus(Long.parseLong(args[1]), ChronoUnit.MINUTES);
        userTimerRepository.put(new UserTimer(workTime, TimerType.WORK), update.getMessage().getChatId());
        System.out.printf("[%s] Размер коллекции %d", Instant.now().toString(), userTimerRepository.size());
        userTimerRepository.put(new UserTimer(breakTime, TimerType.BREAK), update.getMessage().getChatId());
        System.out.printf("[%s] Размер коллекции %d", Instant.now().toString(), userTimerRepository.size());
        sendMsg(update.getMessage().getChatId(), "Поставил таймер ");
    }

    public void checkTimer() throws InterruptedException {
        while (true) {
            System.out.println("Количество таймеров пользователя " + userTimerRepository.size() + " " + "в" + " " + now(ZoneId.of("Europe/Moscow")).truncatedTo(ChronoUnit.SECONDS).toString());
            userTimerRepository.forEach((timer, userId) -> {
                if (Instant.now().isAfter(timer.userTimer)) {
                    switch (timer.timerType) {
                        case WORK -> sendMsg(userId, "Пора отдыхать ");
                        case BREAK -> sendMsg(userId, "Таймер завершил свою работу ");
                    }
                    userTimerRepository.remove(timer);
                }
            });
            Thread.sleep(5000);
        }
    }

    private void sendMsg(Long chatId, String text) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

