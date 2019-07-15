package io.github.softech.dev.sgill.service.impl;

import io.github.softech.dev.sgill.domain.Question;
import io.github.softech.dev.sgill.service.ChoiceService;
import io.github.softech.dev.sgill.domain.Choice;
import io.github.softech.dev.sgill.repository.ChoiceRepository;
import io.github.softech.dev.sgill.repository.search.ChoiceSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Choice.
 */
@Service
@Transactional
public class ChoiceServiceImpl implements ChoiceService {

    private final Logger log = LoggerFactory.getLogger(ChoiceServiceImpl.class);

    private final ChoiceRepository choiceRepository;

    private final ChoiceSearchRepository choiceSearchRepository;

    public ChoiceServiceImpl(ChoiceRepository choiceRepository, ChoiceSearchRepository choiceSearchRepository) {
        this.choiceRepository = choiceRepository;
        this.choiceSearchRepository = choiceSearchRepository;
    }

    /**
     * Save a choice.
     *
     * @param choice the entity to save
     * @return the persisted entity
     */
    @Override
    public Choice save(Choice choice) {
        log.debug("Request to save Choice : {}", choice);        Choice result = choiceRepository.save(choice);
        choiceSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the choices.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Choice> findAll(Pageable pageable) {
        log.debug("Request to get all Choices");
        return choiceRepository.findAll(pageable);
    }

    @Override
    public List<Choice> findChoicesByQuestionId(Long id){
        log.debug("Request to get Question by Quiz Id: {}", id);
        return choiceRepository.findChoicesByQuestionId(id);
    }
    /**
     * Get one choice by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Choice> findOne(Long id) {
        log.debug("Request to get Choice : {}", id);
        return choiceRepository.findById(id);
    }

    /**
     * Delete the choice by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Choice : {}", id);
        choiceRepository.deleteById(id);
        choiceSearchRepository.deleteById(id);
    }

    /**
     * Search for the choice corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Choice> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Choices for query {}", query);
        return choiceSearchRepository.search(queryStringQuery(query), pageable);    }
}
