package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Course.
 */
public interface CourseService {

    /**
     * Save a course.
     *
     * @param course the entity to save
     * @return the persisted entity
     */
    Course save(Course course);

    /**
     * Get all the courses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Course> findAll(Pageable pageable);

    /**
     * Get all the Course with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    Page<Course> findAllWithEagerRelationships(Pageable pageable);
    
    /**
     * Get the "id" course.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Course> findOne(Long id);

    /**
     * Delete the "id" course.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the course corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Course> search(String query, Pageable pageable);
}
