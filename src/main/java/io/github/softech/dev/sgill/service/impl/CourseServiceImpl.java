package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.service.CourseService;
import io.github.softech.dev.sgill.domain.Course;
import io.github.softech.dev.sgill.repository.CourseRepository;
import io.github.softech.dev.sgill.repository.search.CourseSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Course.
 */
@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;

    private final CourseSearchRepository courseSearchRepository;

    public CourseServiceImpl(CourseRepository courseRepository, CourseSearchRepository courseSearchRepository) {
        this.courseRepository = courseRepository;
        this.courseSearchRepository = courseSearchRepository;
    }

    /**
     * Save a course.
     *
     * @param course the entity to save
     * @return the persisted entity
     */
    @Override
    public Course save(Course course) {
        log.debug("Request to save Course : {}", course);
        Course result = courseRepository.save(course);
        courseSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the courses.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Course> findAll(Pageable pageable) {
        log.debug("Request to get all Courses");
        return courseRepository.findAll(pageable);
    }

    /**
     * Get all the Course with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    public Page<Course> findAllWithEagerRelationships(Pageable pageable) {
        return courseRepository.findAllWithEagerRelationships(pageable);
    }
    

    /**
     * Get one course by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Course> findOne(Long id) {
        log.debug("Request to get Course : {}", id);
        return courseRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the course by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Course : {}", id);
        courseRepository.deleteById(id);
        courseSearchRepository.deleteById(id);
    }

    /**
     * Search for the course corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Course> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Courses for query {}", query);
        return courseSearchRepository.search(queryStringQuery(query), pageable);    }
}
