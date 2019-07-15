package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Course;
import io.github.softech.dev.sgill.domain.Section;
import io.github.softech.dev.sgill.domain.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Course entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    @Query(value = "select distinct course from Course course left join fetch course.tags",
        countQuery = "select count(distinct course) from Course course")
    Page<Course> findAllWithEagerRelationships(Pageable pageable);

    @Query(value = "select distinct course from Course course left join fetch course.tags")
    List<Course> findAllWithEagerRelationships();

    @Query("select course from Course course left join fetch course.tags where course.id =:id")
    Optional<Course> findOneWithEagerRelationships(@Param("id") Long id);

    Optional<List<Course>> findCoursesByCountryAndShow(String country, Boolean isShow);

    Optional<List<Course>> findCoursesByTopicAndCountry(Topic topic, String country);
}
