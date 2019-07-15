package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Course;
import io.github.softech.dev.sgill.domain.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Topic entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic> {
    @Query(value = "SELECT * FROM topic t WHERE t.id IN (SELECT c.topic_id FROM course c WHERE c.country = :country)", nativeQuery = true)
    Optional<List<Topic>> findTopicsByCountry(@Param("country") String country);
}
