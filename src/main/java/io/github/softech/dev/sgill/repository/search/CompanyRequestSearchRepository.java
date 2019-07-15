package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.CompanyRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the CompanyRequest entity.
 */
public interface CompanyRequestSearchRepository extends ElasticsearchRepository<CompanyRequest, Long> {
}
