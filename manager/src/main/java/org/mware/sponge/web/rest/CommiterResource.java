package org.mware.sponge.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.mware.sponge.domain.Commiter;

import org.mware.sponge.repository.CommiterRepository;
import org.mware.sponge.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Commiter.
 */
@RestController
@RequestMapping("/sapi")
public class CommiterResource {

    private final Logger log = LoggerFactory.getLogger(CommiterResource.class);

    private static final String ENTITY_NAME = "commiter";

    private final CommiterRepository commiterRepository;

    public CommiterResource(CommiterRepository commiterRepository) {
        this.commiterRepository = commiterRepository;
    }

    /**
     * POST  /commiters : Create a new commiter.
     *
     * @param commiter the commiter to create
     * @return the ResponseEntity with status 201 (Created) and with body the new commiter, or with status 400 (Bad Request) if the commiter has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/commiters")
    @Timed
    public ResponseEntity<Commiter> createCommiter(@Valid @RequestBody Commiter commiter) throws URISyntaxException {
        log.debug("REST request to save Commiter : {}", commiter);
        if (commiter.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new commiter cannot already have an ID")).body(null);
        }
        Commiter result = commiterRepository.save(commiter);
        return ResponseEntity.created(new URI("/api/commiters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /commiters : Updates an existing commiter.
     *
     * @param commiter the commiter to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated commiter,
     * or with status 400 (Bad Request) if the commiter is not valid,
     * or with status 500 (Internal Server Error) if the commiter couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/commiters")
    @Timed
    public ResponseEntity<Commiter> updateCommiter(@Valid @RequestBody Commiter commiter) throws URISyntaxException {
        log.debug("REST request to update Commiter : {}", commiter);
        if (commiter.getId() == null) {
            return createCommiter(commiter);
        }
        Commiter result = commiterRepository.save(commiter);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, commiter.getId().toString()))
            .body(result);
    }

    /**
     * GET  /commiters : get all the commiters.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of commiters in body
     */
    @GetMapping("/commiters")
    @Timed
    public List<Commiter> getAllCommiters() {
        log.debug("REST request to get all Commiters");
        return commiterRepository.findAll();
    }

    /**
     * GET  /commiters/:id : get the "id" commiter.
     *
     * @param id the id of the commiter to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the commiter, or with status 404 (Not Found)
     */
    @GetMapping("/commiters/{id}")
    @Timed
    public ResponseEntity<Commiter> getCommiter(@PathVariable Long id) {
        log.debug("REST request to get Commiter : {}", id);
        Commiter commiter = commiterRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(commiter));
    }

    /**
     * DELETE  /commiters/:id : delete the "id" commiter.
     *
     * @param id the id of the commiter to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/commiters/{id}")
    @Timed
    public ResponseEntity<Void> deleteCommiter(@PathVariable Long id) {
        log.debug("REST request to delete Commiter : {}", id);
        commiterRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
