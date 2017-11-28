package org.mware.sponge.util;

import com.norconex.commons.lang.encrypt.EncryptionKey;
import com.norconex.commons.lang.encrypt.EncryptionUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.mware.sponge.domain.CommiterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Dan on 8/18/2017.
 */
public class DatabaseHandler {
    private static final Logger log = LoggerFactory.getLogger(DatabaseHandler.class);

    private String driverPath;
    private String driverClass;
    private String connectionUrl;
    private String username;
    private String password;
    private EncryptionKey passwordKey;
    private final Properties properties = new Properties();

    private BasicDataSource datasource;

    /**
     *
     */
    public DatabaseHandler() {
    }

    /**
     * @param driverPath
     * @param driverClass
     * @param connectionUrl
     * @param username
     * @param password
     */
    public DatabaseHandler(String driverPath, String driverClass, String connectionUrl, String username, String password) {
        this.driverPath = driverPath;
        this.driverClass = driverClass;
        this.connectionUrl = connectionUrl;
        this.username = username;
        this.password = password;
    }

    /**
     * @param tableName
     * @return
     * @throws Exception
     */
    public TableData getTableData(String tableName) throws Exception {
        log.info("Retrieving table data for table: "+tableName);

        TableData tableData = new TableData();
        String SQL = "select * from " + tableName;
        Statement statement = nullSafeDataSource().getConnection().createStatement();
        ResultSet rs = statement.executeQuery(SQL);
        ResultSetMetaData meta = rs.getMetaData();
        for (int i=1; i<=meta.getColumnCount(); i++) {
            tableData.getColumns().add(meta.getColumnName(i));
        }

        TableRow _row = null;
        while (rs.next()) {
            _row = new TableRow();
            for (String column : tableData.getColumns()) {
                _row.getColumnData().add(new TableCell(rs.getString(column), column));
            }
            tableData.getRows().add(_row);
        }

        this.closeSession();

        return tableData;
    }

    /**
     * @return
     * @throws Exception
     */
    public TableInfo getTableInfo() throws Exception {
        log.info("Retrieving table names");

        List<String> tables = new ArrayList<String>();
        DatabaseMetaData meta = nullSafeDataSource().getConnection().getMetaData();
        ResultSet rs = meta.getTables(null,null,null,null);
        while (rs.next()) {
            tables.add(rs.getString("TABLE_NAME"));
        }

        this.closeSession();

        return new TableInfo(tables);
    }

    /**
     * @param commiterConfig
     */
    public void loadFromCommiterConfig(CommiterConfig commiterConfig) {
        this.datasource = null;
        setDriverClass(commiterConfig.getDbDriverClass());
        setDriverPath(commiterConfig.getDbDriverPath());
        setConnectionUrl(commiterConfig.getDbConnectionUrl());
        setUsername(commiterConfig.getDbUsername());
        setPassword(commiterConfig.getDbPassword());
    }

    private synchronized BasicDataSource nullSafeDataSource() throws Exception {
        if (datasource == null) {
            if (StringUtils.isBlank(getDriverClass())) {
                throw new Exception("No driver class specified.");
            }
            if (StringUtils.isBlank(getConnectionUrl())) {
                throw new Exception("No connection URL specified.");
            }

            BasicDataSource basicDataSourceds = new BasicDataSource();
            if (StringUtils.isNotBlank(driverPath)) {
                try {
                    basicDataSourceds.setDriverClassLoader(new URLClassLoader(
                        new URL[] { new File(driverPath).toURI().toURL() },
                        getClass().getClassLoader()));
                } catch (MalformedURLException e) {
                    throw new Exception(
                        "Invalid driver path: " + driverPath, e);
                }
            }
            basicDataSourceds.setDriverClassName(driverClass);
            basicDataSourceds.setUrl(connectionUrl);
            basicDataSourceds.setDefaultAutoCommit(true);
            basicDataSourceds.setUsername(username);

            // Set default encoding to utf8
            basicDataSourceds.addConnectionProperty("characterEncoding", "utf8");
            basicDataSourceds.addConnectionProperty("useUnicode", "yes");

            basicDataSourceds.setPassword(EncryptionUtil.decrypt(
                getPassword(), getPasswordKey()));
            for (String key : properties.stringPropertyNames()) {
                basicDataSourceds.addConnectionProperty(key, properties.getProperty(key));
            }
            datasource = basicDataSourceds;
        }

        return datasource;
    }

    private void closeSession() {
        try {
            this.closeDatasource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void closeDatasource() throws Exception {
        if (datasource != null) {
            try {
                datasource.close();
            } catch (SQLException e) {
                throw new Exception("Could not close datasource.", e);
            }
        }
    }

    /**
     * @return
     */
    public String getDriverPath() {
        return driverPath;
    }

    /**
     * @param driverPath
     */
    public void setDriverPath(String driverPath) {
        this.driverPath = driverPath;
    }

    public String getDriverClass() {
        return driverClass;
    }

    /**
     * @param driverClass
     */
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    /**
     * @return
     */
    public String getConnectionUrl() {
        return connectionUrl;
    }

    /**
     * @param connectionUrl
     */
    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    /**
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return
     */
    public EncryptionKey getPasswordKey() {
        return passwordKey;
    }

    /**
     * @param passwordKey
     */
    public void setPasswordKey(EncryptionKey passwordKey) {
        this.passwordKey = passwordKey;
    }

    /**
     * @return
     */
    public Properties getProperties() {
        return properties;
    }
}
