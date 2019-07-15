package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the TimeCourseLog entity.
 */

@Repository
public interface TimeCourseLogRepository extends JpaRepository<TimeCourseLog, Long>, JpaSpecificationExecutor<TimeCourseLog> {
    Optional<TimeCourseLog> findTimeCourseLogByCustomerIdAndCourseHistoryId(Long custid, Long courseHistoryid);

    Optional<List<TimeCourseLog>> findTimeCourseLogsByCustomer(Customer customer);

    Optional<List<TimeCourseLog>> findTimeCourseLogsByCustomerAndCourseHistory(Customer customer, CourseHistory courseHistory);

    Optional<List<TimeCourseLog>> findTimeCourseLogsByCustomerAndCourseHistoryOrderByIdDesc(Customer customer, CourseHistory courseHistory);

}
