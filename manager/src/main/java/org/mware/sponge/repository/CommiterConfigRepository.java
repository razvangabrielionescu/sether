package org.mware.sponge.repository;

import org.mware.sponge.domain.CommiterConfig;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the CommiterConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommiterConfigRepository extends JpaRepository<CommiterConfig,Long> {
    
}
