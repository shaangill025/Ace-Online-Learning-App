package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.FileManager;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the FileManager entity.
 */
public interface FileManagerSearchRepository extends ElasticsearchRepository<FileManager, Long> {
}
