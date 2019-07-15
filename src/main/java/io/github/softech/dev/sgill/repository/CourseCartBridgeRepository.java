package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Cart;
import io.github.softech.dev.sgill.domain.Course;
import io.github.softech.dev.sgill.domain.CourseCartBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.LongFunction;


/**
 * Spring Data  repository for the CourseCartBridge entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseCartBridgeRepository extends JpaRepository<CourseCartBridge, Long>, JpaSpecificationExecutor<CourseCartBridge> {
    List<CourseCartBridge> findCourseCartBridgesByCartId(Long id);
    Optional<CourseCartBridge> findCourseCartBridgeByCartIdAndCourseId(Long cartid, Long courseid);
    Optional<List<CourseCartBridge>> findCourseCartBridgesByCart(Cart cart);
    Optional<List<CourseCartBridge>> findCourseCartBridgesByCartAndCourse(Cart cart, Course course);
    Long countCourseCartBridgesByCart(Cart cart);
}
