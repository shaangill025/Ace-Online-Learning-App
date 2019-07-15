package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.SectionHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the SectionHistory entity.
 */
public interface SectionHistorySearchRepository extends ElasticsearchRepository<SectionHistory, Long> {
}
