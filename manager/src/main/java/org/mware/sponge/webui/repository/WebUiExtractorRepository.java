package org.mware.sponge.webui.repository;

import org.mware.sponge.webui.domain.WebUiAnnotation;
import org.mware.sponge.webui.domain.WebUiExtractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebUiExtractorRepository extends JpaRepository<WebUiExtractor, Long> {
}
