package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.TimeCourseLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the TimeCourseLog entity.
 */
public interface TimeCourseLogSearchRepository extends ElasticsearchRepository<TimeCourseLog, Long> {
}
