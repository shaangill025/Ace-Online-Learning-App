package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Course;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.LegacyCourses;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the LegacyCourses entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LegacyCoursesRepository extends JpaRepository<LegacyCourses, Long>, JpaSpecificationExecutor<LegacyCourses> {
    Optional<List<LegacyCourses>> findLegacyCoursesByCourseAndCustomer(Course course, Customer customer);
    Optional<List<LegacyCourses>> findLegacyCoursesByCustomer(Customer customer);
}
