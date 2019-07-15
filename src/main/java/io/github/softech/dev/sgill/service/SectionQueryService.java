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

import io.github.softech.dev.sgill.domain.Section;
import io.github.softech.dev.sgill.domain.*; // for static metamodels
import io.github.softech.dev.sgill.repository.SectionRepository;
import io.github.softech.dev.sgill.repository.search.SectionSearchRepository;
import io.github.softech.dev.sgill.service.dto.SectionCriteria;


/**
 * Service for executing complex queries for Section entities in the database.
 * The main input is a {@link SectionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Section} or a {@link Page} of {@link Section} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SectionQueryService extends QueryService<Section> {

    private final Logger log = LoggerFactory.getLogger(SectionQueryService.class);

    private final SectionRepository sectionRepository;

    private final SectionSearchRepository sectionSearchRepository;

    public SectionQueryService(SectionRepository sectionRepository, SectionSearchRepository sectionSearchRepository) {
        this.sectionRepository = sectionRepository;
        this.sectionSearchRepository = sectionSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Section} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Section> findByCriteria(SectionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Section> specification = createSpecification(criteria);
        return sectionRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Section} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Section> findByCriteria(SectionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Section> specification = createSpecification(criteria);
        return sectionRepository.findAll(specification, page);
    }

    /**
     * Function to convert SectionCriteria to a {@link Specification}
     */
    private Specification<Section> createSpecification(SectionCriteria criteria) {
        Specification<Section> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Section_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Section_.name));
            }
            if (criteria.getNotes() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNotes(), Section_.notes));
            }
            if (criteria.getNormSection() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNormSection(), Section_.normSection));
            }
            if (criteria.getVideoUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getVideoUrl(), Section_.videoUrl));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), Section_.type));
            }
            if (criteria.getPdfUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPdfUrl(), Section_.pdfUrl));
            }
            if (criteria.getTotalPages() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalPages(), Section_.totalPages));
            }
            if (criteria.getQuizId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getQuizId(), Section_.quiz, Quiz_.id));
            }
            if (criteria.getTagsId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getTagsId(), Section_.tags, Tags_.id));
            }
            if (criteria.getCourseId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getCourseId(), Section_.course, Course_.id));
            }
        }
        return specification;
    }

}
