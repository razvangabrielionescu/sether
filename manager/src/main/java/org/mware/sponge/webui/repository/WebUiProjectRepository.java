package org.mware.sponge.webui.repository;

import org.mware.sponge.webui.domain.WebUiProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface WebUiProjectRepository extends JpaRepository<WebUiProject, Long> {
    Optional<WebUiProject> findOneByName(String projectName);
}
