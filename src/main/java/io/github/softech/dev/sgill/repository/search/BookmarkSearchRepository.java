package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.Bookmark;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Bookmark entity.
 */
public interface BookmarkSearchRepository extends ElasticsearchRepository<Bookmark, Long> {
}
