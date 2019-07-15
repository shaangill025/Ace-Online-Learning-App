package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.CourseCartBridge;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the CourseCartBridge entity.
 */
public interface CourseCartBridgeSearchRepository extends ElasticsearchRepository<CourseCartBridge, Long> {
}
