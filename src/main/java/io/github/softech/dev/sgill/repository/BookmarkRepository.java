package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Bookmark entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, JpaSpecificationExecutor<Bookmark> {
    Page<Bookmark> findBookmarksBySectionId(Long Id, Pageable pageable);
}
