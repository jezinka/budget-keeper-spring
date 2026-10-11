package com.example.budgetkeeperspring.repository;

import com.example.budgetkeeperspring.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Integer> {
    Optional<Plan> findByStartDate(LocalDate startDate);

    List<Plan> findAllByStartDateBetween(LocalDate begin, LocalDate end);

    @Query(value = """
            select p.id, year(p.start_date), month(p.start_date),
                   coalesce(planned.planned_amount, 0), coalesce(unplanned.unplanned_amount, 0),
                   coalesce(planned.paid_amount, 0), coalesce(planned.remaining_amount, 0),
                   coalesce(planned.planned_count, 0), coalesce(unplanned.unplanned_count, 0)
            from plan p
            left join (
                select summary.plan_id, sum(summary.spent) as planned_amount,
                       sum(case when summary.paid then summary.spent else 0 end) as paid_amount,
                       sum(case when not summary.paid then greatest(summary.amount - summary.spent, 0) else 0 end) as remaining_amount,
                       count(*) as planned_count
                from (
                    select pe.id, pe.plan_id, pe.amount, pe.paid,
                           coalesce(sum(case when e.deleted = false and e.excluded_from_plan = false and e.amount < 0
                                                and (c.level is null or c.level <> :investmentLevel)
                                                and e.transaction_date between plan_dates.start_date and plan_dates.end_date
                                             then abs(e.amount) else 0 end), 0) as spent
                    from planned_expense pe
                    join plan plan_dates on plan_dates.id = pe.plan_id
                    left join (expense e join category c on c.id = e.category_id) on e.planned_expense_id = pe.id
                    group by pe.id, pe.plan_id, pe.amount, pe.paid
                ) summary
                group by summary.plan_id
            ) planned on planned.plan_id = p.id
            left join (
                select plan_dates.id as plan_id,
                       sum(case when e.excluded_from_plan = false and e.amount < 0
                                     and (c.level is null or c.level <> :investmentLevel)
                                then abs(e.amount) else 0 end) as unplanned_amount,
                       sum(case when c.level is null or c.level <> :investmentLevel
                                then 1 else 0 end) as unplanned_count
                from plan plan_dates
                join expense e on e.planned_expense_id is null and e.deleted = false
                              and e.transaction_date between plan_dates.start_date and plan_dates.end_date
                join category c on c.id = e.category_id
                group by plan_dates.id
            ) unplanned on unplanned.plan_id = p.id
            """, nativeQuery = true)
    List<Object[]> findAllAdminSummaries(@Param("investmentLevel") int investmentLevel);
}
