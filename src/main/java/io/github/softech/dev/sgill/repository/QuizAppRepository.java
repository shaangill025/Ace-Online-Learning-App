package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.QuizApp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the QuizApp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuizAppRepository extends JpaRepository<QuizApp, Long>, JpaSpecificationExecutor<QuizApp> {

    /**@Query(value = "select distinct * from quiz_app left join fetch quiz_app.questions",
        countQuery = "select count(distinct *) from quiz_app", nativeQuery = true)
    Page<QuizApp> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct * from quiz_app left join fetch quiz_app.questions", nativeQuery = true)
    List<QuizApp> findAllWithEagerRelationships();

    @Query(value = "select * from quiz_app left join fetch quiz_app.questions where quiz_app.id =:id", nativeQuery = true)
    Optional<QuizApp> findOneWithEagerRelationships(@Param("id") Long id);*/

    Optional<QuizApp> findQuizAppById(Long id);
}
