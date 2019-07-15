package io.github.softech.dev.sgill.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of BookmarkSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class BookmarkSearchRepositoryMockConfiguration {

    @MockBean
    private BookmarkSearchRepository mockBookmarkSearchRepository;

}
