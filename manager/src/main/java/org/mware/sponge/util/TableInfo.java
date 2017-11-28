package org.mware.sponge.util;

import java.util.List;

/**
 * Created by Dan on 8/19/2017.
 */
public class TableInfo {
    private List<String> tableNames;

    /**
     * @param tableNames
     */
    public TableInfo(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    /**
     * @return
     */
    public List<String> getTableNames() {
        return tableNames;
    }

    /**
     * @param tableNames
     */
    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    @Override
    public String toString() {
        return "TableInfo{" +
            "tableNames=" + tableNames +
            '}';
    }
}
