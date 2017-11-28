package org.mware.sponge.webui.repository;

import org.mware.sponge.webui.domain.WebUiOriginalBody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebUiOriginalBodyRepository extends JpaRepository<WebUiOriginalBody, Long> {
}
