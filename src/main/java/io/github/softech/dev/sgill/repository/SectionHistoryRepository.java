package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the SectionHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SectionHistoryRepository extends JpaRepository<SectionHistory, Long>, JpaSpecificationExecutor<SectionHistory> {
    Optional<List<SectionHistory>> getSectionHistoriesByCustomer(Customer customer);

    Optional<SectionHistory> getSectionHistoryByCustomerIdAndSectionId(Long customerId, Long sectionId);

    Optional<SectionHistory> findTopByCustomerAndSectionOrderByIdDesc(Customer customer, Section section);
    @Query(value = "SELECT * FROM section_history u WHERE u.customer_id = :customer_id AND u.section_id IN (SELECT id from section r WHERE r.course_id = :course_id) ORDER BY u.id DESC", nativeQuery = true)
    Optional<List<SectionHistory>> getSectionCountByCourse(@Param("customer_id") Long customer_id, @Param("course_id") Long course_id);

    @Query(value = "SELECT * FROM section_history u WHERE u.customer_id = :customer_id AND u.section_id = (SELECT id from section r WHERE r.quiz_id = (SELECT h.quiz_id FROM quiz_history h WHERE h.passed is TRUE AND h.customer_id is :customer_id AND h.quiz_id = (SELECT p.id from quiz p WHERE p.new_section_id = :reqd_id) order by h.id DESC LIMIT 1))", nativeQuery = true)
    Optional<SectionHistory> getLastSectionWatchedCountByCourse(@Param("customer_id") Long customer_id, @Param("reqd_id") Long section_id);

    Optional<List<SectionHistory>> findSectionHistoriesByCustomer(Customer customer);
    Optional<SectionHistory> getTopByCustomerAndSection_CourseOrderByStartdateDesc(Customer customer, Course course);
    Optional<SectionHistory> getTopByCustomerOrderByIdDesc(Customer customer);
    Optional<SectionHistory> getTopByCustomerAndSection_CourseOrderByIdDesc(Customer customer, Course course);
    Optional<SectionHistory> getCountByCustomerAndSection_CourseOrderByIdDesc(Customer customer, Course course);
    Optional<List<SectionHistory>> findSectionHistoriesBySectionAndCustomerOrderByIdDesc(Section section, Customer customer);
}
