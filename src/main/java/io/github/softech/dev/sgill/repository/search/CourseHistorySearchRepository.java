package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.CourseHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the CourseHistory entity.
 */
public interface CourseHistorySearchRepository extends ElasticsearchRepository<CourseHistory, Long> {
}
