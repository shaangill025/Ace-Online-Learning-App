package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.QuestionHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the QuestionHistory entity.
 */
public interface QuestionHistorySearchRepository extends ElasticsearchRepository<QuestionHistory, Long> {
}
