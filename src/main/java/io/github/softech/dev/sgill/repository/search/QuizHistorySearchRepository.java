package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.QuizHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the QuizHistory entity.
 */
public interface QuizHistorySearchRepository extends ElasticsearchRepository<QuizHistory, Long> {
}
