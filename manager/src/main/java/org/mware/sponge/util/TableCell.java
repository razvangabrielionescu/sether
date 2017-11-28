package org.mware.sponge.util;

/**
 * Created by Dan on 8/19/2017.
 */
public class TableCell {
    private String data;
    private String summary;
    private String column;

    /**
     * @param data
     * @param column
     */
    public TableCell(String data, String column) {
        this.data = data;
        this.column = column;
        setSummary(
            this.data != null && this.data.length() > 200 ? this.data.substring(0, 200) : this.data);
    }

    /**
     * @return
     */
    public String getData() {
        return data;
    }

    /**
     * @param data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * @return
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return
     */
    public String getColumn() {
        return column;
    }

    /**
     * @param column
     */
    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return "TableCell{" +
                "data='" + data + '\'' +
                ", summary='" + summary + '\'' +
                ", column='" + column + '\'' +
                '}';
    }
}
