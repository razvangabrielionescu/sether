package org.mware.sponge.webui.repository;

import org.mware.sponge.webui.domain.WebUiItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface WebUiItemRepository extends JpaRepository<WebUiItem, Long> {
}
