package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Tags;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Tags entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagsRepository extends JpaRepository<Tags, Long>, JpaSpecificationExecutor<Tags> {

}
