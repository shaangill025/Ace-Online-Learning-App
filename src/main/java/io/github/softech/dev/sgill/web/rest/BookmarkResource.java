package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.Bookmark;
import io.github.softech.dev.sgill.service.BookmarkService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.BookmarkCriteria;
import io.github.softech.dev.sgill.service.BookmarkQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Bookmark.
 */
@RestController
@RequestMapping("/api")
public class BookmarkResource {

    private final Logger log = LoggerFactory.getLogger(BookmarkResource.class);

    private static final String ENTITY_NAME = "bookmark";

    private final BookmarkService bookmarkService;

    private final BookmarkQueryService bookmarkQueryService;

    public BookmarkResource(BookmarkService bookmarkService, BookmarkQueryService bookmarkQueryService) {
        this.bookmarkService = bookmarkService;
        this.bookmarkQueryService = bookmarkQueryService;
    }

    /**
     * POST  /bookmarks : Create a new bookmark.
     *
     * @param bookmark the bookmark to create
     * @return the ResponseEntity with status 201 (Created) and with body the new bookmark, or with status 400 (Bad Request) if the bookmark has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/bookmarks")
    @Timed
    public ResponseEntity<Bookmark> createBookmark(@Valid @RequestBody Bookmark bookmark) throws URISyntaxException {
        log.debug("REST request to save Bookmark : {}", bookmark);
        if (bookmark.getId() != null) {
            throw new BadRequestAlertException("A new bookmark cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bookmark.setModule(bookmark.getSection().getName());
        Bookmark result = bookmarkService.save(bookmark);
        return ResponseEntity.created(new URI("/api/bookmarks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bookmarks : Updates an existing bookmark.
     *
     * @param bookmark the bookmark to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated bookmark,
     * or with status 400 (Bad Request) if the bookmark is not valid,
     * or with status 500 (Internal Server Error) if the bookmark couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/bookmarks")
    @Timed
    public ResponseEntity<Bookmark> updateBookmark(@Valid @RequestBody Bookmark bookmark) throws URISyntaxException {
        log.debug("REST request to update Bookmark : {}", bookmark);
        if (bookmark.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Bookmark result = bookmarkService.save(bookmark);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, bookmark.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bookmarks : get all the bookmarks.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of bookmarks in body
     */
    @GetMapping("/bookmarks")
    @Timed
    public ResponseEntity<List<Bookmark>> getAllBookmarks(BookmarkCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Bookmarks by criteria: {}", criteria);
        Page<Bookmark> page = bookmarkQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bookmarks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /bookmarks/:id : get the "id" bookmark.
     *
     * @param id the id of the bookmark to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the bookmark, or with status 404 (Not Found)
     */
    @GetMapping("/bookmarks/{id}")
    @Timed
    public ResponseEntity<Bookmark> getBookmark(@PathVariable Long id) {
        log.debug("REST request to get Bookmark : {}", id);
        Optional<Bookmark> bookmark = bookmarkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookmark);
    }

    /**
     * DELETE  /bookmarks/:id : delete the "id" bookmark.
     *
     * @param id the id of the bookmark to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/bookmarks/{id}")
    @Timed
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long id) {
        log.debug("REST request to delete Bookmark : {}", id);
        bookmarkService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/bookmarks?query=:query : search for the bookmark corresponding
     * to the query.
     *
     * @param query the query of the bookmark search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/bookmarks")
    @Timed
    public ResponseEntity<List<Bookmark>> searchBookmarks(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Bookmarks for query {}", query);
        Page<Bookmark> page = bookmarkService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/bookmarks");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/_sections/bookmarks/{id}")
    @Timed
    public ResponseEntity<List<Bookmark>> getSectionBookmarks(@PathVariable Long id, Pageable pageable) {
        log.debug("REST request to get Bookmarks by Section : {}", id);
        Page<Bookmark> bookmark = bookmarkService.findbySectionId(id, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(bookmark, "/api/_sections/bookmarks");
        return new ResponseEntity<>(bookmark.getContent(), headers, HttpStatus.OK);
    }

}
