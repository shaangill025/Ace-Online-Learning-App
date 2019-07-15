package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.Topic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Topic entity.
 */
public interface TopicSearchRepository extends ElasticsearchRepository<Topic, Long> {
}
