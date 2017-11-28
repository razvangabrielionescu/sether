package org.mware.sponge.repository;

import org.mware.sponge.domain.Commiter;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Commiter entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommiterRepository extends JpaRepository<Commiter,Long> {
    
}
