package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Course;
import io.github.softech.dev.sgill.domain.Quiz;
import io.github.softech.dev.sgill.domain.Section;
import io.github.softech.dev.sgill.repository.search.CourseSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Section entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SectionRepository extends JpaRepository<Section, Long>, JpaSpecificationExecutor<Section> {

    @Query(value = "select distinct section from Section section left join fetch section.tags",
        countQuery = "select count(distinct section) from Section section")
    Page<Section> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct section from Section section left join fetch section.tags")
    List<Section> findAllWithEagerRelationships();

    @Query("select section from Section section left join fetch section.tags where section.id =:id")
    Optional<Section> findOneWithEagerRelationships(@Param("id") Long id);

    @Query(value = "SELECT * FROM section u WHERE u.course_id = :course_id ORDER BY u.id ASC LIMIT 1", nativeQuery = true)
    Optional<Section> getSectionByCourse(@Param("course_id") Long course_id);

    Optional<List<Section>> findSectionsByCourseOrderByIdAsc(Course course);

    Optional<List<Section>> findSectionsByCourse(Course course);

    Optional<List<Section>> findSectionsByCourseOrderByIdDesc(Course course);

    @Query(value = "SELECT * FROM section u WHERE u.course_id = :course_id ORDER BY u.id ASC", nativeQuery = true)
    Optional<List<Section>> getSectionsByCourse(@Param("course_id") Long course_id);

    Optional<Section> getSectionsByQuiz(Quiz quiz);
}
