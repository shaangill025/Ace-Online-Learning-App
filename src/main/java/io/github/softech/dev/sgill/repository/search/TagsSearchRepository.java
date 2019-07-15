package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.Tags;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Tags entity.
 */
public interface TagsSearchRepository extends ElasticsearchRepository<Tags, Long> {
}
