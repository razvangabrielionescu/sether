package org.mware.sponge.webui.repository;

import org.mware.sponge.webui.domain.WebUiProject;
import org.mware.sponge.webui.domain.WebUiSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("unused")
@Repository
public interface WebUiSampleRepository extends JpaRepository<WebUiSample, Long> {
    public List<WebUiSample> findAllByName(String name);
}
