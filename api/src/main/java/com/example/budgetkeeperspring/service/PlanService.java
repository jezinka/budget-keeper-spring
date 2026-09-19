package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.*;
import com.example.budgetkeeperspring.entity.Expense;
import com.example.budgetkeeperspring.entity.Plan;
import com.example.budgetkeeperspring.entity.PlannedExpense;
import com.example.budgetkeeperspring.entity.RecurringPlannedExpense;
import com.example.budgetkeeperspring.exception.NotFoundException;
import com.example.budgetkeeperspring.mapper.ExpenseMapper;
import com.example.budgetkeeperspring.repository.ExpenseRepository;
import com.example.budgetkeeperspring.repository.PlanRepository;
import com.example.budgetkeeperspring.repository.PlannedExpenseRepository;
import com.example.budgetkeeperspring.repository.RecurringPlannedExpenseRepository;
import com.example.budgetkeeperspring.utils.FileService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.example.budgetkeeperspring.service.CategoryLevelService.INVESTMENT_CATEGORY_LEVEL;

@RequiredArgsConstructor
@Service
public class PlanService {
    private final PlanRepository planRepository;
    private final PlannedExpenseRepository plannedExpenseRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final RecurringPlannedExpenseRepository recurringPlannedExpenseRepository;

    public List<PlanDTO> findAll() {
        return planRepository.findAll().stream().map(this::toDto).toList();
    }

    public PlanDTO findById(Integer id) {
        return toDto(plan(id));
    }

    public PlanDTO create(PlanDTO dto) {
        YearMonth month = YearMonth.of(dto.getYear(), dto.getMonth());
        if (planRepository.findByStartDate(month.atDay(1)).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Plan already exists for this month");
        }
        Plan plan = planRepository.save(planFrom(dto, new Plan()));
        applyRecurringPayments(plan);
        return toDto(plan);
    }

    public PlanDTO update(Integer id, PlanDTO dto) {
        Plan plan = plan(id);
        YearMonth month = YearMonth.of(dto.getYear(), dto.getMonth());
        planRepository.findByStartDate(month.atDay(1))
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Plan already exists for this month");
                });
        return toDto(planRepository.save(planFrom(dto, plan)));
    }

    @Transactional
    public void delete(Integer id) {
        List<PlannedExpense> plannedExpenses = plannedExpenseRepository.findAllByPlanId(id);
        List<Expense> expenses = expenseRepository.findAllByPlannedExpensePlanId(id);
        expenses.forEach(expense -> expense.setPlannedExpense(null));
        expenseRepository.saveAll(expenses);
        plannedExpenseRepository.deleteAll(plannedExpenses);
        planRepository.delete(plan(id));
    }

    public List<PlannedExpenseDTO> findPlannedExpenses(Integer planId) {
        plan(planId);
        return plannedExpenseRepository.findAllByPlanId(planId).stream().map(this::toDto).toList();
    }

    public PlannedExpenseDTO findPlannedExpense(Integer id) {
        return toDto(plannedExpense(id));
    }

    public PlannedExpenseDTO createPlannedExpense(PlannedExpenseDTO dto) {
        return toDto(plannedExpenseRepository.save(plannedExpenseFrom(dto, new PlannedExpense())));
    }

    @Transactional
    public void importPlannedExpenses(Integer planId, MultipartFile file) {
        Plan plan = plan(planId);
        try (CSVReader reader = new CSVReader(new FileReader(FileService.getTempFile(file)))) {
            String[] header = reader.readNext();
            boolean hasDueDate = Arrays.equals(header, new String[]{"Kategoria", "Kwota", "Termin"});
            if (!Arrays.equals(header, new String[]{"Kategoria", "Kwota"}) && !hasDueDate) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV headers must be: Kategoria,Kwota[,Termin]");
            }
            List<PlannedExpense> plannedExpenses = new ArrayList<>();
            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length != (hasDueDate ? 3 : 2) || row[0].isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each CSV row must contain a name and amount");
                }
                BigDecimal amount = new BigDecimal(row[1].replace(",", "."));
                if (amount.signum() <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Planned expense amount must be positive");
                }
                PlannedExpense plannedExpense = new PlannedExpense();
                plannedExpense.setPlan(plan);
                plannedExpense.setName(row[0]);
                plannedExpense.setAmount(amount);
                if (hasDueDate && !row[2].isBlank()) {
                    plannedExpense.setDueDate(LocalDate.parse(row[2]));
                }
                plannedExpenses.add(plannedExpense);
            }
            plannedExpenseRepository.saveAll(plannedExpenses);
        } catch (IOException | CsvValidationException | NumberFormatException | DateTimeParseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid CSV file", exception);
        }
    }

    public PlannedExpenseDTO updatePlannedExpense(Integer id, PlannedExpenseDTO dto) {
        return toDto(plannedExpenseRepository.save(plannedExpenseFrom(dto, plannedExpense(id))));
    }

    public PlannedExpenseDTO updatePaid(Integer id, boolean paid) {
        PlannedExpense plannedExpense = plannedExpense(id);
        plannedExpense.setPaid(paid);
        return toDto(plannedExpenseRepository.save(plannedExpense));
    }

    public List<RecurringPlannedExpenseDTO> findRecurringPayments() {
        return recurringPlannedExpenseRepository.findAll().stream().map(this::toDto).toList();
    }

    public RecurringPlannedExpenseDTO createRecurringPayment(RecurringPlannedExpenseDTO dto) {
        return toDto(recurringPlannedExpenseRepository.save(recurringPaymentFrom(dto, new RecurringPlannedExpense())));
    }

    public RecurringPlannedExpenseDTO updateRecurringPayment(Integer id, RecurringPlannedExpenseDTO dto) {
        RecurringPlannedExpense recurringPayment = recurringPlannedExpenseRepository.findById(id).orElseThrow(NotFoundException::new);
        return toDto(recurringPlannedExpenseRepository.save(recurringPaymentFrom(dto, recurringPayment)));
    }

    public void deleteRecurringPayment(Integer id) {
        recurringPlannedExpenseRepository.deleteById(id);
    }

    @Transactional
    public RecurringPlannedExpenseDTO convertPlannedExpenseToRecurring(Integer plannedExpenseId) {
        PlannedExpense plannedExpense = plannedExpense(plannedExpenseId);
        if (plannedExpense.getRecurringPayment() != null) {
            return toDto(plannedExpense.getRecurringPayment());
        }
        RecurringPlannedExpense recurringPayment = createRecurringPayment(
                plannedExpense.getName(), plannedExpense.getAmount(),
                plannedExpense.getDueDate() == null ? plannedExpense.getPlan().getStartDate().getDayOfMonth() : plannedExpense.getDueDate().getDayOfMonth());
        plannedExpense.setRecurringPayment(recurringPayment);
        plannedExpenseRepository.save(plannedExpense);
        return toDto(recurringPayment);
    }

    @Transactional
    public RecurringPlannedExpenseDTO convertUnplannedExpenseToRecurring(Integer planId, Long expenseId) {
        Plan plan = plan(planId);
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(NotFoundException::new);
        validateExpenseForPlan(expense, plan);
        RecurringPlannedExpense recurringPayment = createRecurringPayment(
                expense.getTitle() == null || expense.getTitle().isBlank() ? "Wydatek cykliczny" : expense.getTitle(),
                expense.getAmount().abs(), expense.getTransactionDate().getDayOfMonth());
        PlannedExpense plannedExpense = plannedExpenseRepository.save(plannedExpenseFromRecurringPayment(plan, recurringPayment));
        expense.setPlannedExpense(plannedExpense);
        expenseRepository.save(expense);
        return toDto(recurringPayment);
    }

    @Transactional
    public void applyRecurringPayments(Integer planId) {
        applyRecurringPayments(plan(planId));
    }

    @Transactional
    public void deletePlannedExpense(Integer id) {
        PlannedExpense plannedExpense = plannedExpense(id);
        List<Expense> expenses = plannedExpense.getExpenses().stream().toList();
        expenses.forEach(expense -> expense.setPlannedExpense(null));
        expenseRepository.saveAll(expenses);
        plannedExpenseRepository.delete(plannedExpense);
    }

    @Transactional
    public PlannedExpenseDTO markExpenseAsPlanned(Integer planId, Long expenseId) {
        Plan plan = plan(planId);
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(NotFoundException::new);
        if (expense.getAmount().signum() >= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only an expense can be planned");
        }
        if (expense.getPlannedExpense() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Expense is already planned");
        }
        if (expense.getTransactionDate().isBefore(plan.getStartDate()) || expense.getTransactionDate().isAfter(plan.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expense date must be within the plan month");
        }
        PlannedExpense plannedExpense = new PlannedExpense();
        plannedExpense.setPlan(plan);
        plannedExpense.setAmount(expense.getAmount().abs());
        plannedExpense.setName(expense.getTitle());
        expense.setPlannedExpense(plannedExpenseRepository.save(plannedExpense));
        expenseRepository.save(expense);
        return toDto(expense.getPlannedExpense());
    }

    @Transactional
    public void assignExpenseToPlannedExpense(Integer plannedExpenseId, Long expenseId) {
        PlannedExpense plannedExpense = plannedExpense(plannedExpenseId);
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(NotFoundException::new);
        validateExpenseForPlan(expense, plannedExpense.getPlan());
        expense.setPlannedExpense(plannedExpense);
        expenseRepository.save(expense);
    }

    @Transactional
    public void assignExpensesToPlannedExpense(Integer plannedExpenseId, List<Long> expenseIds) {
        PlannedExpense plannedExpense = plannedExpense(plannedExpenseId);
        List<Expense> expenses = expenses(expenseIds);
        expenses.forEach(expense -> validateExpenseForPlan(expense, plannedExpense.getPlan()));
        expenses.forEach(expense -> expense.setPlannedExpense(plannedExpense));
        expenseRepository.saveAll(expenses);
    }

    @Transactional
    public PlannedExpenseDTO createPlannedExpenseFromExpenses(Integer planId, List<Long> expenseIds) {
        Plan plan = plan(planId);
        List<Expense> expenses = expenses(expenseIds);
        expenses.forEach(expense -> validateExpenseForPlan(expense, plan));
        PlannedExpense plannedExpense = new PlannedExpense();
        plannedExpense.setPlan(plan);
        plannedExpense.setName("Wybrane wydatki");
        plannedExpense.setAmount(expenses.stream().map(Expense::getAmount).map(BigDecimal::abs).reduce(BigDecimal.ZERO, BigDecimal::add));
        plannedExpenseRepository.save(plannedExpense);
        expenses.forEach(expense -> expense.setPlannedExpense(plannedExpense));
        expenseRepository.saveAll(expenses);
        return toDto(plannedExpense);
    }

    @Transactional
    public void unassignExpenseFromPlannedExpense(Integer plannedExpenseId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(NotFoundException::new);
        if (expense.getPlannedExpense() == null || !expense.getPlannedExpense().getId().equals(plannedExpenseId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Expense is not assigned to this planned expense");
        }
        expense.setPlannedExpense(null);
        expenseRepository.save(expense);
    }

    public List<ExpenseDTO> findExpensesForPlan(Integer planId) {
        plan(planId);
        return expenseRepository.findAllByPlannedExpensePlanId(planId).stream().map(expenseMapper::mapToDto).toList();
    }

    public List<ExpenseDTO> findUnplannedExpenses(int year, int month) {
        YearMonth selectedMonth = YearMonth.of(year, month);
        return withoutInvestments(expenseRepository.findAllUnplannedByTransactionDateBetween(selectedMonth.atDay(1), selectedMonth.atEndOfMonth()))
                .stream().map(expenseMapper::mapToDto).toList();
    }

    public PlanSummaryDTO summary(int year, int month) {
        YearMonth selectedMonth = YearMonth.of(year, month);
        LocalDate begin = selectedMonth.atDay(1);
        LocalDate end = selectedMonth.atEndOfMonth();
        Plan plan = planRepository.findByStartDate(begin).orElse(null);
        List<PlannedExpense> plannedExpenses = plan == null ? List.of() : plannedExpenseRepository.findAllByPlanId(plan.getId());
        List<Expense> plannedTransactions = plan == null ? List.of() :
                withoutInvestments(expenseRepository.findAllPlannedByTransactionDateBetweenAndPlanId(begin, end, plan.getId()));
        List<Expense> unplannedExpenses = withoutInvestments(expenseRepository.findAllUnplannedByTransactionDateBetween(begin, end));

        BigDecimal plannedAmount = expenseAmount(plannedTransactions);
        BigDecimal unplannedAmount = expenseAmount(unplannedExpenses);
        BigDecimal paidPlannedAmount = plannedExpenses.stream().filter(PlannedExpense::isPaid)
                .map(PlannedExpense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingPlannedAmount = plannedExpenses.stream().filter(plannedExpense -> !plannedExpense.isPaid())
                .map(PlannedExpense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        long paidPlannedCount = plannedExpenses.stream().filter(PlannedExpense::isPaid).count();
        long remainingPlannedCount = plannedExpenses.size() - paidPlannedCount;
        Map<Long, CategoryPlanSummaryDTO> categories = new LinkedHashMap<>();
        addActualAmounts(categories, plannedTransactions, true);
        addActualAmounts(categories, unplannedExpenses, false);
        addPlannedAmounts(categories, plannedExpenses, plannedTransactions);

        return new PlanSummaryDTO(
                plan == null ? null : toDto(plan),
                plannedExpenses.stream().map(this::toDto).toList(),
                plannedTransactions.stream().map(expenseMapper::mapToDto).toList(),
                unplannedExpenses.stream().map(expenseMapper::mapToDto).toList(),
                paidPlannedAmount,
                remainingPlannedAmount,
                paidPlannedCount,
                remainingPlannedCount,
                plannedAmount,
                unplannedAmount,
                List.of(new PieChartExpenseDto("Planned", plannedAmount), new PieChartExpenseDto("Unplanned", unplannedAmount)),
                categories.values().stream().sorted(Comparator.comparing(CategoryPlanSummaryDTO::getCategoryName)).toList());
    }

    private void addActualAmounts(Map<Long, CategoryPlanSummaryDTO> categories, List<Expense> expenses, boolean planned) {
        expenses.stream().filter(expense -> expense.getCategory() != null && expense.getAmount().signum() < 0).forEach(expense -> {
            var category = expense.getCategory();
            CategoryPlanSummaryDTO summary = categories.computeIfAbsent(category.getId(), id ->
                    new CategoryPlanSummaryDTO(id, category.getName(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
            if (planned) {
                summary.setPlannedExpenseAmount(summary.getPlannedExpenseAmount().add(expense.getAmount().abs()));
            } else {
                summary.setUnplannedExpenseAmount(summary.getUnplannedExpenseAmount().add(expense.getAmount().abs()));
            }
        });
    }

    private void addPlannedAmounts(Map<Long, CategoryPlanSummaryDTO> categories, List<PlannedExpense> plannedExpenses,
                                   List<Expense> plannedTransactions) {
        plannedExpenses.forEach(plannedExpense -> {
            List<Expense> expenses = plannedTransactions.stream()
                    .filter(expense -> expense.getPlannedExpense().getId().equals(plannedExpense.getId()))
                    .filter(expense -> expense.getCategory() != null)
                    .toList();
            if (expenses.stream().map(expense -> expense.getCategory().getId()).distinct().count() != 1) {
                return;
            }
            var category = expenses.get(0).getCategory();
            CategoryPlanSummaryDTO summary = categories.computeIfAbsent(category.getId(), id ->
                    new CategoryPlanSummaryDTO(id, category.getName(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
            summary.setPlannedAmount(summary.getPlannedAmount().add(plannedExpense.getAmount()));
        });
    }

    private BigDecimal expenseAmount(List<Expense> expenses) {
        return expenses.stream().map(Expense::getAmount).filter(amount -> amount.signum() < 0)
                .map(BigDecimal::abs).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Expense> withoutInvestments(List<Expense> expenses) {
        return expenses.stream().filter(expense -> expense.getCategory() == null ||
                !INVESTMENT_CATEGORY_LEVEL.equals(expense.getCategory().getLevel())).toList();
    }

    private Plan plan(Integer id) {
        return planRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private PlannedExpense plannedExpense(Integer id) {
        return plannedExpenseRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    private List<Expense> expenses(List<Long> expenseIds) {
        if (expenseIds == null || expenseIds.isEmpty() || new HashSet<>(expenseIds).size() != expenseIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Select one or more unique expenses");
        }
        List<Expense> expenses = expenseRepository.findAllById(expenseIds);
        if (expenses.size() != expenseIds.size()) {
            throw new NotFoundException();
        }
        return expenses;
    }

    private void validateExpenseForPlan(Expense expense, Plan plan) {
        if (expense.getAmount().signum() >= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only an expense can be planned");
        }
        if (expense.getPlannedExpense() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Expense is already planned");
        }
        if (expense.getTransactionDate().isBefore(plan.getStartDate()) || expense.getTransactionDate().isAfter(plan.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expense date must be within the plan month");
        }
    }

    private Plan planFrom(PlanDTO dto, Plan plan) {
        YearMonth month = YearMonth.of(dto.getYear(), dto.getMonth());
        plan.setStartDate(month.atDay(1));
        plan.setEndDate(month.atEndOfMonth());
        return plan;
    }

    private PlannedExpense plannedExpenseFrom(PlannedExpenseDTO dto, PlannedExpense plannedExpense) {
        plannedExpense.setAmount(dto.getAmount());
        plannedExpense.setName(dto.getName());
        if (dto.getPaid() != null) {
            plannedExpense.setPaid(dto.getPaid());
        }
        plannedExpense.setDueDate(dto.getDueDate());
        plannedExpense.setPlan(plan(dto.getPlanId()));
        return plannedExpense;
    }

    private void applyRecurringPayments(Plan plan) {
        List<PlannedExpense> plannedExpenses = recurringPlannedExpenseRepository.findAllByActiveTrueOrderByDueDayAscNameAsc().stream()
                .filter(recurringPayment -> !plannedExpenseRepository.existsByPlan_IdAndRecurringPayment_Id(plan.getId(), recurringPayment.getId()))
                .map(recurringPayment -> plannedExpenseFromRecurringPayment(plan, recurringPayment))
                .toList();
        plannedExpenseRepository.saveAll(plannedExpenses);
    }

    private PlannedExpense plannedExpenseFromRecurringPayment(Plan plan, RecurringPlannedExpense recurringPayment) {
        PlannedExpense plannedExpense = new PlannedExpense();
        plannedExpense.setPlan(plan);
        plannedExpense.setRecurringPayment(recurringPayment);
        plannedExpense.setName(recurringPayment.getName());
        plannedExpense.setAmount(recurringPayment.getAmount());
        plannedExpense.setDueDate(plan.getStartDate().withDayOfMonth(Math.min(recurringPayment.getDueDay(), plan.getEndDate().getDayOfMonth())));
        return plannedExpense;
    }

    private RecurringPlannedExpense createRecurringPayment(String name, BigDecimal amount, int dueDay) {
        RecurringPlannedExpense recurringPayment = new RecurringPlannedExpense();
        recurringPayment.setName(name);
        recurringPayment.setAmount(amount);
        recurringPayment.setDueDay(dueDay);
        return recurringPlannedExpenseRepository.save(recurringPayment);
    }

    private RecurringPlannedExpense recurringPaymentFrom(RecurringPlannedExpenseDTO dto, RecurringPlannedExpense recurringPayment) {
        recurringPayment.setName(dto.getName());
        recurringPayment.setAmount(dto.getAmount());
        recurringPayment.setDueDay(dto.getDueDay());
        if (dto.getActive() != null) {
            recurringPayment.setActive(dto.getActive());
        }
        return recurringPayment;
    }

    private PlanDTO toDto(Plan plan) {
        PlanDTO dto = new PlanDTO();
        dto.setId(plan.getId());
        dto.setYear(plan.getStartDate().getYear());
        dto.setMonth(plan.getStartDate().getMonthValue());
        return dto;
    }

    private PlannedExpenseDTO toDto(PlannedExpense plannedExpense) {
        PlannedExpenseDTO dto = new PlannedExpenseDTO();
        dto.setId(plannedExpense.getId());
        dto.setAmount(plannedExpense.getAmount());
        dto.setPlanId(plannedExpense.getPlan().getId());
        dto.setName(plannedExpense.getName());
        dto.setPaid(plannedExpense.isPaid());
        dto.setDueDate(plannedExpense.getDueDate());
        dto.setRecurringPaymentId(plannedExpense.getRecurringPayment() == null ? null : plannedExpense.getRecurringPayment().getId());
        return dto;
    }

    private RecurringPlannedExpenseDTO toDto(RecurringPlannedExpense recurringPayment) {
        RecurringPlannedExpenseDTO dto = new RecurringPlannedExpenseDTO();
        dto.setId(recurringPayment.getId());
        dto.setName(recurringPayment.getName());
        dto.setAmount(recurringPayment.getAmount());
        dto.setDueDay(recurringPayment.getDueDay());
        dto.setActive(recurringPayment.isActive());
        return dto;
    }
}
