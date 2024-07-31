package application.repositories;

import application.models.MonthlyLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthlyLimitRepository extends JpaRepository<MonthlyLimit, Long> {
    Optional<MonthlyLimit> findByExpenseCategoryAndActiveTrue(String expenseCategory);
}
