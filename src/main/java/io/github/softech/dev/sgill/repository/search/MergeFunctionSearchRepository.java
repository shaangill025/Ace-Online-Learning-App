package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.MergeFunction;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the MergeFunction entity.
 */
public interface MergeFunctionSearchRepository extends ElasticsearchRepository<MergeFunction, Long> {
}
