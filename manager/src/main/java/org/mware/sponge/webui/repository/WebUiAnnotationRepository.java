package org.mware.sponge.webui.repository;

import org.mware.sponge.webui.domain.WebUiAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebUiAnnotationRepository extends JpaRepository<WebUiAnnotation, Long> {
}
