package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Quiz;
import io.github.softech.dev.sgill.domain.Section;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Quiz entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>, JpaSpecificationExecutor<Quiz> {
    Optional<List<Quiz>> findQuizzesByNewSectionOrderByIdDesc(Section section);
}
