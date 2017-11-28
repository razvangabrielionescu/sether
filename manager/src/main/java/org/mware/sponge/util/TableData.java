package org.mware.sponge.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 8/19/2017.
 */
public class TableData {
    private List<String> columns;
    private List<TableRow> rows;

    /**
     *
     */
    public TableData() {
        columns = new ArrayList<String>();
        rows = new ArrayList<TableRow>();
    }

    /**
     * @return
     */
    public List<String> getColumns() {
        return columns;
    }

    /**
     * @param columns
     */
    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    /**
     * @return
     */
    public List<TableRow> getRows() {
        return rows;
    }

    /**
     * @param rows
     */
    public void setRows(List<TableRow> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "TableData{" +
            "columns=" + columns +
            ", rows=" + rows +
            '}';
    }
}
