package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.Servicelist;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Servicelist entity.
 */
public interface ServicelistSearchRepository extends ElasticsearchRepository<Servicelist, Long> {
}
