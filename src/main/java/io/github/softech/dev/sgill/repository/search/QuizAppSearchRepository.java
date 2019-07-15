package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.QuizApp;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the QuizApp entity.
 */
public interface QuizAppSearchRepository extends ElasticsearchRepository<QuizApp, Long> {
}
