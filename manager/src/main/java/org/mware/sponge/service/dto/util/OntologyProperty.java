package org.mware.sponge.service.dto.util;

/**
 * Created by Dan on 7/25/2017.
 */
public class OntologyProperty {
    private String title;
    private String displayName;

    /**
     * @param title
     * @param displayName
     */
    public OntologyProperty(String title, String displayName) {
        this.title = title;
        this.displayName = displayName;
    }

    /**
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
