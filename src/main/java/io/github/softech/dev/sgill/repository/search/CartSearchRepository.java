package io.github.softech.dev.sgill.repository.search;

import io.github.softech.dev.sgill.domain.Cart;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Cart entity.
 */
public interface CartSearchRepository extends ElasticsearchRepository<Cart, Long> {
}
