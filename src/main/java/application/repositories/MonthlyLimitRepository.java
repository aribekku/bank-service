package application.repositories;

import application.models.MonthlyLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonthlyLimitRepository extends JpaRepository<MonthlyLimit, Long> {

    MonthlyLimit findFirstByExpenseCategoryOrderByCreatedDesc(String category);
    List<MonthlyLimit> findAllByLimitExceeded(Boolean exceeded);
    List<MonthlyLimit> findAllByTransactionNull();
}
