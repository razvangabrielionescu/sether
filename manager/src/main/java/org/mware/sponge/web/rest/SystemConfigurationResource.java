package org.mware.sponge.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.mware.sponge.domain.SystemConfiguration;

import org.mware.sponge.repository.SystemConfigurationRepository;
import org.mware.sponge.util.Constants;
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
 * REST controller for managing SystemConfiguration.
 */
@RestController
@RequestMapping("/sapi")
public class SystemConfigurationResource {

    private final Logger log = LoggerFactory.getLogger(SystemConfigurationResource.class);

    private static final String ENTITY_NAME = "systemConfiguration";

    private final SystemConfigurationRepository systemConfigurationRepository;

    public SystemConfigurationResource(SystemConfigurationRepository systemConfigurationRepository) {
        this.systemConfigurationRepository = systemConfigurationRepository;
    }

    /**
     * POST  /system-configurations : Create a new systemConfiguration.
     *
     * @param systemConfiguration the systemConfiguration to create
     * @return the ResponseEntity with status 201 (Created) and with body the new systemConfiguration, or with status 400 (Bad Request) if the systemConfiguration has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/system-configurations")
    @Timed
    public ResponseEntity<SystemConfiguration> createSystemConfiguration(@RequestBody SystemConfiguration systemConfiguration) throws URISyntaxException {
        log.debug("REST request to save SystemConfiguration : {}", systemConfiguration);
        if (systemConfiguration.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new systemConfiguration cannot already have an ID")).body(null);
        }
        SystemConfiguration result = systemConfigurationRepository.save(systemConfiguration);
        return ResponseEntity.created(new URI("/sapi/system-configurations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /system-configurations : Updates an existing systemConfiguration.
     *
     * @param systemConfiguration the systemConfiguration to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated systemConfiguration,
     * or with status 400 (Bad Request) if the systemConfiguration is not valid,
     * or with status 500 (Internal Server Error) if the systemConfiguration couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/system-configurations")
    @Timed
    public ResponseEntity<SystemConfiguration> updateSystemConfiguration(@RequestBody SystemConfiguration systemConfiguration) throws URISyntaxException {
        log.debug("REST request to update SystemConfiguration : {}", systemConfiguration);
        if (systemConfiguration.getId() == null) {
            return createSystemConfiguration(systemConfiguration);
        }
        SystemConfiguration result = systemConfigurationRepository.save(systemConfiguration);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, systemConfiguration.getId().toString()))
            .body(result);
    }

    /**
     * GET  /system-configurations : get all the systemConfigurations.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of systemConfigurations in body
     */
    @GetMapping("/system-configurations")
    @Timed
    public List<SystemConfiguration> getAllSystemConfigurations() {
        log.debug("REST request to get all SystemConfigurations");
        return systemConfigurationRepository.findAll();
    }

    /**
     * @param configKey
     * @return
     */
    @GetMapping("/system-configurations/configKey/{configKey}")
    @Timed
    public ResponseEntity<SystemConfiguration> getSystemConfigurationByConfigKey(@PathVariable String configKey) {
        log.debug("REST request to get SystemConfiguration by configKey");
        Optional<SystemConfiguration> config = systemConfigurationRepository.findOneByConfigKey(configKey);
        return ResponseUtil.wrapOrNotFound(config);
    }

    @GetMapping("/system-configurations/socialUrl")
    @Timed
    public ResponseEntity<SystemConfiguration> getSocialUrl() {
        log.debug("REST request to get SystemConfiguration for Social URL");
        Optional<SystemConfiguration> config =
            systemConfigurationRepository.findOneByConfigKey(Constants.CONFIG_KEY_SOCIAL_URL);
        return ResponseUtil.wrapOrNotFound(config);
    }

    /**
     * GET  /system-configurations/:id : get the "id" systemConfiguration.
     *
     * @param id the id of the systemConfiguration to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the systemConfiguration, or with status 404 (Not Found)
     */
    @GetMapping("/system-configurations/{id}")
    @Timed
    public ResponseEntity<SystemConfiguration> getSystemConfiguration(@PathVariable Long id) {
        log.debug("REST request to get SystemConfiguration : {}", id);
        SystemConfiguration systemConfiguration = systemConfigurationRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(systemConfiguration));
    }

    /**
     * DELETE  /system-configurations/:id : delete the "id" systemConfiguration.
     *
     * @param id the id of the systemConfiguration to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/system-configurations/{id}")
    @Timed
    public ResponseEntity<Void> deleteSystemConfiguration(@PathVariable Long id) {
        log.debug("REST request to delete SystemConfiguration : {}", id);
        systemConfigurationRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
