package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.Certificate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Certificate entity.
 */
public interface CertificateSearchRepository extends ElasticsearchRepository<Certificate, Long> {
}
