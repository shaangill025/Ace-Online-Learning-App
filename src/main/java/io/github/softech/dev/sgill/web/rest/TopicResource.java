package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.Topic;
import io.github.softech.dev.sgill.repository.TopicRepository;
import io.github.softech.dev.sgill.service.TopicService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.TopicCriteria;
import io.github.softech.dev.sgill.service.TopicQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Topic.
 */
@RestController
@RequestMapping("/api")
public class TopicResource {

    private final Logger log = LoggerFactory.getLogger(TopicResource.class);

    private static final String ENTITY_NAME = "topic";

    private final TopicService topicService;

    private final TopicQueryService topicQueryService;

    private final TopicRepository topicRepository;

    public TopicResource(TopicService topicService, TopicQueryService topicQueryService, TopicRepository topicRepository) {
        this.topicService = topicService;
        this.topicQueryService = topicQueryService;
        this.topicRepository = topicRepository;
    }

    /**
     * POST  /topics : Create a new topic.
     *
     * @param topic the topic to create
     * @return the ResponseEntity with status 201 (Created) and with body the new topic, or with status 400 (Bad Request) if the topic has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/topics")
    @Timed
    public ResponseEntity<Topic> createTopic(@Valid @RequestBody Topic topic) throws URISyntaxException {
        log.debug("REST request to save Topic : {}", topic);
        if (topic.getId() != null) {
            throw new BadRequestAlertException("A new topic cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Topic result = topicService.save(topic);
        return ResponseEntity.created(new URI("/api/topics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @GetMapping("/topics/country/{country}")
    public ResponseEntity<List<Topic>> getAllTopicsbyCountry(@PathVariable String country) {
        log.debug("REST request to get Courses by country: {}", country);
        List<Topic> topics = topicRepository.findTopicsByCountry(country).orElse(null);
        return ResponseEntity.ok().body(topics);
    }

    /**
     * PUT  /topics : Updates an existing topic.
     *
     * @param topic the topic to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated topic,
     * or with status 400 (Bad Request) if the topic is not valid,
     * or with status 500 (Internal Server Error) if the topic couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/topics")
    @Timed
    public ResponseEntity<Topic> updateTopic(@Valid @RequestBody Topic topic) throws URISyntaxException {
        log.debug("REST request to update Topic : {}", topic);
        if (topic.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Topic result = topicService.save(topic);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, topic.getId().toString()))
            .body(result);
    }

    /**
     * GET  /topics : get all the topics.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of topics in body
     */
    @GetMapping("/topics")
    @Timed
    public ResponseEntity<List<Topic>> getAllTopics(TopicCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Topics by criteria: {}", criteria);
        Page<Topic> page = topicQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/topics");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /topics/:id : get the "id" topic.
     *
     * @param id the id of the topic to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the topic, or with status 404 (Not Found)
     */
    @GetMapping("/topics/{id}")
    @Timed
    public ResponseEntity<Topic> getTopic(@PathVariable Long id) {
        log.debug("REST request to get Topic : {}", id);
        Optional<Topic> topic = topicService.findOne(id);
        return ResponseUtil.wrapOrNotFound(topic);
    }

    /**
     * DELETE  /topics/:id : delete the "id" topic.
     *
     * @param id the id of the topic to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/topics/{id}")
    @Timed
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        log.debug("REST request to delete Topic : {}", id);
        topicService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/topics?query=:query : search for the topic corresponding
     * to the query.
     *
     * @param query the query of the topic search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/topics")
    @Timed
    public ResponseEntity<List<Topic>> searchTopics(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Topics for query {}", query);
        Page<Topic> page = topicService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/topics");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
