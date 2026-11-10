package org.skypro;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.skypro.Recommendations.RecommendationsDTO;
import org.skypro.Repository.BankRepository;
import org.skypro.Service.BankService;
import org.springframework.stereotype.Component;
import com.pengrad.telegrambot.model.Update;
import java.util.List;

@Component
public class RecommendationBot implements UpdatesListener {

    private final TelegramBot bot;
    private final BankRepository repository;
    private final BankService bankService;

    public RecommendationBot(TelegramBot bot, BankRepository repository, BankService bankService) {
        this.bot = bot;
        this.repository = repository;
        this.bankService = bankService;
    }

    @PostConstruct
    public void init() {
        bot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            if (update.message() == null || update.message().text() == null) continue;
            long chatId = update.message().chat().id();
            String text = update.message().text().trim();

            if (text.equals("/start")) {
                send(chatId, "Здравствуйте! Я бот рекомендаций.\nКоманда: /recommend username");
            } else if (text.startsWith("/recommend")) {
                send(chatId, buildAnswer(text.substring("/recommend".length()).trim()));
            } else {
                send(chatId, "Неизвестная команда. Используйте: /recommend username");
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private String buildAnswer(String username) {
        List<UserInfo> users = username.isEmpty()
                ? List.of() : repository.findUsersByUsername(username);
        if (users.size() != 1) {
            return "Пользователь не найден";
        }
        UserInfo user = users.get(0);
        List<RecommendationsDTO> recs = bankService.getRecommendationsByUUID(user.id());

        StringBuilder sb = new StringBuilder("Здравствуйте ")
                .append(user.firstName()).append(" ").append(user.lastName()).append("\n")
                .append("Новые продукты для вас:\n");
        for (RecommendationsDTO r : recs) {
            sb.append("\n• ").append(r.name()).append("\n").append(r.text()).append("\n");
        }
        return sb.toString();
    }

    private void send(long chatId, String message) {
        bot.execute(new SendMessage(chatId, message));
    }
}