package io.github.softech.dev.sgill.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import io.github.softech.dev.sgill.domain.Bookmark;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.BookmarkRepository;
import io.github.softech.dev.sgill.repository.search.BookmarkSearchRepository;
import io.github.softech.dev.sgill.service.dto.BookmarkCriteria;


/**
 * Service for executing complex queries for Bookmark entities in the database.
 * The main input is a {@link BookmarkCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Bookmark} or a {@link Page} of {@link Bookmark} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookmarkQueryService extends QueryService<Bookmark> {

    private final Logger log = LoggerFactory.getLogger(BookmarkQueryService.class);

    private final BookmarkRepository bookmarkRepository;

    private final BookmarkSearchRepository bookmarkSearchRepository;

    public BookmarkQueryService(BookmarkRepository bookmarkRepository, BookmarkSearchRepository bookmarkSearchRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.bookmarkSearchRepository = bookmarkSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Bookmark} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Bookmark> findByCriteria(BookmarkCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Bookmark> specification = createSpecification(criteria);
        return bookmarkRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Bookmark} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Bookmark> findByCriteria(BookmarkCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Bookmark> specification = createSpecification(criteria);
        return bookmarkRepository.findAll(specification, page);
    }

    /**
     * Function to convert BookmarkCriteria to a {@link Specification}
     */
    private Specification<Bookmark> createSpecification(BookmarkCriteria criteria) {
        Specification<Bookmark> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Bookmark_.id));
            }
            if (criteria.getText() != null) {
                specification = specification.and(buildStringSpecification(criteria.getText(), Bookmark_.text));
            }
            if (criteria.getSlide() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSlide(), Bookmark_.slide));
            }
            if (criteria.getTimestamp() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTimestamp(), Bookmark_.timestamp));
            }
            if (criteria.getModule() != null) {
                specification = specification.and(buildStringSpecification(criteria.getModule(), Bookmark_.module));
            }
            if (criteria.getSeconds() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSeconds(), Bookmark_.seconds));
            }
            if (criteria.getSectionId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getSectionId(), Bookmark_.section, Section_.id));
            }
        }
        return specification;
    }

}
