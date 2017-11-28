package org.mware.sponge.service.dto.util;

/**
 * Created by Dan on 7/17/2017.
 */
public class StatusDTO {
    private String status;
    private String note;
    private String progress;
    private String lastActivity;

    /**
     *
     */
    public StatusDTO() {
        this("", "", "", "");
    }

    /**
     * @param status
     * @param note
     * @param progress
     * @param lastActivity
     */
    public StatusDTO(String status, String note, String progress, String lastActivity) {
        this.status = status;
        this.note = note;
        this.progress = progress;
        this.lastActivity = lastActivity;
    }

    /**
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return
     */
    public String getProgress() {
        return progress;
    }

    /**
     * @param progress
     */
    public void setProgress(String progress) {
        this.progress = progress;
    }

    /**
     * @return
     */
    public String getLastActivity() {
        return lastActivity;
    }

    /**
     * @param lastActivity
     */
    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    @Override
    public String toString() {
        return "StatusDTO{" +
            "status='" + status + '\'' +
            ", note='" + note + '\'' +
            ", progress='" + progress + '\'' +
            ", lastActivity='" + lastActivity + '\'' +
            '}';
    }
}
