package org.mware.sponge.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.mware.sponge.domain.Spider;

import org.mware.sponge.repository.SpiderRepository;
import org.mware.sponge.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Spider.
 */
@RestController
@RequestMapping("/sapi")
public class SpiderResource {

    private final Logger log = LoggerFactory.getLogger(SpiderResource.class);

    private static final String ENTITY_NAME = "spider";

    private final SpiderRepository spiderRepository;

    public SpiderResource(SpiderRepository spiderRepository) {
        this.spiderRepository = spiderRepository;
    }

    /**
     * POST  /spiders : Create a new spider.
     *
     * @param spider the spider to create
     * @return the ResponseEntity with status 201 (Created) and with body the new spider, or with status 400 (Bad Request) if the spider has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/spiders")
    @Timed
    public ResponseEntity<Spider> createSpider(@RequestBody Spider spider) throws URISyntaxException {
        log.debug("REST request to save Spider : {}", spider);
        if (spider.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new spider cannot already have an ID")).body(null);
        }
        Spider result = spiderRepository.save(spider);
        return ResponseEntity.created(new URI("/sapi/spiders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /spiders : Updates an existing spider.
     *
     * @param spider the spider to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated spider,
     * or with status 400 (Bad Request) if the spider is not valid,
     * or with status 500 (Internal Server Error) if the spider couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/spiders")
    @Timed
    public ResponseEntity<Spider> updateSpider(@RequestBody Spider spider) throws URISyntaxException {
        log.debug("REST request to update Spider : {}", spider);
        if (spider.getId() == null) {
            return createSpider(spider);
        }
        Spider result = spiderRepository.save(spider);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, spider.getId().toString()))
            .body(result);
    }

    /**
     * GET  /spiders : get all the spiders.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of spiders in body
     */
    @GetMapping("/spiders")
    @Timed
    public List<Spider> getAllSpiders() {
        log.debug("REST request to get all Spiders");
        return spiderRepository.findAll();
    }

    /**
     * GET  /spiders/:id : get the "id" spider.
     *
     * @param id the id of the spider to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the spider, or with status 404 (Not Found)
     */
    @GetMapping("/spiders/{id}")
    @Timed
    public ResponseEntity<Spider> getSpider(@PathVariable Long id) {
        log.debug("REST request to get Spider : {}", id);
        Spider spider = spiderRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(spider));
    }

    /**
     * DELETE  /spiders/:id : delete the "id" spider.
     *
     * @param id the id of the spider to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/spiders/{id}")
    @Timed
    public ResponseEntity<Void> deleteSpider(@PathVariable Long id) {
        log.debug("REST request to delete Spider : {}", id);
        spiderRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
