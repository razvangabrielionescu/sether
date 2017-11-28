package org.mware.sponge.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.mware.sponge.domain.CommiterConfig;

import org.mware.sponge.repository.CommiterConfigRepository;
import org.mware.sponge.service.CommiterConfigService;
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
 * REST controller for managing CommiterConfig.
 */
@RestController
@RequestMapping("/sapi")
public class CommiterConfigResource {

    private final Logger log = LoggerFactory.getLogger(CommiterConfigResource.class);

    private static final String ENTITY_NAME = "commiterConfig";

    private final CommiterConfigRepository commiterConfigRepository;
    private final CommiterConfigService commiterConfigService;

    /**
     * @param commiterConfigRepository
     * @param commiterConfigService
     */
    public CommiterConfigResource(CommiterConfigRepository commiterConfigRepository,
                                  CommiterConfigService commiterConfigService) {
        this.commiterConfigRepository = commiterConfigRepository;
        this.commiterConfigService = commiterConfigService;
    }

    /**
     * POST  /commiter-configs : Create a new commiterConfig.
     *
     * @param commiterConfig the commiterConfig to create
     * @return the ResponseEntity with status 201 (Created) and with body the new commiterConfig, or with status 400 (Bad Request) if the commiterConfig has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/commiter-configs")
    @Timed
    public ResponseEntity<CommiterConfig> createCommiterConfig(@Valid @RequestBody CommiterConfig commiterConfig) throws URISyntaxException {
        log.debug("REST request to save CommiterConfig : {}", commiterConfig);
        if (commiterConfig.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new commiterConfig cannot already have an ID")).body(null);
        }
        CommiterConfig result = commiterConfigRepository.save(commiterConfig);
        return ResponseEntity.created(new URI("/api/commiter-configs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /commiter-configs : Updates an existing commiterConfig.
     *
     * @param commiterConfig the commiterConfig to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated commiterConfig,
     * or with status 400 (Bad Request) if the commiterConfig is not valid,
     * or with status 500 (Internal Server Error) if the commiterConfig couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/commiter-configs")
    @Timed
    public ResponseEntity<CommiterConfig> updateCommiterConfig(@Valid @RequestBody CommiterConfig commiterConfig) throws URISyntaxException {
        log.debug("REST request to update CommiterConfig : {}", commiterConfig);
        if (commiterConfig.getId() == null) {
            return createCommiterConfig(commiterConfig);
        }
        CommiterConfig result = commiterConfigRepository.save(commiterConfig);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, commiterConfig.getId().toString()))
            .body(result);
    }

    /**
     * GET  /commiter-configs : get all the commiterConfigs.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of commiterConfigs in body
     */
    @GetMapping("/commiter-configs")
    @Timed
    public List<CommiterConfig> getAllCommiterConfigs() {
        log.debug("REST request to get all CommiterConfigs");
        return commiterConfigRepository.findAll();
    }

    /**
     * GET  /commiter-configs/:id : get the "id" commiterConfig.
     *
     * @param id the id of the commiterConfig to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the commiterConfig, or with status 404 (Not Found)
     */
    @GetMapping("/commiter-configs/{id}")
    @Timed
    public ResponseEntity<CommiterConfig> getCommiterConfig(@PathVariable Long id) {
        log.debug("REST request to get CommiterConfig : {}", id);
        CommiterConfig commiterConfig = commiterConfigRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(commiterConfig));
    }

    /**
     * @param id
     * @param name
     * @param withMapping
     * @return
     */
    @GetMapping("/commiter-configs/{id}/{name}/{withMapping}")
    @Timed
    public ResponseEntity<CommiterConfig> cloneCommiterConfig(@PathVariable Long id,
                                                              @PathVariable String name,
                                                              @PathVariable boolean withMapping) {
        log.debug("REST request to clone CommiterConfig: "+id+" | new name: "+name+"; clone mapping: "+withMapping);
        CommiterConfig commiterConfig = commiterConfigService.cloneCommiterConfig(id, name, withMapping);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(commiterConfig));
    }

    /**
     * DELETE  /commiter-configs/:id : delete the "id" commiterConfig.
     *
     * @param id the id of the commiterConfig to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/commiter-configs/{id}")
    @Timed
    public ResponseEntity<Void> deleteCommiterConfig(@PathVariable Long id) {
        log.debug("REST request to delete CommiterConfig : {}", id);
        commiterConfigRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
