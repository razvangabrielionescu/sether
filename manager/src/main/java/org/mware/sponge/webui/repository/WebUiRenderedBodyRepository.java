package org.mware.sponge.webui.repository;

import org.mware.sponge.webui.domain.WebUiRenderedBody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface WebUiRenderedBodyRepository extends JpaRepository<WebUiRenderedBody, Long> {
}
