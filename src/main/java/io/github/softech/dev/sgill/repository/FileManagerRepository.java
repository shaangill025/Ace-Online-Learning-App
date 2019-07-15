package io.github.softech.dev.sgill.repository;

import io.github.softech.dev.sgill.domain.FileManager;
import io.github.softech.dev.sgill.domain.Section;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the FileManager entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FileManagerRepository extends JpaRepository<FileManager, Long> {
    Optional<List<FileManager>> findFileManagersBySection(Section section);
}
