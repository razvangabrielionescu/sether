package org.mware.sponge.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 8/19/2017.
 */
public class TableRow {
    private List<TableCell> columnData;

    /**
     *
     */
    public TableRow() {
        columnData = new ArrayList<TableCell>();
    }

    public List<TableCell> getColumnData() {
        return columnData;
    }

    public void setColumnData(List<TableCell> columnData) {
        this.columnData = columnData;
    }

    @Override
    public String toString() {
        return "TableRow{" +
            "columnData=" + columnData +
            '}';
    }
}
