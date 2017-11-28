package org.mware.sponge.repository;

import org.mware.sponge.domain.SystemConfiguration;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.Optional;


/**
 * Spring Data JPA repository for the SystemConfiguration entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {

    Optional<SystemConfiguration> findOneByConfigKey(String configKey);

}
