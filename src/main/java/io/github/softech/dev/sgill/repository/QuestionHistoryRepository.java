package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.QuestionHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the QuestionHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionHistoryRepository extends JpaRepository<QuestionHistory, Long>, JpaSpecificationExecutor<QuestionHistory> {
    Optional<List<QuestionHistory>> getQuestionHistoriesByCustomer(Customer customer);
    Optional<List<QuestionHistory>> findQuestionHistoriesByCustomer(Customer customer);
}
