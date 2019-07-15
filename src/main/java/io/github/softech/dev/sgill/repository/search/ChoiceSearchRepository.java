package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.Choice;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Choice entity.
 */
public interface ChoiceSearchRepository extends ElasticsearchRepository<Choice, Long> {
}
