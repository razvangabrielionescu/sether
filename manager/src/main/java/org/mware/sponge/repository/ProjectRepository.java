package org.mware.sponge.repository;

import org.mware.sponge.domain.Project;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data JPA repository for the WebUiProject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * @param name
     * @return
     */
    Optional<Project> findOneByName(String name);

    /**
     * @param name
     * @param userName
     * @return
     */
    Optional<Project> findOneByNameAndUserName(String name, String userName);

    /**
     * @param userName
     * @return
     */
    List<Project> findAllByUserName(String userName);
}
