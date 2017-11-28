package org.mware.sponge.repository;

import org.mware.sponge.domain.Project;
import org.mware.sponge.domain.Spider;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.Set;


/**
 * Spring Data JPA repository for the Spider entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SpiderRepository extends JpaRepository<Spider,Long> {
    Set<Spider> findAllByProject(Project project);
}
