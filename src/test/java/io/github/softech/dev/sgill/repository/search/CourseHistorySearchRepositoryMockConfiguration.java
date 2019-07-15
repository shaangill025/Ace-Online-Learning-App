package io.github.softech.dev.sgill.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of CourseHistorySearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CourseHistorySearchRepositoryMockConfiguration {

    @MockBean
    private CourseHistorySearchRepository mockCourseHistorySearchRepository;

}
