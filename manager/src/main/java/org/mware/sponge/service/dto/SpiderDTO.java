package org.mware.sponge.service.dto;

import org.jetbrains.annotations.NotNull;
import org.mware.sponge.domain.Spider;
import org.mware.sponge.service.dto.util.IJob;
import org.mware.sponge.service.dto.util.StatusDTO;

/**
 * Created by Dan on 7/17/2017.
 */
public class SpiderDTO implements IJob, Comparable{
    private Long id;
    private String name;

//    Transient fields
    private StatusDTO status;

    /**
     *
     */
    public SpiderDTO() {
    }

    /**
     * @param spider
     */
    public SpiderDTO(Spider spider) {
        this(spider.getId(), spider.getName(), new StatusDTO());
    }

    /**
     * @param id
     * @param name
     * @param status
     */
    public SpiderDTO(Long id, String name, StatusDTO status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    /**
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public StatusDTO getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(StatusDTO status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SpiderDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", status=" + status +
            '}';
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return this.getName().compareTo(((SpiderDTO)o).getName());
    }
}
