package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.dto.LogDTO;
import com.example.budgetkeeperspring.dto.PurchaseInfoDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.CategoryCondition;
import com.example.budgetkeeperspring.entity.Circ;
import com.example.budgetkeeperspring.mapper.LogMapper;
import com.example.budgetkeeperspring.repository.LogRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Profile("!local")
@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQService {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final LogRepository logRepository;
    private final LogMapper logMapper;
    private final LogService logService;

    @RabbitListener(queues = "expense")
    public void listenExpenses(String in) {
        log.info("Received message: " + in);
        Gson g = new Gson();
        ExpenseDTO message = g.fromJson(in, ExpenseDTO.class);
        try {
            Optional<CategoryCondition> condition = categoryService.findMatchingCondition(message);
            Category category = condition.map(CategoryCondition::getCategory).orElseGet(categoryService::unknownCategory);
            ExpenseDTO saved = expenseService.createExpenseFromQueue(message, category);
            Long id = saved.getId();
            logService.expense(id, LogService.INFO, "mail z banku: %s | %s | %s".formatted(message.getTitle(), message.getPayee(), message.getAmount()));
            logService.expense(id, condition.isPresent() ? LogService.INFO : LogService.WARN, condition
                    .map(c -> "kategoria auto: %s (warunek %s)".formatted(category.getName(), describe(c.getCirc())))
                    .orElse("kategoria: żaden warunek nie pasuje do tytułu/odbiorcy – do ręcznego przypisania"));
            if (saved.getPlannedExpenseId() != null) {
                logService.expense(id, LogService.INFO, "plan auto: pozycja '%s' (#%d)".formatted(message.getTitle(), saved.getPlannedExpenseId()));
            } else if (message.getAmount().signum() < 0) {
                logService.expense(id, LogService.WARN, "plan: brak pozycji o nazwie '%s' w planie na ten miesiąc – do ręcznego przypisania".formatted(message.getTitle()));
            }
            log.info("Saved expense: " + saved);
        } catch (RuntimeException e) {
            logService.expense(null, LogService.ERROR, "mail z banku nieprzetworzony: %s | %s | %s: %s".formatted(message.getTitle(), message.getPayee(), message.getAmount(), e.getMessage()));
            throw e;
        }
    }

    private static String describe(Circ c) {
        if (c.getTitle() == null) {
            return "odbiorca~'" + c.getPayee() + "'";
        }
        if (c.getPayee() == null) {
            return "tytuł~'" + c.getTitle() + "'";
        }
        return "tytuł~'" + c.getTitle() + "' i odbiorca~'" + c.getPayee() + "'";
    }

    @RabbitListener(queues = "log")
    public void listenLogs(String in) {
        log.info("Received message: " + in);
        Gson g = new Gson();
        LogDTO logDto = g.fromJson(in, LogDTO.class);
        logRepository.save(logMapper.mapToEntity(logDto));
        log.info("Saved log: " + logDto);
    }

    @RabbitListener(queues = "purchase_info", containerFactory = "purchaseInfoListenerFactory")
    public void listenPurchaseInfo(String in) {
        log.info("Received purchase_info: " + in);
        Gson g = new Gson();
        PurchaseInfoDTO purchaseInfo = g.fromJson(in, PurchaseInfoDTO.class);
        boolean matched = expenseService.matchPurchaseInfo(purchaseInfo);
        if (!matched) {
            throw new RuntimeException("No matching expense yet for purchase: " + purchaseInfo.getName()
                    + ", price=" + purchaseInfo.getPrice()
                    + ", orderDate=" + purchaseInfo.getOrderDate());
        }
    }
}
