package org.mware.sponge.webui.repository;

import org.mware.sponge.webui.domain.WebUiStartUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface WebUiStartUrlRepository extends JpaRepository<WebUiStartUrl, Long> {
}
