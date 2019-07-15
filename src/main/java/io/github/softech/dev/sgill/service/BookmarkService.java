package io.github.softech.dev.sgill.service;

import io.github.softech.dev.sgill.domain.Bookmark;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Bookmark.
 */
public interface BookmarkService {

    /**
     * Save a bookmark.
     *
     * @param bookmark the entity to save
     * @return the persisted entity
     */
    Bookmark save(Bookmark bookmark);

    /**
     * Get all the bookmarks.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Bookmark> findAll(Pageable pageable);


    /**
     * Get the "id" bookmark.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Bookmark> findOne(Long id);

    /**
     * Delete the "id" bookmark.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the bookmark corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Bookmark> search(String query, Pageable pageable);

    Page<Bookmark> findbySectionId(Long id, Pageable pageable);
}
