package org.mware.sponge.webui.repository;

import org.mware.sponge.webui.domain.WebUiProject;
import org.mware.sponge.webui.domain.WebUiSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface WebUiSchemaRepository extends JpaRepository<WebUiSchema, Long> {
    Optional<WebUiSchema> findOneByNameAndProject(String name, WebUiProject project);
}
