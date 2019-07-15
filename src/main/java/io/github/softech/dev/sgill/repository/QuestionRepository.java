package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Question;
import io.github.softech.dev.sgill.domain.Quiz;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Question entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    Optional<List<Question>> findQuestionsByQuiz(Quiz quiz);
}
