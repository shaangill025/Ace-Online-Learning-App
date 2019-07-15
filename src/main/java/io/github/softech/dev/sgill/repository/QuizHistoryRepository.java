package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.Quiz;
import io.github.softech.dev.sgill.domain.QuizHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the QuizHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuizHistoryRepository extends JpaRepository<QuizHistory, Long>, JpaSpecificationExecutor<QuizHistory> {
    @Query(value = "SELECT * FROM quiz_history u WHERE u.customer_id = :customer_id AND u.quiz_id = (SELECT id from quiz p WHERE p.new_section_id = :reqd_id) ORDER BY u.id DESC", nativeQuery = true)
    Optional<QuizHistory> getLastQuizAttemptByCourse(@Param("customer_id") Long customer_id, @Param("reqd_id") Long section_id);

    Optional<List<QuizHistory>> findQuizHistoriesByCustomer(Customer customer);

    @Query(value = "SELECT * FROM section_history s WHERE s.customer_id = :customer_id AND s.section_id = (SELECT p.id FROM section p WHERE p.quiz_id = :quiz_id) ORDER BY s.id DESC LIMIT 1", nativeQuery = true)
    Optional<QuizHistory> checkQuizAppAcess(@Param("customer_id") Long customer_id, @Param("quiz_id") Long quiz_id);

    Optional<List<QuizHistory>> findQuizHistoriesByCustomerAndQuizOrderByIdDesc(Customer customer, Quiz quiz);
}
