package org.mware.sponge.webui.repository;

import org.mware.sponge.webui.domain.WebUiField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebUiFieldRepository extends JpaRepository<WebUiField, Long> {
}
