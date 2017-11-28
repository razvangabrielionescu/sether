package org.mware.sponge.webui.repository;

import org.mware.sponge.domain.Project;
import org.mware.sponge.webui.domain.WebUiProject;
import org.mware.sponge.webui.domain.WebUiSpider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface WebUiSpiderRepository extends JpaRepository<WebUiSpider, String> {
    Optional<WebUiSpider> findOneByNameAndProject(String name, WebUiProject project);
}
