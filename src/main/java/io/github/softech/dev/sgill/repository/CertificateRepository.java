package io.github.softech.dev.sgill.repository;

import com.hazelcast.core.LifecycleEvent;
import io.github.softech.dev.sgill.domain.Certificate;
import io.github.softech.dev.sgill.domain.Course;
import io.github.softech.dev.sgill.domain.CourseHistory;
import io.github.softech.dev.sgill.domain.Customer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Certificate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long>, JpaSpecificationExecutor<Certificate> {
    Optional<List<Certificate>> findCertificatesByCustomer(Customer customer);
    Certificate getCertificateByCourseHistoryAndCustomer(CourseHistory courseHistory, Customer customer);
    Optional<List<Certificate>> getCertificatesByCustomer(Customer customer);
    Optional<List<Certificate>> findCertificatesByCustomerAndCourseHistoryOrderByIdDesc(Customer customer, CourseHistory courseHistory);
}
