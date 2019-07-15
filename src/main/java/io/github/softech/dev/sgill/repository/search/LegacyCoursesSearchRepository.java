package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.LegacyCourses;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the LegacyCourses entity.
 */
public interface LegacyCoursesSearchRepository extends ElasticsearchRepository<LegacyCourses, Long> {
}
