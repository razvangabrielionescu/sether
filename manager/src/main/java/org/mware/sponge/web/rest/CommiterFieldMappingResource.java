package org.mware.sponge.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.mware.sponge.domain.CommiterConfig;
import org.mware.sponge.domain.CommiterFieldMapping;

import org.mware.sponge.repository.CommiterConfigRepository;
import org.mware.sponge.repository.CommiterFieldMappingRepository;
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
 * REST controller for managing CommiterFieldMapping.
 */
@RestController
@RequestMapping("/sapi")
public class CommiterFieldMappingResource {

    private final Logger log = LoggerFactory.getLogger(CommiterFieldMappingResource.class);

    private static final String ENTITY_NAME = "commiterFieldMapping";

    private final CommiterFieldMappingRepository commiterFieldMappingRepository;
    private final CommiterConfigRepository commiterConfigRepository;

    /**
     * @param commiterFieldMappingRepository
     * @param commiterConfigRepository
     */
    public CommiterFieldMappingResource(CommiterFieldMappingRepository commiterFieldMappingRepository, CommiterConfigRepository commiterConfigRepository) {
        this.commiterFieldMappingRepository = commiterFieldMappingRepository;
        this.commiterConfigRepository = commiterConfigRepository;
    }

    /**
     * POST  /commiter-field-mappings : Create a new commiterFieldMapping.
     *
     * @param commiterFieldMapping the commiterFieldMapping to create
     * @return the ResponseEntity with status 201 (Created) and with body the new commiterFieldMapping, or with status 400 (Bad Request) if the commiterFieldMapping has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/commiter-field-mappings")
    @Timed
    public ResponseEntity<CommiterFieldMapping> createCommiterFieldMapping(@Valid @RequestBody CommiterFieldMapping commiterFieldMapping) throws URISyntaxException {
        log.debug("REST request to save CommiterFieldMapping : {}", commiterFieldMapping);
        if (commiterFieldMapping.getId() != null) {
           CommiterConfig commiterConfig = commiterConfigRepository.findOne(commiterFieldMapping.getId()); //In this case this is parent's id
           commiterFieldMapping.setId(null);
           commiterFieldMapping.setCommiterConfig(commiterConfig);
        }
        CommiterFieldMapping result = commiterFieldMappingRepository.save(commiterFieldMapping);
        return ResponseEntity.created(new URI("/api/commiter-field-mappings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /commiter-field-mappings : Updates an existing commiterFieldMapping.
     *
     * @param commiterFieldMapping the commiterFieldMapping to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated commiterFieldMapping,
     * or with status 400 (Bad Request) if the commiterFieldMapping is not valid,
     * or with status 500 (Internal Server Error) if the commiterFieldMapping couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/commiter-field-mappings")
    @Timed
    public ResponseEntity<CommiterFieldMapping> updateCommiterFieldMapping(@Valid @RequestBody CommiterFieldMapping commiterFieldMapping) throws URISyntaxException {
        log.debug("REST request to update CommiterFieldMapping : {}", commiterFieldMapping);
        if (commiterFieldMapping.getId() == null) {
            return createCommiterFieldMapping(commiterFieldMapping);
        }
        CommiterFieldMapping result = commiterFieldMappingRepository.save(commiterFieldMapping);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, commiterFieldMapping.getId().toString()))
            .body(result);
    }

    /**
     * GET  /commiter-field-mappings : get all the commiterFieldMappings.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of commiterFieldMappings in body
     */
    @GetMapping("/commiter-field-mappings")
    @Timed
    public List<CommiterFieldMapping> getAllCommiterFieldMappings() {
        log.debug("REST request to get all CommiterFieldMappings");
        return commiterFieldMappingRepository.findAll();
    }

    /**
     * GET  /commiter-field-mappings/:id : get the "id" commiterFieldMapping.
     *
     * @param id the id of the commiterFieldMapping to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the commiterFieldMapping, or with status 404 (Not Found)
     */
    @GetMapping("/commiter-field-mappings/{id}")
    @Timed
    public ResponseEntity<CommiterFieldMapping> getCommiterFieldMapping(@PathVariable Long id) {
        log.debug("REST request to get CommiterFieldMapping : {}", id);
        CommiterFieldMapping commiterFieldMapping = commiterFieldMappingRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(commiterFieldMapping));
    }

    @GetMapping("/commiter-field-mappings/config/{id}")
    @Timed
    public List<CommiterFieldMapping> getCommiterFieldMappingForConfig(@PathVariable Long id) {
        log.debug("REST request to get CommiterFieldMappings for committer config : {}", id);
        return commiterFieldMappingRepository.findAllByCommiterConfigId(id);
    }

    /**
     * DELETE  /commiter-field-mappings/:id : delete the "id" commiterFieldMapping.
     *
     * @param id the id of the commiterFieldMapping to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/commiter-field-mappings/{id}")
    @Timed
    public ResponseEntity<Void> deleteCommiterFieldMapping(@PathVariable Long id) {
        log.debug("REST request to delete CommiterFieldMapping : {}", id);
        commiterFieldMappingRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
