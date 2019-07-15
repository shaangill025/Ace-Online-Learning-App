package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.Quiz;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Quiz entity.
 */
public interface QuizSearchRepository extends ElasticsearchRepository<Quiz, Long> {
}
