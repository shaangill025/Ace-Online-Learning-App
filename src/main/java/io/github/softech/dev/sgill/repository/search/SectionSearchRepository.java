package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.Section;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Section entity.
 */
public interface SectionSearchRepository extends ElasticsearchRepository<Section, Long> {
}
