package org.mware.sponge.repository;

import org.mware.sponge.domain.CommiterFieldMapping;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the CommiterFieldMapping entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommiterFieldMappingRepository extends JpaRepository<CommiterFieldMapping,Long> {

    /**
     * @param id
     * @return
     */
    List<CommiterFieldMapping> findAllByCommiterConfigId(Long id);
}
